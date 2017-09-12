package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 如果rule规则已经存在，则抛出这个异常
 * @author yinwenjie
 */
public class RuleAllreadyExistException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 4725994572519816524L;

  public RuleAllreadyExistException(String message) {
    super(message);
  }
}
