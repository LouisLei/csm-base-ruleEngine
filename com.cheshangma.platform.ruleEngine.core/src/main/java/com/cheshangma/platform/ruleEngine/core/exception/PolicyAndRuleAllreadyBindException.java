package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 如果策略和规则已经有绑定关系，则抛出该异常
 * @author yinwenjie
 *
 */
public class PolicyAndRuleAllreadyBindException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -2211848141212485737L;

  public PolicyAndRuleAllreadyBindException(String message) {
    super(message);
  }
}
