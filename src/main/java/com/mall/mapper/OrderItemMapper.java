package com.mall.mapper;

import com.mall.entity.OrderItem;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderItemMapper {

    @Insert("""
            <script>
            insert into oms_order_item(order_id, order_no, product_id, product_name, product_image, product_price, quantity, total_price, create_time, update_time)
            values
            <foreach collection="items" item="item" separator=",">
                (#{item.orderId}, #{item.orderNo}, #{item.productId}, #{item.productName}, #{item.productImage}, #{item.productPrice},
                 #{item.quantity}, #{item.totalPrice}, now(), now())
            </foreach>
            </script>
            """)
    int batchInsert(@Param("items") List<OrderItem> items);

    @Select("""
            select id, order_id, order_no, product_id, product_name, product_image, product_price, quantity, total_price, create_time, update_time
            from oms_order_item
            where order_no = #{orderNo}
            order by id asc
            """)
    List<OrderItem> selectByOrderNo(@Param("orderNo") String orderNo);
}
