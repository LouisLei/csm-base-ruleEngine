package com.cheshangma.platform.ruleEngine.module;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 具有元数据属性的业务模型类。该类被policy和rule所使用
 * @author yinwenjie
 */
public class MetadataModel implements MorpheusModel {
	
	/**
	 * 元数据中，可以带有若干个输出信息，每个信息都是一个VariableProperty对象
	 */
	private Set<VariableProperty> params;
	
	/**
	 * @return the params
	 */
	public Set<VariableProperty> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Set<VariableProperty> params) {
		this.params = params;
	}

	public static class VariableProperty implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8004584464712388739L;
		/**
		 * 元数据属性名
		 */
		private String name;
		/**
		 * 元数据属性值
		 */
		private Object value;
		/**
		 * 元数据描述信息
		 */
		private String description;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(Object value) {
			this.value = value;
		}
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			/*
			 * 重写hashCode方法，对于一个VariableProperty来说，
			 * 只要name相同，其hashcode就是相同的
			 * */
			final int initialOddNumber = 17;
			final int multiplierOddNumber = 37;
			return new HashCodeBuilder(initialOddNumber, multiplierOddNumber).append(this.name).toHashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			/*
			 * 重写比较方法，对于VariableProperty来说 只要名字相同，就表示两个属性是相同的
			 */
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}

			VariableProperty variableProperty = (VariableProperty) obj;
			return new EqualsBuilder().append(this.name, variableProperty.name).isEquals();
		}
	}
}
