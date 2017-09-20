package com.cheshangma.platform.ruleEngine.httpclient;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheshangma.platform.ruleEngine.core.executor.ScriptReversableAbstractFactory;
import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.core.utils.JSONMapper;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.httpclient.iface.ExecuteRemote;
import com.cheshangma.platform.ruleEngine.httpclient.iface.PolicyRemote;
import com.cheshangma.platform.ruleEngine.httpclient.iface.RuleRemote;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecutePolicyRequest;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteScriptRequest;
import com.cheshangma.platform.ruleEngine.module.ExecutionPolicyModel;
import com.cheshangma.platform.ruleEngine.module.ExecutionRuleModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel;
import com.cheshangma.platform.ruleEngine.module.MetadataModel.VariableProperty;
import com.cheshangma.platform.ruleEngine.module.PolicyModel;
import com.cheshangma.platform.ruleEngine.module.PolicyStepModel;
import com.cheshangma.platform.ruleEngine.module.RuleEngineModel;
import com.cheshangma.platform.ruleEngine.module.RuleModel;

import feign.Feign;
import feign.Request.Options;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * RuleEngine Client，由需要在本地集成RuleEngine规则引擎的各个业务系统使用。其中汇总了RuleEngine规则引擎能够提供给技术人员使用的所有功能<br>
 * RuleEngine Client对于policy和rule信息维护以及远程执行的本质是通过feign去执行远程调用，所以如果要使用RuleEngine Client，则需要通过类似以下方式进行RuleEngine Client的初始化：<p>
 * 
 * RuleEngineClient.Builder build = new RuleEngineClient.Builder();<br>
 * RuleEngineClient = build.setRemoteURL("http://localhost:8080").build();
 * 
 * @author yinwenjie
 */
public class RuleEngineClient {
	
	/**
	 * 系统中唯一一个RuleEngineClient类的实例化对象
	 */
	private static RuleEngineClient engineClient;
	
	/**
	 * 日志
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RuleEngineClient.class);
	
	/**
	 * 远程HTTP API服务中和rule基本信息管理有关的在这里
	 */
	private RuleRemote ruleRemote;
	
	/**
	 * 远程HTTP API服务中和policy基本信息管理有关的在这里
	 */
	private PolicyRemote policyRemote;
	
	/**
	 * 远程HTTP API服务中和policy/rule执行有关的在这里
	 */
	private ExecuteRemote executeRemote;
	
	/**
	 * 在本地直接运行RuleEngine脚本服务所需要的RuleEngine服务框架
	 */
	private RuleEngineFramework ruleEngineFramework;
	
	private RuleEngineClient(Builder builder) {		
		feign.Feign.Builder feignBuilder = Feign.builder()
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.client(new ApacheHttpClient())
				.options(new Options(10000, 10000));
		
		this.ruleRemote = feignBuilder.target(RuleRemote.class, builder.getRemoteURL());
		this.policyRemote = feignBuilder.target(PolicyRemote.class, builder.getRemoteURL());
		this.executeRemote = feignBuilder.target(ExecuteRemote.class, builder.getRemoteURL());
		
		// 继续初始化本地的RuleEngine服务，以便它为反调功能服务
		RuleEngineFramework.Builder fBuilder = RuleEngineFramework.Builder.getInstanceBuilder();
		fBuilder.setAllowInverse(builder.getAllowInverse());
		fBuilder.setMaxExecutionThread(builder.getMaxExecutionThread()).
			setMinExecutionThread(builder.getMinExecutionThread()).
			setScriptQueueSize(builder.getScriptQueueSize()).
			setScriptThreadName(builder.getScriptThreadName()).
			setWaitingTimeout(builder.getWaitingTimeout()).
			setScriptReversableFactory(builder.getScriptReversableAbstractFactory());
		this.ruleEngineFramework = fBuilder.buildIfAbent();
	}
	
	private static RuleEngineClient  getNewInstance (Builder builder) {
		if(RuleEngineClient.engineClient != null) {
			return RuleEngineClient.engineClient;
		}
		
		// 避免并发情况下被多次初始化
		synchronized (RuleEngineClient.class) {
			while(RuleEngineClient.engineClient == null) {
				RuleEngineClient.engineClient = new RuleEngineClient(builder);
			}
		}
		return RuleEngineClient.engineClient;
	}
	
	/**
	 * 创建一个新的policy信息，如果这个policy的policyId已经存在于系统中，则会创建失败<br>
	 * 创建policy所需要的数据格式请参见com.dianrong.RuleEngine.core.model.PolicyModel对象的属性描述<br>
	 * 创建policy的过程不但包括对policy的基本信息进行创建，还包括对policy可能同时创建的“元数据”信息同时进行创建<br>
	 * 但是创建policy的功能并不包括对可能存在的rule关系进行同时绑定，如果要实现后者，请使用RuleEngineClient中的bindRule方法<p>
	 * 创建一个policy时，其中只有policyId是必须填写的。
	 * @see #bindRule(String, String)
	 * @param policy 这是要进行新创建的policy信息
	 */
	public PolicyModel createPolicy(PolicyModel policy) {
		this.checkPolicy(policy);
		
		// 拆解成json
		String json = this.toJSON(policy);
		
		// 构建返回信息
		ExecuteHttpResponse response = this.policyRemote.createPolicy(json);
		return this.responseTransfer(response, PolicyModel.class);
	}
	
	/**
	 * 新增或者修改一个指定的policy信息：<br>
	 * 新增policy信息时的规则逻辑请参考createPolicy的规则。<p>
	 * 
	 * 修改信息时只能修改（只允许修改）以下属性（都是policy的基本信息）<br>
	 * meta、expression、scriptLanguage、creator和description信息<br>
	 * 另外，也可以一同修改policy的元数据信息<p>
	 * 
	 * 而添加或者修改一个policy的基本判定原则，就是传入的policy对象中，policyId是否已经存在了。
	 * @param policy 这是要进行创建/修改的policy信息
	 * @return 注意返回信息，返回信息中将会携带修改后的policy最新属性
	 */
	public PolicyModel upsertPolicy(PolicyModel policy) {
		this.checkPolicy(policy);
		
		// 拆解成json
		String json = this.toJSON(policy);
		
		// 构建返回信息
		ExecuteHttpResponse response = this.policyRemote.upsertPolicy(json);
		return this.responseTransfer(response, PolicyModel.class);
	}
	
	/**
	 * 该方法用于删除指定的policy信息，请注意，不是逻辑删除，是真删除。<br>
	 * 所以在使用该方法前一定要确认您的操作。因为这条policy信息的关联的元数据信息将一并被删除。<br>
	 * 但是关联的rule关系，却会保留下来（因为这个rule还可能关联了其它rule信息
	 */
	public boolean deletePolicy(String policyid)  {
		this.checkPolicyId(policyid);
		ExecuteHttpResponse response = this.policyRemote.delete(policyid);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该HTTP API方法用于提供给操作者，检查指定的policy的状态是否可用<br>
	 * 换句话说，检查指定的policy的policyEnable属性的值
	 */
	public boolean checkPolicy(String policyid) {
		this.checkPolicyId(policyid);
		ExecuteHttpResponse response = this.policyRemote.check(policyid);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该方法用于标记/修改指定的policy为“可用”状态<br>
	 * @return 如果设置操作成功（无论之前的policy的enable状态如何），则返回true；其它情况返回false
	 */
	public boolean enablePolicy(String policyid) {
		this.checkPolicyId(policyid);
		ExecuteHttpResponse response = this.policyRemote.enable(policyid);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该方法用于标记/修改指定的policy为“不可用”状态<br>
	 * 如果操作成功，那么无论该policy之前的状态是否为“可用”，反正现在的状态为“不可用”了<br>
	 * 一旦某个policy的状态为不可用，则该policy下的动态代码内容或者其下若干个rule的动态代码内容，都不会执行。<br>
	 * 且在执行execute方法时，会抛出异常
	 * @return 如果设置操作成功（无论之前的policy的enable状态如何），则返回true；其它情况返回false
	 */
	public boolean disablePolicy(String policyid)  {
		this.checkPolicyId(policyid);
		ExecuteHttpResponse response = this.policyRemote.disable(policyid);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该方法用于查询一个指定的Policy信息，返回的信息中，
	 * 除了包括policy基本信息外，还包括这个policy关联的元数据信息和可能绑定的Rule基本信息
	 */
	public PolicyModel getPolicy(String policyid) {
		this.checkPolicyId(policyid);
		ExecuteHttpResponse response = this.policyRemote.retrieveOne(policyid);
		return this.responseTransfer(response, PolicyModel.class);
	}
	
	/**
	 * 该方法查询当前系统中所有policy信息，无论这些policy是否可用 <br>
	 * 注意，该方法只会查询policy的基本信息，并不会连带查询出类似policy已绑定的rule信息，或者元数据信息
	 */
	public List<PolicyModel> queryAllPolicy() {
	  ExecuteHttpResponse response = this.policyRemote.retrieve();
		return this.responseTransfers(response, PolicyModel.class);
	}
	
	/**
	 * 创建一个新的rule信息，如果这个rule的ruleId已经存在于系统中，则会创建失败<br>
	 * 创建rule所需要的数据格式请参见com.dianrong.RuleEngine.core.model.RuleModel对象的属性描述<br>
	 * 创建过程只包括对Rule的基本信息进行创建，并不包括同时对Rule和Policy进行绑定<p>
	 * 
	 * 以下信息是必须填写的：<br>
	 * ruleId、expression、scriptLanguage（有默认值）
	 * @see com.dianrong.RuleEngine.core.model.RuleModel
	 */
	public RuleModel createRule(RuleModel rule)  {
		this.checkRule(rule);
		
		// 转换成json
		String json = this.toJSON(rule);
		
		// 调用并返回
		ExecuteHttpResponse response = this.ruleRemote.createRule(json);
		return this.responseTransfer(response , RuleModel.class);
	}
	
	/**
	 * 新增或者修改一个指定的rule信息：<br>
	 * 新增rule信息时的规则逻辑请参考createRule的规则。<p>
	 * 
	 * 修改信息时只能修改（只允许修改）以下属性<br>
	 * meta、expression、scriptLanguage、creator和description信息<br>
	 * 其它信息，即使传入也不会发生变更<p>
	 * 
	 * 而添加或者修改一个rule的基本判定原则，就是传入的rule对象中，RuleId是否已经存在了。
	 */
	public RuleModel upsertRule(RuleModel rule)  {
		this.checkRule(rule);
		
		// 转换成json
		String json = this.toJSON(rule);
		
		// 调用并返回
		ExecuteHttpResponse response = this.ruleRemote.upsertRule(json);
		return this.responseTransfer(response , RuleModel.class);
	}
	
	/**
	 * 该HTTP API用于绑定指定policy和若干个rules之间的执行关系。这样在指定policy的时候<br>
	 * policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。<br>
	 * 注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true
	 * @param ruleIds 特别说明以下ruleId，这里的ruleId就是操作者在创建rule时自己定义的唯一业务编号
	 */
	public boolean bindRule(String policyId , String[] ruleIds) {
		this.checkPolicyId(policyId);
		if(ruleIds == null || ruleIds.length == 0) {
			throw new IllegalArgumentException("ruleIds must not be empty!");
		}
		
		// 多个ruleid以“,”符号分割
		String arrayRules = StringUtils.join(ruleIds, ",");
		ExecuteHttpResponse response = this.ruleRemote.bindRule(policyId, arrayRules);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该方法用于删除指定的rule信息，请注意，不是逻辑删除，是真删除。<br>
	 * 所以在使用该方法前一定要确认您的操作。因为这条rule信息的和若干个policy的关联关系将一并删除。<br>
	 * 当然并不是说要同时删除这些policy，而只是rule和policy的关联关系
	 */
	public boolean deleteRule(String ruleId) {
		this.checkRuleId(ruleId);
		ExecuteHttpResponse response = this.ruleRemote.delete(ruleId);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该HTTP API用于解绑指定policy和若干个rules之间的执行关系。这样在指定policy的时候
	 * policy本身的动态脚本将不会执行，转而依次执行这些rule的动态脚本代码，并返回执行结果。
	 * 注意，前提是policy本身应该是可用的，即policy的policyEnabled属性为true
	 * @param ruleIds 特别说明以下ruleId，这里的ruleId就是操作者在创建rule时自己定义的唯一业务编号
	 */
	public boolean unbindRule(String policyId , String[] ruleIds) {
		this.checkPolicyId(policyId);
		if(ruleIds == null || ruleIds.length == 0) {
			throw new IllegalArgumentException("ruleIds must not be empty!");
		}
		
		// 多个ruleid以“,”符号分割
		String arrayRules = StringUtils.join(ruleIds, ",");
		ExecuteHttpResponse response = this.ruleRemote.unbindRule(policyId, arrayRules);
		return this.responseTransfer(response);
	}
	
	/**
	 * 该方法用于查询一个指定的Rule信息，返回的结果中，包括了这个rule的所有基本信息
	 * @param ruleId 指定的rule业务级编号ruleId。
	 */
	public RuleModel getRule(String ruleId) {
		this.checkRuleId(ruleId);
		ExecuteHttpResponse response = this.ruleRemote.retrieveOne(ruleId);
		return this.responseTransfer(response , RuleModel.class);
	}
	
	/**
	 * 查询当前系统中可用的所有rule信息。这些信息将按照创建时间进行反向排序
	 */
	public List<RuleModel> queryAllRules() {
	  ExecuteHttpResponse response = this.ruleRemote.retrieveAllRule();
		return this.responseTransfers(response , RuleModel.class);
	}
	
	/**
	 * 这些rule信息将依据绑定的policyId被查询出来，并且按照执行顺序依次排列。<br>
	 * 如果当前policy没有绑定任何编号信息，则返回一个空集合
	 */
	public List<RuleModel> queryRuleByPolicy(String policyId) {
		this.checkPolicyId(policyId);
		ExecuteHttpResponse response = this.ruleRemote.retrieveRuleStep(policyId);
		return this.responseTransfers(response , RuleModel.class);
	}
	
	/**
	 * 该方法用于执行一个rule下设定的动态脚本信息，通过传入的inputs入参信息<br>
	 * 这种方式您必须保证ruleId对应的规则信息，已经在RuleEngine的数据持久层进行了保存，并且是能够在持久层被查询到的<p>
	 * 
	 * 使用该方法请注意，由于向服务器的通讯依靠JSON结构完成，但是因为是执行动态脚本，所以并不知道返回的score属性携带那些类型
	 * 所以这里返回最原始的JSON信息，由调用这自己根据实际情况完成转换<p>
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 */
	public ExecuteHttpResponse executeRemoteRule(String ruleId , Map<String, Object> inputs) {
		this.checkRuleId(ruleId);
		Map<String, Object> currentInputs;
		if(inputs == null) {
			currentInputs = Collections.emptyMap();
		} else {
			currentInputs = inputs;
		}
		
		// 转json
		String json = this.toJSON(currentInputs);
		
		return this.executeRemote.executeRule(ruleId, json);
	}
	
	/**
	 * 该方法用于执行指定的policy，要成功执行这个方法首先您必须确认policyId对应的policy信息已经在数据持久层保存成功了
	 * 另外，无论执行的policy是自己就有脚本代码，还是绑定了若干个rule，都支持调用该方法进行执行；
	 * 但是如果这个policy既没有自己的脚本代码，也没有绑定任何rule，那么调用该方法将会出现异常。<p>
	 * 
	 * 使用该方法请注意，由于向服务器的通讯依靠JSON结构完成，但是因为是执行动态脚本，所以并不知道返回的score属性携带那些类型
	 * 所以这里返回最原始的JSON信息，由调用这自己根据实际情况完成转换<p>
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 */
	public ExecuteHttpResponse executeRemotePolicy(String policyid , Map<String, Object> inputs) {
		this.checkPolicyId(policyid);
		Map<String, Object> currentInputs;
		if(inputs == null) {
			currentInputs = Collections.emptyMap();
		} else {
			currentInputs = inputs;
		}
		
		// 转json
		String json = this.toJSON(currentInputs);
		
		return this.executeRemote.executePolicy(policyid, json); 
	}
	
	/**
	 * 该方法直接执行动态脚本。主要用于对以编写的脚本进行测试，确定脚本内容本身是可以正常工作的<p>
	 * 
	 * 使用该方法请注意，由于向服务器的通讯依靠JSON结构完成，但是因为是执行动态脚本，所以并不知道返回的score属性携带那些类型
	 * 所以这里返回最原始的JSON信息，由调用这自己根据实际情况完成转换<p>
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 * 
	 * @param expression 需要执行的动态代码内容
	 * @param scriptLanguage 脚本语言类型，目前支持groovy和Python
	 * @param inputs 入参，在动态代码内容中反映成全局变量
	 * @return
	 */
	public  ExecuteHttpResponse executeRemote(String expression , ScriptLanguageType scriptLanguage , Map<String, Object> inputs) {
		/*
		 * 直接执行一个expression，实际上就是将这些输入信息封装成一个ExecuteScriptRequest并请求远程执行
		 * */
		if(StringUtils.isEmpty(expression)) {
			throw new IllegalArgumentException("expression  must not be empty!");
		} 
		ScriptLanguageType currentScriptLanguage;
		if(scriptLanguage == null) {
			currentScriptLanguage = ScriptLanguageType.LANGUAGE_GROOVY;
		} else {
			currentScriptLanguage = scriptLanguage;
		}
		Map<String, Object> currentInputs; 
		if(inputs == null) {
			currentInputs = Collections.emptyMap();
		} else {
			currentInputs = inputs;
		}
		
		// 构造对象
		ExecuteScriptRequest scriptRequest = new ExecuteScriptRequest();
		scriptRequest.setExpression(expression);
		scriptRequest.setInputs(currentInputs);
		scriptRequest.setScriptLanguage(currentScriptLanguage);
		
		// 拆解成json
		String json = this.toJSON(scriptRequest);
		
		// 执行并返回
		return this.executeRemote.executeScript(json);
	}
	
	/**
	 * 该方法用于执行一个rule下设定的动态脚本信息，通过传入的inputs入参信息<br>
	 * 这种方式下存在于远程的rule将被查询到本地进行运行<br>
	 * 并且在allowInverse属性设置为true的前提下，支持groovy脚本对java的反调
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 */
	public ExecutionRuleModel executeRule(String ruleId , Map<String, Object> inputs) {
		RuleModel rule = this.getRule(ruleId);
		
		// 开始本地执行
		ExecutionRuleModel result = this.ruleEngineFramework.executeRule(rule, inputs);
		return result;
	}
	
	/**
	 * 该方法用于执行指定的policy，要成功执行这个方法首先您必须确认policyId对应的policy信息已经在数据持久层保存成功了
	 * 这种方式下存在于远程的rule将被查询到本地进行运行<br>
	 * 并且在allowInverse属性设置为true的前提下，支持groovy脚本对java的反调
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 */
	public ExecutionPolicyModel executePolicy(String policyid , Map<String, Object> inputs) {
		if(StringUtils.isEmpty(policyid)) {
			return null;
		}
		PolicyModel policy = this.getPolicy(policyid);
		
		/*
		 * 执行情况分为：
		 * 1、执行policy下绑定的若干个rule信息
		 * 2、执行policy下本身的动态脚本信息
		 * */
		// 如果条件成立，则是情况二、否则是情况一
		ExecutionPolicyModel result;
		List<PolicyStepModel> policySteps = policy.getExecution();
		if(policySteps == null || policySteps.isEmpty()) {
			String expression = policy.getScoreExpression();
			if(StringUtils.isEmpty(expression)) {
//				throw new ExpressionNotFound("not found policy expression (policy id : " + policy.getPolicyId() + ")");
			}
			result = this.ruleEngineFramework.executePolicy(policy, null, inputs);
		} else {
			// 根据ruleids查询所有的rule信息
			List<String> ruleids = policySteps.stream().map(PolicyStepModel::getRuleId).collect(Collectors.toList());
			ExecuteHttpResponse response = this.ruleRemote.retrieveRuleStep(policyid);
			List<RuleModel> rules = this.responseTransfers(response, RuleModel.class);
			 
			/*
			 * 注意由于数据库中使用in关键字进行查询，所以查询出来的结果顺序和policySteps要求的结果顺序可能不一致。
			 * 而且policySteps中可能连续标注了同一个rule需要执行多次，在ruleModelIters中同一个rule却只记录了一次。
			 * 所以这里需要根据policySteps和ruleModelIters两个集合的情况，进行重排，新的集合记为ruleSteps
			 * */
			List<RuleModel> ruleSteps = new LinkedList<>();
			ruleids.forEach(r -> {
				for(int index = 0 ; index < rules.size() ; index++) {
					RuleModel targerule = rules.get(index);
					if(StringUtils.equals(targerule.getId(), r)) {
						ruleSteps.add(targerule);
						break;
					}
				}
			});
			
			// 开始执行其中的每一个rule
			result = this.ruleEngineFramework.executePolicy(policy, ruleSteps, inputs);
		}
		
		return result;
	}
	
	/**
     * 该方法用于执行正在编辑的policy，该方法用于还未持久化policy对象之前.
     * 并且在allowInverse属性设置为true的前提下，支持groovy脚本对java的反调
     * 
     * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
     */
    public ExecutionPolicyModel executePolicyTry(ExecutePolicyRequest executionPolicy) {
      if(executionPolicy == null) {
        throw new IllegalArgumentException("policy must be input !!");
    }
        ExecutionPolicyModel result;
        /*
         * 执行情况：
         * 1.封装为一个policyModel对象
         * 2.不存在绑定的rule，则执行本身的脚本
         * 3.存在绑定的rule，依次执行rule下的脚本
         */
        PolicyModel policy = new PolicyModel();
        policy.setExecution(executionPolicy.getExecution());
        policy.setScoreExpression(executionPolicy.getExpression());
        policy.setScriptLanguage(executionPolicy.getScriptLanguage());
        policy.setMetadata(executionPolicy.getMetadata());
        policy.setMode(executionPolicy.getMode());
        
        Map<String, Object> inputs = executionPolicy.getInputs();
        List<PolicyStepModel> policySteps = policy.getExecution();
        if(policySteps == null || policySteps.isEmpty()) {
            result = this.ruleEngineFramework.executePolicy(policy, null, inputs);
        }else {
          // 根据ruleids查询所有的rule信息
          List<String> ruleids = policySteps.stream().map(PolicyStepModel::getRuleId).collect(Collectors.toList());
          //TODO 查询出绑定的rule，依次执行
          ExecuteHttpResponse response = null;//this.ruleRemote.retrieveRuleStep(policyid);
          List<RuleModel> rules = this.responseTransfers(response, RuleModel.class);
          /*
          * 注意由于数据库中使用in关键字进行查询，所以查询出来的结果顺序和policySteps要求的结果顺序可能不一致。
          * 而且policySteps中可能连续标注了同一个rule需要执行多次，在ruleModelIters中同一个rule却只记录了一次。
          * 所以这里需要根据policySteps和ruleModelIters两个集合的情况，进行重排，新的集合记为ruleSteps
          * */
         List<RuleModel> ruleSteps = new LinkedList<>();
         ruleids.forEach(r -> {
             for(int index = 0 ; index < rules.size() ; index++) {
                 RuleModel targerule = rules.get(index);
                 if(StringUtils.equals(targerule.getId(), r)) {
                     ruleSteps.add(targerule);
                     break;
                 }
             }
         });
          result = this.ruleEngineFramework.executePolicy(policy, ruleSteps, inputs);
        }
        
        return result;
    }
	
	/**
	 * 该方法直接执行动态脚本。主要用于对以编写的脚本进行测试，确定脚本内容本身是可以正常工作的<p>
	 * 这种方式下存在于远程的rule将被查询到本地进行运行<br>
	 * 并且在allowInverse属性设置为true的前提下，支持groovy脚本对java的反调
	 * 
	 * 返回的JSON信息中包括的字段意义，可参见wiki文档或原始的用户手册
	 * 
	 * @param expression 需要执行的动态代码内容
	 * @param scriptLanguage 脚本语言类型，目前支持groovy和Python
	 * @param inputs 入参，在动态代码内容中反映成全局变量
	 * @return
	 */
	public  ExecutionRuleModel execute(String expression ,  Map<String, Object> inputs) {
		// 这种方式下的本地执行，实际上就是包装成一个rule进行执行
		RuleModel rule = new RuleModel();
		rule.setExpression(expression);
		rule.setRuleId(UUID.randomUUID().toString());
		
		ExecutionRuleModel result = this.ruleEngineFramework.executeRule(rule, inputs);
		return result;
	}
	
	/**
	 * 该方法帮助该类中多个需要object转json的位置，重用代码
	 * @param object
	 * @return
	 */
	private String toJSON(Object object) {
//		String json;
//		try {
//			json = JSONMapper.OBJECTMAPPER.writeValueAsString(object);
//		} catch (JsonProcessingException e) {
//			LOG.error(e.getMessage() , e);
//			throw new RuntimeException(e);
//		}
//		
//		return json;
	  String json;
      json = JSONMapper.OBJECTMAPPER.convertObjectToJson(object);
      return json;
	}
	
	/**
	 * 检查rule的基本信息是否满足新增或者修改操作的要求
	 * @param rule
	 */
	private void checkRule(RuleModel rule) {
		if(rule == null) {
			throw new IllegalArgumentException("rule must be input !!");
		}
		// 检查id
		this.checkRuleId(rule.getRuleId());
		// 检查表达式
		String  expression = rule.getExpression();
		if(StringUtils.isEmpty(expression)) {
			throw new IllegalArgumentException("expression must not be empty!");
		}
		if(expression.length() >= 2000) {
			throw new IllegalArgumentException("expression length must < 2000!");
		}
		// 检查语言种类
		if(rule.getScriptLanguage() == null) {
			throw new IllegalArgumentException("script language must not be selected!");
		}
		// 检查描述信息
		if(rule.getDescription() == null) {
			rule.setDescription("");
		}
		if(rule.getDescription().length() >= 2000) {
			throw new IllegalArgumentException("description length must < 2000!");
		}
	}
	
	/**
	 * 检查ruleId，既是rule的唯一业务编号
	 * @param ruleId
	 */
	private void checkRuleId(String ruleId) {
		if(StringUtils.isEmpty(ruleId)) {
			throw new IllegalArgumentException("ruleId  must not be empty!");
		}
	}
	
	/**
	 * 检查policy的基本信息是否满足新增或者修改操作的要求
	 * @param rule
	 */
	private void checkPolicy(PolicyModel policy) {
		if(policy == null) {
			throw new IllegalArgumentException("policy must be input !!");
		}
		// 检查id
		this.checkPolicyId(policy.getPolicyId());
		// 检查表达式
		String expression = policy.getScoreExpression();
		if(expression == null) {
			policy.setScoreExpression("");
		}
		if(expression != null && expression.length() >= 2000) {
			throw new IllegalArgumentException("expression length must < 2000!");
		}
		// 检查描述信息
		if(policy.getDescription() == null) {
			policy.setDescription("");
		}
		if(policy.getDescription() != null && policy.getDescription().length() >= 2000) {
			throw new IllegalArgumentException("description length must < 2000!");
		}
		
		// 检测可能的元数据
		MetadataModel metadata = policy.getMetadata();
		if(metadata != null) {
			Set<VariableProperty> vars = metadata.getParams();
			if(!CollectionUtils.isEmpty(vars)) {
				vars.forEach(var -> {
					if(StringUtils.isEmpty(var.getName())) {
						throw new IllegalArgumentException("var name must not be empty! please check!");
					}
					if(var.getDescription() == null) {
						var.setDescription("");
					}
				});
			}
		}
	}
	
	/**
	 * 检查policyId，既是policy的唯一业务编号
	 * @param policyId
	 */
	private void checkPolicyId(String policyId) {
		if(StringUtils.isEmpty(policyId)) {
			throw new IllegalArgumentException("policyId  must not be empty!");
		}
	}
	
	/**
	 * 该私有方法用于提取response中的布尔型返回值
	 * @param response
	 * @return
	 */
	private boolean responseTransfer(ExecuteHttpResponse response) {
		// 如果条件成立，说明处理失败了
		if(!StringUtils.equals(response.getStatus(), "200")) {
			LOG.error(response.getMessage() , response.getException());
			throw new RuntimeException(response.getMessage());
		}
		
		String bValue = response.getData().toString();
		return Boolean.parseBoolean(bValue);
	}
	
	/**
	 * 该方法将response中的返回信息按照类型要求构造成一个RuleEngineModel业务模型信息
	 * @param response
	 * @param modelType
	 * @return
	 */
  private <T extends RuleEngineModel> T responseTransfer(ExecuteHttpResponse response, Class<T> modelType) {
    // 如果条件成立，说明处理失败了
    if (!StringUtils.equals(response.getStatus(), "200")) {
      LOG.error(response.getMessage(), response.getException());
      throw new RuntimeException(response.getMessage());
    }
    // 数据信息都是JSON结构
    String responseJson = JSONMapper.OBJECTMAPPER.convertObjectToJson(response.getData());
    return JSONMapper.OBJECTMAPPER.parseJsonToObject(responseJson, modelType);
  }
	
	/**
	 * 该方法将response中的返回信息按照类型要求构造成一个RuleEngineModel业务模型信息<br>
	 * 支持集合转换
	 * @param response
	 * @param modelType
	 * @return
	 */
  private <T extends RuleEngineModel> List<T> responseTransfers(ExecuteHttpResponse response, Class<T> type) {
    // 如果条件成立，说明处理失败了
    if (!StringUtils.equals(response.getStatus(), "200")) {
      LOG.error(response.getMessage(), response.getException());
      throw new RuntimeException(response.getMessage());
    }

    // 数据信息都是JSON结构
    String responseJson = JSONMapper.OBJECTMAPPER.convertObjectToJson(response.getData());
    return JSONMapper.OBJECTMAPPER.parseJsonToList(responseJson, type);
  }
	
	/**
	 * RuleEngineClient采用创建者模式进行进程中唯一对象的创建
	 * @author yinwenjie
	 */
  public static class Builder {
    /**
     * 远程HTTP API的主调用地址<br>
     * 如果没有指定，就是http://localhost 
     * TODO 目前支持单节点，以后在做负载均衡的支持——不依靠Spring Cloud
     */
    private String remoteURL = "http://localhost";

    /**
     * 等待超时时间，默认为500毫秒<br>
     * 各种RuleEngineFramework的实现中，都要求对远程存储/通知介质的写操作过程中保持同步<br>
     * 但是类似zookeeper这样的远程存储/通知介质又不能保证所有通知100%到达，所以需要设置一个等待超时时间<br>
     * 如果这个超时时间到了，就主动向远程存储/通知介质发起一次询问。
     */
    private long waitingTimeout = 500;

    /**
     * 当前RuleEngineFramework执行判断策略时所使用的线程池最大任务数量 默认为50
     */
    private int maxExecutionThread = 50;

    /**
     * 当前RuleEngineFramework执行判断策略时所使用的线程池最小任务数量 默认为10
     */
    private int minExecutionThread = 10;

    /**
     * script执行线程池所标记的线程名称，最主要的作用是在debug和log时，能够清晰标识出线程性质
     */
    private String scriptThreadName = "Script-Thread";

    /**
     * 执行动态脚本使用一个独立线程池，该属性指定这个独立线程池的等待任务队列的大小
     */
    private int scriptQueueSize = 1000;

    /**
     * 该方法指示当前运行的RuleEngine引擎是否支持“反调”<br>
     * 默认为支持，因为要尽量减少RuleEngine规则引擎在本地运行时，开发人员对于RuleEngine的理解深度。
     */
    private boolean allowInverse = true;

    /**
     * 在“反调”功能执行时，用于进行 java object创建/获取的工厂<br>
     * 默认的是一个ScriptReversableDefaultFactory，但这个默认工厂并不支持从Spring IOC容器中获取bean<br>
     * 所以如果您需要在Spring IOC容器中取得对象，就必须自己构建工厂
     */
    private ScriptReversableAbstractFactory scriptReversableAbstractFactory;

    /**
     * @param remoteURL the remoteURL to set
     */
    public Builder setRemoteURL(String remoteURL) {
      this.remoteURL = remoteURL;
      return this;
    }

    /**
     * @return the maxExecutionThread
     */
    public int getMaxExecutionThread() {
      return maxExecutionThread;
    }

    /**
     * @param maxExecutionThread the maxExecutionThread to set
     */
    public Builder setMaxExecutionThread(int maxExecutionThread) {
      this.maxExecutionThread = maxExecutionThread;
      return this;
    }

    /**
     * @return the minExecutionThread
     */
    public int getMinExecutionThread() {
      return minExecutionThread;
    }

    /**
     * @param minExecutionThread the minExecutionThread to set
     */
    public Builder setMinExecutionThread(int minExecutionThread) {
      this.minExecutionThread = minExecutionThread;
      return this;
    }

    /**
     * @param waitingTimeout the waitingTimeout to set
     */
    public Builder setWaitingTimeout(long waitingTimeout) {
      this.waitingTimeout = waitingTimeout;
      return this;
    }

    /**
     * @param allowInverse the allowInverse to set
     */
    public Builder setAllowInverse(boolean allowInverse) {
      this.allowInverse = allowInverse;
      return this;
    }

    /**
     * @return the waitingTimeout
     */
    public long getWaitingTimeout() {
      return waitingTimeout;
    }

    /**
     * @param scriptThreadName the scriptThreadName to set
     */
    public Builder setScriptThreadName(String scriptThreadName) {
      this.scriptThreadName = scriptThreadName;
      return this;
    }

    /**
     * @param scriptQueueSize the scriptQueueSize to set
     */
    public Builder setScriptQueueSize(int scriptQueueSize) {
      this.scriptQueueSize = scriptQueueSize;
      return this;
    }

    /**
     * @return the remoteURL
     */
    public String getRemoteURL() {
      return remoteURL;
    }

    /**
     * @return the scriptThreadName
     */
    public String getScriptThreadName() {
      return scriptThreadName;
    }

    /**
     * @return the scriptQueueSize
     */
    public int getScriptQueueSize() {
      return scriptQueueSize;
    }

    /**
     * @return the allowInverse
     */
    public boolean getAllowInverse() {
      return allowInverse;
    }

    public ScriptReversableAbstractFactory getScriptReversableAbstractFactory() {
      return scriptReversableAbstractFactory;
    }

    public void setScriptReversableAbstractFactory(
        ScriptReversableAbstractFactory scriptReversableAbstractFactory) {
      this.scriptReversableAbstractFactory = scriptReversableAbstractFactory;
    }

    /**
     * 调用此方法开始创建过程
     * 
     * @return
     */
    public RuleEngineClient build() {
      return RuleEngineClient.getNewInstance(this);
    }
  }
}
