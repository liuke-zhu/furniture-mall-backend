package com.mall.service.app;

import com.mall.common.api.PageResult;
import com.mall.entity.Product;
import com.mall.vo.product.ProductDetailResponse;

public interface ProductService {

    PageResult<Product> page(Integer pageNum, Integer pageSize);

    PageResult<Product> search(Long categoryId, String keyword, Integer pageNum, Integer pageSize);

    ProductDetailResponse detail(Long id);
}
