package com.mall.common.constant;

public final class SecurityConstants {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String USER_ID_CLAIM = "userId";
    public static final String USERNAME_CLAIM = "username";
    public static final String ROLE_CLAIM = "role";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    private SecurityConstants() {
    }
}
