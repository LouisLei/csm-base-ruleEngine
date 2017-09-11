package com.cheshangma.platform.ruleEngine.core.service;

import java.util.List;

import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * 规则数据持久层服务
 * @author yinwenjie
 */
public interface RuleService {
  /**
   * 该方法用于新增规则基本信息，并不包括对规则关联信息的操作
   * @param model 
   * @return 新增成功后的规则信息将被返回
   */
  public RuleModel save(RuleModel model);
  
  /**
   * 该方法用于修改规则基本信息，并不包括对规则关联信息的操作
   * @param model
   * @return 修改成功后的规则信息将被返回
   */
  public RuleModel update(RuleModel model);
  
  /**
   * 根据规则业务编号，查询指定的规则是否存在
   * @param ruleId 规则业务编号 
   * @return 如果存在则返回true，其它情况下返回false
   */
  public boolean exists(String ruleId);
  
  /**
   * 进行策略和规则的绑定操作，之前的绑定关系不会改变，新增的绑定将会添加到执行顺序的最后。注意：<br>
   * 1、同一个policy不能重复绑定相同的rule<br>
   * 2、如果没有指定index，则以当前时间为index顺序排位，这样能够保证在没有并发绑定的情况下，顺序是正确的。
   * @param policyId 绑定的策略业务编号
   * @param ruleId 绑定的规则业务编号
   * @return 如果绑定成功，则返回true；其它情况返回false
   */
  public boolean bindRule(String policyId, String ruleId);
  
  /**
   * 进行策略和规则的绑定操作——绑定到指定的操作步骤上。之前的绑定关系不会改变。
   * @param policyId
   * @param ruleId
   * @param index 绑定到的执行步骤上，0表示第一个执行，数字越大执行顺序越往后
   * @see #bindRule(String, String);
   * @return 如果绑定成功，则返回true；其它情况返回false
   */
  public boolean bindRule(String policyId, String ruleId, Long index);
  
  /**
   * 进行策略和规则的绑定操作——多个绑定操作，rules的index依次增加。之前的绑定关系被作废掉
   * @param policyId 绑定的策略业务编号
   * @param ruleIds 绑定的多个规则业务编号
   * @return 如果绑定成功，则返回true；其它情况返回false
   */
  public boolean bindRule(String policyId, List<String> ruleIds);
  
  /**
   * 解除指定的策略业务和规则业务的绑定关系
   * @param policyId 绑定的策略业务编号
   * @param ruleIds 绑定的多个规则业务编号
   * @return 如果操作成功，则返回true；其它情况返回false
   */
  public boolean unbind(String policyId, String ruleId);
  
  /**
   * 真删除指定的规则业务编号。包括这个rule的绑定关系（例如策略和规则的关联关系，但不是说连策略都删除了）
   * @param ruleId 规则的业务编号
   * @return 如果删除成功，则返回true；其它情况返回false
   */
  public boolean deleteByRuleId(String ruleId);
  
  /**
   * 按照业务规则编号，查询规则基本信息。
   * @param ruleId 规则的业务编号
   */
  public RuleModel findByRuleId(String ruleId);
  
  /**
   * 按照多个业务规则编号，查询规则基本信息。
   * @param ruleIds 多个规则业务编号
   * @return 如果没有查询到任何符合要求的信息，则返回一个空集合
   */
  public List<RuleModel> findAll(Iterable<String> ruleIds);
  
  /**
   * 查询所有规则的基本信息，无论这些规则的状态如何
   */
  public List<RuleModel> findAll();
  
  /**
   * 查询指定策略编号下已经绑定的规则信息，并按照执行顺序进行返回
   * @param policyId 指定的策略编号信息
   * @return 如果没有查询到任何符合要求的信息，则返回一个空集合
   */
  public List<RuleModel> findByPolicyId(String policyId);
}
