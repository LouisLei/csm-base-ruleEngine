package com.cheshangma.platform.ruleEngine.httpapi.service;

import java.util.List;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.RuleEntity;

/**
 * 规则service接口.
 * 
 * @author ly
 * @date 2017年9月8日 上午10:11:59
 * @version V1.0
 */
public interface RuleService {

  /**
   * 增加/修改规则.
   * 
   * @param ruleEntity 规则对象
   * @return RuleEntity
   */
  RuleEntity addUpdateRule(RuleEntity ruleEntity);

  /**
   * 删除规则.
   * 
   * @param id 逻辑键
   * @return RuleEntity
   */
  RuleEntity delRule(String id);

  /**
   * 获取所有规则.
   * 
   * @return List<RuleEntity>
   */
  List<RuleEntity> findAllRule();

  /**
   * 获取详情（单条）.
   * 
   * @param id 逻辑键
   * @return RuleEntity
   */
  RuleEntity findById(String id);

  /**
   * 根据规则id获取规则.
   * 
   * @param ruleId 规则id
   * @return RuleEntity
   */
  RuleEntity findByRuleId(String ruleId);
}
