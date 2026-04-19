package cn.net.miroku.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Choice {
    /**
     * 回答索引
     */
    private Integer index;
    /**
     * 回答
     */
    private Message message;
    /**
     * 结束原因
     */
    @JsonProperty("finish_reason")
    private String finishReason;
}
