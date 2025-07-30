# Spring AI Demo 快速开始

本指南提供Spring AI Demo应用的快速部署步骤。

## 环境要求

- **Java**: 8+
- **Maven**: 3.6+
- **Ollama**: v0.1.29+
- **内存**: 至少2GB RAM

## 快速部署步骤

### 1. 构建应用

```bash
# 克隆项目（如果从Git仓库）
git clone <repository-url>
cd spring-ai-demo

# 构建项目
mvn clean package
```

### 2. 安装Ollama

```bash
# 下载兼容版本（CentOS 7兼容）
wget https://hk.gh-proxy.com/github.com/ollama/ollama/releases/download/v0.1.29/ollama-linux-amd64 -O /tmp/ollama
chmod +x /tmp/ollama
mv /tmp/ollama /usr/local/bin/ollama

# 启动Ollama
ollama serve > /var/log/ollama.log 2>&1 &

# 下载模型
ollama pull qwen:0.5b
```

### 3. 运行应用

```bash
# 直接运行
java -jar target/spring-ai-demo-1.0-SNAPSHOT.jar

# 或后台运行
nohup java -jar target/spring-ai-demo-1.0-SNAPSHOT.jar > app.log 2>&1 &
```

### 4. 测试应用

```bash
# 健康检查
curl http://localhost:8082/api/ai/health

# 测试AI聊天
curl -X POST http://localhost:8082/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

## 服务器部署

### 使用部署脚本

```bash
# 上传文件
scp deploy.sh root@your-server:/tmp/
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@your-server:/tmp/

# 执行部署
ssh root@your-server
chmod +x /tmp/deploy.sh
/tmp/deploy.sh
```

### 手动部署

```bash
# 1. 创建目录
mkdir -p /opt/spring-ai-demo
cd /opt/spring-ai-demo

# 2. 上传JAR文件
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@server:/opt/spring-ai-demo/

# 3. 创建配置
cat > application.yml << EOF
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
EOF

# 4. 创建服务
cat > /etc/systemd/system/spring-ai-demo.service << EOF
[Unit]
Description=Spring AI Demo Application
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/spring-ai-demo
ExecStart=java -jar spring-ai-demo-1.0-SNAPSHOT.jar --spring.config.location=file:./application.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 5. 启动服务
systemctl daemon-reload
systemctl enable spring-ai-demo
systemctl start spring-ai-demo
```

## 配置说明

### 应用配置 (application.yml)

```yaml
server:
  port: 8082                    # 应用端口

spring:
  application:
    name: spring-ai-demo

ollama:
  base-url: http://localhost:11434  # Ollama服务地址
  model: qwen:0.5b                  # 使用的AI模型

logging:
  level:
    org.example: DEBUG
```

### Maven配置 (pom.xml)

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
</properties>
```

## API接口

### 基础接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/health` | GET | 健康检查 |
| `/api/ai/chat` | POST | AI聊天 |

### 高级接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/advanced/code` | POST | 代码生成 |
| `/api/ai/advanced/summarize` | POST | 文档摘要 |
| `/api/ai/advanced/image` | POST | 图像描述 |

### 请求示例

```bash
# AI聊天
curl -X POST http://localhost:8082/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下自己"}'

# 代码生成
curl -X POST http://localhost:8082/api/ai/advanced/code \
  -H "Content-Type: application/json" \
  -d '{"requirement": "实现一个简单的计算器", "language": "Java"}'
```

## 故障排除

### 常见问题

1. **Ollama连接失败**
   ```bash
   # 检查Ollama状态
   ps aux | grep ollama
   
   # 重启Ollama
   pkill ollama
   ollama serve > /var/log/ollama.log 2>&1 &
   ```

2. **模型加载失败**
   ```bash
   # 检查模型
   ollama list
   
   # 重新下载
   ollama pull qwen:0.5b
   ```

3. **应用启动失败**
   ```bash
   # 检查Java版本
   java -version
   
   # 检查端口
   netstat -tlnp | grep 8082
   
   # 查看日志
   journalctl -u spring-ai-demo -f
   ```

### 性能优化

1. **内存优化**
   - 使用小模型：`qwen:0.5b` (394MB)
   - 调整JVM参数：`-Xmx1g -Xms512m`

2. **网络优化**
   - 配置Nginx反向代理
   - 使用CDN加速

## 监控命令

```bash
# 查看服务状态
systemctl status spring-ai-demo

# 查看日志
journalctl -u spring-ai-demo -f

# 查看资源使用
free -h
df -h

# 查看进程
ps aux | grep -E "(java|ollama)"
```

## 更新应用

```bash
# 停止服务
systemctl stop spring-ai-demo

# 备份旧版本
cp /opt/spring-ai-demo/spring-ai-demo-1.0-SNAPSHOT.jar /backup/

# 上传新版本
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@server:/opt/spring-ai-demo/

# 启动服务
systemctl start spring-ai-demo
```

---

**注意**: 本指南适用于CentOS 7环境，其他系统可能需要调整相应命令。 