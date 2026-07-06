package com.mall.mapper;

import com.mall.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OperationLogMapper {

    @Insert("""
            insert into sys_operation_log(admin_id, module_name, operation_type, operation_desc, request_url, request_method, create_time)
            values(#{adminId}, #{moduleName}, #{operationType}, #{operationDesc}, #{requestUrl}, #{requestMethod}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog operationLog);
}
