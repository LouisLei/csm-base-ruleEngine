package com.cheshangma.platform.ruleEngine.httpapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.RuleEntity;
import com.cheshangma.platform.ruleEngine.httpapi.service.RuleService;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;

/**
 * 和rule基本管理有关的操作都在这里（但是不包括执行rule的相关操作）
 * 
 * @author yinwenjie
 */
@Api(value = "API - RuleEngine API Rule Controller")
@RestController
@RequestMapping("/v2/rule")
public class RuleController extends BasicController {
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(RuleController.class);

  @Autowired
  private RuleService ruleService;

  /**
   * 创建一个新的rule信息，如果这个rule的ruleId已经存在于系统中，则会创建失败<br>
   * 创建rule所需要的数据格式请参见com.dianrong.morpheus.core.model.RuleModel对象的属性描述<br>
   * 创建过程只包括对Rule的基本信息进行创建，并不包括同时对Rule和Policy进行绑定
   * 
   * @param rule
   * @return
   */
  @ApiOperation("创建一个新的rule信息，如果这个rule的ruleId已经存在于系统中，则会创建失败。"
      + "创建规则请参见com.dianrong.morpheus.core.model.RuleModel对象的属性描述。"
      + "创建过程只包括对Rule的基本信息进行创建，并不包括同时对Rule和Policy进行绑定")
  @RequestMapping(value = "", method = RequestMethod.POST)
  public ExecuteHttpResponse createRule(@RequestBody RuleEntity rule) {
//    RuleEntity ruleEntity = ruleService.addUpdateRule(rule);
//    ExecuteHttpResponse result = new ExecuteHttpResponse();
//    if (ruleEntity == null) {
//      result.setMessage("创建失败！");
//    }else {
//      result.setData(ruleEntity);
//    }
//    return result;
    return null;
  }

  /**
   * 新增或者修改一个指定的rule信息：<br>
   * 新增rule信息时的规则逻辑请参考createRule的规则。
   * <p>
   * 修改信息时只能修改（只允许修改）以下属性<br>
   * meta、expression、scriptLanguage、creator和description信息<br>
   * 其它信息，即使传入也不会发生变更
   * <p>
   * 
   * 而添加或者修改一个rule的基本判定原则，就是传入的rule对象中，RuleId是否已经存在了。
   * 
   * @param rule
   * @return
   */
  @ApiOperation("新增或者修改一个指定的rule信息：" + "新增rule信息时的规则逻辑请参考createRule的规则。" + "修改信息时只能修改（只允许修改）以下属性"
      + "meta、expression、scriptLanguage、creator和description信息"
      + "其它信息，即使传入也不会发生变更。而添加或者修改一个rule的基本判定原则，就是传入的rule对象中，RuleId是否已经存在了。")
  @RequestMapping(value = "", method = RequestMethod.PATCH)
  public ExecuteHttpResponse upsertRule(@RequestBody RuleEntity rule) {
//    RuleEntity ruleEntity = ruleService.addUpdateRule(rule);
//    
//    
//    ExecuteHttpResponse result = new ExecuteHttpResponse();
//    if (ruleEntity == null) {
//      result.setMessage("修改失败！");
//    }else {
//      result.setData(ruleEntity);
//    }
//    return result;
    return null;
  }

  /**
   * 该HTTP API用于绑定指定policy和若干个rules之间的执行关系。这样在指定policy的时候<br>
   * policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。<br>
   * 注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true
   * 
   * @param policyId
   * @param ruleIds
   * @return
   */
  @ApiOperation("该HTTP API用于绑定指定policy和若干个rules之间的执行关系。这样在指定policy的时候"
      + "policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。"
      + "注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true")
  @RequestMapping(value = "/bind/{policyId}/{ruleIds}", method = RequestMethod.POST)
  public ExecuteHttpResponse bind(@PathVariable("policyId") String policyId,
      @PathVariable("ruleIds") String[] ruleIds) {
    return null;
  }

  /**
   * 该HTTP API用于解绑指定policy和若干个rules之间的执行关系。这样在指定policy的时候<br>
   * policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。<br>
   * 注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true
   * 
   * @param policyId
   * @param ruleIds
   * @return
   */
  @ApiOperation("该HTTP API用于解绑指定policy和若干个rules之间的执行关系。这样在指定policy的时候"
      + "policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。"
      + "注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true")
  @RequestMapping(value = "/unbind/{policyId}/{ruleIds}", method = RequestMethod.POST)
  public ExecuteHttpResponse unbind(@PathVariable("policyId") String policyId,
      @PathVariable("ruleIds") String[] ruleIds) {
    return null;
  }

  /**
   * 该方法用于删除指定的rule信息，请注意，不是逻辑删除，是真删除。<br>
   * 所以在使用该方法前一定要确认您的操作。因为这条rule信息的和若干个policy的关联关系将一并删除。<br>
   * 当然并不是说要同时删除这些policy，而只是rule和policy的关联关系
   * 
   * @param ruleId 这个ruleid是rule信息的业务编号
   * @return
   */
  @ApiOperation("该方法用于删除指定的rule信息，请注意，不是逻辑删除，是真删除。"
      + "所以在使用该方法前一定要确认您的操作。因为这条rule信息的和若干个policy的关联关系将一并删除。"
      + "当然并不是说要同时删除这些policy，而只是rule和policy的关联关系")
  @RequestMapping(value = "/{ruleId}", method = RequestMethod.DELETE)
  public ExecuteHttpResponse delete(@PathVariable("ruleId") String ruleId) {
    ExecuteHttpResponse result = new ExecuteHttpResponse();
    try {
      ruleService.deleteByRuleId(ruleId);
      result.setStatus("删除成功！");
      result.setData(true);
      result.setStatus("200");
    } catch (Exception e) {
      result.setMessage("删除失败！");
      result.setStatus("500");
      result.setException(e.getMessage());
    }
    return result;
  }

  /**
   * 查询当前系统中可用的所有rule信息。
   * 
   * @return
   */
  @ApiOperation("查询当前系统中可用的所有rule信息。这些信息将按照被创建的时间倒序排列" + "。注意，目前还没有分谢方法，后续该系统真正被使用了，在进行增加")
  @RequestMapping(value = "/retrieve/", method = {RequestMethod.GET, RequestMethod.POST})
  public ExecuteHttpResponse retrieveAllRule() {
    return null;
  }

  /**
   * 这些rule信息将依据绑定的policyId被查询出来，并且按照执行顺序依次排列。<br>
   * 如果当前policy没有绑定任何编号信息，则返回一个空集合
   * 
   * @param policyId 指定的policy业务级编号policyId。
   * @return
   */
  @ApiOperation("这些rule信息将依据绑定的policyId被查询出来，并且按照执行顺序依次排列。" + "如果当前policy没有绑定任何编号信息，则返回一个空集合")
  @RequestMapping(value = "/retrieve/{policyId}", method = RequestMethod.GET)
  public ExecuteHttpResponse retrieveRuleStep(@PathVariable("policyId") String policyId) {
    return null;
  }

  /**
   * 该方法用于查询一个指定的Rule信息，返回的结果中，包括了这个rule的所有基本信息
   * 
   * @param ruleId 指定的rule业务级编号ruleId。
   */
  @ApiOperation("该方法用于查询一个指定的Rule信息，返回的结果中，包括了这个rule的所有基本信息")
  @RequestMapping(value = "/retrieveOne/{ruleId}", method = RequestMethod.GET)
  public ExecuteHttpResponse retrieveOne(@PathVariable("ruleId") String ruleId) {
    return null;
  }
}
