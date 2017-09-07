package com.cheshangma.platform.ruleEngine.httpclient.iface;

import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * 该feign接口定义，不再加注释了。如果要查看具体的业务功能，请参见《Morpheus2用户手册》
 * @author yinwenjie
 */
public interface ExecuteRemote {
	
	@RequestLine("POST /v2/execute/rule/{ruleId}")
	@Headers({"Content-Type: application/json" })
	@Body("{inputs}")
	public ExecuteHttpResponse executeRule(@Param("ruleId") String ruleId , @Param("inputs") String inputs) ;
	
	@RequestLine("POST /v2/execute/policy/{policyId}")
	@Headers({"Content-Type: application/json" })
	@Body("{inputs}")
	public ExecuteHttpResponse executePolicy(@Param("policyId") String policyId , @Param("inputs") String inputs) ;
	
	@RequestLine("POST /v2/execute/policy/try")
	@Headers({"Content-Type: application/json" })
	@Body("{policyRequest}")
	public ExecuteHttpResponse executeScript(@Param("policyId") String policyRequest) ;
}