package com.cheshangma.platform.ruleEngine.httpapi.controller;

import java.util.Date;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.httpmodule.ExecuteHttpResponse;

/**
 * controller层的基本父类，里面封装了一些共用的方法
 * 
 * @author yinwenjie
 */
public class BasicController {
  /**
   * 该方法用于获取一个已经可以使用的MorpheusFramework
   * 
   * @return
   */
  protected RuleEngineFramework getMorpheusFramework() {
    RuleEngineFramework.Builder builder = RuleEngineFramework.Builder.getInstanceBuilder();
//    RuleEngineFramework framework = builder.waitUntilBuilded();
    // TODO 还不可用
    return null;
  }

  /**
   * 组装一个成功的处理相应描述，准备发送给页面。
   * 
   * @param data
   * @return
   */
  protected ExecuteHttpResponse sendSuccessResponse(Object data) {
    ExecuteHttpResponse rs = new ExecuteHttpResponse();
    rs.setTimestamp(new Date().getTime());
    rs.setStatus("200");
    rs.setData(data);
    return rs;
  }

  /**
   * 组装一个处理失败的异常描述信息，准备发送给页面
   * 
   * @param httpStatus
   * @param exception
   */
  protected ExecuteHttpResponse sendErrorResponse(String httpStatus, Exception exception) {
    ExecuteHttpResponse rs = new ExecuteHttpResponse();
    rs.setTimestamp(new Date().getTime());
    rs.setStatus(httpStatus);
    rs.setMessage(exception.getMessage());
    rs.setException(exception.getClass().getName());
    return rs;
  }
}
