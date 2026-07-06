package com.mall.service.impl.admin;

import com.mall.common.constant.SecurityConstants;
import com.mall.common.exception.BusinessException;
import com.mall.common.util.JwtUtils;
import com.mall.common.util.PasswordUtils;
import com.mall.dto.auth.AdminLoginRequest;
import com.mall.entity.Admin;
import com.mall.mapper.AdminMapper;
import com.mall.service.admin.AdminAuthService;
import com.mall.vo.auth.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(AdminLoginRequest request) {
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin == null || !PasswordUtils.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("管理员账号或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() == 0) {
            throw new BusinessException("管理员账号已被禁用");
        }
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), SecurityConstants.ROLE_ADMIN);
        return new LoginResponse(token, admin.getId(), admin.getUsername(), admin.getNickname(), SecurityConstants.ROLE_ADMIN);
    }
}
