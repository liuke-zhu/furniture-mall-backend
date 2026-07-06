"""
按商品名称搜索并下载「纯家具、无人」封面图。
优先 Pexels 搜索结果，跳过含人物关键词的图片。
"""

from __future__ import annotations

import json
import re
import shutil
import ssl
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
IMAGE_DIR = ROOT / "image"
UPLOAD_DIR = ROOT / "uploads"
META_FILE = IMAGE_DIR / "sources.json"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Referer": "https://www.pexels.com/",
    "Accept-Language": "en-US,en;q=0.9",
}

SKIP_KEYWORDS = (
    "person", "people", "woman", "man", "girl", "boy", "child", "family",
    "portrait", "model", "businesswoman", "businessman", "worker", "sitting on",
    "using laptop", "working", "reading", "sleeping", "couple", "friends",
)

# 文件名 -> (中文名, 英文搜索词, 宽, 高)
PRODUCTS: dict[str, tuple[str, str, int, int]] = {
    "banner_1.jpg": ("新品上市 全场8折", "modern furniture showroom interior empty", 1200, 450),
    "banner_2.jpg": ("北欧简约风 精选家居", "scandinavian living room furniture empty", 1200, 450),
    "banner_3.jpg": ("客厅焕新计划", "modern living room sofa interior empty", 1200, 450),
    "product_sofa_1.jpg": ("北欧布艺三人沙发", "fabric three seater sofa furniture product", 800, 600),
    "product_sofa_2.jpg": ("意式真皮转角沙发", "leather sectional sofa L shape furniture", 800, 600),
    "product_sofa_3.jpg": ("日式原木双人沙发", "wooden two seater sofa furniture", 800, 600),
    "product_bed_1.jpg": ("北欧实木双人床1.8m", "wooden double bed frame furniture", 800, 600),
    "product_bed_2.jpg": ("轻奢皮艺软包床", "upholstered leather headboard bed furniture", 800, 600),
    "product_table_1.jpg": ("北欧白橡木餐桌", "light wood dining table furniture", 800, 600),
    "product_table_2.jpg": ("现代岩板餐桌椅组合", "modern dining table with chairs set furniture", 800, 600),
    "product_table_3.jpg": ("日式原木折叠餐桌", "folding wooden dining table furniture", 800, 600),
    "product_wardrobe_1.jpg": ("现代简约推拉门衣柜", "sliding door wardrobe closet furniture", 800, 600),
    "product_wardrobe_2.jpg": ("北欧实木四门衣柜", "wooden wardrobe cabinet furniture", 800, 600),
    "product_chair_1.jpg": ("北欧实木餐椅", "wooden dining chair furniture product", 800, 600),
    "product_chair_2.jpg": ("人体工学办公椅", "mesh ergonomic office chair product", 800, 600),
    "product_chair_3.jpg": ("伊姆斯休闲椅", "eames lounge chair ottoman furniture", 800, 600),
    "product_coffee_1.jpg": ("北欧圆形茶几", "round wooden coffee table furniture", 800, 600),
    "product_coffee_2.jpg": ("大理石面茶几", "marble coffee table furniture", 800, 600),
    "product_coffee_3.jpg": ("北欧原木边几", "wooden side table furniture", 800, 600),
    "product_desk_1.jpg": ("北欧实木书桌", "wooden desk table furniture", 800, 600),
    "product_desk_2.jpg": ("现代烤漆电脑桌", "modern computer desk L shape furniture", 800, 600),
    "product_nightstand_1.jpg": ("北欧实木床头柜", "wooden nightstand bedside table furniture", 800, 600),
    "product_nightstand_2.jpg": ("简约烤漆床头柜", "white nightstand bedside table furniture", 800, 600),
}

# 人工兜底：上面搜索失败时使用（尽量选纯家具图）
FALLBACK_IDS: dict[str, list[int]] = {
    "product_sofa_1.jpg": [1571463, 4050317, 7319354],
    "product_sofa_2.jpg": [276583, 1540517, 1571453],
    "product_sofa_3.jpg": [5490712, 1866144, 1571460],
    "product_bed_1.jpg": [1454806, 1743227, 1125137],
    "product_bed_2.jpg": [6483568, 3097112, 271743],
    "product_table_1.jpg": [1080721, 1125137, 8131387],
    "product_table_2.jpg": [242492, 6969942, 263503],
    "product_table_3.jpg": [3935331, 1893334, 1080721],
    "product_wardrobe_1.jpg": [5824904, 8131419, 4391470],
    "product_wardrobe_2.jpg": [5824904, 8131419, 1571457],
    "product_chair_1.jpg": [4588778, 8137420, 276224],
    "product_chair_2.jpg": [3771834, 1181690, 1957478],
    "product_chair_3.jpg": [7319070, 904616, 1350789],
    "product_coffee_1.jpg": [1571459, 1089338, 439391],
    "product_coffee_2.jpg": [1668853, 1570119, 189295],
    "product_coffee_3.jpg": [439391, 1089338, 1571459],
    "product_desk_1.jpg": [1957477, 159711, 4144222],
    "product_desk_2.jpg": [4144222, 667838, 159711],
    "product_nightstand_1.jpg": [1648776, 2035172, 5824496],
    "product_nightstand_2.jpg": [2035172, 1648776, 1457842],
    "banner_1.jpg": [1866144, 1571453, 1571460],
    "banner_2.jpg": [1571453, 1866144, 1571460],
    "banner_3.jpg": [1571460, 1866144, 1571453],
}


def fetch_text(url: str, timeout: int = 45) -> str:
    request = urllib.request.Request(url, headers=HEADERS)
    context = ssl.create_default_context()
    with urllib.request.urlopen(request, context=context, timeout=timeout) as response:
        return response.read().decode("utf-8", errors="ignore")


def fetch_bytes(url: str, timeout: int = 90) -> bytes:
    request = urllib.request.Request(url, headers=HEADERS)
    context = ssl.create_default_context()
    with urllib.request.urlopen(request, context=context, timeout=timeout) as response:
        return response.read()


def pexels_image_url(photo_id: int, width: int, height: int) -> str:
    return (
        f"https://images.pexels.com/photos/{photo_id}/pexels-photo-{photo_id}.jpeg"
        f"?auto=compress&cs=tinysrgb&w={width}&h={height}&fit=crop"
    )


def search_pexels_ids(query: str, limit: int = 12) -> list[int]:
    url = "https://www.pexels.com/search/" + urllib.parse.quote(query) + "/"
    html = fetch_text(url)
    ids = []
    for match in re.finditer(r"/photo/[^\"'?]+-(\d+)/", html):
        photo_id = int(match.group(1))
        if photo_id not in ids:
            ids.append(photo_id)
        if len(ids) >= limit:
            break
    if ids:
        return ids
    for match in re.finditer(r"photos/(\d+)/", html):
        photo_id = int(match.group(1))
        if photo_id not in ids:
            ids.append(photo_id)
        if len(ids) >= limit:
            break
    return ids


def photo_description(photo_id: int) -> str:
    try:
        html = fetch_text(f"https://www.pexels.com/photo/{photo_id}/")
    except Exception:
        return ""
    title_match = re.search(r"<title>([^<]+)</title>", html, re.I)
    meta_match = re.search(r'property="og:description"\s+content="([^"]+)"', html)
    alt_match = re.search(r'alt="([^"]+)"', html)
    parts = []
    if title_match:
        parts.append(title_match.group(1))
    if meta_match:
        parts.append(meta_match.group(1))
    if alt_match:
        parts.append(alt_match.group(1))
    return " ".join(parts).lower()


def contains_person(text: str) -> bool:
    return any(keyword in text for keyword in SKIP_KEYWORDS)


def save_image(filename: str, data: bytes) -> None:
    if len(data) < 8000:
        raise RuntimeError(f"image too small ({len(data)} bytes)")
    IMAGE_DIR.mkdir(parents=True, exist_ok=True)
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    target = IMAGE_DIR / filename
    target.write_bytes(data)
    shutil.copy2(target, UPLOAD_DIR / filename)


def try_download_id(photo_id: int, width: int, height: int) -> bytes:
    data = fetch_bytes(pexels_image_url(photo_id, width, height))
    if len(data) < 8000:
        raise RuntimeError("too small")
    return data


def pick_image(filename: str, cn_name: str, query: str, width: int, height: int) -> tuple[int, str]:
    candidates: list[int] = []
    try:
        candidates.extend(search_pexels_ids(query))
    except Exception as exc:
        print(f"  search failed: {exc}")
    candidates.extend(FALLBACK_IDS.get(filename, []))

    seen: set[int] = set()
    for photo_id in candidates:
        if photo_id in seen:
            continue
        seen.add(photo_id)

        desc = photo_description(photo_id)
        if desc and contains_person(desc):
            print(f"  skip pexels:{photo_id} (含人物) -> {desc[:80]}")
            continue

        for attempt in range(2):
            try:
                data = try_download_id(photo_id, width, height)
                save_image(filename, data)
                source = desc or query
                return photo_id, source
            except Exception as exc:
                if attempt == 0:
                    time.sleep(1.2)
                else:
                    print(f"  download fail pexels:{photo_id}: {exc}")
    raise RuntimeError("no suitable image found")


def main() -> None:
    IMAGE_DIR.mkdir(parents=True, exist_ok=True)
    meta: dict[str, dict[str, str]] = {}

    ok = 0
    failed: list[str] = []
    for filename, (cn_name, query, width, height) in PRODUCTS.items():
        print(f"\n[{cn_name}] 搜索: {query}")
        try:
            photo_id, source = pick_image(filename, cn_name, query, width, height)
            meta[filename] = {
                "name": cn_name,
                "query": query,
                "pexels_id": str(photo_id),
                "source": source[:200],
            }
            ok += 1
            print(f"OK  {filename} <- pexels:{photo_id}")
        except Exception as exc:
            failed.append(filename)
            print(f"FAIL {filename}: {exc}")
        time.sleep(0.8)

    META_FILE.write_text(json.dumps(meta, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"\n完成: {ok}/{len(PRODUCTS)}")
    if failed:
        raise SystemExit("失败: " + ", ".join(failed))


if __name__ == "__main__":
    main()
