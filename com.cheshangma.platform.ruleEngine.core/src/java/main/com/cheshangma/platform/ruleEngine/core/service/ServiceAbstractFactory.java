package com.cheshangma.platform.ruleEngine.core.service;

/**
 * 规则引擎数据层服务工厂——因为不知道在引用core包的系统中，是以什么样的方式提供持久层实现。<br>
 * 是传统的servlet工程呢？还是spring 3.X base for xml的工程呢？还是spring boot工程呢？
 * @author yinwenjie
 */
public abstract class ServiceAbstractFactory {
  /**
   * 该方法用于创建策略部分的持久化服务
   * @return
   */
  public abstract PolicyService buildPolicyRepository();

  /**
   * 该方法用于创建规则部分的持久化服务
   * @return
   */
  public abstract RuleService buildRuleRepository();
}