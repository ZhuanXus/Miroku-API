# Miroku-API

> 🔄 OpenAI API 兼容的统一大模型访问网关

```
⚠️ 本项目处于活跃开发中，部分功能可能变动
```

---

## 📋 目录

- [项目说明](#-项目说明)
- [架构设计](#-架构设计)
- [鉴权与流程](#-鉴权与流程)
- [快速启动](#-快速启动)
- [SDK 测试](#-sdk-测试)
- [AIGC 说明](#-aigc-使用说明)

---

## 📦 项目说明

一个对接 **DeepSeek / 豆包 / 千问** 等主流大模型的统一访问服务，提供 **OpenAI API 风格** 的接口封装，屏蔽底层模型差异。

### ✨ 核心特性

| 特性       | 说明                                      |
|----------|-----------------------------------------|
| 🔹 协议兼容  | 支持 `/v1/chat/completions` 等 OpenAI 标准接口 |
| 🔹 多模型路由 | 自动根据 `model` 参数路由到对应服务商                 |
| 🔹 流式转发  | 支持 `stream: true` 实时 SSE 流式响应           |
| 🔹 请求审计  | 异步记录对话日志，支持用量统计与追溯                      |
| 🔹 轻量部署  | Spring Boot + Docker Compose 一键启动       |

### 🛠 技术栈

```
后端框架   : Spring Boot 3.x
持久层     : MyBatis + MySQL 8.0
HTTP 客户端: OkHttp3
鉴权框架   : Sa-Token
构建工具   : Maven + Docker Compose
JDK 版本   : 21 (Eclipse Temurin)
```

### 🎯 适用场景

```
✅ 个人/团队统一调用入口，避免硬编码多套 API
✅ 代理转发 + 日志审计 + 用量统计
✅ 快速切换模型服务商，业务代码零修改
✅ 学习 OpenAI 协议 + Spring Boot 网关实践
```

> [!TIP]
> 本项目定位为「透明网关」，不负责模型微调、向量检索等高级功能，专注做好协议转发与基础治理。

---

## 🏗 架构设计

### 整体架构

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Client    │────▶│   Miroku-API    │────▶│  LLM Providers  │
│  (Apifox/   │     │  (Spring Boot)  │     │                 │
│   SDK/Web)  │◀────│                 │◀────│ • DeepSeek      │
└─────────────┘     └─────────────────┘     │ • 豆包 (Doubao) │
                          │                 │ • 通义千问      │
                          ▼                 └─────────────────┘
                   ┌─────────────────┐
                   │   MySQL 8.0     │
                   │  (审计日志)     │
                   └─────────────────┘
```

### 核心流程

```mermaid
%% 复制此代码到 https://mermaid.live 可渲染流程图
sequenceDiagram
    participant C as Client
    participant G as Miroku-Gateway
    participant D as DeepSeek/豆包/千问
    
    C->>G: POST /v1/chat/completions
    Note right of G: 1. Sa-Token 鉴权拦截
    G->>G: 2. 解析 model 参数，路由适配器
    G->>D: 3. OkHttp 转发请求 (协议适配)
    D-->>G: 4. 返回响应 (流式/静态)
    G-->>C: 5. 原样转发 + ID 替换
    Note right of G: 6. 异步落库审计 (非阻塞)
```

### 模块划分

```
src/
├── controller/     # 接口入口，处理 HTTP 请求/响应
├── service/        # 业务逻辑，路由决策 + 协议适配
├── adapter/        # 模型适配器，封装各厂商差异
├── dto/            # 数据传输对象，OpenAI 协议兼容
├── mapper/         # MyBatis 数据访问层
├── configuration/  # 配置绑定 + 线程池 + 全局配置
└── tool/           # 工具类 (JSON 处理 + 脱敏等)
```

> [!NOTE]
> 新增模型只需实现 `LlmAdapter` 接口 + 配置路由规则，核心转发逻辑零修改。

---

## 🔐 鉴权与生成流程

### 鉴权方式

本项目使用 **Sa-Token** 实现轻量级令牌鉴权：

```http
# 1️⃣ 获取 Token
POST /login
# ← 响应
{
    "code": 200,
    "msg": "ok",
    "data": {
        ......
        "tokenValue": "a0680f25-8e2d-4c14-abd0-e5ed77bfd578"
    }
}

# 2️⃣ 携带 Token 访问业务接口
POST /v1/chat/completions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxxxx
Content-Type: application/json

{
  "model": "deepseek-chat",
  "messages": [{"role": "user", "content": "你好"}],
  "stream": true
}
```

### 请求生成流程

```
┌─────────────────────────────────────┐
│  1. 客户端发起 /v1/chat/completions  │
└────────────┬────────────────────────┘
             ▼
┌─────────────────────────────────────┐
│  2. Sa-Token 拦截器校验 Authorization│
│     • 无效/过期 → 401 Unauthorized  │
│     • 有效 → 放行                   │
└────────────┬────────────────────────┘
             ▼
┌─────────────────────────────────────┐
│  3. Controller 解析请求              │
│     • 生成全局唯一 request_id        │
│     • 根据 model 参数选择 Adapter    │
└────────────┬────────────────────────┘
             ▼
┌─────────────────────────────────────┐
│  4. Adapter 协议适配 + OkHttp 转发   │
│     • 参数转换 (角色/格式/密钥注入)  │
│     • 执行上游 POST 请求             │
└────────────┬────────────────────────┘
             ▼
┌─────────────────────────────────────┐
│  5. 响应处理                        │
│     • 流式: StreamingResponseBody   │
│       - 逐块转发 + ID 替换 + 内容拼装│
│     • 静态: 直接返回 DTO            │
│     • 异步落库审计 (不阻塞响应)     │
└────────────┬────────────────────────┘
             ▼
┌─────────────────────────────────────┐
│  6. 客户端接收响应                  │
│     • SSE 流: 逐字渲染              │
│     • JSON: 一次性解析              │
└─────────────────────────────────────┘
```

> [!WARNING]
> 当前版本未实现用户管理/权限分级，`/login` 仅返回固定 Token，生产环境请自行扩展。

---

## 🚀 快速启动

### 环境准备

| 依赖                | 版本     | 说明        |
|-------------------|--------|-----------|
| 🐳 Docker         | 20.10+ | 容器运行时     |
| 🐳 Docker Compose | v2+    | 编排工具      |
| ☕ Maven           | 3.8+   | Java 构建   |
| 📄 .env 文件        | -      | 环境配置（见下方） |

### 配置说明

1. 复制环境变量模板：
   ```bash
   cp .env.example .env
   ```

2. 编辑 `.env` 文件，填写必要参数：
   ```env
    # MySQL 配置
    MYSQL_USER=xxx
    MYSQL_PASSWORD=xxx
    MYSQL_ROOT_PASSWORD=xxx
   
   # 模型服务商密钥 (按需填写)
   DEEPSEEK_API_KEY=sk-xxx
   DOUBAO_API_KEY=xxx
   QWEN_API_KEY=xxx
   ```

### 一键启动

```powershell
# 1️⃣ 清理 + 构建
mvn clean
mvn package -DskipTests

# 2️⃣ 拉取基础镜像 (首次执行)
docker pull eclipse-temurin:21-jre-jammy
docker pull mysql:8.0

# 3️⃣ 启动服务
docker compose up -d --build
```

### 验证运行

```bash
# 检查容器状态
docker compose ps

# 预期输出：
# NAME                STATUS          PORTS
# miroku-api          Up 30 seconds   0.0.0.0:8080->8080/tcp
# miroku-mysql        Up 30 seconds   3306/tcp
```

### 常用运维命令

```bash
# 🔹 停止服务
docker compose down

# 🔹 重启服务
docker compose restart app

# 🔹 查看实时日志
docker compose logs -f --tail=10 app

# 🔹 进入容器调试
docker compose exec app sh

# 🔹 数据库连接 (需安装 mysql 客户端)
mysql -h 127.0.0.1 -P 3306 -u root -p miroku_api
```

> [!TIP]
> 首次启动 MySQL 可能需要 1~2 分钟初始化，如遇连接超时请稍等重试。

---

## 🧪 SDK 测试说明

```
本项目将外部SDK集成测试放在src/test/.../OpenAISDKExternalTest.java
直接在项目跟目录 打开powershell 输入 mvn test 即可运行喵
  ```

---

## 🤖 AIGC 使用说明

### 开发辅助工具

✅ 代码生成 : 通义灵码 (智能补全/单元测试/注释生成)

✅ 逻辑审阅 : 通义千问 (架构建议/边界检查/异常场景)

✅ 文档撰写 : 千问 + 灵码 (接口说明/故障排查/最佳实践)

```

### 开发工作流

```

1️⃣ 需求构思

└─► 用自然语言描述功能目标 + 输入输出

2️⃣ 原型实现  

└─► 手写核心逻辑 + 基础结构

3️⃣ AI 审阅

└─► 提交代码片段，请求：
• 潜在边界检查
• 性能/安全建议
• 协议兼容性确认

4️⃣ 迭代优化

└─► 根据反馈修正 + 补充测试

5️⃣ 文档同步

└─► 让 AI 辅助生成 README/接口注释/故障手册