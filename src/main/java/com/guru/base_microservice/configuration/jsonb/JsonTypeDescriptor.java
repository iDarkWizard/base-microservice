package com.guru.base_microservice.configuration.jsonb;

import java.util.Properties;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;

import com.guru.base_microservice.service.JacksonUtils;

public class JsonTypeDescriptor extends AbstractTypeDescriptor<Object> implements DynamicParameterizedType {

	private static final long serialVersionUID = 1L;

	private Class<?> jsonObjectClass;

	@Override
	public void setParameterValues(Properties parameters) {
		jsonObjectClass = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();

	}

	@SuppressWarnings("serial")
	public JsonTypeDescriptor() {
		super(Object.class, new MutableMutabilityPlan<Object>() {
			@Override
			protected Object deepCopyNotNull(Object value) {
				return JacksonUtils.clone(value);
			}
		});
	}

	@Override
	public boolean areEqual(Object one, Object another) {
		if (one == another) {
			return true;
		}
		if (one == null || another == null) {
			return false;
		}
		return JacksonUtils.toJsonNode(JacksonUtils.toString(one)).equals(JacksonUtils.toJsonNode(JacksonUtils.toString(another)));
	}

	@Override
	public String toString(Object value) {
		return JacksonUtils.toString(value);
	}

	@Override
	public Object fromString(String string) {
		return JacksonUtils.fromString(string, jsonObjectClass);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
		if (value == null) {
			return null;
		}
		if (String.class.isAssignableFrom(type)) {
			return (X) toString(value);
		}
		if (Object.class.isAssignableFrom(type)) {
			return (X) JacksonUtils.toJsonNode(toString(value));
		}
		throw unknownUnwrap(type);
	}

	@Override
	public <X> Object wrap(X value, WrapperOptions options) {
		if (value == null) {
			return null;
		}
		return fromString(value.toString());
	}

}
