package com.example.security.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.security.payload.ApiResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse> handleError(HttpServletRequest request, WebRequest webRequest) {
        HttpStatus status = getStatus(request);

        // 在生产环境中隐藏敏感信息
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE);

        if (!isProd()) {
            options = options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }

        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, options);

        String message = (String) errorAttributes.get("message");
        if (isProd()) {
            // 生产环境使用通用错误消息
            message = getProductionMessage(status);
        }

        return ResponseEntity
                .status(status)
                .body(new ApiResponse(false, message));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private boolean isProd() {
        String profile = System.getProperty("spring.profiles.active");
        return "prod".equals(profile);
    }

    private String getProductionMessage(HttpStatus status) {
        switch (status) {
            case NOT_FOUND:
                return "请求的资源不存在";
            case FORBIDDEN:
                return "没有权限访问该资源";
            case UNAUTHORIZED:
                return "请先登录";
            case BAD_REQUEST:
                return "请求参数错误";
            default:
                return "服务器内部错误，请稍后重试";
        }
    }
}