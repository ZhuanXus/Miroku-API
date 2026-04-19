package cn.net.miroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * /model 的回复响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponse {
    /**
     * 类型：列表
     */
    private String object;
    /**
     * 数据
     */
    private List<ModelData> data;
}
