package cn.net.miroku.mapper;

import cn.net.miroku.dto.chat.completion.MirokuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ResponseMapper {
    /**
     * 插入响应表
     */
    void insertResponse(@Param("response") MirokuResponse mirokuResponse);

    /**
     * 插入单条详细回复表
     */
    void insertChoices(@Param("response") MirokuResponse mirokuResponse);

    /**
     * 查询某条补全
     */
    MirokuResponse selectResponse(@Param("respId") String respId);
}
