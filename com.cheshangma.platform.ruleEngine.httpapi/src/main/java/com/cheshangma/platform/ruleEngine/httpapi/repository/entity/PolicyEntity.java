package com.cheshangma.platform.ruleEngine.httpapi.repository.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.cheshangma.platform.ruleEngine.enums.ExecModeType;
import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;

/**
 * 策略信息
 * @author yinwenjie
 */
@Entity 
@Table(name = "R_POLICY")
public class PolicyEntity extends UUIDEntity {
  private static final long serialVersionUID = -8286571212490063746L; 
  /**
   * 对应的策略id（注意是业务id号）
   */
  @Column(name = "policyId", length = 64, nullable = false, unique = true)
  private String policyId;
  /**
   * 元数据信息
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "policyId")
  private List<VariablePropertyEntity> variablePropertys;
  /**
   * 规则步骤，一个策略中可以有多个规则。 通过这个PolicyStepModule对象，可以将这些规则排列起来
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "policyId")
  private List<PolicyStepEntity> execution;
  /**
   * 表达式，既是需要被执行的脚本代码内容
   */
  @Column(name = "scoreExpression", length = 4096, nullable = false)
  private String scoreExpression = "";
  /**
   * 该policy策略是否还有效
   */
  @Column(name = "policyEnabled", nullable = false)
  private Boolean policyEnabled = true;
  
  /**
   * 策略的运行模式<br>
   * 简单说，就是当遇到异常时，是否终止运行
   */
  @Column(name = "execMode", nullable = false)
  private ExecModeType execMode = ExecModeType.SIMPLE;
  /**
   * 策略形态<br>
   * 是一个简单的策略呢，还是一个至少有一个rule的策略
   */
  @Column(name = "mode", nullable = false)
  private PolicyModeType mode = PolicyModeType.RULEMODE_SIMPLE;
  /**
   * 脚本类型，目前支持两种类型。Groovy和Python
   */
  @Column(name = "scriptLanguage")
  private ScriptLanguageType scriptLanguage = ScriptLanguageType.LANGUAGE_GROOVY;
  /**
   * 描述信息
   */
  @Column(name = "description", length = 1024, nullable = false)
  private String description = "";
  /**
   * 创建者
   */
  @Column(name = "creator", length = 64, nullable = false)
  private String creator = "";
  /**
   * 创建时间
   */
  @Column(name = "created", nullable = false)
  private Date created = new Date();

  /**
   * @return the policyId
   */
  public String getPolicyId() {
    return policyId;
  }

  /**
   * @param policyId the policyId to set
   */
  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  /**
   * @return the execution
   */
  public List<PolicyStepEntity> getExecution() {
    return execution;
  }

  /**
   * @param execution the execution to set
   */
  public void setExecution(List<PolicyStepEntity> execution) {
    this.execution = execution;
  }

  /**
   * @return the scoreExpression
   */
  public String getScoreExpression() {
    return scoreExpression;
  }

  /**
   * @param scoreExpression the scoreExpression to set
   */
  public void setScoreExpression(String scoreExpression) {
    this.scoreExpression = scoreExpression;
  }

  /**
   * @return the policyEnabled
   */
  public Boolean getPolicyEnabled() {
    return policyEnabled;
  }

  /**
   * @param policyEnabled the policyEnabled to set
   */
  public void setPolicyEnabled(Boolean policyEnabled) {
    this.policyEnabled = policyEnabled;
  }

  /**
   * @return the variableProperty
   */
  public List<VariablePropertyEntity> getVariablePropertys() {
    return variablePropertys;
  }

  /**
   * @param variableProperty the variableProperty to set
   */
  public void setVariablePropertys(List<VariablePropertyEntity> variablePropertys) {
    this.variablePropertys = variablePropertys;
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

  /**
   * @return the creator
   */
  public String getCreator() {
    return creator;
  }

  /**
   * @param creator the creator to set
   */
  public void setCreator(String creator) {
    this.creator = creator;
  }

  /**
   * @return the created
   */
  public Date getCreated() {
    return created;
  }

  /**
   * @param created the created to set
   */
  public void setCreated(Date created) {
    this.created = created;
  }

  public ExecModeType getExecMode() {
    return execMode;
  }

  public void setExecMode(ExecModeType execMode) {
    this.execMode = execMode;
  }

  public PolicyModeType getMode() {
    return mode;
  }

  public void setMode(PolicyModeType mode) {
    this.mode = mode;
  }

  public ScriptLanguageType getScriptLanguage() {
    return scriptLanguage;
  }

  public void setScriptLanguage(ScriptLanguageType scriptLanguage) {
    this.scriptLanguage = scriptLanguage;
  }
}
