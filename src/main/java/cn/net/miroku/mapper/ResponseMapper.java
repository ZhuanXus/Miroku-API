package cn.net.miroku.mapper;

import cn.net.miroku.dto.chat.completion.Response;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ResponseMapper {
    /**
     * 插入响应表
     */
    void insertResponse(@Param("response") Response response);

    /**
     * 插入单条详细回复表
     */
    void insertChoices(@Param("response") Response response);
}
