package com.guanngxu.superlingo.service;

import com.guanngxu.superlingo.dao.AdminUserDao;
import com.guanngxu.superlingo.entity.AdminUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理员鉴权：登录成功后签发一个内存态 token。
 * 说明：演示项目用内存 token 足够；生产环境建议换成 JWT 或 session。
 */
@Service
public class AdminAuthService {

    private final AdminUserDao adminUserDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** token -> username，内存存储（单实例部署下有效） */
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public AdminAuthService(AdminUserDao adminUserDao) {
        this.adminUserDao = adminUserDao;
    }

    /**
     * 校验账号密码，成功返回 token，失败返回 null。
     */
    public String login(String username, String password) {
        AdminUser user = adminUserDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return null;
        }
        String token = generateToken(username);
        sessions.put(token, username);
        return token;
    }

    /**
     * 校验 token 是否有效。
     */
    public boolean isValidToken(String token) {
        return token != null && sessions.containsKey(token);
    }

    /**
     * 生成一个随机 token（演示用，不依赖 JWT 依赖库）。
     */
    private String generateToken(String username) {
        String raw = UUID.randomUUID() + ":" + username;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
