package com.cheshangma.platform.ruleEngine.core.framework;

import java.util.List;
import java.util.Map;

import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * @author yinwenjie
 */
public interface RuleEngineFramework {
  
  /**
   * @param rule
   * @param inputs
   * @return
   */
  public ExecutionRuleModel executeRule(RuleModel rule, Map<String, Object> inputs);
  
  /**
   * @param policy
   * @param ruleSteps
   * @param inputs
   * @return
   */
  public ExecutionPolicyModel executePolicy(PolicyModel policy, List<RuleModel> ruleSteps,Map<String, Object> inputs);
  
  public static class Builder {

    private static final Builder BUILDER = new Builder();

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
     * 
     * @return
     */
    public RuleEngineFramework buildIfAbent() {
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