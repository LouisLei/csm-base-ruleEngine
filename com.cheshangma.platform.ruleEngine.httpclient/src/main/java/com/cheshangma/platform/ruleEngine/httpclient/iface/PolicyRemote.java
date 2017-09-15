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
public interface PolicyRemote {
	
	@RequestLine("POST /v2/policy")
	@Headers({"Content-Type: application/json" })
	@Body("{policy}")
	public ExecuteHttpResponse createPolicy(@Param("policy") String policy) ;
	
	@RequestLine("PATCH /v2/policy")
	@Headers({"Content-Type: application/json" })
	@Body("{policy}")
	public ExecuteHttpResponse upsertPolicy(@Param("policy") String policy) ;
	
	@RequestLine("DELETE /v2/policy/{policyId}")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse delete(@Param("policyId") String policyId) ;
	
	@RequestLine("POST /v2/policy/enable/{policyId}")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse enable(@Param("policyId") String policyId) ;
	
	@RequestLine("POST /v2/policy/disable/{policyId}")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse disable(@Param("policyId") String policyId) ;
	
	@RequestLine("GET /v2/policy/check/{policyId}")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse check(@Param("policyId") String policyId) ;
	
	@RequestLine("GET /v2/policy/retrieve")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse retrieve() ;
	
	@RequestLine("GET /v2/policy/retrieveOne/{policyId}")
	@Headers({"Content-Type: application/json" })
	public ExecuteHttpResponse retrieveOne(@Param("policyId") String policyId);
}