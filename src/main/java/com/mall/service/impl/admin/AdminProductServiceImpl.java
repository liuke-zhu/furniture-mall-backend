package com.mall.service.impl.admin;

import com.mall.common.api.PageResult;
import com.mall.common.constant.CacheConstants;
import com.mall.common.exception.BusinessException;
import com.mall.dto.admin.product.ProductDetailRequest;
import com.mall.dto.admin.product.ProductSaveRequest;
import com.mall.dto.admin.product.ProductStatusRequest;
import com.mall.entity.Product;
import com.mall.entity.ProductDetail;
import com.mall.mapper.ProductDetailMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.admin.AdminProductService;
import com.mall.vo.admin.product.AdminProductDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductMapper productMapper;
    private final ProductDetailMapper productDetailMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<Product> page(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = productMapper.countAdmin(keyword, categoryId, status);
        return new PageResult<>(total, validPageNum, validPageSize,
                productMapper.selectAdminPage(keyword, categoryId, status, (validPageNum - 1) * validPageSize, validPageSize));
    }

    @Override
    public AdminProductDetailResponse detail(Long id) {
        Product product = productMapper.selectAdminById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        ProductDetail detail = productDetailMapper.selectByProductId(id);
        AdminProductDetailResponse response = new AdminProductDetailResponse();
        response.setId(product.getId());
        response.setCategoryId(product.getCategoryId());
        response.setName(product.getName());
        response.setSubTitle(product.getSubTitle());
        response.setCoverImage(product.getCoverImage());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setSales(product.getSales());
        response.setStatus(product.getStatus());
        response.setIsHot(product.getIsHot());
        response.setIsNew(product.getIsNew());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());
        if (detail != null) {
            response.setDetailImages(detail.getDetailImages());
            response.setMaterial(detail.getMaterial());
            response.setSizeInfo(detail.getSizeInfo());
            response.setColorInfo(detail.getColorInfo());
            response.setStyleInfo(detail.getStyleInfo());
            response.setDescription(detail.getDescription());
        }
        return response;
    }

    @Override
    @Transactional
    public void add(ProductSaveRequest request) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setSubTitle(request.getSubTitle());
        product.setCoverImage(request.getCoverImage());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSales(0);
        product.setStatus(request.getStatus());
        product.setIsHot(request.getIsHot() == null ? 0 : request.getIsHot());
        product.setIsNew(request.getIsNew() == null ? 0 : request.getIsNew());
        productMapper.insert(product);
        saveDetail(product.getId(), request.getDetail());
        clearProductCache(product.getId());
    }

    @Override
    @Transactional
    public void update(ProductSaveRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("商品 ID 不能为空");
        }
        if (productMapper.selectAdminById(request.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        Product product = new Product();
        product.setId(request.getId());
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setSubTitle(request.getSubTitle());
        product.setCoverImage(request.getCoverImage());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());
        product.setIsHot(request.getIsHot() == null ? 0 : request.getIsHot());
        product.setIsNew(request.getIsNew() == null ? 0 : request.getIsNew());
        productMapper.updateById(product);
        saveDetail(request.getId(), request.getDetail());
        clearProductCache(request.getId());
    }

    @Override
    public void updateStatus(Long id, ProductStatusRequest request) {
        if (productMapper.selectAdminById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.updateStatus(id, request.getStatus());
        clearProductCache(id);
    }

    @Override
    public void delete(Long id) {
        if (productMapper.selectAdminById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
        clearProductCache(id);
    }

    private void saveDetail(Long productId, ProductDetailRequest detail) {
        if (detail == null) {
            return;
        }
        ProductDetail entity = new ProductDetail();
        entity.setProductId(productId);
        entity.setDetailImages(detail.getDetailImages());
        entity.setMaterial(detail.getMaterial());
        entity.setSizeInfo(detail.getSizeInfo());
        entity.setColorInfo(detail.getColorInfo());
        entity.setStyleInfo(detail.getStyleInfo());
        entity.setDescription(detail.getDescription());
        productDetailMapper.upsert(entity);
    }

    private void clearProductCache(Long productId) {
        redisTemplate.delete(CacheConstants.PRODUCT_DETAIL_PREFIX + productId);
        redisTemplate.delete(CacheConstants.HOME_HOT_LIST);
        redisTemplate.delete(CacheConstants.HOME_NEW_LIST);
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
