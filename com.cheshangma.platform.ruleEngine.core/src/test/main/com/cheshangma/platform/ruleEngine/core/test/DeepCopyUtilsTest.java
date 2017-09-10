package com.cheshangma.platform.ruleEngine.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.cheshangma.platform.ruleEngine.core.utils.DeepCopyUtils;

public class DeepCopyUtilsTest {
  /**
   * 深度复制1的测试
   */
  @Test
  public void test1() {
    // 其中表示不需要进行深度复制
    Map<String, Object> source = new HashMap<>();
    source.put("a", "1");
    source.put("b", 1);
    source.put("c", 1.1);
    source.put("d", 0xaa);
    source.put("e", '4');
    source.put("f", Long.MAX_VALUE);
    
    Map<String, Object> target = DeepCopyUtils.deepCopy(source);
    assertEquals(source, target);
  }
  
  /**
   * 深度复制2的测试
   */
  @Test
  public void test2() {
    // 需要进行深度复制
    Map<String, Object> source = new HashMap<>();
    source.put("a", "1");
    source.put("b", 1);
    source.put("c", 1.1);
    source.put("d", 0xaa);
    source.put("e", '4');
    source.put("f", Long.MAX_VALUE);
    MyClass myClass = new MyClass();
    myClass.a = "yinwenjie";
    myClass.b = 1;
    myClass.c = 0.999f;
    source.put("g", myClass);
    
    Map<String, Object> target = DeepCopyUtils.deepCopy(source);
    assertNotSame(source, target);
  }
  
  public static class MyClass implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4163358777898800628L;
    private String a;
    private Integer b;
    private float c;
    public String getA() {
      return a;
    }
    public void setA(String a) {
      this.a = a;
    }
    public Integer getB() {
      return b;
    }
    public void setB(Integer b) {
      this.b = b;
    }
    public float getC() {
      return c;
    }
    public void setC(float c) {
      this.c = c;
    }
  }
}
