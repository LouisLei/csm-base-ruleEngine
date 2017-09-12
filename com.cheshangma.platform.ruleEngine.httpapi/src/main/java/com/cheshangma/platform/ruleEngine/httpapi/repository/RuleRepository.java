package com.cheshangma.platform.ruleEngine.httpapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.RuleEntity;

/**
 * 与规则相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月7日 下午6:16:11
 * @version V1.0
 */
public interface RuleRepository extends CrudRepository<RuleEntity, String> {

  /**
   * 根据规则id查询（非逻辑键）.
   * 
   * @param ruleId 规则id
   * @return RuleEntity
   */
  public RuleEntity findByRuleId(String ruleId);
  
  /**
   * 根据策略业务编号，查询已绑定的rule信息
   * @param policyId
   * @return
   */
  @Query(value="from RuleEntity r ")
  public List<RuleEntity> findByPolicyId(String policyId);
}
