package com.cheshangma.platform.ruleEngine.core.exception;

import groovy.util.ScriptException;

/**
 * 拒绝反调操作<br>
 * 这个异常一般出现在，动态代码中存在对本地java代码的“反调”操作，但是在Morpheus引擎初始化的时候，却又拒绝了反调操作
 * @author yinwenjie
 */
public class ScriptInverseRejectException extends ScriptException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5709367817625520520L;
	
	public ScriptInverseRejectException(String errorMsg) {
		super(errorMsg);
	}
}