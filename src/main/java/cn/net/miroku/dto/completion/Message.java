package cn.net.miroku.dto.completion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 角色
     */
    private String role;
    /**
     * 内容
     */
    private String content;
}