package com.cheshangma.platform.ruleEngine.httpapi.repository.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 执行步骤，专门用于描述policy中各个子级规则的执行顺序<br>
 * 持久层信息
 * 
 * @author yinwenjie
 */
public class PolicyStepEntity extends UUIDEntity {
  /**
   * 
   */
  private static final long serialVersionUID = -4650538679605552830L;
  /**
   * 执行步骤所属的policy的编号信息
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policyId", nullable = false)
  private PolicyEntity policyId;
  /**
   * 该policy步骤执行rule规则的方式
   */
  private String type;
  /**
   * 该policy执行步骤所需要执行的rule规则编号
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ruleId", nullable = false)
  private RuleEntity ruleId;
  /**
   * 执行步骤（该值越小执行步骤越靠前）
   */
  @Column(name = "index", nullable = false)
  private Long index;

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the index
   */
  public Long getIndex() {
    return index;
  }

  /**
   * @param index the index to set
   */
  public void setIndex(Long index) {
    this.index = index;
  }

  public PolicyEntity getPolicyId() {
    return policyId;
  }

  public void setPolicyId(PolicyEntity policyId) {
    this.policyId = policyId;
  }

  public RuleEntity getRuleId() {
    return ruleId;
  }

  public void setRuleId(RuleEntity ruleId) {
    this.ruleId = ruleId;
  }

}
