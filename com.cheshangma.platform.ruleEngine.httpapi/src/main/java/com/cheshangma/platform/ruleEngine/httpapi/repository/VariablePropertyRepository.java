package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.VariablePropertyEntity;

/**
 * 元数据相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月8日 下午3:08:42
 * @version V1.0
 */
public interface VariablePropertyRepository extends CrudRepository<VariablePropertyEntity, String> {
  /**
   * 根据策略的业务id，删除指定的policy下的所有元数据信息
   * @param policyId 
   */
  @Modifying
  @Query(value="delete from VariablePropertyEntity where policyId = :policyId" , nativeQuery=true)
  public void deleteByPolicyId(@Param("policyId") String policyId);
  
  /**
   * 是否存在属于该policyId策略的元数据.
   * @param policyId 策略id（非逻辑键id）
   * @return int
   */
  @Query(value = "select count(*) from R_VARIABLEPROPERTY where policy_id=:policyId", nativeQuery = true)
  public int getByPolicyId(@Param("policyId") String policyId);
}
