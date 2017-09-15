package com.cheshangma.platform.ruleEngine.httpclient.iface;

import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * 该feign接口定义，不再加注释了。如果要查看具体的业务功能，请参见《RuleEngine用户手册》
 * @author yinwenjie
 */
public interface RuleRemote {
	@RequestLine("POST /v2/rule")
	@Headers({"Content-Type: application/json"})
	@Body("{rule}")
	public ExecuteHttpResponse createRule(@Param("rule") String rule);
	
	@RequestLine("PATCH /v2/rule")
	@Headers({"Content-Type: application/json"})
	@Body("{rule}")
	public ExecuteHttpResponse upsertRule(@Param("rule") String rule);

	@RequestLine("POST /v2/rule/bind/{policyId}/{ruleIds}")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse bindRule(@Param("policyId") String policyId , @Param("ruleIds") String ruleIds);
	
	@RequestLine("POST /v2/rule/unbind/{policyId}/{ruleIds}")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse unbindRule(@Param("policyId") String policyId , @Param("ruleIds") String ruleIds);
	
	@RequestLine("DELETE /v2/rule/{ruleId}")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse delete(@Param("ruleId") String ruleId);
	
	@RequestLine("GET /v2/rule/retrieve/")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse retrieveAllRule();
	
	@RequestLine("GET /v2/rule/retrieve/{policyId}")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse retrieveRuleStep(@Param("policyId") String policyId);
	
	@RequestLine("GET /v2/rule/retrieveOne/{ruleId}")
	@Headers({"Content-Type: application/json"})
	public ExecuteHttpResponse retrieveOne(@Param("ruleId") String ruleId);
}