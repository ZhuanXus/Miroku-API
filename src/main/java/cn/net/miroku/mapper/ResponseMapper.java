package cn.net.miroku.mapper;

import cn.net.miroku.dto.chat.completion.Response;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResponseMapper {
    void insert(Response response);
}
