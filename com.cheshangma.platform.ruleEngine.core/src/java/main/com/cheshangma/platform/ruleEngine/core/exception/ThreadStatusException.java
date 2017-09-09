package com.cheshangma.platform.ruleEngine.core.exception;

/**
 * 当开发人员在非脚本线程中进行诸如规则引擎上下文获取之类的操作时，就会抛出这样的运行期异常
 * @author yinwenjie
 *
 */
public class ThreadStatusException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 912170491507376904L;

  public ThreadStatusException() {
    super("进行规则引擎操作的线程，只能是ScriptThread线程");
  }
}