package com.mall.mapper;

import com.mall.entity.Address;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface AddressMapper {

    @Select("""
            select id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time, update_time
            from ums_address
            where id = #{id} and user_id = #{userId}
            limit 1
            """)
    Address selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("""
            select id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time, update_time
            from ums_address
            where user_id = #{userId}
            order by is_default desc, id desc
            """)
    List<Address> selectListByUserId(@Param("userId") Long userId);

    @Select("""
            select id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time, update_time
            from ums_address
            where user_id = #{userId} and is_default = 1
            limit 1
            """)
    Address selectDefaultByUserId(@Param("userId") Long userId);

    @Insert("""
            insert into ums_address(user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time, update_time)
            values(#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detailAddress}, #{isDefault}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    @Update("""
            update ums_address
            set receiver_name = #{receiverName}, receiver_phone = #{receiverPhone}, province = #{province},
                city = #{city}, district = #{district}, detail_address = #{detailAddress}, is_default = #{isDefault}, update_time = now()
            where id = #{id} and user_id = #{userId}
            """)
    int updateByIdAndUserId(Address address);

    @Delete("delete from ums_address where id = #{id} and user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Update("update ums_address set is_default = 0, update_time = now() where user_id = #{userId}")
    int clearDefault(@Param("userId") Long userId);

    @Update("update ums_address set is_default = 1, update_time = now() where id = #{id} and user_id = #{userId}")
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
}
