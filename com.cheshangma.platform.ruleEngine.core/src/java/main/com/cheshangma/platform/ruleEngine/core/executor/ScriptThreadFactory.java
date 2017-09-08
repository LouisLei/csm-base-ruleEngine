package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.concurrent.ThreadFactory;

/**
 * 该线程工厂用于创建ScriptRunner运行器
 * @author yinwenjie
 */
public class ScriptThreadFactory implements ThreadFactory {
  
  @Override
  public Thread newThread(Runnable r) {
    // 该线程池只接受该类运行器
    if(r instanceof ScriptCaller) {
      throw new IllegalArgumentException("runner is not ScriptRunner implement!!");
    }
    
    return new Thread(r);
  }
}