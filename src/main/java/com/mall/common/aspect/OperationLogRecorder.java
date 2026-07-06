package com.mall.common.aspect;

import com.mall.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogRecorder {

    private final OperationLogMapper operationLogMapper;

    @Async("operationLogExecutor")
    public void record(com.mall.common.annotation.OperationLog operationLog, Long adminId, String url, String method) {
        try {
            com.mall.entity.OperationLog entity = new com.mall.entity.OperationLog();
            entity.setAdminId(adminId);
            entity.setModuleName(operationLog.module());
            entity.setOperationType(operationLog.type());
            entity.setOperationDesc(operationLog.desc());
            entity.setRequestUrl(url);
            entity.setRequestMethod(method);
            operationLogMapper.insert(entity);
        } catch (Exception e) {
            log.warn("Failed to record operation log", e);
        }
    }
}
