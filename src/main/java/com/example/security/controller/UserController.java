package com.example.security.controller;

import com.example.security.payload.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class UserController {

    @GetMapping("/all")
    public ApiResponse allAccess() {
        return new ApiResponse(true, "公共内容");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse userAccess() {
        return new ApiResponse(true, "用户内容");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse adminAccess() {
        return new ApiResponse(true, "管理员内容");
    }
}