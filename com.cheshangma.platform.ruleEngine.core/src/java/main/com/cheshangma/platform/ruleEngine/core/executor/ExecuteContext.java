package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则引擎执行过程中的上下文，当需要执行多个规则时，就可以通过上下文对象，将它们的状态关联起来
 * @author yinwenjie
 *
 */
public class ExecuteContext {
  
  /**
   * 上下文中的K-V信息存放在这里<br>
   * 打开保护权限主要是为了脚本代码中好使用
   */
  public Map<String, Object> params = new HashMap<>();
  
  /**
   * 清空规则引擎执行上下文中的所有信息
   */
  public void clearAll() {
    this.params.clear();
  }
}
