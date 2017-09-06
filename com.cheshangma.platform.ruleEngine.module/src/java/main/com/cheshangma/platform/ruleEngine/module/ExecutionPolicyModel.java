package com.cheshangma.platform.ruleEngine.module;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cheshangma.platform.ruleEngine.enums.ResultModeType;

/**
 * policy的执行过程描述，包括执行结果也在这里
 */
public class ExecutionPolicyModel extends UUIDModel {
  /**
   * 
   */
  private static final long serialVersionUID = 7023483676358145458L;
  /**
   * 关联的策略id
   */
  private String policyId;
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
   * 元数据信息。 如果在策略中存在元数据，这里会关联元数据和执行结果的值进行输出
   */
  private MetadataModel metadata;
  /**
   * 被拒绝后，或者执行失败后显示的信息
   */
  private String rejectMessage;
  /**
   * 虽然policy执行过程中有上下文将其下的若干个rule执行结果合并起来， 但是每一个rule的独立执行结果都会在这里进行记录，以便执行人员观察这些返回结果是如何变化的
   */
  private List<ExecutionRuleModel> ruleResults = new LinkedList<>();

  /**
   * @return the policyId
   */
  public String getPolicyId() {
    return policyId;
  }

  /**
   * @param policyId the policyId to set
   */
  public void setPolicyId(String policyId) {
    this.policyId = policyId;
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
   * @return the metadata
   */
  public MetadataModel getMetadata() {
    return metadata;
  }

  /**
   * @param metadata the metadata to set
   */
  public void setMetadata(MetadataModel metadata) {
    this.metadata = metadata;
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

  /**
   * @return the ruleResults
   */
  public List<ExecutionRuleModel> getRuleResults() {
    return ruleResults;
  }

  /**
   * @param ruleResults the ruleResults to set
   */
  public void setRuleResults(List<ExecutionRuleModel> ruleResults) {
    this.ruleResults = ruleResults;
  }

  public ResultModeType getResultMode() {
    return resultMode;
  }

  public void setResultMode(ResultModeType resultMode) {
    this.resultMode = resultMode;
  }

}
