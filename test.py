import requests
import json

def fetch_and_parse_product(barcode):
    # 1. 构建 API 请求 URL
    # 使用 ?lc=zh 参数要求服务器优先返回中文数据环境
    url = f"https://world.openfoodfacts.org/api/v2/product/{barcode}.json?lc=zh"
    
    # 2. 设置规范的 User-Agent (OFF 官方要求，防止被限流)
    headers = {
        "User-Agent": "MyTranslationApp/1.0 (test@example.com)"
    }
    
    print(f"正在拉取条形码 {barcode} 的数据...\n")
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()
        
        # 检查商品是否在数据库中找到
        if data.get("status") != 1:
            print("❌ 找不到该商品")
            return
            
        product = data.get("product", {})
        
        # ---------------------------------------------------------
        # 核心逻辑 1：商品名称的“自动降级 (Fallback)”提取
        # ---------------------------------------------------------
        # 优先寻找 _zh 结尾的纯中文字段
        name_zh = product.get("product_name_zh")
        name_en = product.get("product_name_en")
        name_default = product.get("product_name") # 原产国默认名称
        
        print("--- 商品名称解析 ---")
        if name_zh:
             print(f"✅ 找到官方中文名: {name_zh}")
             display_name = name_zh
        else:
             print("⚠️ 未找到官方中文名，触发降级策略...")
             # 如果没有中文名，取英文名，如果也没有英文名，取默认名
             fallback_name = name_en or name_default
             print(f"🔤 降级提取外文名: {fallback_name}")
             display_name = f"[{fallback_name}] (此处在真实软件中将接入 DeepL/百度进行机翻)"
             
        print(f"最终界面展示名称: {display_name}\n")


        # ---------------------------------------------------------
        # 核心逻辑 2：配料表的提取
        # ---------------------------------------------------------
        ingredients_zh = product.get("ingredients_text_zh")
        ingredients_en = product.get("ingredients_text_en")
        
        print("--- 配料表解析 ---")
        if ingredients_zh:
            print(f"✅ 找到官方中文配料表: {ingredients_zh}")
        else:
            print("⚠️ 未找到中文配料表，提取外文版本:")
            print(f"🔤 外文配料表: {ingredients_en or product.get('ingredients_text')}\n")

        # ---------------------------------------------------------
        # 核心逻辑 3：系统分类标签 (Taxonomy Tags) 的处理
        # ---------------------------------------------------------
        print("--- 分类标签 (Categories) ---")
        # categories_tags 是一个列表，里面是类似 ["en:mixed-nuts", "en:almonds"] 的系统内置码
        raw_categories = product.get("categories_tags", [])
        print(f"原始系统标签: {raw_categories}")
        
        # OFF 并没有直接返回 "categories_tags_zh" 这个现成的列表。
        # 网页版能显示中文分类，是因为前端加载了 Taxonomy 字典进行了替换。
        # 在 API 中，如果商品分类有对应的翻译，它会隐藏在 'categories_hierarchy' 
        # 或你需要自己去匹配静态词典。为了演示，我们查看 hierarchy 字段。
        hierarchy = product.get("categories_hierarchy", [])
        print("如果该分类在 OFF 系统词典中有翻译，它会包含语言前缀：")
        for item in hierarchy:
            print(f" - {item}")
            
    except requests.exceptions.RequestException as e:
        print(f"网络请求失败: {e}")

# 执行测试，使用 Kirkland 混合坚果的条形码
fetch_and_parse_product("0096619196470")