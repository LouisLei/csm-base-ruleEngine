package com.cheshangma.platform.ruleEngine.core.executor;

/**
 * TODO 未写注释
 * @author yinwenjie
 */
public class ScriptInverseRunner {
	
	private static ScriptInverseRunner scriptInverseRunner;	
	
//	private static ScriptReversableAbstractFactory scriptReversableFactory;
	
	public static void initReversableFactory(ScriptReversableAbstractFactory scriptReversableFactory) {
//		ScriptInverseRunner.scriptReversableFactory = scriptReversableFactory;
	}

	public static ScriptInverseRunner getNewInstance() {
		if(ScriptInverseRunner.scriptInverseRunner != null) {
			return ScriptInverseRunner.scriptInverseRunner;
		}
		
		synchronized (ScriptInverseRunner.class) {
			while(ScriptInverseRunner.scriptInverseRunner == null) {
				ScriptInverseRunner.scriptInverseRunner = new ScriptInverseRunner();
				return ScriptInverseRunner.scriptInverseRunner;
			}
		}
		return ScriptInverseRunner.scriptInverseRunner;
	}
	
	private ScriptInverseRunner() {
		// TODO 代码还未做
	}
	
	/**
	 * 开始进行“反调”操作<br>
	 * componentAndMethod 传入的参数是java完整类型/spring bean名称 + 方法名<br>
	 * 举例如下：<br>
	 * 
	 * springcomponent.method1<br>
	 * package1.name.ClassName.method2<br>
	 * a.b.c.ClassName.method2
	 * 
	 * @return 如果反调过程有返回值，则会通过这里返回到动态脚本执行过程中。如果回调没有返回值，则返回null
	 */
	public Object inverse(String componentAndMethod) {
		return null;
	}
}