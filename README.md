# Spring AI 学习项目

这是一个用于学习 Spring AI 框架的演示项目，包含了多种 AI 功能的实现示例。

## 🚀 项目特性

- **智能聊天**: 与AI进行自然语言对话
- **代码生成**: 根据需求自动生成高质量代码
- **图像生成**: 根据文本描述生成精美图像
- **文本嵌入**: 将文本转换为向量表示
- **文档摘要**: 自动提取文档关键信息
- **模板系统**: 灵活的提示模板管理

## 🛠️ 技术栈

- **Spring Boot 3.2**: 主框架
- **Spring AI 0.8**: AI集成框架
- **Thymeleaf**: 模板引擎
- **Bootstrap 5**: 前端UI框架
- **Java 8**: 编程语言

## 📋 环境要求

- Java 8 或更高版本
- Maven 3.6 或更高版本
- 可选的AI服务API密钥

## 🔧 快速开始

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd spring-ai-demo
```

### 2. 配置AI服务

#### OpenAI (推荐)
1. 获取 OpenAI API 密钥: https://platform.openai.com/api-keys
2. 设置环境变量:
```bash
export OPENAI_API_KEY=your-api-key-here
```

#### Ollama (本地AI)
1. 安装 Ollama: https://ollama.ai/
2. 下载模型:
```bash
ollama pull llama2
```

#### Google Vertex AI
1. 设置 Google Cloud 项目
2. 配置认证
3. 设置环境变量:
```bash
export GOOGLE_CLOUD_PROJECT_ID=your-project-id
```

### 3. 运行项目

```bash
mvn spring-boot:run
```

### 4. 访问应用

打开浏览器访问: http://localhost:8080

## 📖 功能说明

### 1. 基础聊天 (`/chat`)
- 与AI进行简单的对话
- 支持快捷问题按钮
- 实时聊天界面

### 2. 高级功能 (`/advanced`)
- **代码生成**: 输入需求描述和编程语言，AI生成代码
- **文档摘要**: 输入长文档，AI生成摘要
- **图像生成**: 输入描述，AI生成图像
- **文本嵌入**: 将文本转换为向量
- **模板聊天**: 使用模板生成特定风格的内容

### 3. API接口

#### 基础聊天
```bash
POST /api/ai/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下Spring AI"
}
```

#### 代码生成
```bash
POST /api/advanced-ai/generate-code
Content-Type: application/json

{
  "requirement": "实现一个简单的计算器",
  "language": "Java"
}
```

#### 文档摘要
```bash
POST /api/advanced-ai/summarize
Content-Type: application/json

{
  "content": "长文档内容..."
}
```

#### 图像生成
```bash
POST /api/advanced-ai/generate-image
Content-Type: application/json

{
  "prompt": "一只可爱的小猫在花园里玩耍"
}
```

## 🏗️ 项目结构

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── Main.java                 # Spring Boot 主类
│   │   ├── controller/
│   │   │   ├── AiChatController.java     # 基础聊天控制器
│   │   │   ├── AdvancedAiController.java # 高级功能控制器
│   │   │   └── WebController.java        # Web页面控制器
│   │   └── service/
│   │       └── AiService.java            # AI服务类
│   └── resources/
│       ├── application.yml              # 配置文件
│       └── templates/
│           ├── index.html               # 主页
│           ├── chat.html                # 聊天页面
│           └── advanced.html            # 高级功能页面
```

## 🔍 学习要点

### Spring AI 核心概念

1. **ChatClient**: 聊天客户端，用于与AI模型对话
2. **EmbeddingClient**: 嵌入客户端，用于文本向量化
3. **ImageClient**: 图像客户端，用于图像生成
4. **Prompt**: 提示对象，包含用户输入和系统提示
5. **PromptTemplate**: 提示模板，支持变量替换

### 配置说明

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 1000
```

### 代码示例

#### 基础聊天
```java
@Autowired
private ChatClient chatClient;

public String chat(String message) {
    Prompt prompt = new Prompt(new UserMessage(message));
    ChatResponse response = chatClient.call(prompt);
    return response.getResult().getOutput().getContent();
}
```

#### 带系统提示的聊天
```java
public String chatWithSystem(String userMessage, String systemPrompt) {
    Message systemMessage = new SystemMessage(systemPrompt);
    Message userMsg = new UserMessage(userMessage);
    
    Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
    ChatResponse response = chatClient.call(prompt);
    return response.getResult().getOutput().getContent();
}
```

#### 使用模板
```java
String template = "请以{style}的风格，写一篇关于{topic}的文章。";
PromptTemplate promptTemplate = new PromptTemplate(template);
Prompt prompt = promptTemplate.create(Map.of("topic", "AI", "style", "专业"));
```

## 🐛 常见问题

### 1. API密钥配置
确保正确设置了环境变量或配置文件中的API密钥。

### 2. 网络连接
某些AI服务可能需要网络代理，请确保网络连接正常。

### 3. 模型可用性
不同AI服务支持的模型可能不同，请参考官方文档。

## 📚 学习资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [OpenAI API 文档](https://platform.openai.com/docs)
- [Ollama 文档](https://ollama.ai/docs)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 📄 许可证

本项目仅用于学习和演示目的。

---

**注意**: 使用AI服务时请注意API使用限制和费用，建议在开发环境中使用适当的配额限制。 