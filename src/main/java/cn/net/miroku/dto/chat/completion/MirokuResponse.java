package cn.net.miroku.dto.chat.completion;

import cn.net.miroku.dto.Choice;
import lombok.Data;

import java.util.List;

@Data
public class MirokuResponse {
    /**
     */
    private String id;
    /**
     * 类型
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
    private List<Choice> choices;
}
