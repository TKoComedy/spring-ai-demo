# Spring AI Demo 配置指南

本指南详细说明Spring AI Demo应用的各种配置选项。

## 环境配置

### Java环境

```bash
# 检查Java版本（需要Java 8）
java -version

# 设置JAVA_HOME（如果需要）
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Maven配置

#### settings.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <!-- 本地仓库路径 -->
    <localRepository>${user.home}/.m2/repository</localRepository>
    
    <!-- 阿里云Maven镜像 -->
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <name>Aliyun Maven</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
    </mirrors>
    
    <!-- Java 8 配置 -->
    <profiles>
        <profile>
            <id>jdk-1.8</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>1.8</jdk>
            </activation>
            <properties>
                <maven.compiler.source>1.8</maven.compiler.source>
                <maven.compiler.target>1.8</maven.compiler.target>
                <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
</settings>
```

## 应用配置

### application.yml

```yaml
# 服务器配置
server:
  port: 8082                    # 应用端口
  servlet:
    context-path: /             # 上下文路径

# Spring配置
spring:
  application:
    name: spring-ai-demo        # 应用名称
  devtools:
    restart:
      enabled: true             # 开发模式自动重启
    livereload:
      enabled: true             # 热重载

# Ollama配置
ollama:
  base-url: http://localhost:11434  # Ollama服务地址
  model: qwen:0.5b                  # 使用的AI模型

# 日志配置
logging:
  level:
    org.example: DEBUG          # 应用包日志级别
    org.springframework.web: INFO
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/spring-ai-demo.log
    max-size: 10MB
    max-history: 30

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 环境变量配置

```bash
# 设置环境变量
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=qwen:0.5b
export SPRING_PROFILES_ACTIVE=prod
export JAVA_OPTS="-Xmx1g -Xms512m"
```

### 不同环境配置

#### 开发环境 (application-dev.yml)
```yaml
server:
  port: 8082

spring:
  devtools:
    restart:
      enabled: true

ollama:
  base-url: http://localhost:11434
  model: qwen:0.5b

logging:
  level:
    org.example: DEBUG
```

#### 生产环境 (application-prod.yml)
```yaml
server:
  port: 8082

spring:
  devtools:
    restart:
      enabled: false

ollama:
  base-url: http://localhost:11434
  model: qwen:0.5b

logging:
  level:
    org.example: INFO
  file:
    name: /var/log/spring-ai-demo/app.log
```

## Ollama配置

### 安装配置

```bash
# 下载兼容版本
wget https://hk.gh-proxy.com/github.com/ollama/ollama/releases/download/v0.1.29/ollama-linux-amd64 -O /tmp/ollama
chmod +x /tmp/ollama
mv /tmp/ollama /usr/local/bin/ollama

# 验证安装
ollama --version
```

### 服务配置

```bash
# 启动服务
ollama serve > /var/log/ollama.log 2>&1 &

# 检查服务状态
ps aux | grep ollama
curl -s http://localhost:11434/api/tags
```

### 模型配置

```bash
# 下载推荐模型
ollama pull qwen:0.5b

# 查看可用模型
ollama list

# 删除不需要的模型
ollama rm model-name
```

### 系统服务配置

```bash
# 创建systemd服务文件
cat > /etc/systemd/system/ollama.service << EOF
[Unit]
Description=Ollama AI Service
After=network.target

[Service]
Type=simple
User=root
ExecStart=/usr/local/bin/ollama serve
Restart=always
RestartSec=10
StandardOutput=append:/var/log/ollama.log
StandardError=append:/var/log/ollama.log

[Install]
WantedBy=multi-user.target
EOF

# 启用服务
systemctl daemon-reload
systemctl enable ollama
systemctl start ollama
```

## 数据库配置（可选）

### H2数据库（开发环境）
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### MySQL数据库（生产环境）
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring_ai_demo?useSSL=false&serverTimezone=UTC
    username: root
    password: your-password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

## 安全配置

### 基本安全配置
```yaml
spring:
  security:
    user:
      name: admin
      password: your-password
    basic:
      enabled: true
```

### CORS配置
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

## 性能配置

### JVM参数优化
```bash
# 生产环境JVM参数
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
java $JAVA_OPTS -jar spring-ai-demo-1.0-SNAPSHOT.jar
```

### 应用性能配置
```yaml
# 连接池配置
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

# 缓存配置
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=600s
```

## 监控配置

### 健康检查配置
```yaml
management:
  health:
    ollama:
      enabled: true
    defaults:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### 日志监控
```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/spring-ai-demo/app.log
    max-size: 100MB
    max-history: 30
```

## 部署配置

### Docker配置
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/spring-ai-demo-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose配置
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8082:8082"
    environment:
      - OLLAMA_BASE_URL=http://ollama:11434
    depends_on:
      - ollama
  
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama

volumes:
  ollama_data:
```

## 故障排除配置

### 调试配置
```yaml
# 启用调试模式
logging:
  level:
    org.example: DEBUG
    org.springframework.web: DEBUG
    org.springframework.ai: DEBUG

# 启用详细错误信息
server:
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: always
```

### 连接超时配置
```yaml
# WebClient超时配置
spring:
  webflux:
    client:
      connect-timeout: 30s
      read-timeout: 60s
```

---

**注意**: 根据实际部署环境调整相应的配置参数。 