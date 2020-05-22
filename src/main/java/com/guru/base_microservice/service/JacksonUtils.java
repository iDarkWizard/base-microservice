package com.guru.base_microservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {

	public static final ObjectMapper mapper = new ObjectMapper();

	public static <T> T fromString(String string, Class<T> clazz) {
		try {
			return mapper.readValue(string, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"The given string value: " + string + " cannot be transformed to Json object");
		}
	}

	public static String toString(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(
					"The given Json object value: " + value + " cannot be transformed to a String");
		}
	}

	public static JsonNode toJsonNode(String value) {
		try {
			return mapper.readTree(value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T value) {
		return fromString(toString(value), (Class<T>) value.getClass());
	}

	public static <T> T convertValue(Object from, Class<T> toClass) throws RuntimeException {
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.convertValue(from, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			if (from != null) {
				throw new RuntimeException(
						"Error al intentar mappear un objeto de la clase " + from.getClass() + " a la clase " + toClass,
						e);
			} else {
				throw new RuntimeException("Error al intentar mappear un objeto a la clase " + toClass, e);
			}
		}
	}

	public static String convertValue(Object from) throws RuntimeException {
		try {
			return mapper.writeValueAsString(from);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error al intentar mappear un objeto de la clase " + from.getClass() + " a JSON",
					e);

		}
	}

	public static <T> List<T> convertValue(List<T> from, Class<T> toClass) throws RuntimeException {
		try {
			List<T> list = new ArrayList<>();
			for (Object element : from) {

				list.add(convertValue(element, toClass));
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(
					"Error al intentar mappear un objeto de la clase " + from.getClass() + "a la clase " + toClass, e);
		}
	}
}
