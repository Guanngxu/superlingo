**角色设定**
你是一位经验丰富的前端架构师和高级开发工程师。请帮我使用 Vue3 开发一个协助在美华人购买商品的单页 H5 扫码应用。请严格遵循“Simplicity First”（简单优先）的原则，提供能解决问题的最简代码。

**技术栈与规范要求**

1. **核心框架**：Vue3 (使用 `<script setup>` 组合式 API) + TypeScript + Vite。
2. **UI 库**：Vant 4（移动端优先）。
3. **第三方依赖**：允许且仅限引入轻量级的扫码库（如 `html5-qrcode` 或 `@zxing/browser`）和条形码生成库（如 `jsbarcode`）。禁止引入其他多余的大型插件。
4. **样式**：严格使用 Scoped Less 编写，禁止使用任何内联样式（Inline Styles）。
5. **响应式**：以移动端（H5）体验为主，同时需使用 Flex/Grid 布局实现 PC 和平板的自适应呈现。
6. **动画与交互**：页面切换、弹窗、折叠面板（Tab 切换）等交互需结合 Vant 4 内置动画或自定义平滑过渡。
7. **代码质量**：
* 遵循 TSLint/ESLint 规范，代码需添加必要的业务注释。
* 适度模块化（抽离 API、类型、工具和核心组件），不要过度拆分。
* 绝对禁止使用过时的 API 写法（如 Vue2 Options API）。



**业务流与功能需求**

1. **扫码模块**：
* 初始页面为扫码页，支持调用摄像头实时扫码，或扫描本地相册图片识别条形码（Barcode）。


2. **数据请求与状态管理**：
* 识别出 `barcode` 后，向后端发起 GET 请求：`[https://superlingo.onrender.com/api/v1/scan/](https://superlingo.onrender.com/api/v1/scan/){barcode}`。
* 需处理 Loading 加载态，并做防抖处理避免重复请求。


3. **404 与异常处理**：
* 如果接口返回 `code: 404` 或请求失败，页面需跳转/平滑过渡到一个“商品未录入”的空白详情页（可使用 `<van-empty>` 组件），并提供显著的“返回扫码”按钮。


4. **结果展示模块（成功态）**：
* 数据成功返回后，渲染商品详情视图。页面分为上、下两部分：
* **上半部分（主要信息，横向布局）**：
* **左侧（占位 2/3）**：展示商品图片（需处理图片加载失败的占位）。
* **右侧（占位 1/3）**：展示逼真的条形码（使用 `jsbarcode` 生成，尺寸适中不要太大），以及紧凑排布的中英文名称、品牌、重量规格、加工程度。
* *加工程度需转换*：1-未加工或低度加工的食品，2-加工过的烹饪原料，3-加工食品，4-超加工食品。


* **下半部分（Tab 面板）**：
* 使用 Vant 的 `<van-tabs>`，包含：成分信息(ingredients)、原料来源(origins)、可出售国家(countries)、分类信息(categories)。




5. **数据容错（空值处理）**：
* 除图片、中英文名称、条形码外，其余字段可能为 null 或空字符串。
* 检测到空字段时，自动隐藏对应 UI 元素。如果某个 Tab 对应的内容完全为空，则不渲染该 Tab 标签页。


6. **便捷重扫**：
* 在详情页（无论是成功展示还是 404 空白页），必须有一个清晰方便的“继续扫码 / 返回”按钮，点击后重置状态并回到摄像头界面。



**数据结构与接口契约**

* 成功返回：`{ "code": 200, "message": "success", "data": { ... } }`
* 失败返回：`{ "code": 404, "message": "未找到商品信息" }`
  请根据我下方提供的 Java 实体类，推导并定义完整的 TypeScript Interface：

```java
public class Product {
    private String barcode; // 条形码
    private String nameZh; // 中文名称
    private String nameEn; // 英文名称
    private String imageUrl; // 图片
    private String source; // 来源
    private String quantity; // 重量信息（包含单位）
    private String brands; // 品牌
    private String categories; // 类别
    private String countries; // 可出售国家
    private String origins; // 原料来源
    private String ingredients; // 成分及占比
    private Integer novaGroup; // 加工程度：1-4
}

```

**任务执行要求**
请按照以下文件结构依次输出代码，确保代码完整且可直接运行：

1. `types.ts`: 定义接口契约与数据类型。
2. `api.ts`: 封装 Fetch 请求逻辑。
3. `Scanner.vue`: 扫码与相册识别组件封装。
4. `ProductDetail.vue`: 详情页组件（包含 2/3 与 1/3 的布局、条形码生成、空值过滤与 404 空白页适配）。
5. `App.vue`: 主应用入口，管理扫码与详情视图的切换逻辑。