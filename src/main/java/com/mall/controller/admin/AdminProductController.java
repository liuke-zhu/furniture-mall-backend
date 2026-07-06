package com.mall.controller.admin;

import com.mall.common.annotation.OperationLog;
import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.dto.admin.product.ProductSaveRequest;
import com.mall.dto.admin.product.ProductStatusRequest;
import com.mall.entity.Product;
import com.mall.service.admin.AdminProductService;
import com.mall.vo.admin.product.AdminProductDetailResponse;
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
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer pageSize) {
        return Result.success(adminProductService.page(keyword, categoryId, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<AdminProductDetailResponse> detail(@PathVariable Long id) {
        return Result.success(adminProductService.detail(id));
    }

    @PostMapping("/add")
    @OperationLog(module = "商品管理", type = "新增", desc = "新增商品")
    public Result<Void> add(@Valid @RequestBody ProductSaveRequest request) {
        adminProductService.add(request);
        return Result.success("新增商品成功", null);
    }

    @PutMapping("/update")
    @OperationLog(module = "商品管理", type = "修改", desc = "修改商品")
    public Result<Void> update(@Valid @RequestBody ProductSaveRequest request) {
        adminProductService.update(request);
        return Result.success("更新商品成功", null);
    }

    @PutMapping("/status/{id}")
    @OperationLog(module = "商品管理", type = "修改", desc = "更新商品上下架状态")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody ProductStatusRequest request) {
        adminProductService.updateStatus(id, request);
        return Result.success("更新状态成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @OperationLog(module = "商品管理", type = "删除", desc = "删除商品")
    public Result<Void> delete(@PathVariable Long id) {
        adminProductService.delete(id);
        return Result.success("删除成功", null);
    }
}
