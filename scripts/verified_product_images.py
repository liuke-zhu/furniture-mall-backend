"""
按商品名称使用已核对 Pexels 页面 ID 下载封面图。
每个 ID 均来自对应 slug 页面，优先「纯家具、无人」。
"""

from __future__ import annotations

import json
import shutil
import ssl
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
IMAGE_DIR = ROOT / "image"
UPLOAD_DIR = ROOT / "uploads"
META_FILE = IMAGE_DIR / "sources.json"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Referer": "https://www.pexels.com/",
}

# filename -> (商品名, pexels_id, 页面描述, 宽, 高)
VERIFIED: dict[str, tuple[str, int, str, int, int]] = {
    "banner_1.jpg": ("新品上市 全场8折", 1571468, "gray fabric sofa living room", 1200, 450),
    "banner_2.jpg": ("北欧简约风 精选家居", 1866144, "scandinavian living room sofa", 1200, 450),
    "banner_3.jpg": ("客厅焕新计划", 276583, "modern sectional sofa living room", 1200, 450),
    "product_sofa_1.jpg": ("北欧布艺三人沙发", 15226148, "gray fabric sofa close-up no people", 800, 600),
    "product_sofa_2.jpg": ("意式真皮转角沙发", 276583, "black leather sectional sofa", 800, 600),
    "product_sofa_3.jpg": ("日式原木双人沙发", 1866144, "wooden scandinavian sofa living room", 800, 600),
    "product_bed_1.jpg": ("北欧实木双人床1.8m", 1454806, "modern bedroom wooden bed", 800, 600),
    "product_bed_2.jpg": ("轻奢皮艺软包床", 90319, "upholstered white bed headboard", 800, 600),
    "product_table_1.jpg": ("北欧白橡木餐桌", 17205673, "empty wooden dining table and chairs", 800, 600),
    "product_table_2.jpg": ("现代岩板餐桌椅组合", 34298819, "modern dining table with chairs set", 800, 600),
    "product_table_3.jpg": ("日式原木折叠餐桌", 3935331, "folding wooden dining table", 800, 600),
    "product_wardrobe_1.jpg": ("现代简约推拉门衣柜", 6585617, "walk-in wardrobe closet nobody", 800, 600),
    "product_wardrobe_2.jpg": ("北欧实木四门衣柜", 19962637, "classic wooden wardrobe cabinet", 800, 600),
    "product_chair_1.jpg": ("北欧实木餐椅", 11112733, "wooden dining chair white background", 800, 600),
    "product_chair_2.jpg": ("人体工学办公椅", 1957477, "office chair and desk empty", 800, 600),
    "product_chair_3.jpg": ("伊姆斯休闲椅", 0, "eames lounge chair wikimedia", 800, 600),
    "product_coffee_1.jpg": ("北欧圆形茶几", 279607, "brown round coffee table living room", 800, 600),
    "product_coffee_2.jpg": ("大理石面茶几", 33794454, "round marble coffee table", 800, 600),
    "product_coffee_3.jpg": ("北欧原木边几", 6943418, "bedside side table furniture", 800, 600),
    "product_desk_1.jpg": ("北欧实木书桌", 1957477, "wooden office desk setup", 800, 600),
    "product_desk_2.jpg": ("现代烤漆电脑桌", 667838, "modern computer desk workspace", 800, 600),
    "product_nightstand_1.jpg": ("北欧实木床头柜", 6266189, "wooden bedside table with books", 800, 600),
    "product_nightstand_2.jpg": ("简约烤漆床头柜", 6943418, "white nightstand with lamp", 800, 600),
}

# 特殊：伊姆斯备用 Wikimedia（纯家具）
WIKIMEDIA_FALLBACK = {
    "product_chair_3.jpg": "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Eames_lounge_chair_%28cropped%29.jpg/800px-Eames_lounge_chair_%28cropped%29.jpg",
}


def pexels_url(photo_id: int, width: int, height: int) -> str:
    return (
        f"https://images.pexels.com/photos/{photo_id}/pexels-photo-{photo_id}.jpeg"
        f"?auto=compress&cs=tinysrgb&w={width}&h={height}&fit=crop"
    )


def download_url(url: str, timeout: int = 120) -> bytes:
    request = urllib.request.Request(url, headers=HEADERS)
    context = ssl.create_default_context()
    with urllib.request.urlopen(request, context=context, timeout=timeout) as response:
        data = response.read()
    if len(data) < 8000:
        raise RuntimeError(f"too small ({len(data)} bytes)")
    return data


def save(filename: str, data: bytes) -> None:
    IMAGE_DIR.mkdir(parents=True, exist_ok=True)
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    target = IMAGE_DIR / filename
    target.write_bytes(data)
    shutil.copy2(target, UPLOAD_DIR / filename)


def download_one(filename: str, cn_name: str, photo_id: int, desc: str, width: int, height: int) -> None:
    if photo_id == 0 and filename in WIKIMEDIA_FALLBACK:
        data = download_url(WIKIMEDIA_FALLBACK[filename])
        save(filename, data)
        return
    last_error: Exception | None = None
    for attempt in range(3):
        try:
            data = download_url(pexels_url(photo_id, width, height))
            save(filename, data)
            return
        except Exception as exc:
            last_error = exc
            time.sleep(2)
    if filename in WIKIMEDIA_FALLBACK:
        try:
            data = download_url(WIKIMEDIA_FALLBACK[filename])
            save(filename, data)
            return
        except Exception as exc:
            last_error = exc
    raise RuntimeError(str(last_error))


def main() -> None:
    meta: dict[str, dict] = {}
    ok = 0
    failed: list[str] = []

    for filename, (cn_name, photo_id, desc, width, height) in VERIFIED.items():
        print(f"[{cn_name}] pexels:{photo_id} - {desc}")
        try:
            download_one(filename, cn_name, photo_id, desc, width, height)
            meta[filename] = {"name": cn_name, "pexels_id": photo_id, "description": desc}
            ok += 1
            print(f"  OK")
        except Exception as exc:
            failed.append(filename)
            print(f"  FAIL: {exc}")
        time.sleep(0.5)

    META_FILE.write_text(json.dumps(meta, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"\n完成 {ok}/{len(VERIFIED)}")
    if failed:
        raise SystemExit("失败: " + ", ".join(failed))


if __name__ == "__main__":
    main()
