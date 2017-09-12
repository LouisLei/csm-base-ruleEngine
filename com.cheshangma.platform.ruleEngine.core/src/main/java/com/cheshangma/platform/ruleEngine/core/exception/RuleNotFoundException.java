package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 如果rule规则没有被找到，则抛出该异常
 * @author yinwenjie
 */
public class RuleNotFoundException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -7711804476313517181L;

  public RuleNotFoundException(String message) {
    super(message);
  }
}
