package com.cheshangma.platform.ruleEngine.httpapi.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cheshangma.platform.ruleEngine.httpapi.repository.RuleRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.RuleEntity;
import com.cheshangma.platform.ruleEngine.httpapi.service.RuleService;

/**
 * 规则service实现.
 * 
 * @author ly
 * @date 2017年9月8日 上午11:29:39
 * @version V1.0
 */
@Service("ruleService")
public class RuleServiceImp implements RuleService {

  @Autowired
  private RuleRepository ruleRepository;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.httpapi.service.RuleService#addRule(com.cheshangma.platform
   * .ruleEngine.httpapi.repository.entity.RuleEntity)
   */
  @Override
  @Transactional
  public RuleEntity addUpdateRule(RuleEntity ruleEntity) {
    RuleEntity nowRule = null;
    // 规则的逻辑键
    String id = ruleEntity.getId();
    if (StringUtils.isBlank(id)) {
      /** 1.id为空，新增 **/
      // 验证规则的基本信息
      validatInfos(ruleEntity);
      // 创建时间
      ruleEntity.setCreated(new Date());
      nowRule = ruleEntity;
    } else {
      /** 2.id不为空 **/
      nowRule = ruleRepository.findOne(id);
      if (nowRule == null) {
        /** 2.1 不存在该id的规则对象，新增 **/
        // 验证规则的基本信息
        validatInfos(ruleEntity);
        // 创建时间
        ruleEntity.setCreated(new Date());
        nowRule = ruleEntity;
      } else {
        /** 2.2 存在该id的规则对象，修改 **/
        nowRule.setExpression(ruleEntity.getExpression());
        nowRule.setScriptLanguage(ruleEntity.getScriptLanguage());
        nowRule.setExecMode(ruleEntity.getExecMode());
        nowRule.setDescription(ruleEntity.getDescription());
        // 验证规则的基本信息
        validatInfos(nowRule);
      }
    }
    return ruleRepository.save(ruleEntity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.httpapi.service.RuleService#delRule(java.lang.String)
   */
  @Override
  @Transactional
  public RuleEntity delRule(String id) {
    Validate.notBlank(id, "参数错误！");
    RuleEntity rule = ruleRepository.findOne(id);
    Validate.notNull(rule, "规则对象不存在！");
    ruleRepository.delete(rule);
    return rule;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.httpapi.service.RuleService#findAllRule()
   */
  @Override
  public List<RuleEntity> findAllRule() {
    Iterable<RuleEntity> ruleIterable = ruleRepository.findAll();
    List<RuleEntity> rList = new ArrayList<RuleEntity>();
    if (ruleIterable != null) {
      for (RuleEntity r : ruleIterable) {
        rList.add(r);
      }
    }
    return rList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.httpapi.service.RuleService#findById(java.lang.String)
   */
  @Override
  public RuleEntity findById(String id) {
    Validate.notBlank(id, "参数错误！");
    return ruleRepository.findOne(id);
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.httpapi.service.RuleService#findByRuleId(java.lang.String)
   */
  @Override
  public RuleEntity findByRuleId(String ruleId) {
    Validate.notBlank(ruleId, "参数错误！");
    return ruleRepository.findByRuleId(ruleId);
  }

  /**
   * 该私有方法用于规则对象的验证（规则的基本信息的验证）.
   * 
   * @param rule 规则对象
   */
  private void validatInfos(RuleEntity rule) {
    Validate.notNull(rule, "请填写规则信息！");
    Validate.notBlank(rule.getRuleId(), "规则id不能为空！");
    Validate.notBlank(rule.getExpression(), "需要执行的脚本代码内容不能为空！");
    Validate.notNull(rule.getScriptLanguage(), "脚本语言类型不能为空！");
    Validate.notNull(rule.getExecMode(), "策略的执行模式不能为空！");
    Validate.notBlank(rule.getDescription(), "规则的描述信息不能为空！");
  }

}
