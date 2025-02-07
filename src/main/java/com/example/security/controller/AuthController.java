package com.example.security.controller;

import com.example.security.payload.SignupRequest;
import com.example.security.payload.LoginRequest;
import com.example.security.payload.JwtResponse;
import com.example.security.payload.ApiResponse;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import com.example.security.security.JwtTokenProvider;
import com.example.security.validator.PasswordValidator;
import com.example.security.service.LoginAttemptService;
import com.example.security.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "认证管理")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private CaptchaService captchaService;

    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        logger.info("尝试登录用户: {}", loginRequest.getUsername());

        if (!captchaService.validateCaptcha(request.getSession().getId(), loginRequest.getCaptcha())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "验证码错误或已过期"));
        }

        if (loginAttemptService.isBlocked(ip)) {
            logger.warn("IP {} 已被锁定", ip);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "账户已被锁定，请1小时后重试"));
        }

        try {
            // 先检查用户是否存在
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            logger.info("找到用户: {}, 密码哈希: {}", user.getUsername(), user.getPassword());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            if (user.isPasswordExpired()) {
                logger.warn("用户 {} 密码已过期", user.getUsername());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "密码已过期，请修改密码"));
            }

            loginAttemptService.loginSucceeded(ip);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            logger.info("用户 {} 登录成功", user.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roles));
        } catch (AuthenticationException e) {
            logger.error("用户 {} 认证失败: {}", loginRequest.getUsername(), e.getMessage());
            loginAttemptService.loginFailed(ip);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "用户名或密码错误"));
        }
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("开始注册用户: {}", signUpRequest.getUsername());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("用户名已存在: {}", signUpRequest.getUsername());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "用户名已被使用"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("邮箱已存在: {}", signUpRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "邮箱已被使用"));
        }

        // 验证密码强度
        if (!passwordValidator.isValid(signUpRequest.getPassword())) {
            logger.warn("密码强度不足");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "密码不符合要求: " + passwordValidator.getPasswordRequirements()));
        }

        try {
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("未找到默认角色"));
            roles.add(userRole);
            user.setRoles(roles);

            userRepository.save(user);
            logger.info("用户注册成功: {}", user.getUsername());

            return ResponseEntity.ok(new ApiResponse(true, "用户注册成功"));
        } catch (Exception e) {
            logger.error("用户注册失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "注册失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("未找到用户"));

        return ResponseEntity.ok(new ApiResponse(true, "获取成功",
                new JwtResponse(null, user.getId(), user.getUsername(),
                        user.getEmail(), user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toList()))));
    }
}