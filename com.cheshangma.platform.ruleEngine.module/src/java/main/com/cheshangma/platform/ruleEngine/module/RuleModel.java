package com.cheshangma.platform.ruleEngine.module;

import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;

/**
 * 规则信息，规则最典型的使用场景是：多个规则按照一定的顺序集中在一个policy中，被依次顺序执行
 * 
 * @author yinwenjie
 */
public class RuleModel extends UUIDModel {
  /**
   * 
   */
  private static final long serialVersionUID = -7970665949923251123L;

  /**
   * 规则信息id，注意，是规则id，不是id那个业务编号
   */
  private String ruleId;
  /**
   * 脚本语言内容
   */
  private String expression;
  /**
   * 脚本语言类型，目前支持Groovy、Python 默认为Groovy
   */
  private ScriptLanguageType scriptLanguage = ScriptLanguageType.LANGUAGE_GROOVY;

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
  
  /**
   * @return the expression
   */
  public String getExpression() {
    return expression;
  }

  /**
   * @param expression the expression to set
   */
  public void setExpression(String expression) {
    this.expression = expression;
  }

  public ScriptLanguageType getScriptLanguage() {
    return scriptLanguage;
  }

  public void setScriptLanguage(ScriptLanguageType scriptLanguage) {
    this.scriptLanguage = scriptLanguage;
  }

}
