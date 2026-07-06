"""Generate demo product images that match furniture categories."""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
UPLOAD_DIR = ROOT / "uploads"
DEMO_DIR = ROOT / "demo-images"

# (filename, title, subtitle, category_color, accent_color)
IMAGES = [
    ("banner_1.jpg", "新品上市 全场8折", "精选家居好物", (45, 52, 64), (201, 169, 110)),
    ("banner_2.jpg", "北欧简约风", "精选家居", (62, 78, 92), (220, 210, 195)),
    ("banner_3.jpg", "客厅焕新计划", "打造理想生活空间", (74, 85, 104), (180, 160, 130)),
    ("product_sofa_1.jpg", "北欧布艺三人沙发", "简约现代 客厅小户型", (196, 176, 148), (120, 92, 68)),
    ("product_sofa_2.jpg", "意式真皮转角沙发", "头层牛皮 L型组合", (88, 72, 58), (180, 140, 95)),
    ("product_sofa_3.jpg", "日式原木双人沙发", "实木框架 棉麻面料", (210, 190, 165), (130, 100, 72)),
    ("product_bed_1.jpg", "北欧实木双人床", "橡木框架 环保漆", (220, 225, 235), (140, 160, 185)),
    ("product_bed_2.jpg", "轻奢皮艺软包床", "真皮靠背 1.5m/1.8m", (185, 175, 165), (110, 95, 82)),
    ("product_table_1.jpg", "北欧白橡木餐桌", "1.4m 伸缩饭桌", (210, 185, 150), (130, 95, 60)),
    ("product_table_2.jpg", "现代岩板餐桌椅组合", "火烧石面 一桌六椅", (95, 95, 98), (170, 170, 175)),
    ("product_table_3.jpg", "日式原木折叠餐桌", "小户型省空间", (200, 175, 140), (120, 90, 58)),
    ("product_wardrobe_1.jpg", "现代简约推拉门衣柜", "六门大衣柜 环保板", (175, 180, 188), (110, 118, 128)),
    ("product_wardrobe_2.jpg", "北欧实木四门衣柜", "橡木材质 大容量", (195, 180, 158), (120, 95, 68)),
    ("product_chair_1.jpg", "北欧实木餐椅", "温莎椅 实木框架", (205, 185, 155), (125, 95, 62)),
    ("product_chair_2.jpg", "人体工学办公椅", "网布透气 可升降", (70, 78, 92), (120, 145, 175)),
    ("product_chair_3.jpg", "伊姆斯休闲椅", "复古设计 客厅单人沙发", (210, 175, 120), (150, 95, 55)),
    ("product_coffee_1.jpg", "北欧圆形茶几", "白橡木 双层设计", (215, 200, 180), (140, 110, 78)),
    ("product_coffee_2.jpg", "大理石面茶几", "轻奢风 烤漆框架", (205, 205, 210), (150, 150, 158)),
    ("product_coffee_3.jpg", "北欧原木边几", "床头小圆桌", (200, 180, 155), (125, 98, 68)),
    ("product_desk_1.jpg", "北欧实木书桌", "1.2m 简约办公桌", (195, 175, 145), (115, 88, 58)),
    ("product_desk_2.jpg", "现代烤漆电脑桌", "L型转角 大桌面", (88, 92, 98), (145, 150, 160)),
    ("product_nightstand_1.jpg", "北欧实木床头柜", "双抽屉 储物收纳", (205, 185, 160), (125, 98, 68)),
    ("product_nightstand_2.jpg", "简约烤漆床头柜", "金色把手 小户型", (225, 220, 210), (170, 145, 95)),
]

DRAWERS = {
    "banner": "draw_banner",
    "product_sofa": "draw_sofa",
    "product_bed": "draw_bed",
    "product_table": "draw_table",
    "product_wardrobe": "draw_wardrobe",
    "product_chair": "draw_chair",
    "product_coffee": "draw_coffee_table",
    "product_desk": "draw_desk",
    "product_nightstand": "draw_nightstand",
}


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        "C:/Windows/Fonts/msyhbd.ttc" if bold else "C:/Windows/Fonts/msyh.ttc",
        "C:/Windows/Fonts/simhei.ttf",
        "C:/Windows/Fonts/arial.ttf",
    ]
    for path in candidates:
        if Path(path).exists():
            return ImageFont.truetype(path, size=size)
    return ImageFont.load_default()


def blend(base: tuple[int, int, int], accent: tuple[int, int, int], ratio: float) -> tuple[int, int, int]:
    return tuple(int(base[i] * (1 - ratio) + accent[i] * ratio) for i in range(3))


def draw_floor(draw: ImageDraw.ImageDraw, w: int, h: int, color: tuple[int, int, int]) -> None:
    draw.rectangle((0, int(h * 0.72), w, h), fill=blend(color, (255, 255, 255), 0.15))
    draw.line((0, int(h * 0.72), w, int(h * 0.72)), fill=blend(color, (0, 0, 0), 0.12), width=2)


def draw_sofa(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    body = blend(base, accent, 0.35)
    cx, cy = w // 2, int(h * 0.58)
    draw.rounded_rectangle((cx - 180, cy - 45, cx + 180, cy + 55), radius=18, fill=body)
    draw.rounded_rectangle((cx - 210, cy - 70, cx - 150, cy + 20), radius=14, fill=blend(body, accent, 0.2))
    draw.rounded_rectangle((cx + 150, cy - 70, cx + 210, cy + 20), radius=14, fill=blend(body, accent, 0.2))
    draw.rounded_rectangle((cx - 170, cy - 95, cx + 170, cy - 45), radius=12, fill=blend(body, (255, 255, 255), 0.08))


def draw_bed(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    cx, cy = w // 2, int(h * 0.58)
    frame = blend(base, accent, 0.4)
    draw.rounded_rectangle((cx - 190, cy - 20, cx + 190, cy + 70), radius=10, fill=frame)
    draw.rounded_rectangle((cx - 190, cy - 70, cx - 120, cy + 70), radius=12, fill=blend(frame, accent, 0.25))
    draw.rounded_rectangle((cx - 170, cy - 10, cx + 170, cy + 45), radius=8, fill=(245, 247, 250))
    draw.ellipse((cx - 150, cy - 5, cx - 90, cy + 35), fill=(255, 255, 255))
    draw.ellipse((cx + 90, cy - 5, cx + 150, cy + 35), fill=(255, 255, 255))


def draw_table(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    top = blend(base, accent, 0.45)
    cx, cy = w // 2, int(h * 0.55)
    draw.rounded_rectangle((cx - 170, cy - 60, cx + 170, cy - 25), radius=8, fill=top)
    for x in (cx - 140, cx + 120):
        draw.rounded_rectangle((x, cy - 25, x + 18, cy + 55), radius=4, fill=blend(top, accent, 0.15))


def draw_wardrobe(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    body = blend(base, accent, 0.35)
    cx, cy = w // 2, int(h * 0.52)
    draw.rounded_rectangle((cx - 130, cy - 120, cx + 130, cy + 100), radius=10, fill=body)
    draw.line((cx, cy - 120, cx, cy + 100), fill=blend(body, (255, 255, 255), 0.25), width=3)
    draw.line((cx - 130, cy - 35, cx + 130, cy - 35), fill=blend(body, (255, 255, 255), 0.18), width=2)
    for x in (cx - 55, cx + 45):
        draw.ellipse((x, cy + 10, x + 12, cy + 22), fill=accent)


def draw_chair(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    seat = blend(base, accent, 0.4)
    cx, cy = w // 2, int(h * 0.58)
    draw.rounded_rectangle((cx - 70, cy - 10, cx + 70, cy + 35), radius=10, fill=seat)
    draw.rounded_rectangle((cx - 55, cy - 85, cx + 55, cy - 5), radius=12, fill=blend(seat, accent, 0.2))
    for x in (cx - 55, cx + 37):
        draw.rounded_rectangle((x, cy + 35, x + 12, cy + 95), radius=3, fill=blend(seat, accent, 0.15))


def draw_coffee_table(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    top = blend(base, accent, 0.4)
    cx, cy = w // 2, int(h * 0.62)
    draw.ellipse((cx - 95, cy - 35, cx + 95, cy + 35), fill=top)
    draw.ellipse((cx - 18, cy + 35, cx + 18, cy + 95), fill=blend(top, accent, 0.2))


def draw_desk(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    top = blend(base, accent, 0.45)
    cx, cy = w // 2, int(h * 0.56)
    draw.rounded_rectangle((cx - 180, cy - 45, cx + 180, cy - 10), radius=6, fill=top)
    draw.rounded_rectangle((cx + 40, cy - 10, cx + 180, cy + 55), radius=6, fill=blend(top, accent, 0.15))
    for x in (cx - 150, cx - 20, cx + 70, cx + 150):
        draw.rounded_rectangle((x, cy - 10, x + 12, cy + 75), radius=3, fill=blend(top, accent, 0.15))
    draw.rounded_rectangle((cx - 120, cy - 95, cx - 40, cy - 45), radius=4, fill=(210, 215, 225))


def draw_nightstand(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    body = blend(base, accent, 0.35)
    cx, cy = w // 2, int(h * 0.58)
    draw.rounded_rectangle((cx - 65, cy - 60, cx + 65, cy + 70), radius=8, fill=body)
    draw.line((cx - 65, cy - 5, cx + 65, cy - 5), fill=blend(body, (255, 255, 255), 0.2), width=2)
    draw.ellipse((cx + 35, cy + 15, cx + 47, cy + 27), fill=accent)


def draw_banner(draw: ImageDraw.ImageDraw, w: int, h: int, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    draw.rounded_rectangle((80, 70, w - 80, h - 70), radius=24, fill=blend(base, accent, 0.25))
    draw.rounded_rectangle((120, 110, w - 220, h - 110), radius=18, fill=blend(base, (255, 255, 255), 0.12))
    draw.ellipse((w - 260, 90, w - 90, h - 90), fill=blend(accent, (255, 255, 255), 0.25))


def pick_drawer(name: str):
    if name.startswith("banner"):
        return draw_banner
    for prefix, func_name in DRAWERS.items():
        if name.startswith(prefix):
            return globals()[func_name]
    return draw_sofa


def render_image(filename: str, title: str, subtitle: str, base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    is_banner = filename.startswith("banner")
    size = (1200, 450) if is_banner else (800, 600)
    image = Image.new("RGB", size, blend(base, (255, 255, 255), 0.35))
    draw = ImageDraw.Draw(image)

    for i in range(0, size[1], 24):
        alpha = 0.03 if i % 48 == 0 else 0.015
        draw.line((0, i, size[0], i), fill=blend(base, accent, alpha), width=1)

    if not is_banner:
        draw_floor(draw, size[0], size[1], base)
    pick_drawer(filename.replace(".jpg", ""))(draw, size[0], size[1], base, accent)

    title_font = load_font(34 if is_banner else 30, bold=True)
    sub_font = load_font(20 if is_banner else 18)
    title_y = 52 if is_banner else 36
    draw.text((48, title_y), title, fill=(35, 40, 48), font=title_font)
    draw.text((48, title_y + (44 if is_banner else 38)), subtitle, fill=(90, 98, 110), font=sub_font)

    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    DEMO_DIR.mkdir(parents=True, exist_ok=True)
    image.save(UPLOAD_DIR / filename, format="JPEG", quality=92, optimize=True)
    image.save(DEMO_DIR / filename, format="JPEG", quality=92, optimize=True)


def main() -> None:
    for item in IMAGES:
        render_image(*item)
        print(f"generated {item[0]}")


if __name__ == "__main__":
    main()
