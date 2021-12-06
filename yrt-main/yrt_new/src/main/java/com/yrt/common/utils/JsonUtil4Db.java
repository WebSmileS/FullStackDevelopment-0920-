package com.yrt.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yrt.common.dict.BaseIntEnum;

/**
 * generate json for db produces.
 * <p>
 * Enum to int(BaseIntEnum.value() / enum.ordinal() )
 * 
 * @see BaseIntEnum
 */
public class JsonUtil4Db {
	
	public static String toJson(Object obj) throws JsonException {
		return toJson(obj, mapper);
	}

	private static String toJson(Object obj, ObjectMapper jsonMapper) throws JsonException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		try {
			String s = jsonMapper.writeValueAsString(obj);
			return s;
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	private static final ObjectMapper mapper = configMapper();

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static ObjectMapper configMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);//
		// NON_NULL

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule module = new SimpleModule();
		module.addSerializer(Enum.class, new IntEnumSerializer());
		objectMapper.registerModule(module);

		objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
		return objectMapper;
	}

	@SuppressWarnings("rawtypes")
	public static class IntEnumSerializer extends JsonSerializer<Enum> {
		public void serialize(Enum value, JsonGenerator generator, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			generator.writeRawValue(String.valueOf(BaseIntEnum.value(value)));
		}
	}

	public static class JsonException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JsonException(Throwable cause) {
			super(cause);
		}

	}

}
