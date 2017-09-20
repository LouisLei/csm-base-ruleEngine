package com.cheshangma.platform.ruleEngine.httpmodule;

import java.util.List;
import java.util.Map;

import com.cheshangma.platform.ruleEngine.enums.PolicyModeType;
import com.cheshangma.platform.ruleEngine.enums.ScriptLanguageType;
import com.cheshangma.platform.ruleEngine.module.MetadataModel;
import com.cheshangma.platform.ruleEngine.module.PolicyStepModel;

/**
 * 这个类用于描述一个正要测试的policy脚本信息<br>
 * 需要注意一个问题：这个policy的测试描述，只适合运行policy下已有rule绑定的场景。<br>
 * 如果是要请求测试某个policy下本身的脚本运行效果，则可以采用ExecuteRuleRequest描述请求<br>
 * @see ExecuteScriptRequest
 * @author yinwenjie
 */
public class ExecutePolicyRequest {
	/**
	 * 携带的参数信息。参数信息就是多个K-V数据信息
	 */
	private Map<String, Object> inputs;
	/**
	 * 策略的运行模式
	 */
	private PolicyModeType mode = PolicyModeType.RULEMODE_SIMPLE;
	/**
	 * 元数据信息
	 */
	private MetadataModel metadata;
	/**
	 * 规则步骤，一个策略中可以有多个规则。 
	 * 通过这个PolicyStepModule对象，可以将这些规则排列起来
	 */
	private List<PolicyStepModel> execution;
	
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
	 * @return the mode
	 */
	public PolicyModeType getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(PolicyModeType mode) {
		this.mode = mode;
	}
	/**
	 * @return the metadata
	 */
	public MetadataModel getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(MetadataModel metadata) {
		this.metadata = metadata;
	}
	/**
	 * @return the execution
	 */
	public List<PolicyStepModel> getExecution() {
		return execution;
	}
	/**
	 * @param execution the execution to set
	 */
	public void setExecution(List<PolicyStepModel> execution) {
		this.execution = execution;
	}
	/**
     * 脚本语言内容
     */
    private String expression;
    /**
     * 脚本语言类型，目前支持groovy、python和javaScript
     */
    private ScriptLanguageType scriptLanguage;
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