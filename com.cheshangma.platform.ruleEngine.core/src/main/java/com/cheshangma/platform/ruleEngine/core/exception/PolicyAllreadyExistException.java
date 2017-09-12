package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 如果某一个policy已经存在，则抛出该异常
 * @author yinwenjie
 */
public class PolicyAllreadyExistException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -973268159987254052L;

  public PolicyAllreadyExistException(String message) {
    super(message);
  }
}
