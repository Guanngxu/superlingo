import json
import time
import requests

# 按优先级从低到高排列（后加载的词典会覆盖先加载的同名词汇）
TAXONOMIES = [
    ("nova_groups", "https://static.openfoodfacts.org/data/taxonomies/nova_groups.json"),
    ("packaging_shapes", "https://static.openfoodfacts.org/data/taxonomies/packaging_shapes.json"),
    ("packaging_materials", "https://static.openfoodfacts.org/data/taxonomies/packaging_materials.json"),
    ("countries", "https://static.openfoodfacts.org/data/taxonomies/countries.json"),
    ("origins", "https://static.openfoodfacts.org/data/taxonomies/origins.json"),
    ("labels", "https://static.openfoodfacts.org/data/taxonomies/labels.json"),
    ("ingredients_analysis", "https://static.openfoodfacts.org/data/taxonomies/ingredients_analysis.json"),
    ("ingredients_processing", "https://static.openfoodfacts.org/data/taxonomies/ingredients_processing.json"),
    ("allergens", "https://static.openfoodfacts.org/data/taxonomies/allergens.json"),
    ("additives_classes", "https://static.openfoodfacts.org/data/taxonomies/additives_classes.json"),
    ("additives", "https://static.openfoodfacts.org/data/taxonomies/additives.json"),
    ("other_nutritional_substances", "https://static.openfoodfacts.org/data/taxonomies/other_nutritional_substances.json"),
    ("nucleotides", "https://static.openfoodfacts.org/data/taxonomies/nucleotides.json"),
    ("amino_acids", "https://static.openfoodfacts.org/data/taxonomies/amino_acids.json"),
    ("minerals", "https://static.openfoodfacts.org/data/taxonomies/minerals.json"),
    ("vitamins", "https://static.openfoodfacts.org/data/taxonomies/vitamins.json"),
    ("nutrients", "https://static.openfoodfacts.org/data/taxonomies/nutrients.json"),
    ("categories", "https://static.openfoodfacts.org/data/taxonomies/categories.json"),
    ("ingredients", "https://static.openfoodfacts.org/data/taxonomies/ingredients.json"),
]

HEADERS = {
    "User-Agent": "SuperLingo/1.0 (contact@example.com)",
    "Accept": "application/json",
}


def fetch_json_with_progress(name: str, url: str, idx: int, total: int) -> dict:
    prefix = f"[{idx}/{total}] [{name}]"
    start_time = time.time()
    chunks = []
    downloaded = 0
    mb = 0.0

    print(f"{prefix} 正在连接服务器...", end="\r", flush=True)
    with requests.get(url, headers=HEADERS, stream=True, timeout=(10, 30)) as resp:
        resp.raise_for_status()
        for chunk in resp.iter_content(chunk_size=65536):
            if chunk:
                chunks.append(chunk)
                downloaded += len(chunk)
                mb = downloaded / (1024 * 1024)
                elapsed = time.time() - start_time
                print(
                    f"{prefix} 下载中... 已接收 {mb:.2f} MB ({elapsed:.1f}s)    ",
                    end="\r",
                    flush=True,
                )

    print(f"{prefix} 下载完成 ({mb:.2f} MB)，正在解析 JSON...          ", end="\r", flush=True)
    return json.loads(b"".join(chunks).decode("utf-8"))


def build_word_dict(output_file: str = "off_words_zh.json"):
    word_to_zh = {}
    total = len(TAXONOMIES)

    for idx, (name, url) in enumerate(TAXONOMIES, start=1):
        prefix = f"[{idx}/{total}] [{name}]"
        start_time = time.time()

        try:
            raw_data = fetch_json_with_progress(name, url, idx, total)
        except Exception as e:
            print(f"\n{prefix} ⚠️ 跳过 ({e})")
            continue

        count = 0
        for tag_key, tag_info in raw_data.items():
            if not isinstance(tag_info, dict):
                continue

            names = tag_info.get("name", {})
            zh_name = names.get("zh")
            if not zh_name:
                continue

            clean_zh = zh_name.strip()
            if not clean_zh:
                continue

            # 收集该词条对应的所有英文候选词（标准名 + Tag简写 + 同义词）
            en_candidates = set()

            # 1. 标准英文名 name.en
            en_name = names.get("en")
            if en_name:
                en_candidates.add(en_name.strip().lower())

            # 2. en: 前缀的 Tag 标识本身（解决如 en:e100 -> "e100" 映射到 "姜黄素"）
            if tag_key.startswith("en:"):
                slug = tag_key[3:].replace("-", " ").strip().lower()
                if slug:
                    en_candidates.add(slug)

            # 3. 英文同义词 synonyms.en（如果使用的是 .full.json 则包含单复数与别名）
            synonyms = tag_info.get("synonyms", {}).get("en", [])
            if isinstance(synonyms, list):
                for syn in synonyms:
                    if isinstance(syn, str) and syn.strip():
                        en_candidates.add(syn.strip().lower())

            for en_word in en_candidates:
                word_to_zh[en_word] = clean_zh
                count += 1

        elapsed = time.time() - start_time
        print(f"{prefix} ✅ 完成！提取 {count} 条映射 (耗时 {elapsed:.1f}s)                ")

    print("\n正在写入本地文件...", end="\r", flush=True)
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(word_to_zh, f, ensure_ascii=False, indent=2)

    print(f"🎉 全部导出完成！去重后共 {len(word_to_zh)} 条词汇，已保存至: {output_file}")


if __name__ == "__main__":
    build_word_dict()