package cn.net.miroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionResponse {
    /**
     * 聊天补全记录的唯一标识
     */
    private String id;
    /**
     * 响应类型 chat.completion 非流式响应
     */
    private String object;
    /**
     * 响应创建时间
     */
    private Long created;
    /**
     * 使用的模型
     */
    private String model;
    /**
     * 响应配置信息
     */
    private Choice[] choices;
}
