package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.JwtUtils;
import com.mall.dto.auth.UserLoginRequest;
import com.mall.dto.auth.UserRegisterRequest;
import com.mall.entity.User;
import com.mall.mapper.AdminMapper;
import com.mall.mapper.UserMapper;
import com.mall.vo.auth.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private AdminMapper adminMapper;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_shouldSucceed_whenUsernameNotExists() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setUsername("newuser");
        req.setPassword("pass123");
        req.setConfirmPassword("pass123");
        req.setPhone("13800000000");
        when(userMapper.selectByUsername("newuser")).thenReturn(null);

        authService.register(req);

        verify(userMapper).insert(argThat(u ->
                "newuser".equals(u.getUsername())
                        && u.getPassword().startsWith("$2a$")
                        && u.getStatus() == 1
                        && "13800000000".equals(u.getPhone())));
    }

    @Test
    void register_shouldThrow_whenUsernameExists() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setUsername("existing");
        req.setPassword("pass123");
        req.setConfirmPassword("pass123");
        when(userMapper.selectByUsername("existing")).thenReturn(new User());

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名已存在");
        verify(userMapper, never()).insert(any());
    }

    @Test
    void register_shouldThrow_whenPasswordMismatch() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setUsername("u");
        req.setPassword("a");
        req.setConfirmPassword("b");

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("密码不一致");
        verifyNoInteractions(userMapper);
    }

    @Test
    void login_shouldSucceed_whenCredentialsValid() {
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        user.setPassword(com.mall.common.util.PasswordUtils.encode("pwd123"));
        user.setNickname("Alice");
        user.setStatus(1);
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(jwtUtils.generateToken(10L, "alice", "USER")).thenReturn("token-abc");

        UserLoginRequest req = new UserLoginRequest();
        req.setUsername("alice");
        req.setPassword("pwd123");

        LoginResponse resp = authService.login(req);

        assertThat(resp.getToken()).isEqualTo("token-abc");
        assertThat(resp.getUserId()).isEqualTo(10L);
        assertThat(resp.getRole()).isEqualTo("USER");
    }

    @Test
    void login_shouldThrow_whenPasswordWrong() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword(com.mall.common.util.PasswordUtils.encode("correct"));
        user.setStatus(1);
        when(userMapper.selectByUsername("bob")).thenReturn(user);

        UserLoginRequest req = new UserLoginRequest();
        req.setUsername("bob");
        req.setPassword("wrong");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");
        verify(jwtUtils, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void login_shouldThrow_whenAccountDisabled() {
        User user = new User();
        user.setUsername("disabled");
        user.setPassword(com.mall.common.util.PasswordUtils.encode("pwd"));
        user.setStatus(0);
        when(userMapper.selectByUsername("disabled")).thenReturn(user);

        UserLoginRequest req = new UserLoginRequest();
        req.setUsername("disabled");
        req.setPassword("pwd");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("禁用");
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        when(userMapper.selectByUsername("ghost")).thenReturn(null);
        when(adminMapper.selectByUsername("ghost")).thenReturn(null);

        UserLoginRequest req = new UserLoginRequest();
        req.setUsername("ghost");
        req.setPassword("pwd");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");
    }
}
