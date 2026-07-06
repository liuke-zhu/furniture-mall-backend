package com.mall.mapper;

import com.mall.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select id, username, password, nickname, phone, email, avatar, status, create_time, update_time from ums_user where username = #{username} limit 1")
    User selectByUsername(@Param("username") String username);

    @Insert("""
            insert into ums_user(username, password, nickname, phone, status, create_time, update_time)
            values(#{username}, #{password}, #{nickname}, #{phone}, #{status}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("select id, username, password, nickname, phone, email, avatar, status, create_time, update_time from ums_user order by id asc")
    List<User> selectAll();

    @Update("update ums_user set password = #{password}, update_time = now() where id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Select("""
            <script>
            select id, username, password, nickname, phone, email, avatar, status, create_time, update_time
            from ums_user
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and (username like concat('%', #{keyword}, '%') or phone like concat('%', #{keyword}, '%'))
            </if>
            order by id desc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<User> selectAdminPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from ums_user
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and (username like concat('%', #{keyword}, '%') or phone like concat('%', #{keyword}, '%'))
            </if>
            </script>
            """)
    long countAdmin(@Param("keyword") String keyword);

    @Select("select id, username, password, nickname, phone, email, avatar, status, create_time, update_time from ums_user where id = #{id} limit 1")
    User selectById(@Param("id") Long id);

    @Update("update ums_user set status = #{status}, update_time = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
