package com.cheshangma.platform.ruleEngine.enums;

/**
 * 策略执行模式
 * @author yinwenjie
 */
public enum ExecModeType implements EnumTypeInterface {
  /**
   * never stop, just work through the path
   * 即使出现错误，也不停止执行
   */
  PASSBY(1,"PASSBY"), 
  /**
   * stop if one step raise non-pass
   * 如果执行过程中，某个过程没有通过，则停止执行
   */
  SIMPLE(2,"SIMPLE"); 
  
  private int value;
  private String desc;

  ExecModeType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  @Override
  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static ExecModeType get(int value) {
    for (ExecModeType stateType : ExecModeType.values()) {
      if (stateType.value == value) {
        return stateType;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }
}
