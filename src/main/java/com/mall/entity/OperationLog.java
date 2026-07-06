package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OperationLog {
    private Long id;
    private Long adminId;
    private String moduleName;
    private String operationType;
    private String operationDesc;
    private String requestUrl;
    private String requestMethod;
    private LocalDateTime createTime;
}
