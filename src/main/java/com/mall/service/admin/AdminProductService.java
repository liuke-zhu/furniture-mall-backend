package com.mall.service.admin;

import com.mall.common.api.PageResult;
import com.mall.dto.admin.product.ProductSaveRequest;
import com.mall.dto.admin.product.ProductStatusRequest;
import com.mall.entity.Product;
import com.mall.vo.admin.product.AdminProductDetailResponse;

public interface AdminProductService {

    PageResult<Product> page(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize);

    AdminProductDetailResponse detail(Long id);

    void add(ProductSaveRequest request);

    void update(ProductSaveRequest request);

    void updateStatus(Long id, ProductStatusRequest request);

    void delete(Long id);
}
