package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyStepEntity;

/**
 * 策略执行步骤相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月8日 下午3:11:53
 * @version V1.0
 */
public interface PolicyStepRepository extends CrudRepository<PolicyStepEntity, String> {
  /**
   * 按照规则数据编号和策略数据编号查询对应的绑定信息
   * @param policy_id 策略数据层编号
   * @param rule_Id 规则数据层编号
   * @return
   */
  @Query(value="from PolicyStepEntity ps left join fetch ps.policyId p "
      + " left join fetch ps.ruleId r where p.id = :policy_id and r.id = :rule_Id")
  public PolicyStepEntity findByPolicyAndRule(@Param("policy_id") String policy_id ,@Param("rule_Id") String rule_Id);
  
  /**
   * 按照数据编号删除指定的策略和规则绑定关系
   * @param policy_id
   * @param rule_id
   */
  @Modifying
  @Query(value="delete from R_POLICY_STEP where policyId=:policy_id and ruleId=:rule_id " , nativeQuery=true)
  public void deleteByPolicyAndRule(String policy_id , String rule_id);
}
