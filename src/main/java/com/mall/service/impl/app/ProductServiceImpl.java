package com.mall.service.impl.app;

import com.mall.common.api.PageResult;
import com.mall.common.constant.CacheConstants;
import com.mall.common.exception.BusinessException;
import com.mall.entity.Product;
import com.mall.entity.ProductDetail;
import com.mall.mapper.ProductDetailMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.app.ProductService;
import com.mall.vo.product.ProductDetailResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductDetailMapper productDetailMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<Product> page(Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = productMapper.countEnabled();
        return new PageResult<>(total, validPageNum, validPageSize,
                productMapper.selectPage((validPageNum - 1) * validPageSize, validPageSize));
    }

    @Override
    public PageResult<Product> search(Long categoryId, String keyword, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = productMapper.countSearch(categoryId, keyword);
        return new PageResult<>(total, validPageNum, validPageSize,
                productMapper.selectSearchPage(categoryId, keyword, (validPageNum - 1) * validPageSize, validPageSize));
    }

    @Override
    public ProductDetailResponse detail(Long id) {
        String cacheKey = CacheConstants.PRODUCT_DETAIL_PREFIX + id;
        ProductDetailResponse response = (ProductDetailResponse) redisTemplate.opsForValue().get(cacheKey);
        if (response != null) {
            return response;
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }

        ProductDetail detail = productDetailMapper.selectByProductId(id);
        response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setCategoryId(product.getCategoryId());
        response.setName(product.getName());
        response.setSubTitle(product.getSubTitle());
        response.setCoverImage(product.getCoverImage());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setSales(product.getSales());
        if (detail != null) {
            response.setDetailImages(detail.getDetailImages());
            response.setMaterial(detail.getMaterial());
            response.setSizeInfo(detail.getSizeInfo());
            response.setColorInfo(detail.getColorInfo());
            response.setStyleInfo(detail.getStyleInfo());
            response.setDescription(detail.getDescription());
        }

        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(30));
        return response;
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
