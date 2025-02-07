package com.example.security.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.security.service.CaptchaService;
import com.example.security.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping
    public ResponseEntity<?> getCaptcha(HttpServletRequest request) {
        // 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 50);
        String code = captcha.getCode();

        // 保存验证码
        String sessionId = request.getSession().getId();
        captchaService.saveCaptcha(sessionId, code);

        return ResponseEntity.ok(new ApiResponse(true, "获取验证码成功",
                captcha.getImageBase64Data()));
    }
}