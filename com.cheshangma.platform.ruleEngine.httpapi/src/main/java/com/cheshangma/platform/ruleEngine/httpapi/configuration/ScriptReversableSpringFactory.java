package com.cheshangma.platform.ruleEngine.httpapi.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import com.cheshangma.platform.ruleEngine.core.exception.ScriptInvokerNotFound;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptInvokerReversable;
import com.cheshangma.platform.ruleEngine.core.executor.ScriptReversableAbstractFactory;

/**
 * 这个ScriptReversableAbstractFactory的实现可以从spring Ioc容器中获得bean信息
 * @author yinwenjie
 */
@Configuration
public class ScriptReversableSpringFactory extends ScriptReversableAbstractFactory implements ApplicationContextAware {
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(ScriptReversableSpringFactory.class);
  
  private ApplicationContext applicationContext;
  
  private boolean isInit = false;
  
  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.executor.ScriptReversableAbstractFactory#buildReversableBean(java.lang.String)
   */
  @Override
  public ScriptInvokerReversable buildReversableBean(String component) {
    // 注意只有在spring context完成初始化后，才能获取bean
    while(!isInit) {
      synchronized (ScriptReversableSpringFactory.class) {
        try {
          ScriptReversableSpringFactory.class.wait();
        } catch (InterruptedException e) {
          LOG.error(e.getMessage() , e);
        }
      }
    }
    
    // component中的最有一个“.”位置是分割点，需要进行分割
    if(StringUtils.isEmpty(component)) { 
      throw new IllegalArgumentException("component can not be found!!");
    }
    int lastIndex = component.lastIndexOf(".");
    if(lastIndex == -1) {
      throw new IllegalArgumentException("component can not be found!!");
    }
    String beanName = component.substring(0, lastIndex);
    Object springBean = this.applicationContext.getBean(beanName);
    if(springBean != null && springBean instanceof ScriptInvokerReversable) {
      return (ScriptInvokerReversable)springBean;
    }
    
    // 如果在spring ioc容器中没有找到bean，在试图通过反射方式去找
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Class<?> currentClass = null;
    Object instanceObject = null;
    try {
      currentClass = classLoader.loadClass(beanName);
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

  /* (non-Javadoc)
   * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if(applicationContext.getParent() == null) {
      isInit = true;
      synchronized (ScriptReversableSpringFactory.class) {
        ScriptReversableSpringFactory.class.notify();
      }
    }
  }
}