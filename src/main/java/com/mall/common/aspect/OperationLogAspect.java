package com.mall.common.aspect;

import com.mall.common.annotation.OperationLog;
import com.mall.common.util.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogRecorder operationLogRecorder;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        Object result;
        Long adminId = RequestContext.getUserId();
        HttpServletRequest request = currentRequest();
        String url = request != null ? request.getRequestURI() : null;
        String method = request != null ? request.getMethod() : null;
        try {
            result = joinPoint.proceed();
        } finally {
            operationLogRecorder.record(operationLog, adminId, url, method);
        }
        return result;
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }
}
