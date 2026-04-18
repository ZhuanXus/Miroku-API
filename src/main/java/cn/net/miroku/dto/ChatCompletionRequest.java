package cn.net.miroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionRequest {
    /**
     * 使用模型
     */
    private String model;
    /**
     * 消息数组
     */
    private Message[] messages;
    /**
     * 采样温度 越高越随机
     */
    private double temperature;
    /**
     * 是否开启流式传输
     */
    private boolean stream;
}
