package com.mall.controller.admin;

import com.mall.common.annotation.OperationLog;
import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.dto.admin.category.CategoryRequest;
import com.mall.entity.Category;
import com.mall.service.admin.AdminCategoryService;
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
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping("/page")
    public Result<PageResult<Category>> page(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer pageNum,
                                             @RequestParam(required = false) Integer pageSize) {
        return Result.success(adminCategoryService.page(keyword, pageNum, pageSize));
    }

    @PostMapping("/add")
    @OperationLog(module = "分类管理", type = "新增", desc = "新增分类")
    public Result<Void> add(@Valid @RequestBody CategoryRequest request) {
        adminCategoryService.add(request);
        return Result.success("新增分类成功", null);
    }

    @PutMapping("/update")
    @OperationLog(module = "分类管理", type = "修改", desc = "修改分类")
    public Result<Void> update(@Valid @RequestBody CategoryRequest request) {
        adminCategoryService.update(request);
        return Result.success("更新分类成功", null);
    }

    @PutMapping("/status/{id}")
    @OperationLog(module = "分类管理", type = "修改", desc = "更新分类状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminCategoryService.updateStatus(id, status);
        return Result.success("更新状态成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @OperationLog(module = "分类管理", type = "删除", desc = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return Result.success("删除成功", null);
    }
}
