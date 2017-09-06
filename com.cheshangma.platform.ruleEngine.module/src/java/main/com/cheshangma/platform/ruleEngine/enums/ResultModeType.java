package com.cheshangma.platform.ruleEngine.enums;

/**
 * 策略执行模式
 * @author yinwenjie
 */
public enum ResultModeType implements EnumTypeInterface {
  /**
   * Verify Success
   */
  PASS(1,"pass"),
  /**
   * Verify Fail
   */
  REJECT(2,"reject"),
  /**
   * Verify Undetermined
   * 执行结果是一种不确定的错误
   */
  UNDEFINE(3,"undefine"),
  /**
   * Have exception when Verify
   */
  EXCEPTION(4,"exception");
  
  private int value;
  private String desc;

  ResultModeType(int value, String desc) {
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

  public static ResultModeType get(int value) {
    for (ResultModeType stateType : ResultModeType.values()) {
      if (stateType.value == value) {
        return stateType;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }
}
