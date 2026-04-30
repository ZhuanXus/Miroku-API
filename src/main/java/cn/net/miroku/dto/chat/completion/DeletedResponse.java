package cn.net.miroku.dto.chat.completion;

import lombok.Data;

@Data
public class DeletedResponse {
    private String object = "chat.completion.deleted";

    /**
     * 被删除的补全标识
     */
    private String id;

    /**
     * 是否被成功删除
     */
    private Boolean deleted;
}
