/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.util;

import com.google.gson.*;
import org.springframework.util.Base64Utils;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.Base64;

/**
 * @author wangyongjun
 * @date 2019/5/14.
 */
public class GsonUtil {

    private static final JsonParser PARSER = new JsonParser();

    private static Gson gson;

    static {
        // 支撑GsonUtil运作的通用Gson对象
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    /**
     * 给GsonBuilder注册byte[]与Base64之间的序列化与反序列化适配器
     *
     * @param gsonBuilder 要设置的GsonBuilder
     * @return GsonBuilder
     */
    public static GsonBuilder withBase64EncodedByteArrays(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(byte[].class, new Base64GsonTypeAdapter());
        return gsonBuilder;
    }

    /**
     * 设置GsonBuilder允许序列化空值
     *
     * @param gsonBuilder 要设置的GsonBuilder
     * @return GsonBuilder
     */
    public static GsonBuilder serializeNulls(GsonBuilder gsonBuilder) {
        return gsonBuilder.serializeNulls();
    }

    /**
     * 设置GsonBuilder排除某些类型
     *
     * @param gsonBuilder 要设置的GsonBuilder
     * @param fieldClazzs 被排除的类型
     * @return GsonBuilder
     */
    public static GsonBuilder exclude(GsonBuilder gsonBuilder, Class... fieldClazzs) {
        return gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                for (Class fieldClazz : fieldClazzs) {
                    if (clazz.equals(fieldClazz)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 设置GsonBuilder排除某些字段
     *
     * @param gsonBuilder 要设置的GsonBuilder
     * @param fieldNames  被排除的字段名
     * @return GsonBuilder
     */
    public static GsonBuilder exclude(GsonBuilder gsonBuilder, String... fieldNames) {
        return gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                for (String fieldName : fieldNames) {
                    if (f.getName().equals(fieldName)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazzs) {
                return false;
            }
        });
    }

    public static <T> String toJson(T obj) {
        String jsonStr;
        try {
            jsonStr = gson.toJson(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonStr;
    }

    public static <T> T fromJson(String json, Class<T> type) {
        T obj;
        try {
            obj = gson.fromJson(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static JsonElement fromJson(String json) {
        return PARSER.parse(json);
    }

    public static Gson getGson() {
        return gson;
    }

    /*************************************预制一些常用类型的TypeAdapter****************************************/

    public static class ByteBufferGsonTypeAdapter implements JsonDeserializer<ByteBuffer>, JsonSerializer<ByteBuffer> {

        @Override
        public JsonElement serialize(ByteBuffer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src.array()));
        }

        @Override
        public ByteBuffer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            byte[] bytes = Base64.getDecoder().decode(json.toString());
            return ByteBuffer.wrap(bytes);
        }

    }

    public static class ByteArrayGsonTypeAdapter implements JsonDeserializer<byte[]>, JsonSerializer<byte[]> {

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }

        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getDecoder().decode(json.toString());
        }

    }

    public static class Base64GsonTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64Utils.encodeToString(src));
        }

        @Override
        public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext cxt) {
            return Base64Utils.decodeFromString(json.toString());
        }
    }

    public static class InstantGsonTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Instant.parse(json.toString());
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class LocalDateTimeGsonTypeAdapter implements JsonSerializer<LocalDateTime>,
            JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.toString());
        }

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class LocalDateGsonTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.toString());
        }

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class LocalTimeGsonTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

        @Override
        public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalTime.parse(json.toString());
        }

        @Override
        public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class OffsetDateTimeGsonTypeAdapter implements JsonSerializer<OffsetDateTime>,
            JsonDeserializer<OffsetDateTime> {

        @Override
        public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return OffsetDateTime.parse(json.toString());
        }

        @Override
        public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class OffsetTimeGsonTypeAdapter implements JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {

        @Override
        public OffsetTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return OffsetTime.parse(json.toString());
        }

        @Override
        public JsonElement serialize(OffsetTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class ZonedDateTimeGsonTypeAdapter implements JsonSerializer<ZonedDateTime>,
            JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ZonedDateTime.parse(json.toString());
        }

        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
