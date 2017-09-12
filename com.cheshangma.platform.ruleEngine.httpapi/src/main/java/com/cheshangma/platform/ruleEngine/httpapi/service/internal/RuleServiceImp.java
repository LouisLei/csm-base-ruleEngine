package com.cheshangma.platform.ruleEngine.httpapi.service.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cheshangma.platform.ruleEngine.core.exception.PolicyAndRuleAllreadyBindException;
import com.cheshangma.platform.ruleEngine.core.exception.PolicyNotFoundException;
import com.cheshangma.platform.ruleEngine.core.exception.RuleAllreadyExistException;
import com.cheshangma.platform.ruleEngine.core.exception.RuleNotFoundException;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.httpapi.repository.PolicyRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.PolicyStepRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.RuleRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyEntity;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyStepEntity;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.RuleEntity;
import com.cheshangma.platform.ruleEngine.httpapi.service.RuleService;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

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

  @Autowired
  private PolicyRepository policyRepository;
  
  @Autowired
  private PolicyStepRepository policyStepRepository;

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#save(com.cheshangma.platform.ruleEngine.module.RuleModel)
   */
  @Override
  @Transactional
  public RuleModel save(RuleModel model) {
    this.validatInfos(model);
    /*
     * 由于外部服务已经检查了传值信息，这里就不需要重复再检查了
     * */
    // 检查是否已存在重复的ruleId
    RuleEntity exsitRule = this.ruleRepository.findByRuleId(model.getRuleId());
    if(exsitRule == null) { 
        throw new RuleAllreadyExistException("Rule Allready Exist! ruleId = " + model.getRuleId());
    }
    
    // 开始新增
    RuleEntity ruleEntity = new RuleEntity();
    ruleEntity.setCreated(new Date());
    ruleEntity.setCreator(model.getCreator()); 
    ruleEntity.setDescription(model.getDescription());
    ruleEntity.setExpression(model.getExpression());
    ruleEntity.setRuleId(model.getRuleId());
    ruleEntity.setScriptLanguage(model.getScriptLanguage() != null?model.getScriptLanguage():ScriptLanguageType.LANGUAGE_GROOVY);
    this.ruleRepository.save(ruleEntity);
    
    model.setId(ruleEntity.getId());
    return model;
  }
  
  /**
   * 该私有方法用于规则对象的验证（规则的基本信息的验证）.
   * 
   * @param rule 规则对象
   */
  private void validatInfos(RuleModel rule) {
    Validate.notNull(rule, "请填写规则信息！");
    Validate.notBlank(rule.getRuleId(), "规则id不能为空！");
    Validate.notBlank(rule.getExpression(), "需要执行的脚本代码内容不能为空！");
    Validate.notNull(rule.getScriptLanguage(), "脚本语言类型不能为空！");
    Validate.notBlank(rule.getDescription(), "规则的描述信息不能为空！");
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#update(com.cheshangma.platform.ruleEngine.module.RuleModel)
   */
  @Override
  @Transactional
  public RuleModel update(RuleModel model) {
    /*
     * 只能更新Rule的expression、scriptLanguage、creator和description信息
     * */
    RuleEntity ruleEntity = new RuleEntity();
    ruleEntity.setCreator(model.getCreator());
    ruleEntity.setDescription(model.getDescription());
    ruleEntity.setExpression(model.getExpression());
    ruleEntity.setRuleId(model.getRuleId());
    ruleEntity.setScriptLanguage(model.getScriptLanguage() != null?model.getScriptLanguage():ScriptLanguageType.LANGUAGE_GROOVY);
    
    // 检测policy是不是不存在
    RuleEntity existRule = this.ruleRepository.findByRuleId(model.getRuleId());
    if(existRule == null) {
        throw new RuleNotFoundException("rule Not Found ! ruleId = " + model.getRuleId());
    }
    ruleEntity.setId(existRule.getId());
    model.setId(existRule.getId());
    
    // 更新
    this.ruleRepository.save(ruleEntity);
    return model;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#exists(java.lang.String)
   */
  @Override
  public boolean exists(String ruleId) {
    /*
     * 查询到ruleId就可以了
     * */
    RuleEntity exsitRule = this.ruleRepository.findByRuleId(ruleId); 
    return exsitRule != null;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#bindRule(java.lang.String, java.lang.String)
   */
  @Override
  @Transactional
  public boolean bindRule(String policyId, String ruleId) {
    /*
     * 进行绑定最关键的就是：
     * 1、同一个policy不能重复绑定相同的rule
     * 2、如果没有指定index，则以当前时间为index顺序排位，这样能够保证在没有并发绑定的情况下，顺序是正确的。
     * */
    long nowTime = new Date().getTime();
    return this.bindRule(policyId, ruleId, nowTime);
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#bindRule(java.lang.String, java.lang.String, java.lang.Long)
   */
  @Override
  @Transactional
  public boolean bindRule(String policyId, String ruleId, Long index) {
    // 找到数据持久层的policy数据的id和rule数据的id
    PolicyEntity policy = this.policyRepository.findOne(policyId);
    RuleEntity rule = this.ruleRepository.findOne(ruleId);
    if(policy == null) {
        throw new PolicyNotFoundException("policyId = " + policyId + "is not exist !!");
    }
    if(rule == null) {
        throw new RuleNotFoundException("ruleId = " + ruleId + "is not exist !!");
    }
    PolicyStepEntity policyStep =  this.policyStepRepository.findByPolicyAndRule(policy.getId(), rule.getId());
    if(policyStep != null) {
        throw new PolicyAndRuleAllreadyBindException("ruleId = " + policyId + " ; policyId = " + ruleId);
    }
    
    // 开始进行绑定
    policyStep = new  PolicyStepEntity();
    policyStep.setIndex(index);
    policyStep.setPolicyId(policy);
    policyStep.setRuleId(rule); 
    policyStep.setType("simple");
    this.policyStepRepository.save(policyStep);
    return true;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#bindRule(java.lang.String, java.util.List)
   */
  @Override
  @Transactional
  public boolean bindRule(String policyId, List<String> ruleIds) {
    /*
     * 多个绑定操作，rules的index依次增加
     * */
    long nowTime = new Date().getTime();
    for(int index = 0 ;index < ruleIds.size() ; index++) {
        this.bindRule(policyId, ruleIds.get(index), nowTime++);
    }
    
    return true;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#unbind(java.lang.String, java.lang.String)
   */
  @Override
  @Transactional
  public boolean unbind(String policyId, String ruleId) {
    // 找到数据持久层的policy数据的id和rule数据的id
    PolicyEntity policy = this.policyRepository.findByPolicyId(policyId);
    if(policy == null) {
        throw new PolicyNotFoundException("policyId = " + policyId + "is not exist !!");
    }
    RuleEntity rule = this.ruleRepository.findByRuleId(ruleId);
    if(rule == null) {
      throw new RuleNotFoundException("ruleId = " + ruleId + "is not exist !!");
    }
    
    this.policyStepRepository.deleteByPolicyAndRule(policy.getId(), rule.getId());
    return true;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#deleteByRuleId(java.lang.String)
   */
  @Override
  @Transactional
  public boolean deleteByRuleId(String ruleId) {
    RuleEntity rule = this.ruleRepository.findByRuleId(ruleId);
    if(rule == null) {
      throw new RuleNotFoundException("ruleId = " + ruleId + "is not exist !!");
    }
    
    this.ruleRepository.delete(rule.getId());
    return true;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#findByRuleId(java.lang.String)
   */
  @Override
  public RuleModel findByRuleId(String ruleId) {
    // 检测policy是不是不存在
    RuleEntity rule = this.ruleRepository.findByRuleId(ruleId);
    if(rule == null) {
      throw new RuleNotFoundException("rule Not Found ! ruleId = " + ruleId);
    }
    
    RuleEntity ruleEntity = this.ruleRepository.findOne(ruleId);
    return this.transferModel(ruleEntity);
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#findAll(java.lang.Iterable)
   */
  @Override
  public List<RuleModel> findAll(Iterable<String> ruleIds) {
    ArrayList<String> idList = new ArrayList<>();
    ruleIds.forEach(v -> {
        idList.add(v);
    });
    Iterable<RuleEntity> ruleEntitys = this.ruleRepository.findAll(idList);
    if(ruleEntitys == null) {
        return Collections.emptyList();
    }
    
    // 开始转换基本信息
    List<RuleModel> ruleModels = new ArrayList<>();
    ruleEntitys.forEach(re -> {
        RuleModel ruleModel = this.transferModel(re);
        ruleModels.add(ruleModel);
    });
    
    return ruleModels;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#findAll()
   */
  @Override
  public List<RuleModel> findAll() {
    Iterable<RuleEntity> ruleEntitys = this.ruleRepository.findAll();
    if(ruleEntitys == null) {
        return Collections.emptyList();
    }
    
    // 开始转换基本信息
    List<RuleModel> ruleModels = new ArrayList<>();
    ruleEntitys.forEach(re -> {
        RuleModel ruleModel = this.transferModel(re);
        ruleModels.add(ruleModel);
    });
    
    return ruleModels;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.platform.ruleEngine.core.service.RuleService#findByPolicyId(java.lang.String)
   */
  @Override
  public List<RuleModel> findByPolicyId(String policyId) {
    PolicyEntity policy = this.policyRepository.findByPolicyId(policyId);
    if(policy == null) {
        return Collections.emptyList();
    }
    List<RuleEntity> ruleEntitys = this.ruleRepository.findByPolicyId(policyId);
    if(ruleEntitys == null || ruleEntitys.isEmpty()) {
        return Collections.emptyList();
    } 
    
    // 开始转换基本信息
    List<RuleModel> ruleModels = new ArrayList<>();
    ruleEntitys.forEach(re -> {
        RuleModel ruleModel = this.transferModel(re);
        ruleModels.add(ruleModel);
    });
    
    return ruleModels;
  }
  
  /**
   * 该私有方法将RuleEntity的基本信息转换为RuleModel基本信息<br>
   * 注意，只包括基本信息
   * @param ruleEntity
   * @return
   */
  private RuleModel transferModel(RuleEntity ruleEntity) {
      RuleModel ruleModel = new RuleModel();
      ruleModel.setCreated(ruleEntity.getCreated());
      ruleModel.setCreator(ruleEntity.getCreator());
      ruleModel.setDescription(ruleEntity.getDescription());
      ruleModel.setExpression(ruleEntity.getExpression());
      ruleModel.setId(ruleEntity.getId());
      ruleModel.setRuleId(ruleEntity.getRuleId());
      // 语言信息
      if(ruleEntity.getScriptLanguage() == ScriptLanguageType.LANGUAGE_GROOVY) {
          ruleModel.setScriptLanguage(ScriptLanguageType.LANGUAGE_GROOVY);
      } else {
          ruleModel.setScriptLanguage(ScriptLanguageType.LANGUAGE_PYTHON);
      }
      
      return ruleModel;
  }
}
