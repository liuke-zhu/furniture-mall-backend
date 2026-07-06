package com.mall.mapper;

import com.mall.entity.Order;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper {

    @Insert("""
            insert into oms_order(order_no, user_id, total_amount, pay_amount, freight_amount, order_status, pay_type, receiver_name, receiver_phone, receiver_address, remark, create_time, update_time)
            values(#{orderNo}, #{userId}, #{totalAmount}, #{payAmount}, #{freightAmount}, #{orderStatus}, #{payType}, #{receiverName}, #{receiverPhone}, #{receiverAddress}, #{remark}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("""
            select id, order_no, user_id, total_amount, pay_amount, freight_amount, order_status, pay_type, receiver_name, receiver_phone, receiver_address, remark, payment_time, create_time, update_time
            from oms_order
            where user_id = #{userId}
            order by id desc
            """)
    List<Order> selectByUserId(@Param("userId") Long userId);

    @Select("""
            select id, order_no, user_id, total_amount, pay_amount, freight_amount, order_status, pay_type, receiver_name, receiver_phone, receiver_address, remark, payment_time, create_time, update_time
            from oms_order
            where order_no = #{orderNo} and user_id = #{userId}
            limit 1
            """)
    Order selectByOrderNoAndUserId(@Param("orderNo") String orderNo, @Param("userId") Long userId);

    @Update("""
            update oms_order
            set order_status = #{status}, update_time = now()
            where order_no = #{orderNo} and user_id = #{userId}
            """)
    int updateStatus(@Param("orderNo") String orderNo, @Param("userId") Long userId, @Param("status") Integer status);

    @Select("""
            <script>
            select o.id, o.order_no, o.user_id, o.total_amount, o.pay_amount, o.freight_amount,
                   o.order_status, o.pay_type, o.receiver_name, o.receiver_phone, o.receiver_address,
                   o.remark, o.payment_time, o.create_time, o.update_time, u.username as username
            from oms_order o
            left join ums_user u on o.user_id = u.id
            where 1 = 1
            <if test="orderNo != null and orderNo != ''">
                and o.order_no = #{orderNo}
            </if>
            <if test="status != null">
                and o.order_status = #{status}
            </if>
            order by o.id desc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<Order> selectAdminPage(@Param("orderNo") String orderNo, @Param("status") Integer status,
                                @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from oms_order
            where 1 = 1
            <if test="orderNo != null and orderNo != ''">
                and order_no = #{orderNo}
            </if>
            <if test="status != null">
                and order_status = #{status}
            </if>
            </script>
            """)
    long countAdmin(@Param("orderNo") String orderNo, @Param("status") Integer status);

    @Select("""
            select o.id, o.order_no, o.user_id, o.total_amount, o.pay_amount, o.freight_amount,
                   o.order_status, o.pay_type, o.receiver_name, o.receiver_phone, o.receiver_address,
                   o.remark, o.payment_time, o.create_time, o.update_time, u.username as username
            from oms_order o
            left join ums_user u on o.user_id = u.id
            where o.order_no = #{orderNo}
            limit 1
            """)
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    @Update("update oms_order set order_status = #{status}, update_time = now() where order_no = #{orderNo}")
    int updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("status") Integer status);

    @Update("""
            update oms_order
            set order_status = 1, pay_type = #{payType}, payment_time = now(), update_time = now()
            where order_no = #{orderNo} and user_id = #{userId} and order_status = 0
            """)
    int updateToPaid(@Param("orderNo") String orderNo, @Param("userId") Long userId, @Param("payType") Integer payType);
}
