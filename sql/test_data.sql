-- 测试数据
USE furniture_mall;

-- 普通用户（明文密码，后端启动时 DataInitializer 会自动升级为 BCrypt）
INSERT INTO ums_user(username, password, nickname, phone, status) VALUES
('test', '123456', '测试用户', '13800138000', 1),
('zhangsan', '123456', '张三', '13900139000', 1),
('lisi', '123456', '李四', '13700137000', 1);

-- 商品分类
INSERT INTO pms_category(parent_id, name, sort, icon, status) VALUES
(0, '沙发', 1, NULL, 1),
(0, '床/床架', 2, NULL, 1),
(0, '餐桌', 3, NULL, 1),
(0, '衣柜', 4, NULL, 1),
(0, '座椅', 5, NULL, 1),
(0, '茶几/边几', 6, NULL, 1),
(0, '书桌', 7, NULL, 1),
(0, '床头柜', 8, NULL, 1);

-- Banner 轮播图
INSERT INTO cms_banner(title, image_url, link_url, sort, status) VALUES
('新品上市 全场8折', '/uploads/banner_1.jpg', NULL, 1, 1),
('北欧简约风 精选家居', '/uploads/banner_2.jpg', NULL, 2, 1),
('客厅焕新计划', '/uploads/banner_3.jpg', NULL, 3, 1);

-- 商品数据
INSERT INTO pms_product(category_id, name, sub_title, cover_image, price, stock, sales, status, is_hot, is_new) VALUES
-- 沙发（category_id=1）
(1, '北欧布艺三人沙发', '简约现代 客厅小户型', '/uploads/product_sofa_1.jpg', 3599.00, 50, 128, 1, 1, 0),
(1, '意式真皮转角沙发', '头层牛皮 L型组合', '/uploads/product_sofa_2.jpg', 8999.00, 20, 56, 1, 1, 1),
(1, '日式原木双人沙发', '实木框架 棉麻面料', '/uploads/product_sofa_3.jpg', 2899.00, 35, 89, 1, 0, 0),

-- 床/床架（category_id=2）
(2, '北欧实木双人床1.8m', '橡木框架 环保漆', '/uploads/product_bed_1.jpg', 2699.00, 40, 167, 1, 1, 0),
(2, '轻奢皮艺软包床', '真皮靠背 1.5m/1.8m', '/uploads/product_bed_2.jpg', 4599.00, 25, 78, 1, 0, 1),

-- 餐桌（category_id=3）
(3, '北欧白橡木餐桌', '1.4m伸缩饭桌', '/uploads/product_table_1.jpg', 1899.00, 60, 234, 1, 1, 0),
(3, '现代岩板餐桌椅组合', '火烧石面 一桌六椅', '/uploads/product_table_2.jpg', 3299.00, 30, 102, 1, 1, 1),
(3, '日式原木折叠餐桌', '小户型省空间', '/uploads/product_table_3.jpg', 1299.00, 80, 345, 1, 0, 0),

-- 衣柜（category_id=4）
(4, '现代简约推拉门衣柜', '六门大衣柜 环保板', '/uploads/product_wardrobe_1.jpg', 2999.00, 35, 91, 1, 0, 0),
(4, '北欧实木四门衣柜', '橡木材质 大容量', '/uploads/product_wardrobe_2.jpg', 4299.00, 20, 45, 1, 0, 1),

-- 座椅（category_id=5）
(5, '北欧实木餐椅', '温莎椅 实木框架', '/uploads/product_chair_1.jpg', 399.00, 200, 567, 1, 1, 0),
(5, '人体工学办公椅', '网布透气 可升降', '/uploads/product_chair_2.jpg', 899.00, 100, 423, 1, 1, 1),
(5, '伊姆斯休闲椅', '复古设计 客厅单人沙发', '/uploads/product_chair_3.jpg', 1299.00, 50, 178, 1, 0, 1),

-- 茶几/边几（category_id=6）
(6, '北欧圆形茶几', '白橡木 双层设计', '/uploads/product_coffee_1.jpg', 899.00, 70, 256, 1, 1, 0),
(6, '大理石面茶几', '轻奢风 烤漆框架', '/uploads/product_coffee_2.jpg', 1599.00, 40, 134, 1, 0, 1),
(6, '北欧原木边几', '床头小圆桌', '/uploads/product_coffee_3.jpg', 499.00, 150, 389, 1, 0, 0),

-- 书桌（category_id=7）
(7, '北欧实木书桌', '1.2m 简约办公桌', '/uploads/product_desk_1.jpg', 1099.00, 60, 198, 1, 1, 0),
(7, '现代烤漆电脑桌', 'L型转角 大桌面', '/uploads/product_desk_2.jpg', 1399.00, 45, 112, 1, 0, 1),

-- 床头柜（category_id=8）
(8, '北欧实木床头柜', '双抽屉 储物收纳', '/uploads/product_nightstand_1.jpg', 599.00, 100, 278, 1, 0, 0),
(8, '简约烤漆床头柜', '金色把手 小户型', '/uploads/product_nightstand_2.jpg', 459.00, 120, 312, 1, 0, 1);
