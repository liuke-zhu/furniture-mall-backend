package com.mall.service.admin;

import com.mall.common.api.PageResult;
import com.mall.dto.admin.banner.BannerRequest;
import com.mall.entity.Banner;

public interface AdminBannerService {

    PageResult<Banner> page(String keyword, Integer pageNum, Integer pageSize);

    void add(BannerRequest request);

    void update(BannerRequest request);

    void updateStatus(Long id, Integer status);

    void delete(Long id);
}
