package com.cheshangma.platform.ruleEngine.module;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 该类应该是数据层某些数据模型的基类，又来记录这些数据模型共同的属性<br>
 * 例如id、description等信息
 * @author yinwenjie
 */
public abstract class UUIDModel implements Serializable, MorpheusModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4625678068265632064L;
    /**
     * 抽象业务模型的编号信息
     */
    private String id;
    /**
     * 描述信息
     */
    private String description = "";
    /**
     * 创建者
     */
    private String creator = "";
    /**
     * 创建时间
     */
    private Date created = new Date();

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}