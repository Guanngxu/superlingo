package com.guanngxu.superlingo.config;

import com.guanngxu.superlingo.dao.AdminUserDao;
import com.guanngxu.superlingo.entity.AdminUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 启动时若管理员表为空，则初始化一个默认管理员账号，方便首次登录后台。
 * 账号密码可通过环境变量 ADMIN_USERNAME / ADMIN_PASSWORD 覆盖。
 */
@Configuration
public class AdminInitializer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initAdmin(AdminUserDao adminUserDao, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            try {
                if (adminUserDao.count() > 0) {
                    return; // 已有管理员，跳过
                }
                String username = System.getenv().getOrDefault("ADMIN_USERNAME", "admin");
                String password = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");
                AdminUser admin = new AdminUser(username, passwordEncoder.encode(password));
                adminUserDao.insert(admin);
                System.out.println("✅ 已初始化默认管理员账号: " + username);
            } catch (Exception e) {
                // 数据库暂不可达时不阻断应用启动，等数据库就绪后手动执行 schema.sql 并插入管理员即可
                System.err.println("⚠️ 初始化默认管理员失败（数据库不可达？）: " + e.getMessage());
            }
        };
    }
}
