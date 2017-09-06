package com.cheshangma.platform.ruleEngine.core.executor;

/**
 * 这是一个ScriptReversableAbstractFactory抽象工厂的默认实现，用于进行对象的反射初始化。<br>
 * 在spring工程中不建议使用这个默认实现，建议自行实现从Spring IOC容器中加载对象。
 * @author yinwenjie
 */
public class ScriptReversableDefaultFactory extends ScriptReversableAbstractFactory {

	/* (non-Javadoc)
	 * @see com.dianrong.morpheus.core.executor.ScriptReversableAbstractFactory#buildReversableBean(java.lang.String)
	 */
	@Override
	public ScriptInvokerReversable buildReversableBean(String component) {
		// TODO 代码未做
		return null;
	}
}