package com.guanngxu.superlingo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guanngxu.superlingo.entity.Product;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Open Food Facts 官方接口客户端。
 * 文档：https://openfoodfacts.github.io/openfoodfacts-server/api/
 */
@Component
public class OpenFoodFactsClient {

    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product/";
    private static final String FIELDS = "?fields=" +
            "product_name," +
            "product_name_zh," +
            "image_url," +
            "image_front_url," +
            "countries_tags," +
            "categories_tags," +
            "quantity," +
            "nova_group," +
            "brands_tags," +
            "ingredients," +
            "origins_tags";

    private final HttpClient httpClient;

    public OpenFoodFactsClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * 按条码抓取商品信息；找不到该条码或请求失败时返回 null。
     */
    public Product fetch(String barcode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + barcode + FIELDS))
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "SuperLingo/1.0 (bilingual supermarket demo)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            if (root.get("status").getAsInt() != 1) {
                return null; // OFF 明确返回「未找到该条码」
            }

            JsonObject productNode = root.getAsJsonObject("product");
            if (productNode == null || productNode.isJsonNull()) {
                return null;
            }

            String nameEn = getTextFromJson(productNode, "product_name");
            String nameZh = getTextFromJson(productNode, "product_name_zh");
            if (nameZh.isBlank()) {
                nameZh = TranslateUtil.translate(nameEn);
            }
            String imageUrl = getFirstNonBlank(getTextFromJson(productNode, "image_url"), getTextFromJson(productNode, "image_front_url"));
            String quantity = getTextFromJson(productNode, "quantity");
            Integer novaGroup = getIntegerFromJson(productNode, "nova_group");
            String countries = getTranslatedArrayStringFromJson(productNode, "countries_tags");
            String categories = getTranslatedArrayStringFromJson(productNode, "categories_tags");
            String brands = getBrandsStringFromJson(productNode);
            String origins = getTranslatedArrayStringFromJson(productNode, "origins_tags");
            String ingredients = getTranslatedIngredients(productNode);

            return new Product(barcode, nameZh, nameEn, imageUrl, "AUTO", quantity, brands, categories, countries, origins, ingredients, novaGroup);
        } catch (Exception e) {
            System.err.println("❌ Open Food Facts 请求失败: " + e.getMessage());
            return null;
        }
    }

    private String getTranslatedIngredients(JsonObject node) {
        if (!node.has("ingredients") || node.get("ingredients").isJsonNull()) {
            return "";
        }
        JsonArray jsonArray = node.get("ingredients").getAsJsonArray();

        List<String> textList = new ArrayList<>();
        List<String> percentList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            JsonObject ingredient = jsonArray.get(i).getAsJsonObject();
            if(ingredient.has("id") && !ingredient.get("id").isJsonNull()
                && ingredient.has("percent_estimate") && !ingredient.get("percent_estimate").isJsonNull()) {
                String name = ingredient.get("id").getAsString();
                if (name != null && name.startsWith("en:")) {
                    String cleanName = name.substring(3);
                    // 加到我们的 textList 中
                    textList.add(cleanName);
                    percentList.add(ingredient.get("percent_estimate").getAsString());
                }
            }
        }
        String[] translatedNames = TranslateUtil.translateStringArray(textList.toArray(new String[0]));

        String result[] = new String[translatedNames.length];
        for (int i = 0; i < translatedNames.length; i++) {
            result[i] = translatedNames[i] + "（" + percentList.get(i) + "%）";
        }

        return String.join("， ", result);
    }

    private String getBrandsStringFromJson(JsonObject node) {
        if (!node.has("brands_tags") || node.get("brands_tags").isJsonNull()) {
            return "";
        }

        JsonArray jsonArray = node.get("brands_tags").getAsJsonArray();
        String[] brands = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            brands[i] = jsonArray.get(i).getAsString();
        }
        return String.join("， ", brands);
    }

    private String getTranslatedArrayStringFromJson(JsonObject node, String field) {
        String[] strings = parseTagsWithoutPrefix(node, field);
        String[] trans = TranslateUtil.translateStringArray(strings);
        return String.join("， ", trans);
    }

    private String[] parseTagsWithoutPrefix(JsonObject node, String field) {
        if (!node.has(field) || node.get(field).isJsonNull()) {
            return new String[0];
        }
        JsonArray jsonArray = node.get(field).getAsJsonArray();
        List<String> list = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            // 获取当前位置的元素，并转成普通字符串 (例如 "en:snacks")
            String tag = jsonArray.get(i).getAsString();
            // 判断字符串是否以 "en:" 开头
            if (tag != null && tag.startsWith("en:")) {
                // 如果是，截取掉前面的 "en:"（"en:" 的长度是 3，所以从索引 3 开始截取）
                String cleanTag = tag.substring(3);
                // 加到我们的 List 中
                list.add(cleanTag);
            }
        }
        return list.toArray(new String[0]);
    }

    private Integer getIntegerFromJson(JsonObject node, String field) {
        if (!node.has(field) || node.get(field).isJsonNull()) {
            return 0;
        }
        return node.get(field).getAsInt();
    }

    private String getTextFromJson(JsonObject node, String field) {
        if (!node.has(field) || node.get(field).isJsonNull()) {
            return "";
        }
        return node.get(field).getAsString();
    }

    private String getFirstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return "";
    }
}
