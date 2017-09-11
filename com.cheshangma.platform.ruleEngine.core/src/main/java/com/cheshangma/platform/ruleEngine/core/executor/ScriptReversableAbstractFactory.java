package com.cheshangma.platform.ruleEngine.core.executor;

/**
 * 由于动态脚本执行过程中进行的本地java代码的“反调”过程，需要java中具体的对象进行处理<br>
 * 但是作为Morpheus2 规则引擎来说，又不可能知晓集成它的业务系统的具体情况<p>
 * 
 * 例如，继承了Spring组件的工程，可能需要从IOC容器中得到具体的java对象<br>
 * 普通系统可能只需要根据类全名进行初始化。<p>
 * 
 * 所以每当需要进行java中具体对象初始化时，该工厂中的buildReversableBean方法就会被通知到。以便业务端的开发人员能够完成java中对象的初始化过程<p>
 * 不过，ScriptReversableAbstractFactory还是提供了一个默认实现ScriptReversableDefaultFactory，用于进行java对象的反射生成。
 * @see ScriptReversableDefaultFactory
 * @author yinwenjie
 */
public abstract class ScriptReversableAbstractFactory {
	/**
	 * 该方法在Morpheus core executor执行模块需要根据java完整类名或者类的唯一编号初始化对象，并完成动态脚本“反调时”，被激活。<br>
	 * @param component 在动态脚本中指定的需要调用的java的类名或者spring工程中设置的component的名字
	 * @return 
	 */
	public abstract ScriptInvokerReversable buildReversableBean(String component);
}