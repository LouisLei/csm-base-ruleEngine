package com.cheshangma.platform.ruleEngine.httpapi.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.httpapi.repository.PolicyRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.PolicyStepRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.VariablePropertyRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyEntity;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyStepEntity;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.VariablePropertyEntity;
import com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService;
import com.cheshangma.platform.ruleEngine.httpapi.service.RuleService;

/**
 * 策略service实现.
 * 
 * @author ly
 * @date 2017年9月8日 上午10:35:00
 * @version V1.0
 */
@Service("policyService")
public class PolicyServiceImp implements PolicyService {

  @Autowired
  private PolicyRepository policyRepository;
  @Autowired
  private VariablePropertyRepository variablePropertyRepository;
  @Autowired
  private RuleService ruleService;
  @Autowired
  private PolicyStepRepository policyStepRepository;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService#addPolicy(com.cheshangma.platform
   * .ruleEngine.httpapi.repository.entity.PolicyEntity)
   */
  @Override
  @Transactional
  public PolicyEntity addPolicy(PolicyEntity policyEntity) {
    // 验证策略的基本信息
    validateInfos(policyEntity);
    // 1.存在元数据对象的情况
    if (policyEntity.getVariablePropertys() != null
        && policyEntity.getVariablePropertys().size() > 0) {
      List<VariablePropertyEntity> variablePropertyEntities = policyEntity.getVariablePropertys();
      List<VariablePropertyEntity> vaList = new ArrayList<VariablePropertyEntity>();
      int i = 1;
      for (VariablePropertyEntity v : variablePropertyEntities) {
        // 验证元数据的基本信息
        Validate.notBlank(v.getName(), "第" + i + "条元数据的属性名不能为空！");
        Validate.notBlank(v.getDescription(), "第" + i + "条元数据的描述信息不能为空！");
        v.setPolicyId(policyEntity);
        vaList.add(v);
        i++;
      }
      // 验证通过，存储元数据集合
      variablePropertyRepository.save(vaList);
    }
    // 2.存在规则对象的情况，即策略类型=复合策略类型
    if (policyEntity.getMode().equals(PolicyModeType.RULEMODE_CASE)) {
      Validate.notNull(policyEntity.getExecution(), "复合策略类型至少需要一个规则存在！");
      // 规则步骤
      List<PolicyStepEntity> policyStepEntities = policyEntity.getExecution();
      if (policyStepEntities != null && policyStepEntities.size() > 0) {
        List<PolicyStepEntity> pList = new ArrayList<PolicyStepEntity>();
        for (PolicyStepEntity p : policyStepEntities) {
          // 验证规则步骤的基本信息
          Validate.notBlank(p.getType(), "执行方式不能为空！");
          Validate.notNull(p.getIndex(), "执行步骤不能为空！");
          Validate.notNull(p.getRuleId(), "规则信息不能为空！");
          p.setPolicyId(policyEntity);
          pList.add(p);
        }
        policyStepRepository.save(pList);
      }
    }
    // 策略创建时间
    policyEntity.setCreated(new Date());
    // 3.保存策略信息
    return policyRepository.save(policyEntity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService#updatePolicy(com.cheshangma
   * .platform.ruleEngine.httpapi.repository.entity.PolicyEntity)
   */
  @Override
  @Transactional
  public PolicyEntity updatePolicy(PolicyEntity policyEntity) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService#delPolicy(java.lang.String)
   */
  @Override
  @Transactional
  public PolicyEntity delPolicy(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService#findAllPolicy()
   */
  @Override
  public List<PolicyEntity> findAllPolicy() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService#findById(java.lang.String)
   */
  @Override
  public PolicyEntity findById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * 该私有方法用于策略对象的验证（策略的基本信息的验证）.
   * 
   * @param policy 策略对象
   */
  private void validateInfos(PolicyEntity policy) {
    Validate.notNull(policy, "请填写策略信息！");
    Validate.notBlank(policy.getPolicyId(), "策略id不能为空！");
    Validate.notNull(policy.getExecMode(), "策略的执行模式不能为空！");
    Validate.notNull(policy.getMode(), "策略的形态不能为空！");
    Validate.notBlank(policy.getScoreExpression(), "需要执行的脚本代码内容不能为空！");
    Validate.notNull(policy.getScriptLanguage(), "脚本语言类型不能为空！");
    Validate.notBlank(policy.getDescription(), "策略的描述信息不能为空！");
  }

}
