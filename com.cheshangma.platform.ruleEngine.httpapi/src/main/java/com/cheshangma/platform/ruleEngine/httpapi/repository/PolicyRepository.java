package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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
   * 根据规则id查询（非逻辑键）.
   * 
   * @param policyId 策略业务id
   * @return PolicyEntity
   */
  public PolicyEntity findByPolicyId(String policyId);
  
  /**
   * 让指定的策略业务有效
   * @param policyId
   */
  @Modifying
  @Query(value="update PolicyEntity set policyEnabled = 1 where policyId = :policyId")
  public void enable(@Param("policyId") String policyId);
  
  /**
   * 让指定的策略业务失效
   * @param policyId
   */
  @Modifying
  @Query(value="update PolicyEntity set policyEnabled = 0 where policyId = :policyId")
  public void disable(@Param("policyId") String policyId);
}
