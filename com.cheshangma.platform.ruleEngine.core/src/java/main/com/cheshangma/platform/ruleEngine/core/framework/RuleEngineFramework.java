package com.cheshangma.platform.ruleEngine.core.framework;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.exception.ThreadStatusException;
import com.cheshangma.platform.ruleEngine.core.executor.ExecuteContext;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThread;
import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;

/**
 * TODO 还没有注释
 * @author yinwenjie
 */
public interface RuleEngineFramework {
  
  /**
   * 该方法负责执行一个指定的规则
   * @param rule 指定的规则信息，注意这个规则在实现方法内部是需要被验证的
   * @param inputs 当前执行规则的输入参数
   * @return 执行结果将在这里返回，就算执行异常也会有返回
   */
  public ExecutionRuleModel executeRule(String ruleId, Map<String, Object> inputs);
  
  /**
   * @param policy
   * @param ruleSteps
   * @param inputs
   * @return
   */
  public ExecutionPolicyModel executePolicy(String policyId, Map<String, Object> inputs);
  
  /**
   * @return
   */
  public PolicyService getPolicyService();
  
  /**
   * @return
   */
  public RuleService getRuleService();
  
  /**
   * 通过这个方法，可以获得当前正在执行规则任务的ScriptThread线程中的规则上下文<br>
   * 该方法实际上默认就是直接调用RuleEngineFramework.currentContext()方法
   * @return 
   */
  public ExecuteContext getCurrentContext();
  
  /**
   * 使用静态方法，获取当前脚本执行线程中的上下文，更方便啦
   * @see #getCurrentContext()
   * @return
   */
  public static ExecuteContext currentContext() {
    Thread currentThread = Thread.currentThread();
    if(!(currentThread instanceof ScriptThread)) {
      throw new ThreadStatusException();
    }
    
    ScriptThread scriptThread = (ScriptThread)currentThread;
    return scriptThread.getExecuteContext();
  }
  
  public static class Builder {

    private static final Builder BUILDER = new Builder();
    
    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(Builder.class);
    
    /**
     * 唯一的一个规则引擎框架对象。无论builder初始化多少次，也是这个对象
     */
    private static RuleEngineFramework ruleEngineFramework;
    
    /**
     * 获取整个系统中唯一一个规则引擎构造实例
     * 
     * @return
     */
    public static Builder getInstanceBuilder() {
      return BUILDER;
    }

    /**
     * 最大执行线程数
     */
    private int maxExecutionThread = 50;
    /**
     * 最小执行线程数
     */
    private Integer minExecutionThread = 10;
    /**
     * 线程队列中最大等待的脚本执行任务
     */
    private Integer scriptQueueSize = 1000;
    /**
     * 脚本执行线程的线程名<br>
     * 有一个默认值rule-engine-scriptThread
     */
    private String scriptThreadName = "rule-engine-scriptThread";
    /**
     * 线程回收等待时间(单位为毫秒)<br>
     * 默认为500毫秒
     */
    private Long waitingTimeout = 500l;
    /**
     * 规则引擎服务层实现，最关键的就是为了实现持久化层的操作
     */
    private ServiceAbstractFactory serviceAbstractFactory;
    /**
     * 是否允许反调
     */
    private boolean allowInverse = false;
    
    /**
     * 规则引擎框架是否已完成初始化
     */
    private static boolean isBuilded = false;
    
    /**
     * 
     * @return
     */
    public RuleEngineFramework buildIfAbent() {
      synchronized (RuleEngineFramework.class) {
        while(!Builder.isBuilded) {
          try {
            RuleEngineFramework.class.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      // TODO 继续写
      return null;
    }

    public Integer getMaxExecutionThread() {
      return maxExecutionThread;
    }

    public Builder setMaxExecutionThread(Integer maxExecutionThread) {
      this.maxExecutionThread = maxExecutionThread;
      return this;
    }

    public Integer getMinExecutionThread() {
      return minExecutionThread;
    }

    public Builder setMinExecutionThread(Integer minExecutionThread) {
      this.minExecutionThread = minExecutionThread;
      return this;
    }

    public Integer getScriptQueueSize() {
      return scriptQueueSize;
    }

    public Builder setScriptQueueSize(Integer scriptQueueSize) {
      this.scriptQueueSize = scriptQueueSize;
      return this;
    }

    public ServiceAbstractFactory getServiceAbstractFactory() {
      return serviceAbstractFactory;
    }

    public Builder setServiceAbstractFactory(ServiceAbstractFactory serviceAbstractFactory) {
      this.serviceAbstractFactory = serviceAbstractFactory;
      return this;
    }

    public boolean isAllowInverse() {
      return allowInverse;
    }

    public Builder setAllowInverse(boolean allowInverse) {
      this.allowInverse = allowInverse;
      return this;
    }

    public String getScriptThreadName() {
      return scriptThreadName;
    }

    public Builder setScriptThreadName(String scriptThreadName) {
      this.scriptThreadName = scriptThreadName;
      return this;
    }

    public Long getWaitingTimeout() {
      return waitingTimeout;
    }

    public Builder setWaitingTimeout(Long waitingTimeout) {
      this.waitingTimeout = waitingTimeout;
      return this;
    }
  }
}