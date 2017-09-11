package com.cheshangma.platform.ruleEngine.httpapi.repository;

import org.springframework.data.repository.CrudRepository;

import com.cheshangma.platform.ruleEngine.httpapi.repository.entity.VariablePropertyEntity;

/**
 * 元数据相关的CRUD操作接口.
 * 
 * @author ly
 * @date 2017年9月8日 下午3:08:42
 * @version V1.0
 */
public interface VariablePropertyRepository extends CrudRepository<VariablePropertyEntity, String> {

}
