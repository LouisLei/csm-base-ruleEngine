package com.cheshangma.platform.ruleEngine.core.framework;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.executor.ExecuteContext;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptCaller;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptCaller.Result;
import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;
import com.cheshangma.platform.ruleEngine.core.utils.DeepCopyUtils;
import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ResultModeType;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadFactory;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadPoolExecutor;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel.VariableProperty;
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
   * 本框架执行是否允许反调
   */
  private boolean allowInverse = false;
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(SimpleRuleEngineFramework.class);

  // 全系统就只有一个这样的线程池
  SimpleRuleEngineFramework(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue , String scriptThreadName , boolean allowInverse) {
    this.allowInverse = allowInverse;
    this.scriptThreadPoolExecutor = new ScriptThreadPoolExecutor(corePoolSize, maximumPoolSize, 
      keepAliveTime, unit, workQueue, new ScriptThreadFactory(scriptThreadName));
    
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#executeRule(com.cheshangma.platform.ruleEngine.module.RuleModel, java.util.Map)
   */
  @Override
  public ExecutionRuleModel executeRule(RuleModel rule, Map<String, Object> inputs) {
    if(inputs == null || inputs.isEmpty()) {
      inputs = new HashMap<>();
    }
    /*
     * 1、首先验证rule对象描述的执行方式
     * 2、构造输入（注意，输入值必须必须深度复制）
     * 3、构造输出信息（注意，上下文中的信息不会作为输出信息）
     */
    // 1、==============
    Validate.notNull(rule , "rule must not be empty!!");
    String expression = rule.getExpression();
    Validate.notBlank(expression , "rule expression must not be empty!!");
    ExecutionRuleModel executionResult = new ExecutionRuleModel();
    executionResult.setCreated(new Date());
    executionResult.setCreator(rule.getCreator());
    executionResult.setDescription(rule.getDescription());
    
    //2、========= 构造输入（如果不是基础类型就需要做深度复制）
    // 因为在进入脚本执行后，这些对象的属性可能就会改变了
    Map<String, Object> copyInputs = DeepCopyUtils.deepCopy(inputs);
    executionResult.setInputs(copyInputs);
    Future<Result> future = this.executeSript(expression , inputs);
    
    // 3、================
    ScriptCaller.Result result = null;
    try {
      result = future.get();
      executionResult.setRejectMessage(result.getRejectMessage());
      executionResult.setResultMode(result.getResultMode());
      executionResult.setScore(result.getScore());
    } catch (InterruptedException | ExecutionException e) {
      LOG.error(e.getMessage() , e);
      executionResult.setRejectMessage(e.getMessage());
      executionResult.setResultMode(ResultModeType.EXCEPTION);
    }
    return executionResult;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#executePolicy(com.cheshangma.platform.ruleEngine.module.PolicyModel, java.util.List, java.util.Map)
   */
  @Override
  public ExecutionPolicyModel executePolicy(PolicyModel policy, List<RuleModel>  rules, Map<String, Object> inputs) {
    if(inputs == null || inputs.isEmpty()) {
      inputs = new HashMap<>();
    }
    // 1、==============
    Validate.notNull(policy , "not found policy !!");
    ExecutionPolicyModel executionResult = new ExecutionPolicyModel();
    executionResult.setCreated(new Date());
    executionResult.setCreator(policy.getCreator());
    executionResult.setDescription(policy.getDescription());
    
    // 2、==============
    Map<String, Object> copyInputs = DeepCopyUtils.deepCopy(inputs);
    Future<Result> future = null;
    executionResult.setInputs(copyInputs);
    // 如果条件成立，说明policy策略中已带有执行信息，直接执行即可
    if(policy.getMode() == PolicyModeType.RULEMODE_SIMPLE) {
      String expression = policy.getScoreExpression();
      Validate.notBlank(expression , "expression not be empty!! ");
      future = this.executeSript(expression , inputs);
    } 
    // 否则就是批量执行其中的rule规则
    else if (rules != null && !rules.isEmpty()) {
      // TODO 这里还没有写,多步执行
    } else {
      executionResult.setRejectMessage("rules is not be empty!!");
      executionResult.setResultMode(ResultModeType.EXCEPTION);
      return executionResult;
    }
    executionResult.setId(UUID.randomUUID().toString());
    executionResult.setPolicyId(policy.getId());
    
    // 3、================
    ScriptCaller.Result result = null;
    try {
      result = future.get();
      Map<String, Object> score = result.getScore();
      executionResult.setScore(result.getScore());
      MetadataModel metadata = policy.getMetadata();
      // 确定元数据
      if(metadata != null) {
        Set<VariableProperty> targetVars = metadata.getParams();
        MetadataModel existMetadata = new MetadataModel();
        Set<VariableProperty> existVars = new HashSet<VariableProperty>();
        existMetadata.setParams(existVars);
        
        // 只有有值才能记录到结果中
        if(targetVars != null && !targetVars.isEmpty()) {
          for (VariableProperty variable : targetVars) {
             String variableName = variable.getName();
             Object value = score.get(variableName);
             if(value != null) {
               variable.setValue(value);
               existVars.add(variable);
             }
          }
        }
        executionResult.setMetadata(existMetadata);
      }
    } catch (InterruptedException | ExecutionException e) {
      LOG.error(e.getMessage() , e);
      executionResult.setRejectMessage(e.getMessage());
      executionResult.setResultMode(ResultModeType.EXCEPTION);
      return executionResult;
    }
    
    // 4、================
    executionResult.setRejectMessage(result.getRejectMessage());
    executionResult.setResultMode(result.getResultMode());
    return executionResult;
  }
  
  /**
   * 该私有方法用于执行脚本
   * @return
   */
  private Future<Result> executeSript(String expression, Map<String, Object> inputs) {
    ScriptCaller scriptCaller = new ScriptCaller(expression, inputs , this.allowInverse);
    Future<Result> future = this.scriptThreadPoolExecutor.submit(scriptCaller);
    return future;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#getPolicyService()
   */
  @Override
  public PolicyService getPolicyService() {
    if(this.serviceAbstractFactory == null) {
      return null;
    }
    return this.serviceAbstractFactory.buildPolicyRepository();
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#getRuleService()
   */
  @Override
  public RuleService getRuleService() {
    if(this.serviceAbstractFactory == null) {
      return null;
    }
    return this.serviceAbstractFactory.buildRuleRepository();
  }
  
  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#getCurrentContext()
   */
  @Override
  public ExecuteContext getCurrentContext() {
    return RuleEngineFramework.currentContext();
  }
}