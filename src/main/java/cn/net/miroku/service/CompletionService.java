package cn.net.miroku.service;

import cn.net.miroku.dto.chat.completion.MirokuResponse;
import cn.net.miroku.dto.chat.completion.MirokuRequest;
import okhttp3.Call;

import java.io.IOException;

public interface CompletionService {
    /**
     * 创建聊天补全
     */
    Call create(MirokuRequest mirokuRequest, String id) throws IOException;

    /**
     * 保存聊天补全
     */
    void save(MirokuResponse mirokuResponse);

    /**
     * 查询聊天补全
     */
    MirokuResponse select(String respId);

    /**
     * 删除聊天补全
     */
    Boolean delete(String respId);

    /**
     * 取消聊天补全
     */
    void cancel(String id);
}
