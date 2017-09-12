package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.repository.CrudRepository;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyEntity;

/**
 * 与策略相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月7日 下午6:13:46
 * @version V1.0
 */
public interface PolicyRepository extends CrudRepository<PolicyEntity, String> {

  /**
   * 根据策略id查询（非逻辑键）.
   * 
   * @param policyId 策略id
   * @return PolicyEntity
   */
  public PolicyEntity findByPolicyId(String policyId);
}
