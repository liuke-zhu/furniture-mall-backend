package com.mall.service.impl.app;

import com.mall.common.constant.SecurityConstants;
import com.mall.common.exception.BusinessException;
import com.mall.common.util.JwtUtils;
import com.mall.common.util.PasswordUtils;
import com.mall.dto.auth.UserLoginRequest;
import com.mall.dto.auth.UserRegisterRequest;
import com.mall.entity.Admin;
import com.mall.entity.User;
import com.mall.mapper.AdminMapper;
import com.mall.mapper.UserMapper;
import com.mall.service.app.AuthService;
import com.mall.vo.auth.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final JwtUtils jwtUtils;

    @Override
    public void register(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setNickname(request.getUsername());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(UserLoginRequest request) {
        // 先查用户表
        User user = userMapper.selectByUsername(request.getUsername());
        if (user != null) {
            if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
            if (user.getStatus() == null || user.getStatus() == 0) {
                throw new BusinessException("当前账号已被禁用");
            }
            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), SecurityConstants.ROLE_USER);
            return new LoginResponse(token, user.getId(), user.getUsername(), user.getNickname(), SecurityConstants.ROLE_USER);
        }

        // 用户表找不到，查管理员表
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin != null) {
            if (!PasswordUtils.matches(request.getPassword(), admin.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
            if (admin.getStatus() == null || admin.getStatus() == 0) {
                throw new BusinessException("管理员账号已被禁用");
            }
            String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), SecurityConstants.ROLE_ADMIN);
            return new LoginResponse(token, admin.getId(), admin.getUsername(), admin.getNickname(), SecurityConstants.ROLE_ADMIN);
        }

        // 两边都找不到
        throw new BusinessException("用户名或密码错误");
    }
}
