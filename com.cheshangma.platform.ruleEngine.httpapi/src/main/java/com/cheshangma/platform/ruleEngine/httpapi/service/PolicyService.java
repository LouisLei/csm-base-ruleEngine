package com.cheshangma.platform.ruleEngine.httpapi.service;

import java.util.List;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyEntity;

/**
 * 策略service接口.
 * 
 * @author ly
 * @date 2017年9月8日 上午10:11:59
 * @version V1.0
 */
public interface PolicyService {

  /**
   * 增加策略.
   * 
   * @param policyEntity 策略对象
   * @return PolicyEntity
   */
  PolicyEntity addPolicy(PolicyEntity policyEntity);

  /**
   * 修改策略.
   * 
   * @param policyEntity 策略对象
   * @return PolicyEntity
   */
  PolicyEntity updatePolicy(PolicyEntity policyEntity);

  /**
   * 删除策略.
   * 
   * @param id 逻辑键
   * @return PolicyEntity
   */
  PolicyEntity delPolicy(String id);

  /**
   * 获取所有策略.
   * 
   * @return List<PolicyEntity>
   */
  List<PolicyEntity> findAllPolicy();

  /**
   * 获取详情（单条）.
   * 
   * @param id 逻辑键
   * @return PolicyEntity
   */
  PolicyEntity findById(String id);
}
