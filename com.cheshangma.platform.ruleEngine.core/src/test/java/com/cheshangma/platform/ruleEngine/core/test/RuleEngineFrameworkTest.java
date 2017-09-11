package com.cheshangma.platform.ruleEngine.core.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.core.testservice.ServiceDefaultFactory;
import com.cheshangma.platform.ruleEngine.core.utils.JSONMapper;
import com.cheshangma.platform.ruleEngine.enums.ExecModeType;
import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel.VariableProperty;

/**
 * 测试框架初始化的测试过程
 * @author yinwenjie
 */
public class RuleEngineFrameworkTest {
  
  private RuleEngineFramework ruleEngineFramework;
  
  /**
   * 初始化操作
   */
  @Before
  public void before() {
//    /*
//     * 这里测试两个线程同时创建时的状态
//     * */
//    Thread thread1 = new Thread(() -> {
//      RuleEngineFramework.Builder build = RuleEngineFramework.Builder.getInstanceBuilder();
//      this.ruleEngineFramework = build.setAllowInverse(false)
//      .setMaxExecutionThread(20)
//      .setMinExecutionThread(5)
//      .setScriptQueueSize(50)
//      .setScriptThreadName("test-script-thread")
//      // 初始化一个服务工厂
//      .setServiceAbstractFactory(new ServiceDefaultFactory())
//      .setWaitingTimeout(500l)
//      .buildIfAbent();
//    });
//    thread1.start();
//    
//    Thread thread2 = new Thread(() -> {
//      RuleEngineFramework.Builder build = RuleEngineFramework.Builder.getInstanceBuilder();
//      this.ruleEngineFramework = build.setAllowInverse(false)
//      .setMaxExecutionThread(20)
//      .setMinExecutionThread(5)
//      .setScriptQueueSize(50)
//      .setScriptThreadName("test-script-thread")
//      // 初始化一个服务工厂
//      .setServiceAbstractFactory(new ServiceDefaultFactory())
//      .setWaitingTimeout(500l)
//      .buildIfAbent();
//    });
//    thread2.start();
    
    RuleEngineFramework.Builder build = RuleEngineFramework.Builder.getInstanceBuilder();
    this.ruleEngineFramework = build.setAllowInverse(false)
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
   * 通过ruleEngineFramework运行简单的rule
   */
  @Test
  public void executeRule() {
    RuleModel rule = new RuleModel();
    rule.setCreator("yinwenjie");
    rule.setDescription("描述信息");
    rule.setExpression("c = a * b; a++;b++;");
    rule.setRuleId(UUID.randomUUID().toString());
    rule.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
    
    // input参数
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("a", 2);
    inputs.put("b", 3);
    ExecutionRuleModel results = this.ruleEngineFramework.executeRule(rule, inputs);
    
    // 检视返回信息
    String json = JSONMapper.OBJECTMAPPER.convertObjectToJson(results);
    System.out.println("json = " + json);
  }
  
  /**
   * 通过ruleEngineFramework运行简单的rule<br>
   * 包括传入的对象
   */
  @Test
  public void executeRuleObject() {
    RuleModel rule = new RuleModel();
    rule.setCreator("yinwenjie");
    rule.setDescription("描述信息");
    rule.setExpression("c = a * b; a++;b++;d++; user.name = '新的名字';user.id=999999;");
    rule.setRuleId(UUID.randomUUID().toString());
    rule.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
    
    // input参数
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("a", 2);
    inputs.put("b", 3);
    User user = new User();
    user.setId(8888l);
    user.setName("yinwenjie");
    user.setSex(0);
    inputs.put("user", user);
    ExecutionRuleModel results = this.ruleEngineFramework.executeRule(rule, inputs);
    
    // 检视返回信息
    String json = JSONMapper.OBJECTMAPPER.convertObjectToJson(results);
    System.out.println("json = " + json);
  }
  
  /**
   * 测试一个简单的policy<br>
   * 不带有元数据，不带有对象入参
   */
  @Test
  public void executePolicy() {
    PolicyModel policy = new PolicyModel();
    policy.setCreator("policy yinwenjie");
    policy.setDescription("description");
    policy.setExecMode(ExecModeType.PASSBY);
    // 故意的，看看是不是报错(目前要报错)
//    policy.setMode(PolicyModeType.RULEMODE_CASE);
    policy.setMode(PolicyModeType.RULEMODE_SIMPLE);
    policy.setScoreExpression("c = a * b; a++;b++;");
    policy.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
    
    // input参数
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("a", 2);
    inputs.put("b", 3);
    
    // 开始执行
    ExecutionPolicyModel results = this.ruleEngineFramework.executePolicy(policy, null, inputs);
    
    // 检视返回信息
    String json = JSONMapper.OBJECTMAPPER.convertObjectToJson(results);
    System.out.println("json = " + json);
  }
  
  /**
   * 通过ruleEngineFramework运行简单的policy<br>
   * 包括传入的对象，元数据
   */
  @Test
  public void executePolicyObject() {
    PolicyModel policy = new PolicyModel();
    policy.setCreator("yinwenjie");
    policy.setDescription("描述信息");
    policy.setScoreExpression("c = a * b; a++;b++; user.name = '新的名字';user.id=999999;");
    policy.setId(UUID.randomUUID().toString());
    policy.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
    
    // input参数
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("a", 2);
    inputs.put("b", 3);
    User user = new User();
    user.setId(8888l);
    user.setName("yinwenjie");
    user.setSex(0);
    inputs.put("user", user);
    
    // 元数据
    MetadataModel metadata = new MetadataModel();
    Set<VariableProperty> params = new HashSet<VariableProperty>();
    metadata.setParams(params);
    // 元数据1
    VariableProperty var1 = new VariableProperty();
    var1.setDescription("元数据1");
    var1.setName("b");
    params.add(var1);
    // 元数据2
    VariableProperty var2 = new VariableProperty();
    var2.setDescription("元数据2");
    var2.setName("c");
    params.add(var2);
    // 元数据3（测试没有对应的情况）
    VariableProperty var3 = new VariableProperty();
    var3.setDescription("元数据3");
    var3.setName("d");
    params.add(var3);
    policy.setMetadata(metadata);
    // 元数据4，对象元数据
    VariableProperty var4 = new VariableProperty();
    var3.setDescription("元数据4");
    var3.setName("user");
    params.add(var4);
    policy.setMetadata(metadata);
    
    // 开始执行
    ExecutionPolicyModel results = this.ruleEngineFramework.executePolicy(policy, null, inputs);
    
    // 检视返回信息
    String json = JSONMapper.OBJECTMAPPER.convertObjectToJson(results);
    System.out.println("json = " + json);
  }
  
  public static class User implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4186741904549380691L;
    private String name;
    private Integer sex;
    private long id;
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public Integer getSex() {
      return sex;
    }
    public void setSex(Integer sex) {
      this.sex = sex;
    }
    public long getId() {
      return id;
    }
    public void setId(long id) {
      this.id = id;
    }
  }
}
