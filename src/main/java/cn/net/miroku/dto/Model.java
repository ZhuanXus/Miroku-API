package cn.net.miroku.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 宽松模式 忽略未知字段
public class Model {
    /**
     * 模型名称
     */
    private String id;
    /**
     * 类型：模型
     */
    private String object = "model";
    /**
     * 创建时间 默认为 1868-8-12
     */
    private Long created = -3199507543L;
    /**
     * 模型所属组织，默认为 Miroku
     */
    @JsonProperty("owned_by")
    private String ownedBy = "Miroku";
}
