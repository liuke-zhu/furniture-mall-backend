"""Replace mismatched product cover images with category-accurate photos."""

from __future__ import annotations

import shutil
import ssl
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
IMAGE_DIR = ROOT / "image"
UPLOAD_DIR = ROOT / "uploads"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Referer": "https://www.pexels.com/",
}

# product file -> (primary pexels id, fallback ids)
FIXES: dict[str, tuple[int, list[int]]] = {
    # 北欧布艺三人沙发
    "product_sofa_1.jpg": (4050317, [1571463, 7319354]),
    # 轻奢皮艺软包床
    "product_bed_2.jpg": (6483568, [3097112, 1743227]),
    # 北欧白橡木餐桌
    "product_table_1.jpg": (1125137, [1080721, 8131387]),
    # 现代岩板餐桌椅组合
    "product_table_2.jpg": (6969942, [263503, 242492]),
    # 北欧实木四门衣柜
    "product_wardrobe_2.jpg": (5824904, [8131419, 4391470]),
    # 北欧实木餐椅
    "product_chair_1.jpg": (4588778, [8137420, 1181381]),
    # 人体工学办公椅
    "product_chair_2.jpg": (3771834, [1181690, 4143294]),
    # 伊姆斯休闲椅
    "product_chair_3.jpg": (7319070, [904616, 6480197]),
    # 北欧圆形茶几
    "product_coffee_1.jpg": (1571459, [1089338, 439391]),
    # 大理石面茶几
    "product_coffee_2.jpg": (1668853, [1570119, 189295]),
    # 现代烤漆电脑桌
    "product_desk_2.jpg": (4144222, [159711, 667838]),
    # 简约烤漆床头柜
    "product_nightstand_2.jpg": (2035172, [5824496, 1648776]),
}


def pexels_url(photo_id: int, width: int = 800, height: int = 600) -> str:
    return (
        f"https://images.pexels.com/photos/{photo_id}/pexels-photo-{photo_id}.jpeg"
        f"?auto=compress&cs=tinysrgb&w={width}&h={height}&fit=crop"
    )


def fetch(photo_id: int) -> bytes:
    request = urllib.request.Request(pexels_url(photo_id), headers=HEADERS)
    context = ssl.create_default_context()
    with urllib.request.urlopen(request, context=context, timeout=90) as response:
        data = response.read()
    if len(data) < 5000:
        raise RuntimeError(f"too small ({len(data)} bytes)")
    return data


def download_with_fallback(filename: str, primary: int, fallbacks: list[int]) -> int:
    last_error: Exception | None = None
    for photo_id in [primary, *fallbacks]:
        for attempt in range(2):
            try:
                data = fetch(photo_id)
                target = IMAGE_DIR / filename
                target.write_bytes(data)
                shutil.copy2(target, UPLOAD_DIR / filename)
                return photo_id
            except (urllib.error.URLError, TimeoutError, RuntimeError) as exc:
                last_error = exc
                time.sleep(1.5)
    raise RuntimeError(str(last_error))


def main() -> None:
    IMAGE_DIR.mkdir(parents=True, exist_ok=True)
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

    ok = 0
    failed: list[str] = []
    for filename, (primary, fallbacks) in FIXES.items():
        try:
            photo_id = download_with_fallback(filename, primary, fallbacks)
            ok += 1
            print(f"OK  {filename} <- pexels:{photo_id}")
        except Exception as exc:
            failed.append(filename)
            print(f"FAIL {filename}: {exc}")

    print(f"\ndone: {ok}/{len(FIXES)}")
    if failed:
        raise SystemExit(f"failed: {', '.join(failed)}")


if __name__ == "__main__":
    main()
