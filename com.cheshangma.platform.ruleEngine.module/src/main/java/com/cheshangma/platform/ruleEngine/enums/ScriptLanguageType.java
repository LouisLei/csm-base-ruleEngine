package com.cheshangma.platform.ruleEngine.enums;

/**
 * 脚本语言类型
 * @author yinwenjie
 */
public enum ScriptLanguageType implements EnumTypeInterface {

  /**
   * 表示语言是Groovy
   * */
  LANGUAGE_GROOVY(1, "GROOVY"),
  /**
   * 表示语言是Python
   */
  LANGUAGE_PYTHON(2, "PYTHON"),;

  private int value;
  private String desc;

  ScriptLanguageType(int value, String desc) {
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

  public static ScriptLanguageType get(int value) {
    for (ScriptLanguageType stateType : ScriptLanguageType.values()) {
      if (stateType.value == value) {
        return stateType;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }
}
