package com.cheshangma.platform.ruleEngine.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.exception.InputSerializableException;

/**
 * 用来做对象深度复制的工具
 * @author yinwenjie
 */
public class DeepCopyUtils {
  
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(DeepCopyUtils.class);
  
  /**
   * 该私有方法对Map中的值进行深度复制
   * @param inputs
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> deepCopy(Map<String, Object> inputs) {
    /*
     * 1、首先对map中的信息进行遍历
     *  1.1、如果map没有任何信息，则返回一个空的map
     *  1.2、如果map中的值全部为基本类型，也不再需要深度复制
     * 2、直接利用java原生方式进行对象深度复制
     *  TODO 注意，实际上性能不太好，后续再优化吧
     * */
    // 1、===============
    if(inputs == null || inputs.isEmpty()) {
      return Collections.emptyMap();
    }
    boolean mustDeepcopy = false;
    Collection<?> values = inputs.values();
    for (Object object : values) {
      // 如果成立，说明需要进行map的深度复制
      if(!(object instanceof Integer || object instanceof String || object instanceof Float
          || object instanceof Double || object instanceof Byte || object instanceof Short
          || object instanceof Long || object instanceof Character || object instanceof Boolean)) {
        mustDeepcopy = true;
      }
    }
    // 如果条件成立，说明不需要进行深度复制
    if(!mustDeepcopy) {
      return inputs;
    }
    
    // 2、===============
    ByteArrayOutputStream byout = new ByteArrayOutputStream();
    ObjectOutputStream oo = null;
    ObjectInputStream oi = null;
    Map<String, Object> copyObject = null;
    try {
      oo = new ObjectOutputStream(byout);
      oo.writeObject(inputs);
      byte[] objectBytes = byout.toByteArray();
      // 输出
      oi = new ObjectInputStream(new ByteArrayInputStream(objectBytes));
      copyObject = (Map<String, Object>)oi.readObject();
      oo.close();
      oi.close();
    } catch(NotSerializableException e) {
      LOG.error(e.getMessage() , e);
      throw new InputSerializableException();
    } catch (IOException | ClassNotFoundException e) {
      LOG.error(e.getMessage() , e);
      return Collections.emptyMap();
    }
    
    return copyObject;
  }
}
