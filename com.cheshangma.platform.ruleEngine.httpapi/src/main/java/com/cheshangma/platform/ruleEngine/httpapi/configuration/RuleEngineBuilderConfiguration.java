package com.cheshangma.platform.ruleEngine.httpapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cheshangma.platform.ruleEngine.core.framework.RuleEngineFramework;
import com.cheshangma.platform.ruleEngine.core.service.ServiceAbstractFactory;

/**
 * rule engine framework初始化
 * 
 * @author yinwenjie
 */
@Configuration
public class RuleEngineBuilderConfiguration {

  @Value("${ruleengine.maxExecutionThread}")
  private String maxExecutionThread;

  @Value("${ruleengine.minExecutionThread}")
  private String minExecutionThread;

  @Value("${ruleengine.scriptQueueSize}")
  private String scriptQueueSize;

  @Bean
  @Autowired
  public RuleEngineFramework getApplicationContext(ServiceAbstractFactory serviceAbstractFactory) {

    // 开始初始化，在初始化完成前，其它各个使用RuleEngineFramework的代码层，都会被锁定
    RuleEngineFramework.Builder builder = RuleEngineFramework.Builder.getInstanceBuilder();
    builder.setAllowInverse(false)
        .setMaxExecutionThread(Integer.parseInt(maxExecutionThread))
        .setMinExecutionThread(Integer.parseInt(minExecutionThread))
        .setScriptQueueSize(Integer.parseInt(scriptQueueSize))
        .setServiceAbstractFactory(serviceAbstractFactory);

    return builder.buildIfAbent();
  }
}
