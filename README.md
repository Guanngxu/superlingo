# superlingo 超市扫码翻译项目

## 实现步骤

1. 面向对象设计，整个软件的交互如何？有哪些实体类、他们各自之间的关系；
2. 抽象出数据库表，数据库表对应的字段；
3. 编写 Java 后端代码，定义有哪些 API 接口；
4. 部署 Java 后端到服务器，进行测试；
5. 编写前端页面代码（让 AI 写）；
6. 部署前端并绑定自己的域名

## 技术框架

### 前端

前端展示暂定下面两种方式，具体哪种方式实现待定，直接使用 AI 实现，人工做细微调整

1. 使用纯 HTML + CSS + 原生 JavaScript 编写前端页面
2. 使用 Vue 前端框架

### 后端

使用 Java Spring Boot 实现，实际部署时可选 Koyeb 或 Oracle Cloud 平台

### 数据库

项目中涉及到数据库和静态存储（图片、视频），使用 Supabase 平台，其底层时一个强大的 PostgreSQL 关系型数据库

## 相关资料

[HTTP Crash Course & Exploration](https://www.youtube.com/watch?v=iYM2zFP3Zn0)