package com.cheshangma.platform.ruleEngine.httpapi.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 元数据信息——持久层
 * @author yinwenjie
 */
@Entity
@Table(name = "R_VARIABLEPROPERTY")
public class VariablePropertyEntity extends UUIDEntity {
  /**
   * 
   */
  private static final long serialVersionUID = 7405154480767821458L;
  /**
   * 执行步骤所属的policy的编号信息
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policyId", nullable = false)
  private PolicyEntity policyId;
  /**
   * 元数据属性名
   */
  @Column(name = "name", length = 64, nullable = false)
  private String name;
  /**
   * 元数据描述信息
   */
  @Column(name = "description", length = 1024, nullable = false)
  private String description = "";

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public PolicyEntity getPolicyId() {
    return policyId;
  }

  public void setPolicyId(PolicyEntity policyId) {
    this.policyId = policyId;
  }
}
