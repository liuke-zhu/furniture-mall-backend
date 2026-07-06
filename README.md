# furniture-mall-backend

> 家具商城后端 — 面向大学生的 Spring Boot 3 电商实战项目，覆盖用户·商品·购物车·订单·支付·后台管理，以及 JWT 鉴权、Redis 缓存、AOP 日志、幂等、Docker 部署等工程化能力。

[![CI](https://github.com/liuke-zhu/furniture-mall-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/liuke-zhu/furniture-mall-backend/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## 前置要求

| 工具 | 版本 |
|------|------|
| JDK | 21 |
| Maven | 3.9+（或使用项目自带的 `./mvnw`） |
| MySQL | 8.0 |
| Redis | 7 |
| Docker（可选） | 20+ |

> **安全提示**：默认密码 `123456` 和演示 JWT 密钥仅供本地学习，**禁止直接部署到公网生产环境**。

## 快速启动

### 方式一：Docker（推荐）

```bash
git clone <your-repo-url>
cd furniture-mall-backend
docker compose up -d
```

启动后：
- 后端 API：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html（仅 dev 环境）
- 默认管理员：`admin / 123456`
- 测试用户：`test / 123456`

Docker 会自动初始化数据库（含 21 个商品、3 个 Banner）和演示图片。

如需同时启动前端：

```bash
docker compose --profile full up -d
```

> 前端需克隆配套仓库 `furniture-mall-frontend` 到同级目录。

### 方式二：本地开发

```bash
# 1. 复制环境变量模板
cp .env.example .env

# 2. 导入数据库
mysql -u root -p < sql/furniture_mall.sql
mysql -u root -p < sql/test_data.sql

# 3. 准备演示图片（二选一）
python scripts/generate_demo_images.py   # 离线生成占位图
# 或直接使用已提交的 demo-images/ 目录：
# Windows: xcopy demo-images uploads\ /Y
# Linux/Mac: cp demo-images/* uploads/

# 4. 启动
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

前端单独启动见配套仓库 `furniture-mall-frontend`。

## 技术栈

| 层次 | 选型 |
|------|------|
| 语言/框架 | Java 21 + Spring Boot 3.3.1 |
| ORM | MyBatis 3.0.3（注解式 SQL） |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 鉴权 | JWT + 拦截器（USER / ADMIN 双角色） |
| 密码 | BCrypt |
| 文档 | springdoc-openapi (Swagger UI) |
| 测试 | JUnit 5 + Mockito + AssertJ |
| 构建 | Maven + Maven Wrapper |
| 容器 | Docker 多阶段构建 |

## 项目结构

```text
src/main/java/com/mall/
├── controller/app|admin/    # 用户端 + 管理端 REST API
├── service/impl/            # 业务逻辑
├── mapper/                  # MyBatis Mapper
├── config/                  # Web/CORS/Redis/JWT 等配置
├── common/                  # 拦截器、AOP、异常、工具类
sql/
├── furniture_mall.sql       # 建库建表 + 默认管理员
├── test_data.sql            # 分类/商品/Banner/测试用户
demo-images/                 # 演示占位图（Docker 自动使用）
scripts/                     # 图片生成/下载工具
```

## 默认账号

| 角色 | 账号 | 密码 | 登录接口 |
|------|------|------|----------|
| 管理员 | admin | 123456 | `POST /api/admin/auth/login` |
| 普通用户 | test | 123456 | `POST /api/auth/login` |

启动时 `DataInitializer` 会自动将明文密码升级为 BCrypt。

## 环境变量

复制 `.env.example` 为 `.env` 后修改：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_URL` | localhost:3306 | 数据库连接 |
| `DB_USERNAME` | root | 数据库用户 |
| `DB_PASSWORD` | 123456 | 数据库密码 |
| `REDIS_HOST` | localhost | Redis 地址 |
| `REDIS_PORT` | 6379 | Redis 端口 |
| `JWT_SECRET` | 演示密钥 | **生产必改** |
| `CORS_ALLOWED_ORIGINS` | localhost 系列 | 允许的前端地址 |

## 接口文档

开发环境访问 http://localhost:8080/swagger-ui.html

- 用户端：注册/登录、首页、商品、购物车、订单、地址、收藏、上传
- 管理端：商品/分类/订单/用户/Banner/仪表盘
- 其他：`GET /api/health` 健康检查

## 测试

```bash
./mvnw test
```

| 测试类 | 用例数 | 覆盖范围 |
|--------|--------|----------|
| `PasswordUtilsTest` | 6 | 密码加密/匹配 |
| `AuthServiceImplTest` | 7 | 注册/登录 |
| `CartServiceImplTest` | 6 | 购物车 |
| `OrderServiceImplTest` | 7 | 支付/取消/确认 |

共 26 个用例，纯 Mock，不依赖 MySQL/Redis。

## 常见问题

**Q: 启动报数据库连接失败？**
确保 MySQL 已启动且已导入 `sql/furniture_mall.sql`。Docker 用户等待 mysql 健康检查通过。

**Q: 商品图片显示不出来？**
本地开发需运行 `python scripts/generate_demo_images.py` 或复制 `demo-images/` 到 `uploads/`。

**Q: 管理员和普通用户登录接口不同？**
管理员走 `/api/admin/auth/login`，普通用户走 `/api/auth/login`，不要混用。

**Q: 支付是真实的吗？**
不是，`PUT /api/order/pay/{orderNo}` 是 Mock 支付，用于演示订单状态流转。

## 学习路线建议

1. 从 `AuthController` + `JwtAuthInterceptor` 理解 JWT 鉴权
2. 看 `OrderServiceImpl` 学习事务 + 库存扣减
3. 看 `IdempotentAspect` 理解 Redis 幂等
4. 看 `AdminDashboardServiceImpl` 学习统计 SQL
5. 用 Swagger 逐个接口调试

## 开源协议

本项目采用 [Apache License 2.0](LICENSE) 开源。

## 贡献

欢迎提交 Issue 和 PR，详见 [CONTRIBUTING.md](CONTRIBUTING.md)。
