import requests

def build_taxonomy_dict(taxonomy_url):
    headers = {
        "User-Agent": "MyTranslationApp/1.0 (your_email@example.com)"
    }
    try:
        response = requests.get(taxonomy_url, headers=headers)
        response.raise_for_status() # 抛出 4xx/5xx 错误
        
        # 尝试解析 JSON
        data = response.json()
        
        zh_translation_map = {}
        for tag_code, tag_data in data.items():
            names = tag_data.get("name", {})
            if "zh" in names:
                zh_translation_map[tag_code] = names["zh"]
        return zh_translation_map
        
    # 如果依然报错，进入这里进行排查
    except JSONDecodeError:
        print(f"❌ 解析 JSON 失败！")
        print(f"HTTP 状态码: {response.status_code}")
        print(f"服务器实际返回的内容前 200 个字符: \n{response.text[:200]}")
        return {}
    except RequestException as e:
        print(f"❌ 网络请求失败: {e}")
        return {}

# 1. 软件启动阶段：初始化并缓存本地中文词典
CATEGORIES_URL = "https://static.openfoodfacts.org/data/taxonomies/categories.json"
categories_zh_dict = build_taxonomy_dict(CATEGORIES_URL)

# 2. 扫码查商品阶段：模拟 API 返回的纯英文系统标签
api_returned_tags = [
    "en:plant-based-foods-and-beverages", 
    "en:nuts", 
    "en:mixed-nuts",
    "en:some-unknown-new-tag" # 模拟一个没有中文翻译的新标签
]

print("\n--- 开始对 API 返回的标签进行本地秒级翻译 ---")
translated_tags = []

for tag in api_returned_tags:
    # 去内存字典里查，查到了就用中文，查不到就保留原英文标签名
    if tag in categories_zh_dict:
        chinese_name = categories_zh_dict[tag]
        translated_tags.append(chinese_name)
        print(f"翻译成功: {tag} -> {chinese_name}")
    else:
        # 去掉 "en:" 前缀作为兜底显示
        fallback_name = tag.replace("en:", "").replace("-", " ")
        translated_tags.append(fallback_name)
        print(f"无中文翻译，触发兜底: {tag} -> {fallback_name}")

print(f"\n最终要在翻译软件界面上展示的分类：{translated_tags}")