package com.cheshangma.platform.ruleEngine.core.service;

import java.util.List;

import com.cheshangma.platform.ruleEngine.module.PolicyModel;

/**
 * 策略业务持久层服务定义
 * @author yinwenjie
 */
public interface PolicyService {
  /**
   * 注意，保存只是保存policy的基本信息，和一同新增的元数据信息<br>
   * 但是并不包括可能的rule步骤信息，后者这些步骤信息是分步进行绑定的
   * @param model 策略描述信息
   * @return 新增后的策略信息将进行输出
   */
  public PolicyModel save(PolicyModel model);
  
  /**
   * 注意。policy修改操作，包括了修改基本信息中的四个字段，还包括了修改绑定的元数据。<br>
   * 当时和其关联的rule信息，并不通过这里进行修改，而在专门的bind方法中进行修改
   * @param model
   * @return 修改后的策略信息将进行输出
   */
  public PolicyModel update(PolicyModel model);
  
  /**
   * 找到的单个policy信息除了包括这个policy的基本信息外，<br>
   * 还包括了policy包含的子级信息，例如：元数据、rule步骤信息<br>
   * 注意，policyIdid，是指的策略业务id。
   * @param policyId 策略业务id
   * @return 如果找到信息则进行返回；其它情况下返回null
   */
  public PolicyModel findByPolicyId(String policyId);
  
  /**
   * 取出所有的policy信息，不过只包括基本信息
   * @return 所有策略信息都将被返回
   */
  public List<PolicyModel> findAll();
  
  /**
   * 根据持久层编号信息，批量查询。
   * 查询结果只包括policy的基本信息
   * @param ids 编号信息
   * @return 满足条件的信息都将被返回，没有查询到任何信息就返回一个空集合
   */ 
  public List<PolicyModel> findAll(Iterable<String> policyIds);
  
  /**
   * 如果根据数据持久层的id查询到了，就表示存在
   * @param id
   * @return 如果存在则返回true，其它情况返回false
   */
  public boolean exists(String policyId);
  
  /**
   * 按照策略业务id，真删除一个策略信息、元数据信息和关联信息（例如可能和rule的关联信息）
   * @param policyId 策略业务id
   */
  public void deleteByPolicyId(String policyId);
  
  /**
   * 按照策略业务id，将一个策略置为“可用”
   * @param policyId
   * @return 如果操作成功则返回true，其它情况返回false
   */
  public boolean enable(String policyId);
  
  /**
   * 按照策略业务id，将一个策略置为“不可用”
   * @param policyId
   * @return 如果操作成功则返回true，其它情况返回false
   */
  public boolean disable(String policyId);
}