package com.cheshangma.platform.ruleEngine.core.framework.internal;

import java.util.List;
import java.util.Map;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * TODO 未写注释
 * @author yinwenjie
 */
public class SimpleRuleEngineFramework implements RuleEngineFramework {

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#executeRule(com.cheshangma.platform.ruleEngine.module.RuleModel, java.util.Map)
   */
  @Override
  public ExecutionRuleModel executeRule(RuleModel rule, Map<String, Object> inputs) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework#executePolicy(com.cheshangma.platform.ruleEngine.module.PolicyModel, java.util.List, java.util.Map)
   */
  @Override
  public ExecutionPolicyModel executePolicy(PolicyModel policy, List<RuleModel> ruleSteps,
      Map<String, Object> inputs) {
    // TODO Auto-generated method stub
    return null;
  }
}