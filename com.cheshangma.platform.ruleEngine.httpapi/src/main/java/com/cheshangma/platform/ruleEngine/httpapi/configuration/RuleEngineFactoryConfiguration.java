package com.cheshangma.platform.ruleEngine.httpapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;

/**
 * 这个工厂类按照RuleEngine中的规范，可完成数据持久层服务的具体实现
 * @author yinwenjie
 */
@Configuration
public class RuleEngineFactoryConfiguration extends ServiceAbstractFactory {

  @Autowired
  private RuleService ruleService;

  @Autowired
  private PolicyService policyService;

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory#buildPolicyRepository()
   */
  @Override
  public PolicyService buildPolicyRepository() {
    return policyService;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory#buildRuleRepository()
   */
  @Override
  public RuleService buildRuleRepository() {
    return ruleService;
  }
}