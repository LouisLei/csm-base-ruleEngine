package com.cheshangma.platform.ruleEngine.core.executor;

import org.apache.commons.lang3.StringUtils;

import com.cheshangma.platform.ruleEngine.core.exception.ScriptInvokerNotFound;

/**
 * 这是一个ScriptReversableAbstractFactory抽象工厂的默认实现，用于进行对象的反射初始化。<br>
 * 在spring工程中不建议使用这个默认实现，建议自行实现从Spring IOC容器中加载对象。
 * 
 * @author yinwenjie
 */
public class ScriptReversableDefaultFactory extends ScriptReversableAbstractFactory {
  
  /*
   * (non-Javadoc)
   * @see com.dianrong.morpheus.core.executor.ScriptReversableAbstractFactory#buildReversableBean(java.lang.String)
   */
  @Override
  public ScriptInvokerReversable buildReversableBean(String component) {
    // component中的最有一个“.”位置是分割点，需要进行分割
    if(StringUtils.isEmpty(component)) { 
      throw new IllegalArgumentException("component can not be found!!");
    }
    int lastIndex = component.lastIndexOf(".");
    if(lastIndex == -1) {
      throw new IllegalArgumentException("component can not be found!!");
    }
    String className = component.substring(0, lastIndex);
    
    // 进行反射寻找和初始化
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Class<?> currentClass = null;
    Object instanceObject = null;
    try {
      currentClass = classLoader.loadClass(className);
      instanceObject = currentClass.newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      // TODO 这里最好能有异常打印
      throw new IllegalArgumentException(e);
    }
    
    if(instanceObject == null) {
      throw new ScriptInvokerNotFound();
    }
    if(!(instanceObject instanceof ScriptInvokerReversable)) {
      throw new IllegalArgumentException("component bean not instance of ScriptInvokerReversable interface!!");
    }
    
    return (ScriptInvokerReversable)instanceObject;
  }
}
