package com.guanngxu.superlingo.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统一 API 响应封装，保证前后端契约一致：
 * { "code": 200, "message": "success", "data": ... }
 */
public final class ApiResponse {

    private ApiResponse() {}

    public static Map<String, Object> ok(Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", 200);
        body.put("message", "success");
        body.put("data", data);
        return body;
    }

    public static Map<String, Object> error(int code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        return body;
    }
}
