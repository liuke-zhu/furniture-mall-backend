package com.mall.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private static final String BCRYPT_PREFIX = "$2a$";
    private static final String BCRYPT_PREFIX_ALT = "$2b$";

    private PasswordUtils() {
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    public static boolean isEncoded(String password) {
        return password != null
                && (password.startsWith(BCRYPT_PREFIX) || password.startsWith(BCRYPT_PREFIX_ALT));
    }
}
