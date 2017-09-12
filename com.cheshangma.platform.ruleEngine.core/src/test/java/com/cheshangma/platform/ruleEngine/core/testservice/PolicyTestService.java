package com.cheshangma.platform.ruleEngine.core.testservice;

import java.util.List;

import com.cheshangma.platform.ruleEngine.core.service.PolicyService;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;

/**
 * 测试过程的一个实现
 * @author yinwenjie
 */
public class PolicyTestService implements PolicyService {

  @Override
  public PolicyModel save(PolicyModel model) {
    return null;
  }

  @Override
  public PolicyModel update(PolicyModel model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PolicyModel findByPolicyId(String policyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PolicyModel> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PolicyModel> findAll(Iterable<String> policyIds) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean exists(String policyId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void deleteByPolicyId(String policyId) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean enable(String policyId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean disable(String policyId) {
    // TODO Auto-generated method stub
    return false;
  }
}