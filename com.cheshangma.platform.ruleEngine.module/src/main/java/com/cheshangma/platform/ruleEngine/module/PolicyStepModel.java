package com.cheshangma.platform.ruleEngine.module;

import java.io.Serializable;

import com.cheshangma.platform.ruleEngine.enums.ExecModeType;

/**
 * 执行步骤，专门用于描述policy中各个子级规则的执行顺序
 * 
 * @author yinwenjie
 */
public class PolicyStepModel implements Serializable, RuleEngineModel {
  /**
   * 
   */
  private static final long serialVersionUID = -4650538679605552830L;
  /**
   * 该policy步骤执行rule规则的方式
   */
  private ExecModeType type = ExecModeType.SIMPLE;
  /**
   * 该policy执行步骤所需要执行的rule规则编号
   */
  private String ruleId;

  /**
   * @return the ruleId
   */
  public String getRuleId() {
    return ruleId;
  }

  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public ExecModeType getType() {
    return type;
  }

  public void setType(ExecModeType type) {
    this.type = type;
  }
}
