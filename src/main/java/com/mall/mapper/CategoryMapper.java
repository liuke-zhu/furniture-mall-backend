package com.mall.mapper;

import com.mall.entity.Category;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper {

    @Select("select id, parent_id, name, sort, icon, status, create_time, update_time from pms_category where status = 1 order by sort asc, id asc")
    List<Category> selectEnabledList();

    @Select("""
            <script>
            select id, parent_id, name, sort, icon, status, create_time, update_time
            from pms_category
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            order by sort asc, id asc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<Category> selectAdminPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from pms_category
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            </script>
            """)
    long countAdmin(@Param("keyword") String keyword);

    @Select("select id, parent_id, name, sort, icon, status, create_time, update_time from pms_category where id = #{id} limit 1")
    Category selectById(@Param("id") Long id);

    @Insert("""
            insert into pms_category(parent_id, name, sort, icon, status, create_time, update_time)
            values(#{parentId}, #{name}, #{sort}, #{icon}, #{status}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("""
            update pms_category
            set parent_id = #{parentId}, name = #{name}, sort = #{sort}, icon = #{icon}, status = #{status}, update_time = now()
            where id = #{id}
            """)
    int updateById(Category category);

    @Update("update pms_category set status = #{status}, update_time = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("update pms_category set status = 0, update_time = now() where id = #{id}")
    int deleteById(@Param("id") Long id);
}
