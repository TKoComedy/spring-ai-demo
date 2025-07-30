# Spring AI Demo 变更记录

本文档记录了Spring AI Demo项目从初始状态到成功部署的所有重要变更。

## 项目概述

- **项目名称**: Spring AI Demo
- **技术栈**: Java 8 + Spring Boot 2.7.18 + Ollama v0.1.29
- **AI模型**: qwen:0.5b (千问0.5B参数模型)
- **部署环境**: CentOS 7 服务器

## 主要变更

### 1. Java版本兼容性调整

**问题**: 初始项目使用Java 17，但服务器环境为Java 8

**解决方案**:
- 将Spring Boot版本从3.2.0降级到2.7.18
- 设置Maven编译器版本为Java 8
- 移除Java 9+特性（如`List.of()`, `Map.of()`, 文本块等）

**修改文件**:
- `pom.xml`: 更新Spring Boot版本和Java编译器配置
- `src/main/java/org/example/service/AiService.java`: 替换Java 9+语法
- `src/main/java/org/example/service/OllamaService.java`: 替换Java 9+语法

### 2. AI后端从Spring AI切换到Ollama

**问题**: Spring AI框架与Java 8不兼容

**解决方案**:
- 移除Spring AI依赖
- 实现自定义Ollama客户端
- 使用Spring WebFlux的WebClient与Ollama API通信

**修改文件**:
- `pom.xml`: 移除Spring AI依赖，添加WebFlux和Jackson依赖
- `src/main/java/org/example/service/OllamaService.java`: 新增Ollama客户端实现
- `src/main/java/org/example/controller/AiChatController.java`: 更新控制器逻辑

### 3. Ollama版本兼容性

**问题**: 最新版Ollama与CentOS 7的GLIBC版本不兼容

**解决方案**:
- 使用Ollama v0.1.29版本（兼容CentOS 7）
- 通过GitHub代理下载二进制文件
- 手动安装和配置服务

**修改文件**:
- `DEPLOYMENT_GUIDE.md`: 更新安装步骤
- `README.md`: 更新环境要求

### 4. AI模型选择

**问题**: 大模型导致内存不足和响应缓慢

**解决方案**:
- 选择qwen:0.5b模型（394MB，支持中文）
- 删除大模型（deepseek-r1:1.5b等）
- 优化内存使用

**修改文件**:
- `src/main/resources/application.yml`: 更新模型配置
- `src/main/java/org/example/service/OllamaService.java`: 更新默认模型

### 5. 服务器部署配置

**问题**: 需要适配CentOS 7环境

**解决方案**:
- 使用yum包管理器
- 配置systemd服务
- 设置Nginx反向代理
- 配置防火墙规则

**修改文件**:
- `deploy.sh`: 更新部署脚本
- `DEPLOYMENT_GUIDE.md`: 完善部署指南

## 技术细节

### 成功的技术栈组合

```xml
<!-- Java版本 -->
<maven.compiler.source>8</maven.compiler.source>
<maven.compiler.target>8</maven.compiler.target>

<!-- Spring Boot版本 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<!-- 核心依赖 -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### 成功的配置

```yaml
# application.yml
server:
  port: 8082

spring:
  application:
    name: spring-ai-demo

ollama:
  base-url: http://localhost:11434
  model: qwen:0.5b

logging:
  level:
    org.example: DEBUG
```

### 成功的部署环境

- **操作系统**: CentOS 7
- **Java**: OpenJDK 8
- **Maven**: 3.8.1
- **Ollama**: v0.1.29
- **内存**: 3.6GB
- **模型**: qwen:0.5b (394MB)

## 性能优化

### 内存优化
- 使用小模型（qwen:0.5b）
- 删除不必要的依赖
- 优化JVM参数

### 响应速度优化
- 选择轻量级模型
- 优化WebClient配置
- 减少不必要的网络请求

## 故障排除经验

### 常见问题及解决方案

1. **Ollama连接失败**
   - 检查服务状态：`ps aux | grep ollama`
   - 重启服务：`ollama serve > /var/log/ollama.log 2>&1 &`

2. **模型加载失败**
   - 检查模型：`ollama list`
   - 重新下载：`ollama pull qwen:0.5b`

3. **内存不足**
   - 使用小模型
   - 检查内存使用：`free -h`

4. **端口冲突**
   - 修改应用端口
   - 检查端口占用：`netstat -tlnp | grep 8082`

## 文档更新

### 更新的文档
- `README.md`: 项目概述和快速开始
- `DEPLOYMENT_GUIDE.md`: 详细部署指南
- `QUICK_START.md`: 快速部署步骤
- `CONFIG.md`: 配置说明
- `CHANGES.md`: 变更记录（本文档）

### 新增的文档
- 故障排除指南
- 性能优化建议
- 监控和维护说明

## 部署成功验证

### 功能测试
- ✅ 健康检查API: `GET /api/ai/health`
- ✅ AI聊天API: `POST /api/ai/chat`
- ✅ 中文对话支持
- ✅ 响应时间 < 30秒

### 系统测试
- ✅ Ollama服务稳定运行
- ✅ Spring Boot应用正常启动
- ✅ Nginx反向代理配置正确
- ✅ 内存使用合理（< 1GB）

## 总结

通过以上变更，项目成功实现了：
1. Java 8兼容性
2. CentOS 7环境适配
3. 轻量级AI模型集成
4. 稳定的服务器部署
5. 完整的中文AI对话功能

项目现在可以在资源有限的服务器上稳定运行，提供良好的用户体验。 