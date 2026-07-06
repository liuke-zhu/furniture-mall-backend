package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.common.util.RequestContext;
import com.mall.dto.auth.UserLoginRequest;
import com.mall.dto.auth.UserRegisterRequest;
import com.mall.service.app.AuthService;
import com.mall.vo.auth.LoginResponse;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        return Result.success(Map.of(
                "userId", RequestContext.getUserId(),
                "username", RequestContext.getUsername(),
                "role", RequestContext.getRole()
        ));
    }
}
