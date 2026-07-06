package com.mall.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilsTest {

    @Test
    void encode_shouldReturnBcryptHash() {
        String encoded = PasswordUtils.encode("123456");
        assertThat(encoded).isNotNull().startsWith("$2a$");
        assertThat(encoded).isNotEqualTo("123456");
    }

    @Test
    void matches_shouldReturnTrueForCorrectPassword() {
        String encoded = PasswordUtils.encode("mySecret123");
        assertThat(PasswordUtils.matches("mySecret123", encoded)).isTrue();
    }

    @Test
    void matches_shouldReturnFalseForWrongPassword() {
        String encoded = PasswordUtils.encode("correctPwd");
        assertThat(PasswordUtils.matches("wrongPwd", encoded)).isFalse();
    }

    @Test
    void matches_shouldReturnFalseForNullInputs() {
        assertThat(PasswordUtils.matches(null, "$2a$abc")).isFalse();
        assertThat(PasswordUtils.matches("pwd", null)).isFalse();
    }

    @Test
    void isEncoded_shouldDetectBcryptFormat() {
        assertThat(PasswordUtils.isEncoded("$2a$10$abcdef")).isTrue();
        assertThat(PasswordUtils.isEncoded("$2b$10$abcdef")).isTrue();
        assertThat(PasswordUtils.isEncoded("123456")).isFalse();
        assertThat(PasswordUtils.isEncoded(null)).isFalse();
    }

    @Test
    void encode_shouldProduceDifferentHashesForSamePassword() {
        String e1 = PasswordUtils.encode("samePwd");
        String e2 = PasswordUtils.encode("samePwd");
        assertThat(e1).isNotEqualTo(e2);
        assertThat(PasswordUtils.matches("samePwd", e1)).isTrue();
        assertThat(PasswordUtils.matches("samePwd", e2)).isTrue();
    }
}
