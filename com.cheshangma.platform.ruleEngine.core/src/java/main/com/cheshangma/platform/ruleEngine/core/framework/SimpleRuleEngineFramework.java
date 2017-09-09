package com.cheshangma.platform.ruleEngine.core.framework;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.executor.ExecuteContext;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptCaller;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptCaller.Result;
import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadFactory;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadPoolExecutor;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * 只可由RuleEngineFramework.Build进行初始化
 * @author yinwenjie
 */
class SimpleRuleEngineFramework implements RuleEngineFramework {

  /**
   * 执行规则任务所使用的线程池
   */
  private ScriptThreadPoolExecutor scriptThreadPoolExecutor;
  
  /**
   * 规则引擎服务层实现，最关键的就是为了实现持久化层的操作
   */
  private ServiceAbstractFactory serviceAbstractFactory;
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(SimpleRuleEngineFramework.class);

  // 全系统就只有一个这样的线程池
  private SimpleRuleEngineFramework(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue , String scriptThreadName) {
    this.scriptThreadPoolExecutor = new ScriptThreadPoolExecutor(corePoolSize, maximumPoolSize, 
      keepAliveTime, unit, workQueue, new ScriptThreadFactory(scriptThreadName));
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#executeRule(java.lang.String, java.util.Map)
   */
  @Override
  public ExecutionRuleModel executeRule(String ruleId, Map<String, Object> inputs) {
    /*
     * 1、首先验证rule对象描述的执行方式
     * 2、构造输入，执行脚本
     * 3、构造输出信息（注意，上下文中的信息不会作为输出信息）
     */
    // 1、==============
    RuleService ruleService = this.serviceAbstractFactory.buildRuleRepository();
    if(ruleService == null) {
      throw new IllegalArgumentException("not found rule service impl!!");
    }
    RuleModel rule = ruleService.findByRuleId(ruleId);
    String expression = rule.getExpression();
    
    // 构造输入（如果不是基础类型就需要做深度复制）
    // 因为在进入脚本执行后，这些对象的属性可能就会改变了
    // TODO 继续写
    ScriptCaller scriptCaller = new ScriptCaller(expression, inputs);
    Future<Result> future = this.scriptThreadPoolExecutor.submit(scriptCaller);
    
    // 3、================
    ExecutionRuleModel executionResult = new ExecutionRuleModel();
    ScriptCaller.Result result = null;
    try {
      result = future.get();
    } catch (InterruptedException | ExecutionException e) {
      LOG.error(e.getMessage() , e);
    }
    return null;
  }

  @Override
  public ExecutionPolicyModel executePolicy(String policyId, Map<String, Object> inputs) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PolicyService getPolicyService() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RuleService getRuleService() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public ExecuteContext getCurrentContext() {
    // TODO Auto-generated method stub
    return null;
  }
}