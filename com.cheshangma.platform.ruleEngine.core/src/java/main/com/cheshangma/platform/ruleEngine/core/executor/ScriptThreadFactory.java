package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 该线程工厂用于创建ScriptRunner运行器
 * @author yinwenjie
 */
public class ScriptThreadFactory implements ThreadFactory {
  
  private String threadName;
  
  /**
   * 当前已生成的执行线程数量的计数器
   */
  private AtomicInteger threadCount = new AtomicInteger(1);
  
  public ScriptThreadFactory(String threadName) {
    this.threadName = threadName;
  }
  
  @Override
  public Thread newThread(Runnable r) {
    // 该线程池只接受该类运行器
    if(r instanceof ScriptCaller) {
      throw new IllegalArgumentException("runner is not ScriptRunner implement!!");
    }
    
    String threadName = this.threadName + "-" + this.threadCount.getAndIncrement();
    return new ScriptThread(r , threadName);
  }
}