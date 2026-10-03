package com.guanngxu.superlingo.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 获取客户端真实 IP。
 * 部署在反向代理（Nginx / Koyeb / Oracle Cloud）之后时，
 * 真实 IP 通常放在 X-Forwarded-For 头里，这里逐层回退。
 */
public final class ClientIpUtil {

    private ClientIpUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            // 该头可能是「client, proxy1, proxy2」形式，取第一个
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}
