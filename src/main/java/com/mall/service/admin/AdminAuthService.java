package com.mall.service.admin;

import com.mall.dto.auth.AdminLoginRequest;
import com.mall.vo.auth.LoginResponse;

public interface AdminAuthService {

    LoginResponse login(AdminLoginRequest request);
}
