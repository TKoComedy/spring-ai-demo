# Spring AI Demo

一个基于Spring Boot的AI聊天应用，使用Ollama本地大语言模型。

## 技术栈

- **Java**: 8
- **Spring Boot**: 2.7.18
- **Maven**: 3.8.1
- **Ollama**: v0.1.29
- **AI模型**: qwen:0.5b (千问0.5B参数模型)

## 功能特性

- ✅ AI聊天对话
- ✅ 系统提示词支持
- ✅ 模板化对话
- ✅ 代码生成
- ✅ 文档摘要
- ✅ 图像生成（支持多种API）
- ✅ 文本嵌入（模拟）
- ✅ 批量嵌入处理
- ✅ 流式输出（Server-Sent Events）
- ✅ 健康检查API

## 快速开始

### 环境要求

- Java 8+
- Maven 3.6+
- Ollama v0.1.29+

### 1. 安装Ollama

#### 下载兼容版本
由于服务器环境限制，需要使用兼容的Ollama版本：

```bash
# 下载v0.1.29版本（兼容CentOS 7）
wget https://hk.gh-proxy.com/github.com/ollama/ollama/releases/download/v0.1.29/ollama-linux-amd64 -O /tmp/ollama
chmod +x /tmp/ollama
mv /tmp/ollama /usr/local/bin/ollama
```

#### 启动Ollama服务
```bash
ollama serve > /var/log/ollama.log 2>&1 &
```

#### 下载模型
```bash
# 下载千问0.5B模型（推荐，支持中文，内存占用小）
ollama pull qwen:0.5b
```

### 2. 构建应用

```bash
mvn clean package
```

### 3. 运行应用

```bash
java -jar target/spring-ai-demo-1.0-SNAPSHOT.jar
```

应用将在 `http://localhost:8082` 启动。

## API接口

### 健康检查
```bash
GET /api/ai/health
```

### AI聊天
```bash
POST /api/ai/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下自己"
}
```

### 流式输出

#### 流式聊天
```bash
POST /api/ai/chat
Content-Type: application/json
Accept: text/event-stream

{
  "message": "你好，请介绍一下自己"
}
```

#### 流式代码生成
```bash
POST /api/advanced-ai/generate-code
Content-Type: application/json
Accept: text/event-stream

{
  "requirement": "实现一个简单的计算器",
  "language": "Java"
}
```

#### 流式文档摘要
```bash
POST /api/advanced-ai/summarize
Content-Type: application/json
Accept: text/event-stream

{
  "content": "要摘要的文档内容..."
}
```

#### 流式模板聊天
```bash
POST /api/advanced-ai/chat-template
Content-Type: application/json
Accept: text/event-stream

{
  "topic": "人工智能",
  "style": "科普"
}
```

#### 流式系统提示聊天
```bash
POST /api/advanced-ai/chat-with-system
Content-Type: application/json
Accept: text/event-stream

{
  "userMessage": "请帮我写一首诗",
  "systemPrompt": "你是一个诗人"
}
```

### 高级功能

#### 代码生成（流式）
```bash
POST /api/advanced-ai/generate-code
Content-Type: application/json
Accept: text/event-stream

{
  "requirement": "实现一个简单的计算器",
  "language": "Java"
}
```

#### 文档摘要（流式）
```bash
POST /api/advanced-ai/summarize
Content-Type: application/json
Accept: text/event-stream

{
  "content": "要摘要的文档内容..."
}
```

#### 模板聊天（流式）
```bash
POST /api/advanced-ai/chat-template
Content-Type: application/json
Accept: text/event-stream

{
  "topic": "人工智能",
  "style": "科普"
}
```

#### 系统提示聊天（流式）
```bash
POST /api/advanced-ai/chat-with-system
Content-Type: application/json
Accept: text/event-stream

{
  "userMessage": "请帮我写一首诗",
  "systemPrompt": "你是一个诗人"
}
```

#### 图像生成
```bash
POST /api/ai/advanced/generate-image
Content-Type: application/json

{
  "prompt": "一只可爱的小猫"
}
```

## 配置说明

### application.yml
```yaml
server:
  port: 8082

spring:
  application:
    name: spring-ai-demo

# Ollama配置
ollama:
  base-url: http://localhost:11434
  model: qwen:0.5b

# 日志配置
logging:
  level:
    org.example: DEBUG
```

### Maven配置
```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
</properties>
```

## 部署指南

详细的服务器部署指南请参考 [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

### 快速部署
```bash
# 上传部署脚本
scp deploy.sh root@your-server:/tmp/
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@your-server:/tmp/

# 在服务器上执行
ssh root@your-server
chmod +x /tmp/deploy.sh
/tmp/deploy.sh
```

## 故障排除

### 常见问题

1. **Ollama连接失败**
   - 检查Ollama服务是否运行：`ps aux | grep ollama`
   - 重启Ollama：`ollama serve > /var/log/ollama.log 2>&1 &`

2. **模型加载失败**
   - 检查模型是否存在：`ollama list`
   - 重新下载模型：`ollama pull qwen:0.5b`

3. **内存不足**
   - 使用更小的模型：`qwen:0.5b` (394MB)
   - 检查内存使用：`free -h`

4. **端口冲突**
   - 修改`application.yml`中的端口配置
   - 检查端口占用：`netstat -tlnp | grep 8082`

### 日志查看
```bash
# 应用日志
journalctl -u spring-ai-demo -f

# Ollama日志
tail -f /var/log/ollama.log
```

## 测试

启动应用后，可以使用以下方式测试：

1. **浏览器访问**: http://localhost:8082
2. **流式输出测试**: http://localhost:8082/streaming-test.html
3. **API测试**: 使用Postman或curl测试各个接口
4. **健康检查**: http://localhost:8082/api/ai/health

### 流式输出测试
访问 `http://localhost:8082/streaming-test.html` 可以测试：
- 流式聊天对话
- 流式代码生成
- 流式文档摘要
- 图像生成功能

## 开发说明

### 项目结构
```
src/main/java/org/example/
├── Main.java                 # 应用入口
├── controller/
│   ├── AiChatController.java     # 基础聊天控制器
│   └── AdvancedAiController.java # 高级功能控制器
└── service/
    ├── AiService.java            # AI服务接口
    └── OllamaService.java        # Ollama客户端
```

### 添加新功能
1. 在`AiService`中添加新方法
2. 在`OllamaService`中实现具体逻辑
3. 在控制器中添加API接口
4. 更新文档

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！ 