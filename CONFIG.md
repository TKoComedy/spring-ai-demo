# Spring AI Demo 配置说明

## 环境变量配置

在运行项目前，请设置以下环境变量：

### OpenAI 配置 (推荐)

```bash
# Windows PowerShell
$env:OPENAI_API_KEY="your-openai-api-key-here"

# Windows CMD
set OPENAI_API_KEY=your-openai-api-key-here

# Linux/Mac
export OPENAI_API_KEY=your-openai-api-key-here
```

### Google Cloud 配置 (可选)

```bash
# Windows PowerShell
$env:GOOGLE_CLOUD_PROJECT_ID="your-google-cloud-project-id"

# Windows CMD
set GOOGLE_CLOUD_PROJECT_ID=your-google-cloud-project-id

# Linux/Mac
export GOOGLE_CLOUD_PROJECT_ID=your-google-cloud-project-id
```

## 获取API密钥

### OpenAI API密钥

1. 访问 [OpenAI Platform](https://platform.openai.com/)
2. 注册或登录账户
3. 进入 [API Keys](https://platform.openai.com/api-keys) 页面
4. 点击 "Create new secret key"
5. 复制生成的密钥

### Google Cloud 项目ID

1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建新项目或选择现有项目
3. 在项目概览页面找到项目ID
4. 启用 Vertex AI API

## 本地AI模型 (Ollama)

如果你想使用本地AI模型，可以安装 Ollama：

1. 访问 [Ollama](https://ollama.ai/) 下载安装
2. 启动 Ollama 服务
3. 下载模型：
   ```bash
   ollama pull llama2
   ```

## 配置文件说明

项目使用 `application.yml` 进行配置，主要配置项包括：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-openai-api-key}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 1000
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

## 常见配置问题

### 1. API密钥无效
- 检查API密钥是否正确设置
- 确认API密钥有足够的余额
- 验证API密钥的权限设置

### 2. 网络连接问题
- 检查网络连接是否正常
- 某些地区可能需要配置代理
- 确认防火墙设置

### 3. 模型不可用
- 检查模型名称是否正确
- 确认模型是否在对应服务中可用
- 验证账户权限

## 开发环境配置

### IDE配置 (IntelliJ IDEA)

1. 打开项目设置 (File > Settings)
2. 进入 Build, Execution, Deployment > Build Tools > Maven
3. 确保Maven配置正确
4. 在运行配置中设置环境变量

### 命令行运行

```bash
# 设置环境变量并运行
export OPENAI_API_KEY=your-key
mvn spring-boot:run

# 或者使用Maven profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 生产环境配置

在生产环境中，建议：

1. 使用环境变量而不是硬编码配置
2. 设置适当的日志级别
3. 配置监控和告警
4. 使用HTTPS
5. 设置API使用限制

```yaml
spring:
  profiles:
    active: prod
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.3
          max-tokens: 500

logging:
  level:
    org.springframework.ai: INFO
    org.example: INFO
``` 