package com.cj.cn.util;

import com.cj.cn.pojo.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //设置参与序列化与反序列化的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //忽略空对象转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //统一设置JDK8新的时间类的序列化和反序列化格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(LocalDateTimeUtil.DATE_FORMAT);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        objectMapper.registerModule(javaTimeModule);

        //忽略在json字符串中存在, 但是再java对象中不存在对应属性的情况, 防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    //将对象转换成json字符串
    public static <T> String objectToJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.info("Parse object to json error", e);
            return null;
        }
    }

    //将json字符串转化为对象
    public static <T> T jsonToObject(String json, Class<T> objectType) {
        if (StringUtils.isBlank(json) || objectType == null) {
            return null;
        }
        try {
            return objectType.equals(String.class) ? (T) json : objectMapper.readValue(json, objectType);
        } catch (JsonProcessingException e) {
            log.info("Parse json to object error", e);
            return null;
        }
    }

    //将json字符串转化为对象(用在多泛型的时候)
    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json) || typeReference == null) {
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T) json : objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.info("Parse json to object error", e);
            return null;
        }
    }

    //将json字符串转化为对象(用在多泛型的时候)
    public static <T> T jsonToObject(String json, Class<?> collectionClass, Class<?>... elementClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.info("Parse json to object error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user1 = new User().setId(1).setUsername("chenjie").setUpdateTime(LocalDateTime.now());
        User user2 = new User().setId(2).setUsername("caichenzhe").setUpdateTime(LocalDateTime.now());
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        String json = objectToJson(list);
        System.out.println(json);

        List<User> delist1 = jsonToObject(json, new TypeReference<List<User>>() {
        });
        List<User> delist2 = jsonToObject(json, List.class, User.class);
        System.out.println(delist1);
        System.out.println(delist2);
    }
}
