package com.example.security.controller;

import com.example.security.payload.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/test")
@Tag(name = "用户管理")
public class UserController {

    @Operation(summary = "公共接口", description = "所有用户都可以访问")
    @GetMapping("/all")
    public ApiResponse allAccess() {
        return new ApiResponse(true, "公共内容");
    }

    @Operation(summary = "用户接口", description = "需要USER或ADMIN角色")
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse userAccess() {
        return new ApiResponse(true, "用户内容");
    }

    @Operation(summary = "管理员接口", description = "需要ADMIN角色")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse adminAccess() {
        return new ApiResponse(true, "管理员内容");
    }
}