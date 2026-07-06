package com.mall.mapper;

import com.mall.entity.Cart;
import com.mall.vo.cart.CartItemResponse;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CartMapper {

    @Select("""
            select id, user_id, product_id, quantity, checked, create_time, update_time
            from oms_cart
            where user_id = #{userId} and product_id = #{productId}
            limit 1
            """)
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Insert("""
            insert into oms_cart(user_id, product_id, quantity, checked, create_time, update_time)
            values(#{userId}, #{productId}, #{quantity}, #{checked}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);

    @Update("""
            update oms_cart
            set quantity = #{quantity}, checked = #{checked}, update_time = now()
            where id = #{id} and user_id = #{userId}
            """)
    int updateByIdAndUserId(Cart cart);

    @Select("""
            select c.id as cart_id, c.product_id, p.name as product_name, p.cover_image, p.price, p.stock, c.quantity, c.checked,
                   p.price * c.quantity as total_price
            from oms_cart c
            join pms_product p on c.product_id = p.id
            where c.user_id = #{userId} and p.status = 1
            order by c.id desc
            """)
    List<CartItemResponse> selectCartList(@Param("userId") Long userId);

    @Update("""
            <script>
            update oms_cart
            set checked = #{checked}, update_time = now()
            where user_id = #{userId}
            and id in
            <foreach collection="cartIds" item="cartId" open="(" separator="," close=")">
                #{cartId}
            </foreach>
            </script>
            """)
    int updateChecked(@Param("userId") Long userId, @Param("cartIds") List<Long> cartIds, @Param("checked") Integer checked);

    @Delete("delete from oms_cart where id = #{cartId} and user_id = #{userId}")
    int deleteByIdAndUserId(@Param("cartId") Long cartId, @Param("userId") Long userId);

    @Select("""
            select id, user_id, product_id, quantity, checked, create_time, update_time
            from oms_cart
            where user_id = #{userId} and checked = 1
            """)
    List<Cart> selectCheckedList(@Param("userId") Long userId);

    @Delete("""
            <script>
            delete from oms_cart
            where user_id = #{userId}
            and id in
            <foreach collection="cartIds" item="cartId" open="(" separator="," close=")">
                #{cartId}
            </foreach>
            </script>
            """)
    int deleteCheckedByIds(@Param("userId") Long userId, @Param("cartIds") List<Long> cartIds);
}
