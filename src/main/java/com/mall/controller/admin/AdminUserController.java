package com.mall.controller.admin;

import com.mall.common.annotation.OperationLog;
import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.service.admin.AdminUserService;
import com.mall.vo.admin.user.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/page")
    public Result<PageResult<AdminUserResponse>> page(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize) {
        return Result.success(adminUserService.page(keyword, pageNum, pageSize));
    }

    @PutMapping("/status/{id}")
    @OperationLog(module = "用户管理", type = "修改", desc = "更新用户状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminUserService.updateStatus(id, status);
        return Result.success("更新状态成功", null);
    }
}
