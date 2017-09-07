package com.cheshangma.platform.ruleEngine.httpmodule;

import java.util.Map;

import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;

/**
 * 这个类用于描述一个正要测试的脚本信息
 * @author yinwenjie
 */
public class ExecuteScriptRequest {
	/**
	 * 携带的参数信息。参数信息就是多个K-V数据信息
	 */
	private Map<String, Object> inputs;
	/**
	 * 脚本语言内容
	 */
	private String expression;
	/**
	 * 脚本语言类型，目前支持groovy、python和javaScript
	 */
	private ScriptLanguageType scriptLanguage;
	/**
	 * @return the inputs
	 */
	public Map<String, Object> getInputs() {
		return inputs;
	}
	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(Map<String, Object> inputs) {
		this.inputs = inputs;
	}
	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}
	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}
	/**
	 * @return the scriptLanguage
	 */
	public ScriptLanguageType getScriptLanguage() {
		return scriptLanguage;
	}
	/**
	 * @param scriptLanguage the scriptLanguage to set
	 */
	public void setScriptLanguage(ScriptLanguageType scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}
}