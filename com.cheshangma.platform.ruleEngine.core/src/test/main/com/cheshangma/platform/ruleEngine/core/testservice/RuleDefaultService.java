package com.cheshangma.platform.ruleEngine.core.testservice;

import java.util.List;

import com.cheshangma.platform.ruleEngine.core.service.RuleService;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * 测试过程的实现
 * @author yinwenjie
 *
 */
public class RuleDefaultService implements RuleService {

  @Override
  public RuleModel save(RuleModel model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RuleModel update(RuleModel model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean exists(String ruleId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean bindRule(String policyId, String ruleId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean bindRule(String policyId, String ruleId, Long index) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean bindRule(String policyId, List<String> ruleIds) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean unbind(String policyId, String ruleId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteByRuleId(String ruleId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public RuleModel findByRuleId(String ruleId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RuleModel> findAll(Iterable<String> ruleIds) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RuleModel> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RuleModel> findByPolicyId(String policyId) {
    // TODO Auto-generated method stub
    return null;
  }

}
