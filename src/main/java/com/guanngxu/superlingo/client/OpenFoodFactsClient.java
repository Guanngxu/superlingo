package com.guanngxu.superlingo.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guanngxu.superlingo.entity.Product;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Open Food Facts 官方接口客户端。
 * 文档：https://openfoodfacts.github.io/openfoodfacts-server/api/
 */
@Component
public class OpenFoodFactsClient {

    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product/";
    private static final String FIELDS = "?fields=product_name,product_name_zh,image_url,image_front_url";

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

            JsonObject product = root.getAsJsonObject("product");
            if (product == null || product.isJsonNull()) {
                return null;
            }

            String nameEn = text(product, "product_name");
            String nameZh = text(product, "product_name_zh");
            if (nameZh.isBlank()) {
                nameZh = nameEn; // 中文缺失时回退到英文名
            }
            String imageUrl = firstNonBlank(text(product, "image_url"), text(product, "image_front_url"));

            return new Product(barcode, nameZh, nameEn, imageUrl, "AUTO");
        } catch (Exception e) {
            System.err.println("❌ Open Food Facts 请求失败: " + e.getMessage());
            return null;
        }
    }

    private String text(JsonObject node, String field) {
        if (!node.has(field) || node.get(field).isJsonNull()) {
            return "";
        }
        return node.get(field).getAsString();
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return "";
    }
}
