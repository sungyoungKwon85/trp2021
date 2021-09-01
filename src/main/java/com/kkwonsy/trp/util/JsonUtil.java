package com.kkwonsy.trp.util;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {

    public static ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public static String toJsonString(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }

    public static <T> T toObject(String jsonResult, Class<T> classType) throws JsonProcessingException {
        if (!StringUtils.hasText(jsonResult)) {
            return null;
        }
        return mapper.readValue(jsonResult, classType);
    }
}
