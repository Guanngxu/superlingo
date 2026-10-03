# SuperLingo

这是一个超市双语练习项目，用于帮助在英文国家生活的华人快速识别超市商品信息。通过手机扫描扫描商品条形码即可快速获取该商品的基本信息（中英对照，中文为主）。

## 功能描述

### 一、 C端（用户扫码端）：极简交互与结果直出

* **Web 唤醒与实时扫码**：用户在手机浏览器中输入指定网址，页面加载后自动请求摄像头权限，唤起一个全屏或半屏的取景框。用户将商品条形码置于取景框内，系统自动捕捉并进行条码解析，全程无需下载任何独立
  App。
* **双语信息秒切展示**：条码识别成功后，系统在取景框上方或底部平滑弹出一个结果卡片，直观展示该商品的基础信息（有中英对照展示）。数据的初始来源对接
  Open Food Facts 官方接口。

### 二、 数据流转与治理：缓存穿透与人工纠错 (Human-in-the-loop)

* **“首扫落库”机制**：为了解决 Open Food Facts 接口部分中文翻译生硬或不准确的痛点，系统实行数据本地化策略。当某个新商品**首次
  **被扫码并从官方接口获取数据后，系统会在后台自动将这条原始数据永久保存到我们自己的数据库中。
* **人工覆盖与最高优先级**
  ：可以登录后台，查阅这些被系统自动保存下来的商品记录，并对翻译有误的中文信息进行手动修正与润色。一旦你完成了修改，未来任何用户再次扫描该条形码时，系统将直接调取你完善后的本地数据，不再依赖外部接口。

### 三、 B端（管理后台）：安全看板与流量追踪

* **私有化鉴权墙**：由于后台涉及数据篡改，系统配备独立的管理员登录页面。必须输入预设的管理员账号与密码，验证通过后方可进入数据管理中心，阻断所有越权访问。
* **扫码埋点与热度统计**
  ：系统对每一次成功的扫码行为进行静默记录。在你的管理后台中，不仅能修改商品信息，还能清晰地看到一个数据统计看板：包括特定条形码的“累计被扫总频次”（发现热门商品），以及精准记录每次扫码触发时的“用户终端
  IP 地址”和时间戳。

## 技术架构设计

### 面向对象设计

这个程序比较简单，从功能描述部分可以看到有**商品**、**历史记录**、**管理员**三个实体就可以了，当然这三个实体会直接对应代码中的三个类，也是数据库中的三个数据表。

每个类有哪些属性呢？其中最简单的是管理员类，账户、密码是必不可少的属性，当然还可以考虑增加头像、昵称等字段。

历史记录也很容易想到需要哪些属性，记录条形码（可关联商品类）、IP 地址、扫码时间即可。

比较麻烦的是商品，由于我们自身精力有限，无法去超市或厂家逐一比对不同商品有哪些属性，所以此处我们暂按着自己的认知罗列一些属性，后续在整个系统维护过程中遇到哪些需要补充再完善。目前能想到的属性有：商品条形码、中文名称、英文名称、商品图片、数据来源（API或人工修正）。

```java
public class AdminUser {
    private Integer id;
    private String username;
    private String passwordHash;
}

public class Product {
    private String barcode;
    private String nameZh;
    private String nameEn;
    private String imageUrl;
    private String source;
}

public class ScanLog {
    private Integer id;            // 对应自增流水号
    private String barcode;        // 关联商品的外键
    private String ipAddress;      // 用户 IP
    private Timestamp scannedAt;   // 扫码时间
}
```

### 数据库设计

有了前面面向对象设计的基础，我们就可以快速的将数据库表设计好，同时也可以将数据库建好。

1. 商品表 (`products`)

`barcode` (VARCHAR, 主键): 商品条形码，全宇宙唯一标识。

`name_zh` (VARCHAR): 中文名称。

`name_en` (VARCHAR): 英文名称。

`image_url` (VARCHAR): 商品图片链接。

`source` (VARCHAR): 数据来源标记。如果是刚从 API 拉取的，标记为 AUTO；如果你在后台修改过，标记为 MANUAL。

2. 扫码流水日志表 (scan_logs)

`id` (INT, 主键, 自增): 日志流水号。

`barcode` (VARCHAR, 外键): 关联 products 表。

`ip_address` (VARCHAR): 用户的真实 IP 地址。

`scanned_at` (TIMESTAMP): 扫码发生的精确时间戳。

3. 管理员表 (admin_users)

`id` (INT, 主键, 自增): 管理员 ID。

`username` (VARCHAR, 唯一): 登录账号。

`password_hash` (VARCHAR): 密码哈希值（教学点：教学生绝不能在数据库存明文密码）。

完整的建库建表 SQL 如下所示：

```sql
-- 1. 创建商品表 (products) 
-- 核心缓存表，没有自增ID，条形码就是唯一的身份证
CREATE TABLE products (
    barcode VARCHAR(50) PRIMARY KEY,    -- 扫码直接出条码，用作主键
    name_zh VARCHAR(255),               -- 中文名称
    name_en VARCHAR(255),               -- 英文名称
    image_url VARCHAR(500),             -- 商品图片链接
    source VARCHAR(50)                  -- 数据来源 (AUTO / MANUAL)
);

-- 2. 创建扫码流水日志表 (scan_logs)
-- 这是一张追加表，用到了之前学过的“外键”概念来关联商品表
CREATE TABLE scan_logs (
    id SERIAL PRIMARY KEY,              -- 注意：PostgreSQL 中使用 SERIAL 替代 AUTO_INCREMENT
    barcode VARCHAR(50),                -- 记录扫了哪个商品
    ip_address VARCHAR(50),             -- 记录谁扫的 (IP)
    scanned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 自动记录当前时间，Java 代码里不用传
    FOREIGN KEY (barcode) REFERENCES products(barcode) -- 建立外键羁绊，确保扫的码在库里存在
);

-- 3. 创建管理员表 (admin_users)
-- 用于后台安全鉴权
CREATE TABLE admin_users (
    id SERIAL PRIMARY KEY,              -- 注意：PostgreSQL 中使用 SERIAL 替代 AUTO_INCREMENT
    username VARCHAR(100) UNIQUE,       -- UNIQUE 确保管理员账号不能重复注册
    password_hash VARCHAR(255)          -- 绝对不存明文，存哈希值
);
```

### RESTful API 接口设计

RESTful API 接口是前后端分离架构中，前端和后端沟通的契约。

1. 扫码查询并记录日志

请求路径： `GET /api/v1/scan/{barcode}`

2. 人工修正商品信息

请求路径： `POST /api/v1/admin/products/{barcode}`

3. 管理员登录

请求路径： `POST /api/v1/admin/login`

4. 获取统计数据

请求路径： `GET /api/v1/admin/stats/hot`
