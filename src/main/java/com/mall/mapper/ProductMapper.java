package com.mall.mapper;

import com.mall.entity.Product;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {

    @Select("select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time from pms_product where status = 1 and is_hot = 1 order by sales desc, id desc limit #{limit}")
    List<Product> selectHotList(int limit);

    @Select("select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time from pms_product where status = 1 and is_new = 1 order by id desc limit #{limit}")
    List<Product> selectNewList(int limit);

    @Select("""
            select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time
            from pms_product
            where status = 1
            order by id desc
            limit #{offset}, #{pageSize}
            """)
    List<Product> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(1) from pms_product where status = 1")
    long countEnabled();

    @Select("""
            <script>
            select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time
            from pms_product
            where status = 1
            <if test="categoryId != null">
                and category_id = #{categoryId}
            </if>
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            order by id desc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<Product> selectSearchPage(@Param("categoryId") Long categoryId, @Param("keyword") String keyword,
                                   @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from pms_product
            where status = 1
            <if test="categoryId != null">
                and category_id = #{categoryId}
            </if>
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            </script>
            """)
    long countSearch(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Select("""
            select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time
            from pms_product
            where id = #{id} and status = 1
            limit 1
            """)
    Product selectById(@Param("id") Long id);

    @Update("""
            update pms_product
            set stock = stock - #{quantity}, sales = sales + #{quantity}, update_time = now()
            where id = #{productId} and stock >= #{quantity} and status = 1
            """)
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Select("""
            <script>
            select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time
            from pms_product
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            <if test="categoryId != null">
                and category_id = #{categoryId}
            </if>
            <if test="status != null">
                and status = #{status}
            </if>
            order by id desc
            limit #{offset}, #{pageSize}
            </script>
            """)
    List<Product> selectAdminPage(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                                  @Param("status") Integer status, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
            <script>
            select count(1)
            from pms_product
            where 1 = 1
            <if test="keyword != null and keyword != ''">
                and name like concat('%', #{keyword}, '%')
            </if>
            <if test="categoryId != null">
                and category_id = #{categoryId}
            </if>
            <if test="status != null">
                and status = #{status}
            </if>
            </script>
            """)
    long countAdmin(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("status") Integer status);

    @Select("""
            select id, category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time
            from pms_product
            where id = #{id}
            limit 1
            """)
    Product selectAdminById(@Param("id") Long id);

    @Insert("""
            insert into pms_product(category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new, create_time, update_time)
            values(#{categoryId}, #{name}, #{subTitle}, #{coverImage}, #{price}, #{stock}, #{sales}, #{status}, #{isHot}, #{isNew}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("""
            update pms_product
            set category_id = #{categoryId}, name = #{name}, sub_title = #{subTitle}, cover_image = #{coverImage},
                price = #{price}, stock = #{stock}, status = #{status}, is_hot = #{isHot}, is_new = #{isNew}, update_time = now()
            where id = #{id}
            """)
    int updateById(Product product);

    @Update("update pms_product set status = #{status}, update_time = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("update pms_product set status = 2, update_time = now() where id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("""
            update pms_product
            set stock = stock + #{quantity}, sales = sales - #{quantity}, update_time = now()
            where id = #{productId}
            """)
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
