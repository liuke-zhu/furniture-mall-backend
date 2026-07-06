package com.mall.service.app;

import com.mall.dto.auth.UserLoginRequest;
import com.mall.dto.auth.UserRegisterRequest;
import com.mall.vo.auth.LoginResponse;

public interface AuthService {

    void register(UserRegisterRequest request);

    LoginResponse login(UserLoginRequest request);
}
