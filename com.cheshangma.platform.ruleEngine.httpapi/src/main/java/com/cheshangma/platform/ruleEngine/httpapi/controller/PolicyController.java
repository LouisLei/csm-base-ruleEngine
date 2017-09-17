package com.cheshangma.platform.ruleEngine.httpapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cheshangma.platform.ruleEngine.httpapi.service.PolicyService;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;

/**
 * 和Policy基本管理有关的操作都在这里（但是不包括执行policy的相关操作）
 * 
 * @author yinwenjie
 */
@Api(value = "API - RuleEngine API Policy Controller")
@RestController
@RequestMapping("/v2/policy")
public class PolicyController extends BasicController {

  @Autowired
  private PolicyService policyService;
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(PolicyController.class);

  /**
   * 创建一个新的policy信息，如果这个policy的policyId已经存在于系统中，则会创建失败<br>
   * 创建policy所需要的数据格式请参见com.dianrong.morpheus.core.model.PolicyModel对象的属性描述<br>
   * 创建policy的过程不但包括对policy的基本信息进行创建，还包括对policy可能同时创建的“元数据”信息同时进行创建<br>
   * 但是创建policy并不包括对可能存在的rule关系进行同时绑定
   * 
   * @param policy
   * @return
   */
  @ApiOperation("创建一个新的policy信息，如果这个policy的policyId已经存在于系统中，则会创建失败"
      + "创建policy所需要的数据格式请参见com.dianrong.morpheus.core.model.PolicyModel对象的属性描述"
      + "创建policy的过程不但包括对policy的基本信息进行创建，还包括对policy可能同时创建的“元数据”信息同时进行创建"
      + "但是创建policy并不包括对可能存在的rule关系进行同时绑定")
  @RequestMapping(value = "", method = RequestMethod.POST)
  public ExecuteHttpResponse createPolicy(@RequestBody PolicyModel policy) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notNull(policy, "策略信息不能为空！");
    try {
      policyService.save(policy);
      result.setStatus("200");
      result.setMessage("创建成功！");
    } catch (Exception e) {
      result.setStatus("500");
      result.setException(e.getMessage());
      result.setMessage("创建失败！");
    }
    result.setData(policy);
    return result;
  }

  /**
   * 新增或者修改一个指定的policy信息：<br>
   * 新增policy信息时的规则逻辑请参考createPolicy的规则。
   * <p>
   * 修改信息时只能修改（只允许修改）以下属性（都是policy的基本信息）<br>
   * meta、expression、scriptLanguage、creator和description信息<br>
   * 另外，也可以一同修改policy的元数据信息
   * <p>
   * 
   * 而添加或者修改一个policy的基本判定原则，就是传入的policy对象中，policyId是否已经存在了。
   * 
   * @param policy
   * @return
   */
  @ApiOperation("新增或者修改一个指定的policy信息：" + "新增policy信息时的规则逻辑请参考createPolicy的规则。"
      + "修改信息时只能修改（只允许修改）以下属性（都是policy的基本信息）"
      + "meta、expression、scriptLanguage、creator和description信息" + "另外，也可以一同修改policy的元数据信息"
      + "而添加或者修改一个policy的基本判定原则，就是传入的policy对象中，policyId是否已经存在了。")
  @RequestMapping(value = "", method = RequestMethod.PATCH)
  public ExecuteHttpResponse upsertPolicy(@RequestBody PolicyModel policy) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notNull(policy, "策略信息不能为空！");
    try {
      policyService.update(policy);
      result.setStatus("200");
      result.setMessage("修改成功！");
    } catch (Exception e) {
      result.setStatus("500");
      result.setException(e.getMessage());
      result.setMessage("修改失败！");
    }
    result.setData(policy);
    return result;
  }

  /**
   * 该方法用于删除指定的policy信息，请注意，不是逻辑删除，是真删除。<br>
   * 所以在使用该方法前一定要确认您的操作。因为这条policy信息的关联的元数据信息将一并被删除。<br>
   * 但是关联的rule关系，却会保留下来（因为这个rule还可能关联了其它rule信息
   * 
   * @param policyId
   * @return
   */
  @ApiOperation("该方法用于删除指定的policy信息，请注意，不是逻辑删除，是真删除。"
      + "所以在使用该方法前一定要确认您的操作。因为这条policy信息的关联的元数据信息将一并被删除。"
      + "但是关联的rule关系，却会保留下来（因为这个rule还可能关联了其它rule信息")
  @RequestMapping(value = "/{policyId}", method = RequestMethod.DELETE)
  public ExecuteHttpResponse delete(@PathVariable("policyId") String policyId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notBlank(policyId, "策略的业务id不能为空！");
    try {
      policyService.deleteByPolicyId(policyId);
      result.setStatus("200");
      result.setData(true);
      result.setMessage("删除成功！");
    } catch (Exception e) {
      result.setStatus("500");
      result.setException(e.getMessage());
      result.setMessage("删除失败！");
    }
    return result;
  }

  /**
   * 该方法用于标记/修改指定的policy为“可用”状态<br>
   * 如果操作成功，那么无论该policy之前的状态是否为“可用”，反正现在的状态为“可用”了
   * 
   * @param policyId
   * @return
   */
  @ApiOperation("该方法用于标记/修改指定的policy为“可用”状态" + "如果操作成功，那么无论该policy之前的状态是否为“可用”，反正现在的状态为“可用”了")
  @RequestMapping(value = "/enable/{policyId}", method = RequestMethod.POST)
  public ExecuteHttpResponse enable(@PathVariable("policyId") String policyId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notBlank(policyId, "策略的业务id不能为空！");
    try {
      policyService.enable(policyId);
      result.setStatus("200");
      result.setData(true);
      result.setMessage("成功！");
    } catch (Exception e) {
      result.setStatus("500");
      result.setException(e.getMessage());
      result.setMessage("失败！");
    }
    return result;
  }

  /**
   * 该方法用于标记/修改指定的policy为“不可用”状态<br>
   * 如果操作成功，那么无论该policy之前的状态是否为“可用”，反正现在的状态为“不可用”了<br>
   * 一旦某个policy的状态为不可用，则该policy下的动态代码内容或者其下若干个rule的动态代码内容，都不会执行。<br>
   * 且在执行execute方法时，会抛出异常
   * 
   * @param policyId
   * @return
   */
  @ApiOperation("该方法用于标记/修改指定的policy为“不可用”状态" + "如果操作成功，那么无论该policy之前的状态是否为“可用”，反正现在的状态为“不可用”了"
      + "一旦某个policy的状态为不可用，则该policy下的动态代码内容或者其下若干个rule的动态代码内容，都不会执行。" + "且在执行execute方法时，会抛出异常")
  @RequestMapping(value = "/disable/{policyId}", method = RequestMethod.POST)
  public ExecuteHttpResponse disable(@PathVariable String policyId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notBlank(policyId, "策略的业务id不能为空！");
    try {
      policyService.disable(policyId);
      result.setStatus("200");
      result.setData(true);
      result.setMessage("成功！");
    } catch (Exception e) {
      result.setStatus("500");
      result.setException(e.getMessage());
      result.setMessage("失败！");
    }
    return result;
  }

  /**
   * 该Http API方法用于提供给操作者，检查指定的policy的状态是否可用<br>
   * 换句话说，检查指定的policy的policyEnable属性的值<br>
   * 
   * @param policyId
   * @return 如果当前policy存在，且policyEnable属性的值为true，则返回true；其它情况下返回false
   */
  @RequestMapping(value = "/check/{policyId}", method = RequestMethod.GET)
  public ExecuteHttpResponse check(@PathVariable("policyId") String policyId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notBlank(policyId, "策略的业务id不能为空！");
    PolicyModel policyModel = null;
    try {
      policyModel = policyService.findByPolicyId(policyId);
      result.setData(policyModel.getPolicyEnabled());
      result.setStatus("200");
    } catch (Exception e) {
      result.setException(e.getMessage());
      result.setStatus("500");
      result.setData(false);
    }
    return result;
  }

  /**
   * 该方法查询当前系统中所有policy信息，无论这些policy是否可用 <br>
   * 注意，该方法只会查询policy的基本信息，并不会连带查询出类似policy已绑定的rule信息
   * 
   * @return 如果当前没有任何policy信息，则返回一个空集合
   */
  @ApiOperation("该方法查询当前系统中所有policy信息，无论这些policy是否可用。"
      + "注意，该方法只会查询policy的基本信息，并不会连带查询出类似policy已绑定的rule信息。" + "如果当前没有任何policy信息，则返回一个空集合")
  @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
  public ExecuteHttpResponse retrieve() {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    List<PolicyModel> list = policyService.findAll();
    if (list != null && list.size() > 0) {
      List<PolicyModel> nowList = list.stream().map((source) -> {
        source.setExecution(null);
        source.setMetadata(null);
        return source;
      }).collect(Collectors.toList());
      result.setData(nowList);
    } else {
      result.setData(Collections.emptyList());
    }
    result.setStatus("200");
    return result;
  }

  /**
   * 该方法用于查询一个指定的Policy信息，返回的信息中，<br>
   * 除了包括policy基本信息外，还包括这个policy关联的元数据信息和可能绑定的Rule基本信息
   * 
   * @param policyId 指定的policy业务级编号policyId。
   */
  @ApiOperation("该方法用于查询一个指定的Policy信息，返回的信息中，"
      + "除了包括policy基本信息外，还包括这个policy关联的元数据信息和可能绑定的Rule基本信息")
  @RequestMapping(value = "/retrieveOne/{policyId}", method = RequestMethod.GET)
  public ExecuteHttpResponse retrieveOne(@PathVariable("policyId") String policyId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    Validate.notBlank(policyId, "策略业务id不能为空！");
    PolicyModel policyModel = policyService.findByPolicyId(policyId);
    if (policyModel == null) {
      result.setData(null);
      result.setStatus("404");
      result.setMessage("数据不存在！");
    } else {
      result.setData(policyModel);
      result.setStatus("200");
    }
    return result;
  }
  
}
