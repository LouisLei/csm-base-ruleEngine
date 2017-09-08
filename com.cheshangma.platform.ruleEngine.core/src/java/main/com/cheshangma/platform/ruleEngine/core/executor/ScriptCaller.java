package com.cheshangma.platform.ruleEngine.core.executor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 这是专门用于进行脚本代码执行的运行器。
 * @author yinwenjie
 */
public class ScriptCaller implements Callable<Object> {
  
  /**
   * 需要执行的groovy脚本在这里
   */
  private String groovyExpression;
  
  /**
   * 绑定的执行参数
   */
  private Map<String, Object> variables;
  
  /**
   * 这里是一个本地缓存，基于google的一个本地缓存实现。
   */
  private static Cache<Object,Object> CACHE = null;
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(ScriptCaller.class);
  
  static {
    CACHE = CacheBuilder.newBuilder()
        // 设置并发级别为3，并发级别是指可以同时写缓存的线程数
        .concurrencyLevel(3)
        // 设置写缓存后30秒钟过期
        // .expireAfterWrite(30, TimeUnit.SECONDS)
        // 设置读缓存后30秒钟过期
        .expireAfterAccess(30, TimeUnit.SECONDS)
        // 设置缓存容器的初始容量为10
        .initialCapacity(10)
        // 设置缓存最大容量为20，超过20之后就会按照LRU最近虽少使用算法来移除缓存项
        .maximumSize(20)
        .build();
  }
  
  public ScriptCaller(String groovyExpression , Map<String, Object> variables) {
    this.groovyExpression = groovyExpression;
    this.variables = variables;
  }
  
  @Override
  public Object call() {
    /*
     * 执行过程为：
     * 1、首先确定当前执行的内容是否需要支持反调。如果需要支持反调则需要重新组合groovy脚本
     * 2、然后从缓存中寻找内容是否被编译保存，如果是则直接取出执行；如果不是就进行编译
     * 3、开始执行（执行最简的方式，以便进行测试）
     * */
    // TODO 1、还没有写
    
    // 2、=============试图从本地缓存中找到脚本
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");
    CompiledScript compiledScript = null;
    try {
      Object cacheObject = CACHE.get(this.groovyExpression.hashCode(), new CompiledScriptCreator(this.groovyExpression));
      if(cacheObject != null) {
        compiledScript = (CompiledScript)cacheObject;
      }
    } catch (ExecutionException e) {
      LOG.error(e.getMessage() , e);
      return null;
    }
    
    // 绑定外部变量
    Bindings binding = engine.createBindings();
    if(this.variables != null && !this.variables.isEmpty()) {
      Set<String> keys = this.variables.keySet();
      for (String key : keys) {
        binding.put(key, this.variables.get(key));
      }
    }
    
    // 3、=============开始执行
    Object result = null;
    try {
      result = compiledScript.eval(binding);
    } catch (ScriptException e) {
      LOG.error(e.getMessage() , e);
    }
    return result;
  }
  
  /**
   * 脚本编译对象创建者
   * @author yinwenjie
   */
  class CompiledScriptCreator implements Callable<CompiledScript> {

    private String groovyExpression;
    
    CompiledScriptCreator(String groovyExpression) {
      this.groovyExpression = groovyExpression;
    }
    
    @Override
    public CompiledScript call() throws Exception {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("groovy");
      // 编译
      Compilable compilable = (Compilable) engine;
      CompiledScript compiledScript = compilable.compile(this.groovyExpression);
      
      return compiledScript;
    }
  }
}