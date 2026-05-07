package cn.net.miroku.tool;

import cn.dev33.satoken.exception.SaTokenException;
import cn.net.miroku.dto.OpenAiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<OpenAiError> handleSaTokenException(SaTokenException e) {
        e.printStackTrace();

        /*
            https://sa-token.cc/doc.html#/fun/exception-code
            这里其实根据 SaToken的异常码 可以分为很多种类的
            我这里直接偷个懒 统一识别为未登录了
        */
        OpenAiError.ErrorDetail errorDetail = OpenAiError.ErrorDetail.builder()
                .message("用户未登录")
                .type("unauthorized")
                .param(null)
                .code(null)
                .build();

        OpenAiError openAiError = new OpenAiError();
        openAiError.setError(errorDetail);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(openAiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OpenAiError> handleException(Exception e) {
        e.printStackTrace();

        OpenAiError.ErrorDetail errorDetail = OpenAiError.ErrorDetail.builder()
                .message("服务器内部发生错误")
                .type("server_error")
                .param(null)
                .code(null)
                .build();

        OpenAiError openAiError = new OpenAiError();
        openAiError.setError(errorDetail);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(openAiError);
    }
}
