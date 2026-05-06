package cn.net.miroku.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenAiError {
    public ErrorDetail error;

    @Data
    @Builder
    public static class ErrorDetail {
        /**
         * 错误信息
         */
        private String message;
        /**
         * 错误类型
         */
        private String type;
        /**
         * 错误参数（可选）
         */
        private String param;
        /**
         * 机器可读错误码（可选）
         */
        private String code;
    }
}
