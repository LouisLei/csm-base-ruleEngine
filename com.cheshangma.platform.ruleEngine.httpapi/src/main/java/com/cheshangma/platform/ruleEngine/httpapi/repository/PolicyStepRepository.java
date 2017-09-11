package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.repository.CrudRepository;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.PolicyStepEntity;

/**
 * 策略执行步骤相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月8日 下午3:11:53
 * @version V1.0
 */
public interface PolicyStepRepository extends CrudRepository<PolicyStepEntity, String> {

}
