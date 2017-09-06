package com.cheshangma.platform.ruleEngine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JSONMapper {
	public static final ObjectMapper OBJECTMAPPER = new ObjectMapper();
	
	static {
		// 支持日期格式
		OBJECTMAPPER.registerModule(new JavaTimeModule());
		OBJECTMAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
	
	/**
	 * 工具类不允许实例化
	 */
	private JSONMapper() {
		
	}
}