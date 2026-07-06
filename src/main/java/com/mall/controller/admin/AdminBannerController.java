package com.mall.controller.admin;

import com.mall.common.annotation.OperationLog;
import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.dto.admin.banner.BannerRequest;
import com.mall.entity.Banner;
import com.mall.service.admin.AdminBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @GetMapping("/page")
    public Result<PageResult<Banner>> page(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize) {
        return Result.success(adminBannerService.page(keyword, pageNum, pageSize));
    }

    @PostMapping("/add")
    @OperationLog(module = "Banner管理", type = "新增", desc = "新增Banner")
    public Result<Void> add(@Valid @RequestBody BannerRequest request) {
        adminBannerService.add(request);
        return Result.success("新增成功", null);
    }

    @PutMapping("/update")
    @OperationLog(module = "Banner管理", type = "修改", desc = "修改Banner")
    public Result<Void> update(@Valid @RequestBody BannerRequest request) {
        adminBannerService.update(request);
        return Result.success("更新成功", null);
    }

    @PutMapping("/status/{id}")
    @OperationLog(module = "Banner管理", type = "修改", desc = "更新Banner状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminBannerService.updateStatus(id, status);
        return Result.success("更新状态成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @OperationLog(module = "Banner管理", type = "删除", desc = "删除Banner")
    public Result<Void> delete(@PathVariable Long id) {
        adminBannerService.delete(id);
        return Result.success("删除成功", null);
    }
}
