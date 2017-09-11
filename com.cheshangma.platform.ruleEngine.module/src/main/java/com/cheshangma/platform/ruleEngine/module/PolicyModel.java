package com.cheshangma.platform.ruleEngine.module;

import java.util.List;

import com.cheshangma.platform.ruleEngine.enums.ExecModeType;
import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;

/**
 * 策略信息
 * 
 * @author yinwenjie
 */
public class PolicyModel extends UUIDModel {
  /**
   * 
   */
  private static final long serialVersionUID = 7685477170290600375L;
  /**
   * 对应的策略id（注意不是业务id号）
   */
  private String policyId;
  /**
   * 策略的运行模式<br>
   * 简单说，就是当遇到异常时，是否终止运行
   */
  private ExecModeType execMode = ExecModeType.SIMPLE;
  
  /**
   * 策略形态<br>
   * 是一个简单的策略呢，还是一个至少有一个rule的策略
   */
  private PolicyModeType mode = PolicyModeType.RULEMODE_SIMPLE;
  
  /**
   * 元数据信息
   */
  private MetadataModel metadata;
  /**
   * 规则步骤，一个策略中可以有多个规则。 通过这个PolicyStepModule对象，可以将这些规则排列起来
   */
  private List<PolicyStepModel> execution;
  /**
   * 表达式，既是需要被执行的脚本代码内容
   */
  private String scoreExpression;
  /**
   * 该policy策略是否还有效
   */
  private Boolean policyEnabled = true;
  /**
   * 脚本类型，目前支持两种类型。Groovy和Python
   */
  private ScriptLanguageType scriptLanguage;

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
   * @return the metadata
   */
  public MetadataModel getMetadata() {
    return metadata;
  }

  /**
   * @param metadata the metadata to set
   */
  public void setMetadata(MetadataModel metadata) {
    this.metadata = metadata;
  }

  /**
   * @return the execution
   */
  public List<PolicyStepModel> getExecution() {
    return execution;
  }

  /**
   * @param execution the execution to set
   */
  public void setExecution(List<PolicyStepModel> execution) {
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
