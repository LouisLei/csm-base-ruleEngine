package com.cheshangma.platform.ruleEngine.httpapi.service.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cheshangma.platform.ruleEngine.core.exception.PolicyAllreadyExistException;
import com.cheshangma.platform.ruleEngine.core.exception.PolicyNotFoundException;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.httpapi.repository.PolicyRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.VariablePropertyRepository;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyEntity;
import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.VariablePropertyEntity;
import com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService;
import com.cheshangma.platform.ruleEngine.module.MetadataModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel.VariableProperty;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;

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

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.core.service.PolicyService#save(com.cheshangma.platform.
   * ruleEngine.module.PolicyModel)
   */
  @Override
  @Transactional
  public PolicyModel save(PolicyModel model) {
    this.validateInfos(model);
    /*
     * 注意，保存只是保存policy的基本信息，和一同新增的元数据信息 但是并不包括可能的rule步骤信息，后者这些步骤信息是分步进行绑定的 之前都已经做过参数正确性判断了，这里不再进行赘述
     */
    PolicyEntity policyEntity = new PolicyEntity();
    policyEntity.setExecMode(model.getExecMode());
    policyEntity.setMode(model.getMode());
    policyEntity.setPolicyEnabled(model.getPolicyEnabled());
    policyEntity.setPolicyId(model.getPolicyId());
    policyEntity.setScoreExpression(!StringUtils.isEmpty(model.getScoreExpression()) ? model
        .getScoreExpression() : "");
    policyEntity.setScriptLanguage(model.getScriptLanguage() != null
        ? model.getScriptLanguage()
        : ScriptLanguageType.LANGUAGE_GROOVY);
    policyEntity.setCreated(new Date());
    policyEntity.setCreator(model.getCreator());
    policyEntity.setDescription(model.getDescription());

    // 检测policy是不是已经存在
    PolicyEntity policy = this.policyRepository.findByPolicyId(model.getPolicyId());
    if (policy != null) {
      throw new PolicyAllreadyExistException("policy allready exist! policyId = "
          + model.getPolicyId());
    }
    // 插入基本信息
    this.policyRepository.save(policyEntity);

    // 可能的元数据信息
    MetadataModel metadata = model.getMetadata();
    if (metadata != null) {
      this.upsertMetadata(metadata, policyEntity.getId());
    }

    // 返回
    model.setId(policyEntity.getId());
    return model;
  }

  /**
   * 该私有方法用于策略对象的验证（策略的基本信息的验证）.
   * 
   * @param policy 策略对象
   */
  private void validateInfos(PolicyModel policy) {
    Validate.notNull(policy, "请填写策略信息！");
    Validate.notBlank(policy.getPolicyId(), "策略id不能为空！");
    Validate.notNull(policy.getExecMode(), "策略的执行模式不能为空！");
    Validate.notNull(policy.getMode(), "策略的形态不能为空！");
    Validate.notBlank(policy.getScoreExpression(), "需要执行的脚本代码内容不能为空！");
    Validate.notNull(policy.getScriptLanguage(), "脚本语言类型不能为空！");
    Validate.notBlank(policy.getDescription(), "策略的描述信息不能为空！");
  }

  /**
   * 该私有方法根据policy携带的元数据信息描述，进行添加
   * 
   * @param metadata
   * @param id 特别注意以下这个参数，它不是指业务层面上的policyId，而是指数据表内部使用的policy数据表的id主键
   */
  private void upsertMetadata(MetadataModel metadata, final String id) {
    if (variablePropertyRepository.getByPolicyId(id) > 0) {
      // 操作过程是首先删除原来的元数据信息，然后在进行重新添加
      this.variablePropertyRepository.deleteByPolicyId(id);
    }
    // 重新进行添加
    Set<VariableProperty> vars = metadata.getParams();
    if (vars != null && !vars.isEmpty()) {
      vars.forEach(var -> {
        VariablePropertyEntity varEntity = new VariablePropertyEntity();
        varEntity.setDescription(var.getDescription());
        // TODO 判断元数据的名字只能是英文
        varEntity.setName(var.getName());
        PolicyEntity policy = new PolicyEntity();
        policy.setId(id);
        varEntity.setPolicyId(policy);
        // 插入
        this.variablePropertyRepository.save(varEntity);
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.core.service.PolicyService#update(com.cheshangma.platform
   * .ruleEngine.module.PolicyModel)
   */
  @Override
  @Transactional
  public PolicyModel update(PolicyModel model) {
    /*
     * 注意。policy修改操作，包括了修改基本信息中的四个字段 还包括了修改绑定的元数据 当时和其关联的rule信息，并不通过这里进行修改，而在专门的bind方法中进行修改
     */

    // 检测policy是不是不存在
    PolicyEntity policy = this.policyRepository.findByPolicyId(model.getPolicyId());
    if (policy == null) {
      throw new PolicyNotFoundException("Policy Not Found ! policyId = " + model.getPolicyId());
    }
    // 返回
    model.setId(policy.getId());

    policy.setId(model.getId());
    policy.setMode(model.getMode());
    policy.setExecMode(model.getExecMode());
    policy.setPolicyId(model.getPolicyId());
    policy.setScoreExpression(model.getScoreExpression() == null ? "" : model.getScoreExpression());
    policy.setScriptLanguage(model.getScriptLanguage() == null
        ? ScriptLanguageType.LANGUAGE_GROOVY
        : model.getScriptLanguage());
    policy.setDescription(model.getDescription() == null ? "" : model.getDescription());
    // 更新基本信息
    this.policyRepository.save(policy);

    // 可能的元数据信息
    MetadataModel metadata = model.getMetadata();
    if (metadata != null) {
      this.upsertMetadata(metadata, policy.getId());
    }

    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.core.service.PolicyService#findByPolicyId(java.lang.String)
   */
  @Override
  public PolicyModel findByPolicyId(String policyId) {
    PolicyEntity policy = this.policyRepository.findByPolicyId(policyId);
    if (policy == null) {
      return null;
    }
    return this.transferModel(policy);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.core.service.PolicyService#findAll()
   */
  @Override
  public List<PolicyModel> findAll() {
    /*
     * 取出所有的policy信息，不过只包括基本信息
     */
    Iterable<PolicyEntity> policyEntitys = this.policyRepository.findAll();
    if (policyEntitys == null) {
      return Collections.emptyList();
    }

    // 开始转换基本信息
    List<PolicyModel> policyModels = new ArrayList<>();
    policyEntitys.forEach(pm -> {
      PolicyModel policyModel = this.transferModel(pm);
      policyModels.add(policyModel);
    });
    return policyModels;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.core.service.PolicyService#findAll(java.lang.Iterable)
   */
  @Override
  public List<PolicyModel> findAll(Iterable<String> policyIds) {
    /*
     * 根据主键信息，批量查询。 查询结果只包括policy的基本信息
     */
    if (policyIds == null) {
      return Collections.emptyList();
    }
    Iterable<PolicyEntity> policyEntitys = this.policyRepository.findAll(policyIds);
    if (policyEntitys == null) {
      return Collections.emptyList();
    }

    // 开始转换基本信息
    List<PolicyModel> policyModels = new ArrayList<>();
    policyEntitys.forEach(pm -> {
      PolicyModel policyModel = this.transferModel(pm);
      policyModels.add(policyModel);
    });

    return policyModels;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.core.service.PolicyService#exists(java.lang.String)
   */
  @Override
  public boolean exists(String policyId) {
    /*
     * 如果根据id查询到了policyId，就说明数据存在
     */
    PolicyEntity policy = this.policyRepository.findByPolicyId(policyId);
    return policy != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.platform.ruleEngine.core.service.PolicyService#deleteByPolicyId(java.lang.String
   * )
   */
  @Override
  @Transactional
  public void deleteByPolicyId(String policyId) {
    PolicyEntity policy = this.policyRepository.findByPolicyId(policyId);
    if (policy == null) {
      return;
    }
    this.policyRepository.delete(policy.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.core.service.PolicyService#enable(java.lang.String)
   */
  @Override
  public boolean enable(String policyId) {
    Validate.notBlank(policyId, "policy id must not empty!!");
    this.policyRepository.enable(policyId);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.platform.ruleEngine.core.service.PolicyService#disable(java.lang.String)
   */
  @Override
  public boolean disable(String policyId) {
    Validate.notBlank(policyId, "policy id must not empty!!");
    this.policyRepository.disable(policyId);
    return true;
  }

  /**
   * 该私有方法将PolicyEntity的基本信息转换为PolicyModel基本信息<br>
   * 注意，只包括基本信息
   * 
   * @param policyEntity
   * @return
   */
  private PolicyModel transferModel(PolicyEntity policyEntity) {
    PolicyModel policyModel = new PolicyModel();
    policyModel.setCreated(policyEntity.getCreated());
    // TODO 由于还没有人员信息，所以该字段暂时还没有使用
    policyModel.setCreator("");
    policyModel.setDescription(policyEntity.getDescription());
    policyModel.setId(policyEntity.getId());
    policyModel.setMode(policyEntity.getMode());
    policyModel.setPolicyEnabled(policyEntity.getPolicyEnabled());
    policyModel.setPolicyId(policyEntity.getPolicyId());
    policyModel.setScoreExpression(policyEntity.getScoreExpression());
    policyModel.setScriptLanguage(policyEntity.getScriptLanguage());
    // 元数据
    if (policyEntity.getVariablePropertys() != null
        && policyEntity.getVariablePropertys().size() > 0) {
      Set<VariableProperty> set = new HashSet<MetadataModel.VariableProperty>();
      for (VariablePropertyEntity iterable : policyEntity.getVariablePropertys()) {
        set.add(transferVariableModel(iterable));
      }
      MetadataModel metadata = new MetadataModel();
      metadata.setParams(set);
      policyModel.setMetadata(metadata);
    }

    return policyModel;
  }

  /**
   * 将variablePropertyEntity基本信息转换为VariableProperty.
   * 
   * @param variablePropertyEntity 数据层的元数据
   * @return VariableProperty
   */
  private VariableProperty transferVariableModel(VariablePropertyEntity variablePropertyEntity) {
    VariableProperty variable = new VariableProperty();
    variable.setName(variablePropertyEntity.getName());
    variable.setDescription(variablePropertyEntity.getDescription());
    return variable;
  }
}
