package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 如果policy策略没有被找到，则抛出该异常
 * @author yinwenjie
 *
 */
public class PolicyNotFoundException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 3817258907354301113L;

  public PolicyNotFoundException(String message) {
    super(message);
  }
}
