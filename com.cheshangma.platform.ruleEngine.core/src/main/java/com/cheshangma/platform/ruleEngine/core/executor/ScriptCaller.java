package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.HashMap;
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

import com.cheshangma.platform.ruleEngine.enums.ResultModeType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 这是专门用于进行脚本代码执行的运行器。
 * @author yinwenjie
 */
public class ScriptCaller implements Callable<ScriptCaller.Result> {
  
  /**
   * 需要执行的groovy脚本在这里
   */
  private String groovyExpression;
  
  /**
   * 绑定的执行参数
   */
  private Map<String, Object> variables;
  
  /**
   * 这里是一个本地缓存，基于google的一个本地缓存实现。
   */
  private static Cache<Object,Object> CACHE = null;
  
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
  
  public ScriptCaller(String groovyExpression , Map<String, Object> variables) {
    this.groovyExpression = groovyExpression;
    this.variables = variables;
  }
  
  @Override
  public Result call() {
    /*
     * 执行过程为：
     * 1、首先确定当前执行的内容是否需要支持反调。如果需要支持反调则需要重新组合groovy脚本
     * 2、然后从缓存中寻找内容是否被编译保存，如果是则直接取出执行；如果不是就进行编译
     * 3、开始执行（执行最简的方式，以便进行测试）
     * 4、开始构造返回信息，返回信息不只包括脚本的正式返回值，还包括了bings中的信息（而且主要还是后者）
     * */
    // TODO 1、还没有写
    
    // 2、=============试图从本地缓存中找到脚本
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");
    CompiledScript compiledScript = null;
    try {
      Object cacheObject = CACHE.get(this.groovyExpression.hashCode(), new CompiledScriptCreator(this.groovyExpression));
      if(cacheObject != null) {
        compiledScript = (CompiledScript)cacheObject;
      }
    } catch (ExecutionException e) {
      LOG.error(e.getMessage() , e);
      return null;
    }
    
    // 绑定外部变量
    Bindings binding = engine.createBindings();
    if(this.variables != null && !this.variables.isEmpty()) {
      Set<String> keys = this.variables.keySet();
      for (String key : keys) {
        binding.put(key, this.variables.get(key));
      }
    }
    
    // 3、=============开始执行
    Result result = new Result();
    Object returnResult = null;
    try {
      returnResult = compiledScript.eval(binding);
      result.resultMode = ResultModeType.PASS;
      result.score = new HashMap<>();
      result.score.put("_return", returnResult);
    } catch (ScriptException e) {
      LOG.error(e.getMessage() , e);
      result.rejectMessage = e.getMessage();
      result.resultMode = ResultModeType.EXCEPTION;
      return result;
    }
    
    // 4、============构造返回
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
  }
}