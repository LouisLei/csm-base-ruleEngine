package com.cheshangma.platform.ruleEngine.httpapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecutePolicyRequest;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteScriptRequest;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.PolicyStepModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

/**
 * 和执行policy或者rule的设定有关的Http API都通过这个controller提供支持
 * @author yinwenjie
 * @version 2.X
 */
@Api(value = "API - RuleEngine API ExecuteController")
@RestController
@RequestMapping("/v2/execute")
public class ExecuteController extends BasicController{
  
  @Autowired
  private RuleEngineFramework ruleEngineFramework;
  
	/**
	 * 该方法用于执行一个rule下设定的动态脚本信息，通过传入的inputs入参信息<br>
	 * 这种方式您必须保证ruleId对应的规则信息，已经在Morpheus的数据持久层进行了保存，并且是能够在持久层被查询到的
	 * @return 执行结果将会被返回，无论执行是否成功
	 */
	@ApiOperation("该方法用于执行一个rule下设定的动态脚本信息，通过传入的inputs入参信息"
			+ "这种方式您必须保证ruleId对应的规则信息，已经在Morpheus的数据持久层进行了保存，并且是能够在持久层被查询到的")
	@RequestMapping(value = "/rule/{ruleId}", method = RequestMethod.POST)
	public ExecuteHttpResponse executeRule(@PathVariable("ruleId") String ruleId , @RequestBody Map<String, Object> inputs) {
	  ExecuteHttpResponse result = new ExecuteHttpResponse();
	  Validate.notBlank(ruleId, "规则业务id不能为空！");
	  RuleModel rule = ruleEngineFramework.getRuleService().findByRuleId(ruleId);
	  try {
	    ruleEngineFramework.executeRule(rule, inputs);
	    result.setStatus("200");
      } catch (Exception e) {
        result.setStatus("500");
        result.setException(e.getMessage());
      }
		return result;
	}
	
	/**
	 * 该方法用于执行一个从外部传入的动态脚本信息（通过ExecuteRuleRequest对象进行描述）,<br>
	 * 这个方法一般用于测试目前正在界面上进行编辑的脚本信息，以便操作者随时检查脚本的正确性<br>
	 * 这个脚本可能是来自于一个正在编辑的policy信息，也可能是来自于一个正在编辑的rule信息
	 * @see #ExecuteRuleRequest
	 * @return
	 */
	@ApiOperation("该方法用于执行一个从外部传入的动态脚本信息（通过ExecuteScriptRequest对象进行描述）,"
			+ "这个方法一般用于测试目前正在界面上进行编辑的脚本信息，以便操作者随时检查脚本的正确性"
			+ "这个脚本可能是来自于一个正在编辑的policy信息，也可能是来自于一个正在编辑的rule信息")
	@RequestMapping(value = "/try", method = RequestMethod.POST)
	public ExecuteHttpResponse executeScript(@RequestBody ExecuteScriptRequest ruleRequest) {
		return null;
	}
	
	/**
	 * 该方法用于执行指定的policy，要成功执行这个方法首先您必须确认policyId对应的policy信息已经在数据持久层保存成功了<br>
	 * 另外，无论执行的policy是自己就有脚本代码，还是绑定了若干个rule，都支持调用该方法进行执行；<br>
	 * 但是如果这个policy既没有自己的脚本代码，也没有绑定任何rule，那么调用该方法将会出现异常。
	 * @return
	 */
	@ApiOperation("该方法用于执行指定的policy，要成功执行这个方法首先您必须确认policyId对应的policy信息已经在数据持久层保存成功了"
			+ "另外，无论执行的policy是自己就有脚本代码，还是绑定了若干个rule，都支持调用该方法进行执行；"
			+ "但是如果这个policy既没有自己的脚本代码，也没有绑定任何rule，那么调用该方法将会出现异常。")
	@RequestMapping(value = "/policy/{policyId}", method = RequestMethod.POST)
	public ExecuteHttpResponse executePolicy(@PathVariable("policyId") String policyId , @RequestBody Map<String, Object> inputs) {
	  ExecuteHttpResponse result = new ExecuteHttpResponse();
      Validate.notBlank(policyId, "策略业务id不能为空！");
      PolicyModel policy = ruleEngineFramework.getPolicyService().findByPolicyId(policyId);
      List<PolicyStepModel> policySteps = policy.getExecution();
      // TODO 规则从哪里获取
      List<RuleModel> rules = null;
      ruleEngineFramework.executePolicy(policy, rules, inputs);
	  return null;
	}
	
	/**
	 * 该方法用于测试一个policy的执行，注意该方法只支持对policy绑定了一个或者多个rule的情况进行测试，不支持对policy自有脚本信息的测试<br>
	 * 如果希望测试后一种场景可以使用/v2/execute/policy这个方法<br>
	 * 这个方法需要注意一点，其中传入的execution信息中ruleId，不是rule的业务id，而是这个rule保存在数据持久层时使用的id信息<br>
	 * 具体以Mysql为例，就是Mysql数据表的rule主键id信息
	 * @see #executeScript
	 * @param policyRequest 其中的execution属性中至少有一个绑定的ruleId，且这个RuleId对应的rule必须是已经保存到数据持久层中的。
	 * @return
	 */
	@ApiOperation("该方法用于测试一个policy的执行，注意该方法只支持对policy绑定了一个或者多个rule的情况进行测试，"
			+ "不支持对policy自有脚本信息的测试。"
			+ "如果希望测试后一种场景可以使用/v2/execute/policy这个方法。"
			+ "其传入的对象中，execution属性至少有一个绑定的ruleId，且这个RuleId对应的rule必须是已经保存到数据持久层中的。"
			+ "这个方法还需要注意一点，其中传入的execution信息中ruleId，不是rule的业务id，而是这个rule保存在数据持久层时使用的id信息")
	@RequestMapping(value = "/policy/try", method = RequestMethod.POST)
	public ExecuteHttpResponse executePolicyTry(@RequestBody  ExecutePolicyRequest policyRequest) {
		return null;
	}
}