package com.cheshangma.platform.ruleEngine.enums;

/**
 * 策略类型，目前只有这两个。
 * @author yinwenjie
 */
public enum PolicyModeType implements EnumTypeInterface {
  /**
   * 简单策略执行
   * */
  RULEMODE_SIMPLE(1, "simple"),
  /**
   * 复合策略执行（policy策略至少有一个rule存在）
   */
  RULEMODE_CASE(2, "case"),;
  
  private int value;
  private String desc;

  PolicyModeType(int value, String desc) {
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

  public static PolicyModeType get(int value) {
    for (PolicyModeType stateType : PolicyModeType.values()) {
      if (stateType.value == value) {
        return stateType;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }
}
