package com.mall.mapper;

import com.mall.vo.favorite.FavoriteResponse;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper {

    @Insert("""
            insert ignore into ums_favorite(user_id, product_id, create_time)
            values(#{userId}, #{productId}, now())
            """)
    int insert(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("delete from ums_favorite where user_id = #{userId} and product_id = #{productId}")
    int delete(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            select f.product_id, p.name as product_name, p.cover_image, p.price, p.status, f.create_time as favorite_time
            from ums_favorite f
            inner join pms_product p on f.product_id = p.id
            where f.user_id = #{userId}
            order by f.id desc
            """)
    List<FavoriteResponse> selectListByUserId(@Param("userId") Long userId);

    @Select("select count(1) from ums_favorite where user_id = #{userId} and product_id = #{productId}")
    long countByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
