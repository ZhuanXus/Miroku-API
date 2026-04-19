package cn.net.miroku.dto;

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
    private String finish_reason;
}
