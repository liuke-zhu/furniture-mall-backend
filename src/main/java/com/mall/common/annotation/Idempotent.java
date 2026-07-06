package com.mall.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口幂等性注解：防止用户在短时间内重复提交同一请求（基于 Redis token）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 幂等窗口（秒），默认 5 秒内同一请求视为重复
     */
    int interval() default 5;

    /**
     * 提示信息
     */
    String message() default "请勿重复提交";
}
