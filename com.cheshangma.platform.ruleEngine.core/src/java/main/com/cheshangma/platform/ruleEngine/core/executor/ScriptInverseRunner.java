package com.cheshangma.platform.ruleEngine.core.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 未写注释
 * @author yinwenjie
 */
public class ScriptInverseRunner {

  private static ScriptInverseRunner scriptInverseRunner;

  private static ScriptReversableAbstractFactory scriptReversableFactory;
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(ScriptInverseRunner.class);

  public static void initReversableFactory(ScriptReversableAbstractFactory scriptReversableFactory) {
     ScriptInverseRunner.scriptReversableFactory = scriptReversableFactory;
  }

  public static ScriptInverseRunner getNewInstance() {
    if (ScriptInverseRunner.scriptInverseRunner != null) {
      return ScriptInverseRunner.scriptInverseRunner;
    }

    synchronized (ScriptInverseRunner.class) {
      while (ScriptInverseRunner.scriptInverseRunner == null) {
        ScriptInverseRunner.scriptInverseRunner = new ScriptInverseRunner();
        return ScriptInverseRunner.scriptInverseRunner;
      }
    }
    return ScriptInverseRunner.scriptInverseRunner;
  }
  
  /**
   * 
   */
  private ScriptInverseRunner() {
    // TODO 代码还未做
  }

  /**
   * 开始进行“反调”操作<br>
   * componentAndMethod 传入的参数是java完整类型/spring bean名称 + 方法名<br>
   * 举例如下：<br>
   * 
   * springcomponent.method1<br>
   * package1.name.ClassName.method2<br>
   * a.b.c.ClassName.method2
   * 
   * @return 如果反调过程有返回值，则会通过这里返回到动态脚本执行过程中。如果回调没有返回值，则返回null
   */
  public Object inverse(String componentAndMethod) {
    if(StringUtils.isEmpty(componentAndMethod)) {
      return null;
    }
    
    /*
     * 操作过程为：
     * 1、根据componentAndMethod确定的组件，取得对象
     * 2、在进行“反射”调用执行的方法
     *  2.1、如果这个方法没有任何入参，则直接调用
     *  2.2、如果这个方法有上下文信息，则还要输入上下文对象
     *  2.3、其它情况不进行反射调用
     * 3、如果反调过程有返回值，则会通过这里返回到动态脚本执行过程中；其它情况返回null
     * */
    //1、================
    ScriptInvokerReversable invokerReversable = scriptReversableFactory.buildReversableBean(componentAndMethod);
    if(invokerReversable == null) {
      return null;
    }
    
    //2、=================
    // 开始反调方法
    int lastIndex = componentAndMethod.lastIndexOf(".");
    if(lastIndex == -1) {
      throw new IllegalArgumentException("component can not be found!!");
    }
    String methodName = componentAndMethod.substring(lastIndex + 1);
    Method invokerMethod = this.findMethod(invokerReversable.getClass(), methodName);
    if(invokerMethod == null) {
      return null;
    }
    
    //3、===============调用
    Object result = null;
    try {
      if(invokerMethod.getParameterCount() == 1) {
        result = invokerMethod.invoke(invokerReversable, new Object[]{});
      } else {
        result = invokerMethod.invoke(invokerReversable, new Object[]{});
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      LOG.error(e.getMessage() , e);
      return null;
    }
    return result;
  }
  
  /**
   * 该私有方法用于找到要被反调的方法，如果没找到就在其父类实现找（不是父级接口找哦）。<br>
   * 直到找到方法或者没有更高级别的父类了
   * @param targetClass
   * @param methodName
   * @return
   */
  private Method findMethod(Class<?> targetClass , String methodName) {
    Method[] methods = targetClass.getDeclaredMethods();
    
    // 开始在本级class中找
    for (int index = 0 ; methods != null && index < methods.length ; index++) {
      Method method = methods[index];
      // 如果条件成立，说明找到了名字吻合的方法名（但是不一定能被调用）
      if(StringUtils.equals(method.getName(), methodName)) {
        int parameterCount = method.getParameterCount();
        if(parameterCount > 1) {
          continue;
        }
        
        // 如果条件成立，说明真的找到了
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes == null || parameterTypes.length == 0
            || parameterTypes[0] == ExecuteContext.class) {
          return method;
        } 
      }
    }
    
    // 如果代码运行到这里，说明在这个class中没有找到需要的method方法。
    // 那么到它的父级类中找
    Class<?> superCLass = targetClass.getSuperclass();
    if(superCLass != null) {
      return this.findMethod(superCLass, methodName);
    }
    return null;
  }
}
