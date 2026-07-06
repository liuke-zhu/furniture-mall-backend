package com.mall.service.app;

import com.mall.vo.favorite.FavoriteResponse;
import java.util.List;

public interface FavoriteService {

    void add(Long productId);

    void delete(Long productId);

    List<FavoriteResponse> list();

    boolean check(Long productId);
}
