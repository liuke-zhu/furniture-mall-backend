package com.mall.mapper;

import com.mall.entity.Banner;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BannerMapper {

    @Select("select id, title, image_url, link_url, sort, status, create_time, update_time from cms_banner where status = 1 order by sort asc, id desc")
    List<Banner> selectEnabledList();

    @Select("""
            <script>
            select id, title, image_url, link_url, sort, status, create_time, update_time
            from cms_banner
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and title like concat('%', #{keyword}, '%')
            </if>
            order by sort asc, id desc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<Banner> selectAdminPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from cms_banner
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and title like concat('%', #{keyword}, '%')
            </if>
            </script>
            """)
    long countAdmin(@Param("keyword") String keyword);

    @Select("select id, title, image_url, link_url, sort, status, create_time, update_time from cms_banner where id = #{id} limit 1")
    Banner selectById(@Param("id") Long id);

    @Insert("""
            insert into cms_banner(title, image_url, link_url, sort, status, create_time, update_time)
            values(#{title}, #{imageUrl}, #{linkUrl}, #{sort}, #{status}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    @Update("""
            update cms_banner
            set title = #{title}, image_url = #{imageUrl}, link_url = #{linkUrl}, sort = #{sort}, status = #{status}, update_time = now()
            where id = #{id}
            """)
    int updateById(Banner banner);

    @Update("update cms_banner set status = #{status}, update_time = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("delete from cms_banner where id = #{id}")
    int deleteById(@Param("id") Long id);
}
