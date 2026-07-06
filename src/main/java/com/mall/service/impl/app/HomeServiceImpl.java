package com.mall.service.impl.app;

import com.mall.common.constant.CacheConstants;
import com.mall.entity.Banner;
import com.mall.entity.Category;
import com.mall.entity.Product;
import com.mall.mapper.BannerMapper;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.app.HomeService;
import com.mall.vo.home.HomeDataResponse;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BannerMapper bannerMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public HomeDataResponse getHomeData() {
        List<Banner> banners = (List<Banner>) redisTemplate.opsForValue().get(CacheConstants.BANNER_LIST);
        if (banners == null) {
            banners = bannerMapper.selectEnabledList();
            redisTemplate.opsForValue().set(CacheConstants.BANNER_LIST, banners, Duration.ofMinutes(30));
        }

        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(CacheConstants.CATEGORY_LIST);
        if (categories == null) {
            categories = categoryMapper.selectEnabledList();
            redisTemplate.opsForValue().set(CacheConstants.CATEGORY_LIST, categories, Duration.ofMinutes(30));
        }

        List<Product> hotProducts = (List<Product>) redisTemplate.opsForValue().get(CacheConstants.HOME_HOT_LIST);
        if (hotProducts == null) {
            hotProducts = productMapper.selectHotList(8);
            redisTemplate.opsForValue().set(CacheConstants.HOME_HOT_LIST, hotProducts, Duration.ofMinutes(30));
        }

        List<Product> newProducts = (List<Product>) redisTemplate.opsForValue().get(CacheConstants.HOME_NEW_LIST);
        if (newProducts == null) {
            newProducts = productMapper.selectNewList(8);
            redisTemplate.opsForValue().set(CacheConstants.HOME_NEW_LIST, newProducts, Duration.ofMinutes(30));
        }

        return new HomeDataResponse(banners, categories, hotProducts, newProducts);
    }
}
