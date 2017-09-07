package com.cheshangma.platform.ruleEngine.httpmodule;

/**
 * 这个视图层对象，主要用于以一个标准格式向HTTP API调用者返回调用结果
 * @author yinwenjie
 */
public class ExecuteHttpResponse {
	/**
	 * 一个时间戳，主要为了避免返回信息受客户端缓存影响
	 */
	private Long timestamp;
	/**
	 * 这个属性用于向调用者返回正式的内容，例如如果调用者是对某个policy进行添加，那么这里就是添加后的policy信息<br>
	 * 再例如，如果调用者是对rule进行检索，这里就是符合条件的检索结果
	 */
	private Object data;
	/**
	 * 这个属性和http status对应，例如正常情况下就是200，例如出现服务端逻辑错误就是500……
	 */
	private String status;
	/**
	 * Java中的异常类型，如果没有对应的异常类型（或者异常类型已废弃），就是一个字符串描述
	 */
	private String exception;
	/**
	 * 这里是具体的错误信息
	 */
	private String message;
	
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the error
	 */
	public String getException() {
		return exception;
	}
	/**
	 * @param error the error to set
	 */
	public void setException(String exception) {
		this.exception = exception;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}