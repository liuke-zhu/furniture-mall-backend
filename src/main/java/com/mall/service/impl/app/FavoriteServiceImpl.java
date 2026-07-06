package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.entity.Product;
import com.mall.mapper.FavoriteMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.app.FavoriteService;
import com.mall.vo.favorite.FavoriteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    public void add(Long productId) {
        if (productId == null) {
            throw new BusinessException("商品ID不能为空");
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }
        favoriteMapper.insert(currentUserId(), productId);
    }

    @Override
    public void delete(Long productId) {
        favoriteMapper.delete(currentUserId(), productId);
    }

    @Override
    public List<FavoriteResponse> list() {
        return favoriteMapper.selectListByUserId(currentUserId());
    }

    @Override
    public boolean check(Long productId) {
        return favoriteMapper.countByUserAndProduct(currentUserId(), productId) > 0;
    }

    private Long currentUserId() {
        Long userId = RequestContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return userId;
    }
}
