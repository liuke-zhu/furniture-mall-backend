package com.mall.vo.admin.user;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AdminUserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
