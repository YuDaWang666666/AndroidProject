# 新蜂商城 Android 客户端 - AI 指令

## 项目信息

- **项目类型**：Android 原生应用（Java）
- **构建工具**：Gradle 8.11.1
- **API 地址**：`http://115.158.64.84:28019`
- **参考文档**：[项目实现文档.md](项目实现文档.md)

## 开发规范

### 代码风格

- 使用 Java 标准命名规范
- Activity/Fragment 使用驼峰命名
- 布局文件使用下划线命名（如 `activity_main.xml`）
- 资源文件使用下划线命名（如 `ic_search.xml`）

### 网络请求

- 使用 `HttpUtil` 工具类封装网络请求
- 所有网络请求必须在子线程执行
- UI 更新必须使用 `runOnUiThread`
- Token 通过 `TokenManager` 管理

### 图片加载

- 使用 Glide 加载网络图片
- 图片 URL 需要处理相对路径（拼接 BASE_URL）

### 数据存储

- 用户 Token 使用 SharedPreferences 存储
- 存储文件名：`info`
- 包含字段：`token`、`userName`、`userId`

## 关键文件

### 工具类

- `Constants.java` - API 地址常量
- `HttpUtil.java` - 网络请求封装
- `MD5Util.java` - MD5 加密工具
- `TokenManager.java` - Token 管理
- `ToastUtil.java` - Toast 工具
- `ImageUtil.java` - 图片下载工具

### 自定义 View

- `MyGridView.java` - 自适应高度 GridView

## API 接口

接口地址：`http://115.158.64.84:28019`

详细接口文档：`http://115.158.64.84:28019/swagger-ui/index.html`

### 认证方式

- 登录后获取 Token
- 请求头添加：`token: <your_token>`

### 主要接口

- 登录：`POST /api/v1/user/login`
- 商品列表：`GET /api/v1/search`
- 购物车：`GET/POST/PUT/DELETE /api/v1/shop-cart`
- 订单：`GET/POST /api/v1/order`

## 构建与运行

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK API 34

### Gradle 配置

- 使用阿里云镜像下载 Gradle
- 镜像地址：`mirrors.aliyun.com/gradle/distributions/v8.11.1/gradle-8.11.1-all.zip`

### 运行步骤

1. 使用 Android Studio 打开项目
2. 同步 Gradle 依赖
3. 连接设备或启动模拟器
4. 点击 Run 运行应用

## 测试账号

- 手机号：`16666666666`
- 密码：`88888888`（需 MD5 加密）

## 注意事项

1. 所有接口均为 HTTP 协议，需设置 `android:usesCleartextTraffic="true"`
2. 密码传输前必须进行 MD5 加密（32位小写）
3. 价格单位为分，显示时需要处理
4. 网络权限：`<uses-permission android:name="android.permission.INTERNET" />`

## 文档

- [项目实现文档.md](项目实现文档.md) - 详细实现文档
- [README.md](README.md) - 项目说明
