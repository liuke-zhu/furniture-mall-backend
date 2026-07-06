"""Download real furniture photos from Pexels and sync to uploads."""

from __future__ import annotations

import shutil
import ssl
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
IMAGE_DIR = ROOT / "image"
UPLOAD_DIR = ROOT / "uploads"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Referer": "https://www.pexels.com/",
}

# filename -> (pexels photo id, width, height)
IMAGES: dict[str, tuple[int, int, int]] = {
    "banner_1.jpg": (1571460, 1200, 450),
    "banner_2.jpg": (1866144, 1200, 450),
    "banner_3.jpg": (1571453, 1200, 450),
    "product_sofa_1.jpg": (4050317, 800, 600),
    "product_sofa_2.jpg": (276583, 800, 600),
    "product_sofa_3.jpg": (5490712, 800, 600),
    "product_bed_1.jpg": (1454806, 800, 600),
    "product_bed_2.jpg": (6483568, 800, 600),
    "product_table_1.jpg": (1125137, 800, 600),
    "product_table_2.jpg": (6969942, 800, 600),
    "product_table_3.jpg": (3935331, 800, 600),
    "product_wardrobe_1.jpg": (271816, 800, 600),
    "product_wardrobe_2.jpg": (5824904, 800, 600),
    "product_chair_1.jpg": (4588778, 800, 600),
    "product_chair_2.jpg": (3771834, 800, 600),
    "product_chair_3.jpg": (7319070, 800, 600),
    "product_coffee_1.jpg": (1571459, 800, 600),
    "product_coffee_2.jpg": (1668853, 800, 600),
    "product_coffee_3.jpg": (439391, 800, 600),
    "product_desk_1.jpg": (1957477, 800, 600),
    "product_desk_2.jpg": (4144222, 800, 600),
    "product_nightstand_1.jpg": (1648776, 800, 600),
    "product_nightstand_2.jpg": (2035172, 800, 600),
}


def pexels_url(photo_id: int, width: int, height: int) -> str:
    return (
        f"https://images.pexels.com/photos/{photo_id}/pexels-photo-{photo_id}.jpeg"
        f"?auto=compress&cs=tinysrgb&w={width}&h={height}&fit=crop"
    )


def download(filename: str, photo_id: int, width: int, height: int) -> None:
    url = pexels_url(photo_id, width, height)
    request = urllib.request.Request(url, headers=HEADERS)
    context = ssl.create_default_context()
    with urllib.request.urlopen(request, context=context, timeout=60) as response:
        data = response.read()
    if len(data) < 5000:
        raise RuntimeError(f"file too small: {len(data)} bytes")
    target = IMAGE_DIR / filename
    target.write_bytes(data)


def main() -> None:
    IMAGE_DIR.mkdir(parents=True, exist_ok=True)
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

    ok = 0
    failed: list[str] = []
    for filename, (photo_id, width, height) in IMAGES.items():
        try:
            download(filename, photo_id, width, height)
            shutil.copy2(IMAGE_DIR / filename, UPLOAD_DIR / filename)
            ok += 1
            print(f"OK  {filename} <- pexels:{photo_id}")
        except Exception as exc:
            failed.append(filename)
            print(f"FAIL {filename}: {exc}")

    print(f"\ndone: {ok}/{len(IMAGES)}")
    if failed:
        raise SystemExit(f"failed files: {', '.join(failed)}")


if __name__ == "__main__":
    main()
