package cn.net.miroku.tool;

import org.springframework.boot.json.JsonParseException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class JsonUtils {
    // 全局单例，线程安全
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.INDENT_OUTPUT) // 可选：美化输出
            .findAndAddModules() // 自动注册 Java8Time 等常用模块
            .build();

    public static <T> T toDto(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }

    // 泛型类型支持（比如 List<User>）
    public static <T> T toDto(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }
}
