package com.mall.service.impl.app;

import com.mall.common.constant.CacheConstants;
import com.mall.entity.Category;
import com.mall.mapper.CategoryMapper;
import com.mall.service.app.CategoryService;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> list() {
        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(CacheConstants.CATEGORY_LIST);
        if (categories == null) {
            categories = categoryMapper.selectEnabledList();
            redisTemplate.opsForValue().set(CacheConstants.CATEGORY_LIST, categories, Duration.ofMinutes(30));
        }
        return categories;
    }
}
