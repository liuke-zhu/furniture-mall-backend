package com.mall.service.impl.admin;

import com.mall.common.api.PageResult;
import com.mall.common.constant.CacheConstants;
import com.mall.common.exception.BusinessException;
import com.mall.dto.admin.banner.BannerRequest;
import com.mall.entity.Banner;
import com.mall.mapper.BannerMapper;
import com.mall.service.admin.AdminBannerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBannerServiceImpl implements AdminBannerService {

    private final BannerMapper bannerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<Banner> page(String keyword, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = bannerMapper.countAdmin(keyword);
        return new PageResult<>(total, validPageNum, validPageSize,
                bannerMapper.selectAdminPage(keyword, (validPageNum - 1) * validPageSize, validPageSize));
    }

    @Override
    public void add(BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setSort(request.getSort());
        banner.setStatus(request.getStatus());
        bannerMapper.insert(banner);
        clearCache();
    }

    @Override
    public void update(BannerRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("Banner ID 不能为空");
        }
        if (bannerMapper.selectById(request.getId()) == null) {
            throw new BusinessException("Banner 不存在");
        }
        Banner banner = new Banner();
        banner.setId(request.getId());
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setSort(request.getSort());
        banner.setStatus(request.getStatus());
        bannerMapper.updateById(banner);
        clearCache();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (bannerMapper.selectById(id) == null) {
            throw new BusinessException("Banner 不存在");
        }
        bannerMapper.updateStatus(id, status);
        clearCache();
    }

    @Override
    public void delete(Long id) {
        if (bannerMapper.selectById(id) == null) {
            throw new BusinessException("Banner 不存在");
        }
        bannerMapper.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        redisTemplate.delete(CacheConstants.BANNER_LIST);
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
