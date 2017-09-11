package com.cheshangma.platform.ruleEngine.core.testservice;

import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;

public class ServiceDefaultFactory extends ServiceAbstractFactory {

  @Override
  public PolicyService buildPolicyRepository() {
    return new PolicyTestService();
  }

  @Override
  public RuleService buildRuleRepository() {
    return new RuleDefaultService();
  }
}
