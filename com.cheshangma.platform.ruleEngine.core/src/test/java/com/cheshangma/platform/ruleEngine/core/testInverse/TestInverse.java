package com.cheshangma.platform.ruleEngine.core.testInverse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.core.testservice.ServiceDefaultFactory;
import com.cheshangma.platform.ruleEngine.core.utils.JSONMapper;
import com.cheshangma.platform.ruleEngine.enums.ExecModeType;
import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;

/**
 * 对反调功能进行测试
 * @author yinwenjie1999
 */
public class TestInverse {
  
  private RuleEngineFramework ruleEngineFramework;
  
  /**
   * 初始化操作
   */
  @Before
  public void before() {
    RuleEngineFramework.Builder build = RuleEngineFramework.Builder.getInstanceBuilder();
    this.ruleEngineFramework = build.setAllowInverse(true)
    .setMaxExecutionThread(20)
    .setMinExecutionThread(5)
    .setScriptQueueSize(50)
    .setScriptThreadName("test-script-thread")
    // 初始化一个服务工厂
    .setServiceAbstractFactory(new ServiceDefaultFactory())
    .setWaitingTimeout(500l)
    .buildIfAbent();
    
    System.out.println("ruleEngineFramework inited!!");
  }
  
  /**
   * 测试最简单的反调
   */
  @Test
  public void testSimple() throws ExecutionException,InterruptedException {
    // 这里是代码
    String groovyExpression = "def a = 10;def b = 12;c += a + b;";
    groovyExpression += "context.params += ['yinwenjie':10];";
    // 开始反调
    groovyExpression += "javaProcess('com.cheshangma.platform.ruleEngine.core.testInverse.InverseClass.doSomething');";
    // 将上下文中的user信息，作为全局变量返回
    groovyExpression += "yinwenjieValue = context.params.users;";
    
    // ==================== 输入变量，第一次执行
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("c", 1);
    PolicyModel policy = new PolicyModel();
    policy.setCreator("policy yinwenjie");
    policy.setDescription("description");
    policy.setExecMode(ExecModeType.PASSBY);
    // 故意的，看看是不是报错(目前要报错)
//    policy.setMode(PolicyModeType.RULEMODE_CASE);
    policy.setMode(PolicyModeType.RULEMODE_SIMPLE);
    policy.setScoreExpression(groovyExpression);
    policy.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
    
    ExecutionPolicyModel result = this.ruleEngineFramework.executePolicy(policy , null, inputs);
    System.out.println("result = " + JSONMapper.OBJECTMAPPER.convertObjectToJson(result));
  }
}