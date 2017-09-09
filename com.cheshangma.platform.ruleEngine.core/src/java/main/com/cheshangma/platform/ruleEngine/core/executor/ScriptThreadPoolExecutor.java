package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 专门用于规则脚本执行任务运行的线程池
 * @author yinwenjie
 */
public class ScriptThreadPoolExecutor extends ThreadPoolExecutor {
  
  public ScriptThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread, java.lang.Runnable)
   */
  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    // 主要是对Thread中的上下文进行初始化
    ScriptThread currentThread = (ScriptThread)t;
    currentThread.resetExecuteContext();
    
    super.beforeExecute(t, r);
  }
}