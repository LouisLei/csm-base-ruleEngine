package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.utils.DeepCopyUtils;
import com.cheshangma.platform.ruleEngine.enums.ExecModeType;
import com.cheshangma.platform.ruleEngine.enums.ResultModeType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 这是专门用于进行脚本代码执行的运行器。
 * @author yinwenjie
 */
public class ScriptCaller implements Callable<List<ScriptCaller.Result>> {
  
  /**
   * 需要执行的groovy脚本在这里<br>
   * 可能是一个脚本，也可能是多个脚本
   */
  private List<String> groovyExpressions;
  
  /**
   * 绑定的执行参数
   */
  private Map<String, Object> variables;
  
  /**
   * 本次执行是否允许反调
   */
  private boolean allowInverse = false;
  
  /**
   * 这里是一个本地缓存，基于google的一个本地缓存实现。
   */
  private static Cache<Object,Object> CACHE = null;
  
  /**
   * 批量脚本的执行要求
   */
  private ExecModeType execModeType;
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(ScriptCaller.class);
  
  static {
    CACHE = CacheBuilder.newBuilder()
        // 设置并发级别为3，并发级别是指可以同时写缓存的线程数
        .concurrencyLevel(3)
        // 设置写缓存后30秒钟过期
        // .expireAfterWrite(30, TimeUnit.SECONDS)
        // 设置读缓存后30秒钟过期
        .expireAfterAccess(30, TimeUnit.SECONDS)
        // 设置缓存容器的初始容量为10
        .initialCapacity(10)
        // 设置缓存最大容量为20，超过20之后就会按照LRU最近虽少使用算法来移除缓存项
        .maximumSize(20)
        .build();
  }
  
  public ScriptCaller(List<String> groovyExpressions , Map<String, Object> variables , boolean allowInverse , ExecModeType execModeType) {
    this.groovyExpressions = groovyExpressions;
    this.variables = variables;
    this.allowInverse = allowInverse;
    this.execModeType = execModeType;
  }
  
  @Override
  public List<Result> call() {
    // 这是当前执行线程中的上下文信息
    ScriptThread scriptThread = (ScriptThread)Thread.currentThread();
    ExecuteContext context = scriptThread.getExecuteContext();
    // 多次执行的结果在这里放置
    List<Result> allResult = new ArrayList<>();
    // 初始化的入参信息
    Map<String, Object> currentVariables = variables;
    
    for (int index = 0 ; index < groovyExpressions.size() ; index++) {
      String groovyExpression = groovyExpressions.get(index);
      Result result = this.executeScript(groovyExpression, currentVariables, this.allowInverse, context);
      allResult.add(result);
      
      // 根据执行结果和设置情况，决定是否继续运行
      // 如果条件成立，说明执行没有成功，且设置执行方式为“一旦执行失败，就停止执行”
      if(result.getResultMode() != ResultModeType.PASS
          && this.execModeType == ExecModeType.SIMPLE) {
        break;
      }
      
      // 重新构造入参，上一个脚本的执行结果就是下次脚本执行的入参（能不做深度复制就不做深度复制）
      Map<String, Object> outputs = result.getScore();
      if(outputs != null && !outputs.isEmpty() && groovyExpressions.size() > 1) {
        // 深度复制——为了不影响之前的输出结果
        currentVariables = DeepCopyUtils.deepCopy(outputs);
      } else {
        currentVariables = new HashMap<>();
      }
    }
    
    // 返回多有脚本的执行结果
    return allResult;
  }
  
  /**
   * 该私有方法用于执行某一单次脚本
   * @param groovyExpression
   * @param variables
   * @param allowInverse 
   * @param context 本次可能由外部传入的上下文对象，也可能没有
   * @return
   */
  private Result executeScript(String groovyExpression , Map<String, Object> variables , boolean allowInverse , ExecuteContext context) {
    /*
     * 执行过程为：
     * 1、首先确定当前执行的内容是否需要支持反调。如果需要支持反调则需要重新组合groovy脚本
     * 2、然后从缓存中寻找内容是否被编译保存，如果是则直接取出执行；如果不是就进行编译
     * 3、开始执行（执行最简的方式，以便进行测试）
     * 4、开始构造返回信息，返回信息不只包括脚本的正式返回值，还包括了bings中的信息（而且主要还是后者）
     * */
    // 如果条件成立，说明需要支持反调（组合的javaProcess方法不一样）
    if(allowInverse) {
      groovyExpression = "def javaProcess(def componentAndMethod) {com.cheshangma.platform.ruleEngine.core.executor.ScriptInverseRunner inverseRunner = com.cheshangma.platform.ruleEngine.core.executor.ScriptInverseRunner.getNewInstance();inverseRunner.inverse(componentAndMethod);};" + groovyExpression;
    } else {
      groovyExpression = "def javaProcess(def componentAndMethod) {throw new com.cheshangma.platform.ruleEngine.core.exception.ScriptInverseRejectException(\"remote execute process not allow inverse!\");};" + groovyExpression;
    }
    
    // 2、=============试图从本地缓存中找到脚本
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");
    CompiledScript compiledScript = null;
    try {
      Object cacheObject = CACHE.get(groovyExpression.hashCode(), new CompiledScriptCreator(groovyExpression));
      if(cacheObject != null) {
        compiledScript = (CompiledScript)cacheObject;
      }
    } catch (ExecutionException e) {
      LOG.error(e.getMessage() , e);
      return null;
    }
    
    // 绑定外部变量
    Result result = new Result();
    Bindings binding = engine.createBindings();
    if(variables != null && !variables.isEmpty()) {
      // 深度复制后最为一个副本保存起来
      // 因为在进入脚本执行后，这些对象的属性可能就会改变了
      result.setInput(DeepCopyUtils.deepCopy(variables));
      Set<String> keys = variables.keySet();
      for (String key : keys) {
        binding.put(key, variables.get(key));
      }
    }
    // 绑定上下文信息
    binding.put("context", context);
    
    // 3、=============开始执行
    try {
      compiledScript.eval(binding);
      result.resultMode = ResultModeType.PASS;
      result.score = new HashMap<>();
    } catch (ScriptException e) {
      LOG.error(e.getMessage() , e);
      result.rejectMessage = e.getMessage();
      result.resultMode = ResultModeType.EXCEPTION;
      return result;
    }
    
    // 4、============构造返回
    // 上下文的信息不能输出
    binding.remove("context");
    if(binding != null && !binding.isEmpty()) {
      result.score.putAll(binding);
    }
    return result;
  }
  
  /**
   * 脚本编译对象创建者
   * @author yinwenjie
   */
  class CompiledScriptCreator implements Callable<CompiledScript> {

    private String groovyExpression;
    
    CompiledScriptCreator(String groovyExpression) {
      this.groovyExpression = groovyExpression;
    }
    
    @Override
    public CompiledScript call() throws Exception {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("groovy");
      // 编译
      Compilable compilable = (Compilable) engine;
      CompiledScript compiledScript = compilable.compile(this.groovyExpression);
      
      return compiledScript;
    }
  }
  
  /**
   * 记录脚本执行结果，注意这些信息并不是
   * @author yinwenjie
   */
  public static class Result {
    /**
     * 规则执行结果，默认的结果是，执行没有通过
     */
    private ResultModeType resultMode = ResultModeType.REJECT;
    /**
     * 最重要的执行结果，Groovy、Python或者JavaScript的执行结果会在这里得到体现。入参的变化也会在这里得到体现。<br>
     * 这么说吧，动态脚本中的全局变量和它们在脚本执行完成后（或者异常退出后）的赋值都会在这里得到体现
     */
    private Map<String, Object> score;
    /**
     * 记录当前运行脚本的输入参数
     */
    private Map<String, Object> input;
    /**
     * 被拒绝后，或者执行失败后显示的信息
     */
    private String rejectMessage = "";
    public ResultModeType getResultMode() {
      return resultMode;
    }
    public void setResultMode(ResultModeType resultMode) {
      this.resultMode = resultMode;
    }
    public Map<String, Object> getScore() {
      return score;
    }
    public void setScore(Map<String, Object> score) {
      this.score = score;
    }
    public String getRejectMessage() {
      return rejectMessage;
    }
    public void setRejectMessage(String rejectMessage) {
      this.rejectMessage = rejectMessage;
    }
    public Map<String, Object> getInput() {
      return input;
    }
    public void setInput(Map<String, Object> input) {
      this.input = input;
    }
  }
}