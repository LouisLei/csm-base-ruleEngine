package com.cheshangma.platform.ruleEngine.core.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONMapper {

  /**
   * 唯一一个工具类的实例
   */
  public static final JSONMapper OBJECTMAPPER = new JSONMapper();

  private ObjectMapper objectMapper;

  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(JSONMapper.class);

  /**
   * 工具类不允许实例化
   */
  private JSONMapper() {
    objectMapper = new ObjectMapper();
    // 去掉默认的时间戳格式
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    // 设置为中国上海时区
    objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    // 空值不序列化
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    // 反序列化时，属性不存在的兼容处理
    objectMapper.getDeserializationConfig()
        .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    // 序列化时，日期的统一格式
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 单引号处理
    objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
  }

  /**
   * 把对象转换成为Json字符串
   * @param obj
   * @return
   */
  public String convertObjectToJson(Object obj) {
    if (obj == null) {
      throw new IllegalArgumentException("对象参数不能为空。");
    }
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * 把json字符串转成Object对象
   * @param jsonString
   * @return T
   */
  public <T> T parseJsonToObject(String jsonString, Class<T> valueType) {
    if (jsonString == null || "".equals((jsonString))) {
      return null;
    }
    try {
      return objectMapper.readValue(jsonString, valueType);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * 把json字符串转成List对象
   * @param jsonString
   * @return List<T>
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> parseJsonToList(String jsonString, Class<T> valueType) {
    if (jsonString == null || "".equals((jsonString))) {
      return null;
    }
    List<T> result = new ArrayList<T>();
    try {
      List<LinkedHashMap<Object, Object>> list = objectMapper.readValue(jsonString, List.class);
      for (LinkedHashMap<Object, Object> map : list) {
        String jsonStr = convertObjectToJson(map);
        T t = parseJsonToObject(jsonStr, valueType);
        result.add(t);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      throw new IllegalArgumentException(e.getMessage(), e);
    }
    return result;
  }
}
