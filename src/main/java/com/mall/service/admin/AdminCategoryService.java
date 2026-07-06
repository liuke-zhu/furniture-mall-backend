package com.mall.service.admin;

import com.mall.common.api.PageResult;
import com.mall.dto.admin.category.CategoryRequest;
import com.mall.entity.Category;

public interface AdminCategoryService {

    PageResult<Category> page(String keyword, Integer pageNum, Integer pageSize);

    void add(CategoryRequest request);

    void update(CategoryRequest request);

    void updateStatus(Long id, Integer status);

    void delete(Long id);
}
