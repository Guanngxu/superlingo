-- SuperLingo 数据库初始化脚本（PostgreSQL）
-- 使用方法：登录 Supabase 控制台 -> SQL Editor，粘贴执行。

-- 1. 商品表：条形码即主键，无需自增 ID
CREATE TABLE IF NOT EXISTS products (
    barcode   VARCHAR(50) PRIMARY KEY,
    name_zh   VARCHAR(255),
    name_en   VARCHAR(255),
    image_url VARCHAR(500),
    source    VARCHAR(50)   -- 数据来源：AUTO（API 抓取）/ MANUAL（人工修正）
);

-- 2. 扫码流水日志表（追加表，外键关联商品表）
CREATE TABLE IF NOT EXISTS scan_logs (
    id         SERIAL PRIMARY KEY,
    barcode    VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    scanned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scan_logs_product FOREIGN KEY (barcode) REFERENCES products (barcode)
);
CREATE INDEX IF NOT EXISTS idx_scan_logs_barcode ON scan_logs (barcode);

-- 3. 管理员表：绝不存明文密码，只存哈希
CREATE TABLE IF NOT EXISTS admin_users (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
