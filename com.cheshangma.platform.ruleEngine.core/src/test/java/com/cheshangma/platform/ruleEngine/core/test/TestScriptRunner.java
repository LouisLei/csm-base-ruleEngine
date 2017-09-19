package com.cheshangma.platform.ruleEngine.core.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.cheshangma.platform.ruleEngine.core.executor.ScriptCaller;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadFactory;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptThreadPoolExecutor;
import com.cheshangma.platform.ruleEngine.core.utils.JSONMapper;
import com.cheshangma.platform.ruleEngine.enums.ExecModeType;

/**
 * 测试groovy的执行过程
 * @author yinwenjie
 */
public class TestScriptRunner {
  @Test
  public void simple() throws ExecutionException,InterruptedException {
    // 初始化线程池
    ScriptThreadPoolExecutor poolExecutor = new ScriptThreadPoolExecutor(10, 20, 1000l, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(), new ScriptThreadFactory("threadName"));
    // 这里是代码
    String groovyExpression = "def a = 10;def b = 12;c += a + b;";
    
    // ==================== 输入变量，第一次执行
    Map<String, Object> variables = new HashMap<>();
    variables.put("c", 1);
    Future<List<ScriptCaller.Result>> future = poolExecutor.submit(new ScriptCaller(Arrays.asList(new String[]{groovyExpression}), variables, true , ExecModeType.PASSBY));
    Object result = future.get().get(0);
    System.out.println("result = " + JSONMapper.OBJECTMAPPER.convertObjectToJson(result));
    
    // ================== 再次执行，主要看缓存工作没有
    variables = new HashMap<>();
    variables.put("c", 3);
    future = poolExecutor.submit(new ScriptCaller(Arrays.asList(new String[]{groovyExpression}), variables, true , ExecModeType.PASSBY));
    result = future.get().get(0);
    System.out.println("result = " + JSONMapper.OBJECTMAPPER.convertObjectToJson(result));
  }
}