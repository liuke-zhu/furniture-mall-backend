package com.mall.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.annotation.Idempotent;
import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private static final String KEY_PREFIX = "idempotent:";
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = buildKey(joinPoint, idempotent);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(idempotent.interval()));
        if (Boolean.FALSE.equals(acquired)) {
            throw new BusinessException(idempotent.message());
        }
        return joinPoint.proceed();
    }

    private String buildKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        StringBuilder sb = new StringBuilder(KEY_PREFIX);
        sb.append(RequestContext.getUserId()).append(":");
        sb.append(method.getDeclaringClass().getSimpleName()).append(":");
        sb.append(method.getName()).append(":");
        try {
            sb.append(objectMapper.writeValueAsString(Arrays.asList(joinPoint.getArgs())));
        } catch (Exception e) {
            sb.append(Arrays.hashCode(joinPoint.getArgs()));
        }
        return sb.toString();
    }
}
