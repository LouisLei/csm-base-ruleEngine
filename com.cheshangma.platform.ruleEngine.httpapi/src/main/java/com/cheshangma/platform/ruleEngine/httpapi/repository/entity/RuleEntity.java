package com.cheshangma.platform.ruleEngine.httpapi.repository.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;

/**
 * 规则信息，规则最典型的使用场景是：多个规则按照一定的顺序集中在一个policy中，被依次顺序执行
 * @author yinwenjie
 */
@Entity
@Table(name="R_RULE")
public class RuleEntity extends UUIDEntity {
  
  private static final long serialVersionUID = -7775615800020373308L;
  /**
   * 规则信息id，注意，是规则id，不是id那个业务编号
   */
  @Column(name="ruleId" , length=225 , unique=true , nullable=false)
  private String ruleId;
  /**
   * 脚本语言内容
   */
  @Column(name="expression" , length=4096 , nullable=false)
  private String expression;
  /**
   * 脚本语言类型，目前支持Groovy、Python
   */
  @Column(name="scriptLanguage" , nullable=false)
  private ScriptLanguageType scriptLanguage = ScriptLanguageType.LANGUAGE_GROOVY;
  
  /**
   * 描述信息
   */
  @Column(name="description" , length=1024 , nullable=false)
  private String description = "";
  /**
   * 创建者
   */
  @Column(name="creator" , length=64 , nullable=false)
  private String creator = "";
  /**
   * 创建时间
   */
  @Column(name="created" , nullable=false)
  private Date created = new Date();
  
  /**
   * 可能对应的多个步骤
   */
  @OneToMany(fetch=FetchType.LAZY ,mappedBy ="ruleId")
  private List<PolicyStepEntity> steps;
  
  public String getRuleId() {
    return ruleId;
  }
  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }
  public String getExpression() {
    return expression;
  }
  public void setExpression(String expression) {
    this.expression = expression;
  }
  public ScriptLanguageType getScriptLanguage() {
    return scriptLanguage;
  }
  public void setScriptLanguage(ScriptLanguageType scriptLanguage) {
    this.scriptLanguage = scriptLanguage;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getCreator() {
    return creator;
  }
  public void setCreator(String creator) {
    this.creator = creator;
  }
  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }
  public List<PolicyStepEntity> getSteps() {
    return steps;
  }
  public void setSteps(List<PolicyStepEntity> steps) {
    this.steps = steps;
  }
}