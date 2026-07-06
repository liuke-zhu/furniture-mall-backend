package com.mall.service.impl.admin;

import com.mall.common.api.PageResult;
import com.mall.common.constant.CacheConstants;
import com.mall.common.exception.BusinessException;
import com.mall.dto.admin.category.CategoryRequest;
import com.mall.entity.Category;
import com.mall.mapper.CategoryMapper;
import com.mall.service.admin.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<Category> page(String keyword, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = categoryMapper.countAdmin(keyword);
        return new PageResult<>(total, validPageNum, validPageSize,
                categoryMapper.selectAdminPage(keyword, (validPageNum - 1) * validPageSize, validPageSize));
    }

    @Override
    public void add(CategoryRequest request) {
        Category category = new Category();
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setIcon(request.getIcon());
        category.setStatus(request.getStatus());
        categoryMapper.insert(category);
        clearCache();
    }

    @Override
    public void update(CategoryRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("分类 ID 不能为空");
        }
        if (categoryMapper.selectById(request.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        Category category = new Category();
        category.setId(request.getId());
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setIcon(request.getIcon());
        category.setStatus(request.getStatus());
        categoryMapper.updateById(category);
        clearCache();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateStatus(id, status);
        clearCache();
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        redisTemplate.delete(CacheConstants.CATEGORY_LIST);
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
