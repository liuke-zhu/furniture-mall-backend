package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String roleName;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
