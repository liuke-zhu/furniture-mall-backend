package com.mall.mapper;

import com.mall.entity.Admin;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {

    @Select("select id, username, password, nickname, role_name, status, create_time, update_time from ums_admin where username = #{username} limit 1")
    Admin selectByUsername(@Param("username") String username);

    @Select("select id, username, password, nickname, role_name, status, create_time, update_time from ums_admin order by id asc")
    List<Admin> selectAll();

    @Update("update ums_admin set password = #{password}, update_time = now() where id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
