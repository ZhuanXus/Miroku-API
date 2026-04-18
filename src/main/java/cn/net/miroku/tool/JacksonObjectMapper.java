package cn.net.miroku.tool;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JacksonObjectMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转json字符串
     */
    public String toJson(Object object) {
        return mapper.writeValueAsString(object);
    }

    /**
     * json字符串转对象
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }
}
