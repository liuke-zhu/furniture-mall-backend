package com.mall.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {

    @NotBlank(message = "管理员账号不能为空")
    private String username;

    @NotBlank(message = "管理员密码不能为空")
    private String password;
}
