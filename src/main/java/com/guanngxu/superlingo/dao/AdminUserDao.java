package com.guanngxu.superlingo.dao;

import com.guanngxu.superlingo.entity.AdminUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminUserDao {

    private final JdbcTemplate jdbc;

    public AdminUserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 按用户名查管理员（用于登录校验）
     */
    public AdminUser findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM admin_users WHERE username = ?";
        List<AdminUser> list = jdbc.query(sql, (rs, i) -> {
            AdminUser user = new AdminUser(rs.getString("username"), rs.getString("password_hash"));
            user.setId(rs.getInt("id"));
            return user;
        }, username);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 统计管理员数量（用于判断是否需要初始化默认账号）
     */
    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM admin_users", Long.class);
    }

    /**
     * 新增管理员
     */
    public void insert(AdminUser user) {
        jdbc.update("INSERT INTO admin_users (username, password_hash) VALUES (?, ?)",
                user.getUsername(), user.getPasswordHash());
    }
}
