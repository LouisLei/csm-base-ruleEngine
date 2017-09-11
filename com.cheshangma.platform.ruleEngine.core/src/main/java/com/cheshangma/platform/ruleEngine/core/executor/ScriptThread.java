package com.cheshangma.platform.ruleEngine.core.executor;

/**
 * 脚本运行线程，其中最关键的是里面有运行规则任务所需要的任务上下文信息
 * @author yinwenjie
 */
public class ScriptThread extends Thread {
  private static ThreadLocal<ExecuteContext> CONTEXT_LOCAL = new ThreadLocal<>();
  
  public ScriptThread(Runnable target, String threadName) {
    super(target, threadName);
  }

  /**
   * 获取当前执行线程中所存在的规则执行上下文
   * @return 肯定是有的，因为执行规则所使用的线程池在第一时间就会为执行任务创建这个上下文对象
   */
  public ExecuteContext getExecuteContext() {
    return ScriptThread.CONTEXT_LOCAL.get();
  }
  
  /**
   * 当执行任务开始时。使用该方法充值任务上下文
   * @param context
   */
  void resetExecuteContext() {
    ExecuteContext context = CONTEXT_LOCAL.get();
    if(context == null) {
      CONTEXT_LOCAL.set(new ExecuteContext());
    } else {
      context.clearAll();
    }
  }
}   
