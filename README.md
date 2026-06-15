# 新蜂商城 Android 客户端

基于新蜂商城 API 的 Android 客户端应用，实现电商核心功能。

## 项目概述

本项目是新蜂商城的 Android 客户端，对接后端 API 实现完整的电商购物流程。

- **接口地址**：`http://115.158.64.84:28019`
- **原项目地址**：
  - 后端 API：https://gitee.com/hbxy/newbee-mall-api
  - Vue 前端：https://gitee.com/newbee-ltd/newbee-mall-vue3-app
  - 参考 Android 项目：https://gitee.com/android-zz/ShopApp.git

## 功能特性

- **首页**：轮播图、导航栏、新品上线、热门商品、为你推荐
- **分类**：左侧分类列表 + 右侧商品列表
- **购物车**：商品管理、数量修改、合计计算
- **我的**：用户信息、订单管理、地址管理
- **商品详情**：商品展示、加入购物车、立即购买
- **搜索**：关键词搜索、排序筛选
- **订单流程**：创建订单、模拟支付、订单状态跟踪
- **地址管理**：收货地址增删改查

## 技术栈

- **开发语言**：Java
- **构建工具**：Gradle 8.11.1
- **最低 SDK**：API 24 (Android 7.0)
- **目标 SDK**：API 34 (Android 14)
- **网络请求**：HttpURLConnection（原生）
- **图片加载**：Glide 4.12.0
- **轮播图**：Banner 2.2.3
- **UI 组件**：Material Design + RecyclerView

## 项目结构

```
app/src/main/
├── java/com/example/newbeemall/
│   ├── activity/          # Activity 页面
│   ├── fragment/          # Fragment 页面
│   ├── adapter/           # RecyclerView 适配器
│   ├── model/             # 数据模型
│   ├── util/              # 工具类
│   └── view/              # 自定义 View
└── res/
    ├── layout/            # 布局文件
    ├── drawable/          # 图形资源
    ├── menu/              # 菜单资源
    └── values/            # 值资源
```

## 快速开始

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK API 34

### 运行步骤

1. 克隆项目
   ```bash
   git clone https://github.com/YuDaWang666666/AndroidProject.git
   ```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 连接设备或启动模拟器

5. 点击 Run 运行应用

### 测试账号

- 手机号：`16666666666`
- 密码：`88888888`（需 MD5 加密）

## API 接口

主要接口列表：

| 功能 | 方法 | URL | 需要 Token |
|------|------|-----|-----------|
| 登录 | POST | /api/v1/user/login | 否 |
| 首页轮播 | GET | /api/v1/index/carousels | 否 |
| 商品搜索 | GET | /api/v1/search | 否 |
| 购物车列表 | GET | /api/v1/shop-cart | 是 |
| 创建订单 | POST | /api/v1/saveOrder | 是 |

完整接口文档：`http://115.158.64.84:28019/swagger-ui/index.html`

## 开发说明

详细开发文档请参考：[项目实现文档.md](项目实现文档.md)

## 注意事项

1. 所有接口均为 HTTP 协议，需在 AndroidManifest 中设置 `android:usesCleartextTraffic="true"`
2. 密码传输前必须进行 MD5 加密（32位小写）
3. 图片 URL 如果不以 http 开头，需要拼接 BASE_URL
4. 网络请求必须在子线程执行，UI 更新必须回到主线程

## 许可证

本项目仅供学习交流使用。
