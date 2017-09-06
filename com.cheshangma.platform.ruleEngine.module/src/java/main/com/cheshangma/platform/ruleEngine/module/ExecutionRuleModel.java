package com.cheshangma.platform.ruleEngine.module;

import java.util.Map;

import com.cheshangma.platform.ruleEngine.enums.ResultModeType;

/**
 * rule的执行过程描述，包括执行结果也在这里。<br>
 * 一个rule的执行过程可以独立存在，也可以和其它多个rule一起，存在于某个policy下
 */
public class ExecutionRuleModel extends UUIDModel {
  /**
   * 
   */
  private static final long serialVersionUID = 7023483676358145458L;
  /**
   * 关联的策略id
   */
  private String ruleId;
  /**
   * 携带的参数信息。参数信息就是多个K-V数据信息
   */
  private Map<String, Object> inputs;

  // ==============================================分解线以下的属性和执行结果有关
  /**
   * 规则执行结果，默认的结果是，执行没有通过
   */
  private ResultModeType resultMode = ResultModeType.REJECT;
  /**
   * 最重要的执行结果，Groovy、Python或者JavaScript的执行结果会在这里得到体现。入参的变化也会在这里得到体现。<br>
   * 这么说吧，动态脚本中的全局变量和它们在脚本执行完成后（或者异常退出后）的赋值都会在这里得到体现
   */
  private Map<String, Object> score;
  /**
   * 被拒绝后，或者执行失败后显示的信息
   */
  private String rejectMessage;

  /**
   * @return the ruleId
   */
  public String getRuleId() {
    return ruleId;
  }

  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  /**
   * @return the inputs
   */
  public Map<String, Object> getInputs() {
    return inputs;
  }

  /**
   * @param inputs the inputs to set
   */
  public void setInputs(Map<String, Object> inputs) {
    this.inputs = inputs;
  }

  /**
   * @return the score
   */
  public Map<String, Object> getScore() {
    return score;
  }

  /**
   * @param score the score to set
   */
  public void setScore(Map<String, Object> score) {
    this.score = score;
  }

  /**
   * @return the rejectMessage
   */
  public String getRejectMessage() {
    return rejectMessage;
  }

  /**
   * @param rejectMessage the rejectMessage to set
   */
  public void setRejectMessage(String rejectMessage) {
    this.rejectMessage = rejectMessage;
  }

  public ResultModeType getResultMode() {
    return resultMode;
  }

  public void setResultMode(ResultModeType resultMode) {
    this.resultMode = resultMode;
  }

}
