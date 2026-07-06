package com.mall.mapper;

import com.mall.entity.ProductDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductDetailMapper {

    @Select("""
            select id, product_id, detail_images, material, size_info, color_info, style_info, description, create_time, update_time
            from pms_product_detail
            where product_id = #{productId}
            limit 1
            """)
    ProductDetail selectByProductId(@Param("productId") Long productId);

    @Insert("""
            insert into pms_product_detail(product_id, detail_images, material, size_info, color_info, style_info, description, create_time, update_time)
            values(#{productId}, #{detailImages}, #{material}, #{sizeInfo}, #{colorInfo}, #{styleInfo}, #{description}, now(), now())
            on duplicate key update
                detail_images = values(detail_images), material = values(material), size_info = values(size_info),
                color_info = values(color_info), style_info = values(style_info), description = values(description), update_time = now()
            """)
    int upsert(ProductDetail productDetail);

    @Delete("delete from pms_product_detail where product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
}
