package cn.net.miroku.dto.completion;

import lombok.Data;

import java.util.List;

@Data
public class MirokuRequest {
    /**
     * 使用模型
     */
    private String model;
    /**
     * 消息数组
     */
    private List<Message> messages;
    /**
     * 采样温度 越高越随机
     */
    private Double temperature = 1.0;
    /**
     * 是否开启流式传输
     */
    private Boolean stream = false;
}
