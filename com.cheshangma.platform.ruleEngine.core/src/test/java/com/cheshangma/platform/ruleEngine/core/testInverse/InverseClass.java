package com.cheshangma.platform.ruleEngine.core.testInverse;

import java.util.LinkedList;

import com.cheshangma.platform.ruleEngine.core.executor.ExecuteContext;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptInvokerReversable;

/**
 * 被反调的对象
 * @author yinwenjie
 */
public class InverseClass implements ScriptInvokerReversable {
  public void doSomething(ExecuteContext context) {
    // 向上下文中存入新的结果
    context.params.put("users", "yinwenjie orders!");
    context.params.put("orders", new LinkedList<>());
    System.out.println("doSomething(ExecuteContext context) 已执行！");
  }
}