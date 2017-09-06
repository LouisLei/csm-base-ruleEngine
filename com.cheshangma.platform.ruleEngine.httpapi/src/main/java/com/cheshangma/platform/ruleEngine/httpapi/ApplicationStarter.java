package com.cheshangma.platform.ruleEngine.httpapi;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 启动spring boot
@SpringBootApplication
@EnableFeignClients
//启动swagger配置
@EnableSwagger2
// 启动spring缓存组件（基于redis）
@EnableCaching
// 启动spring boot服务注册客户端
@EnableDiscoveryClient
public class ApplicationStarter {
  public static void main(String[] args) throws Exception {
    new SpringApplicationBuilder(ApplicationStarter.class).web(true).run(args);
  }
}
