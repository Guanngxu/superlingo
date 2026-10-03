package com.guanngxu.superlingo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * 用 Gson 处理 JSON。
 * 提供一个统一配置的 Gson bean，Spring Boot 会自动将其包装成
 * GsonHttpMessageConverter 用于 @ResponseBody / @RequestBody。
 */
@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        // java.sql.Timestamp / java.util.Date 统一输出 ISO-8601 字符串
        JsonSerializer<java.util.Date> dateSerializer =
                (src, type, ctx) -> new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src.toInstant()));

        JsonSerializer<java.sql.Timestamp> timestampSerializer =
                (src, type, ctx) -> new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src.toInstant()));

        return new GsonBuilder()
                .registerTypeAdapter(java.util.Date.class, dateSerializer)
                .registerTypeAdapter(java.sql.Timestamp.class, timestampSerializer)
                .create();
    }
}
