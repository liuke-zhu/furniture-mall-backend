package com.mall.service.admin;

import com.mall.common.api.PageResult;
import com.mall.vo.admin.user.AdminUserResponse;

public interface AdminUserService {

    PageResult<AdminUserResponse> page(String keyword, Integer pageNum, Integer pageSize);

    void updateStatus(Long id, Integer status);
}
