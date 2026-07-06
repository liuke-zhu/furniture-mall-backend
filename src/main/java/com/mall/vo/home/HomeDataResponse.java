package com.mall.vo.home;

import com.mall.entity.Banner;
import com.mall.entity.Category;
import com.mall.entity.Product;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponse {
    private List<Banner> banners;
    private List<Category> categories;
    private List<Product> hotProducts;
    private List<Product> newProducts;
}
