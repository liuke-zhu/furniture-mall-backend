# 图片资源脚本

克隆仓库后商品图默认不存在，可用以下方式准备：

## 方式一：离线生成占位图（推荐，无需联网）

```bash
pip install -r scripts/requirements.txt
python scripts/generate_demo_images.py
```

图片会生成到 `uploads/` 目录，与 `sql/test_data.sql` 中的路径一致。

## 方式二：从 Pexels 下载真实图片（需 API Key）

1. 在 [Pexels API](https://www.pexels.com/api/) 申请免费 API Key
2. 设置环境变量 `PEXELS_API_KEY=你的key`
3. 运行：

```bash
python scripts/verified_product_images.py
# 或
python scripts/download_furniture_images.py
```

> Pexels 图片遵循 [Pexels License](https://www.pexels.com/license/)，可免费用于学习演示。

## 脚本说明

| 脚本 | 用途 |
|------|------|
| `generate_demo_images.py` | 用 Pillow 生成彩色占位图，离线可用 |
| `verified_product_images.py` | 按 `image/sources.json` 下载已验证图片 |
| `download_furniture_images.py` | 批量搜索下载家具图 |
| `fix_product_images.py` | 修复错配的商品封面 |
| `search_and_download_images.py` | 搜索并过滤含人物的图片 |

## Docker 用户

Docker 镜像已内置 `demo-images/` 目录中的占位图，无需手动生成。
