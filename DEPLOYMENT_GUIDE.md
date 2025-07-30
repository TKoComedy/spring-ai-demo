# Spring AI Demo 服务器部署指南

本指南详细说明如何在CentOS 7服务器上部署Spring AI Demo应用。

## 系统要求

- **操作系统**: CentOS 7 / RHEL 7
- **内存**: 至少2GB RAM（推荐4GB）
- **存储**: 至少10GB可用空间
- **网络**: 可访问外网下载依赖

## 环境准备

### 1. 更新系统
```bash
yum update -y
```

### 2. 安装Java 8
```bash
# 安装OpenJDK 8
yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

# 验证安装
java -version
```

### 3. 安装Maven
```bash
# 安装Maven
yum install -y maven

# 验证安装
mvn -version
```

### 4. 安装Docker（可选）
```bash
# 安装Docker
yum install -y docker

# 启动Docker服务
systemctl start docker
systemctl enable docker

# 配置Docker镜像加速
mkdir -p /etc/docker
cat > /etc/docker/daemon.json << EOF
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ]
}
EOF

systemctl restart docker
```

## 安装Ollama

### 1. 下载兼容版本
由于CentOS 7的GLIBC版本限制，需要使用兼容的Ollama版本：

```bash
# 下载v0.1.29版本（兼容CentOS 7）
wget https://hk.gh-proxy.com/github.com/ollama/ollama/releases/download/v0.1.29/ollama-linux-amd64 -O /tmp/ollama

# 安装到系统
chmod +x /tmp/ollama
mv /tmp/ollama /usr/local/bin/ollama

# 验证安装
ollama --version
```

### 2. 启动Ollama服务
```bash
# 后台启动Ollama
ollama serve > /var/log/ollama.log 2>&1 &

# 检查服务状态
ps aux | grep ollama
```

### 3. 下载AI模型
```bash
# 下载千问0.5B模型（推荐：支持中文，内存占用小）
ollama pull qwen:0.5b

# 验证模型
ollama list
```

## 部署Spring Boot应用

### 1. 创建应用目录
```bash
mkdir -p /opt/spring-ai-demo
cd /opt/spring-ai-demo
```

### 2. 上传应用文件
```bash
# 从本地上传JAR文件
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@your-server:/opt/spring-ai-demo/

# 或使用提供的上传脚本
./upload.sh your-server-ip
```

### 3. 创建配置文件
```bash
cat > /opt/spring-ai-demo/application.yml << EOF
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
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
EOF
```

### 4. 创建启动脚本
```bash
cat > /opt/spring-ai-demo/start.sh << 'EOF'
#!/bin/bash
cd /opt/spring-ai-demo
java -jar spring-ai-demo-1.0-SNAPSHOT.jar --spring.config.location=file:./application.yml
EOF

chmod +x /opt/spring-ai-demo/start.sh
```

### 5. 创建systemd服务
```bash
cat > /etc/systemd/system/spring-ai-demo.service << EOF
[Unit]
Description=Spring AI Demo Application
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/spring-ai-demo
ExecStart=/opt/spring-ai-demo/start.sh
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemd配置
systemctl daemon-reload

# 启用服务
systemctl enable spring-ai-demo

# 启动服务
systemctl start spring-ai-demo

# 检查服务状态
systemctl status spring-ai-demo
```

## 配置Nginx反向代理

### 1. 安装Nginx
```bash
yum install -y nginx
systemctl start nginx
systemctl enable nginx
```

### 2. 配置Nginx
```bash
cat > /etc/nginx/conf.d/spring-ai-demo.conf << EOF
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    location / {
        proxy_pass http://127.0.0.1:8082;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;

        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
EOF

# 测试配置
nginx -t

# 重启Nginx
systemctl restart nginx
```

## 配置防火墙

### 1. 开放必要端口
```bash
# 开放HTTP端口
firewall-cmd --permanent --add-service=http
firewall-cmd --permanent --add-service=https

# 开放应用端口（如果需要直接访问）
firewall-cmd --permanent --add-port=8082/tcp

# 重新加载防火墙规则
firewall-cmd --reload
```

### 2. 检查端口状态
```bash
# 检查端口监听状态
netstat -tlnp | grep -E "(80|8082|11434)"

# 检查防火墙规则
firewall-cmd --list-all
```

## 测试部署

### 1. 测试Ollama服务
```bash
# 检查Ollama状态
curl -s http://localhost:11434/api/tags

# 测试模型
ollama run qwen:0.5b "你好"
```

### 2. 测试Spring Boot应用
```bash
# 健康检查
curl -s http://localhost:8082/api/ai/health

# 测试AI聊天
curl -X POST http://localhost:8082/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

### 3. 测试Nginx代理
```bash
# 通过域名访问
curl -s http://your-domain.com/api/ai/health
```

## 监控和维护

### 1. 查看日志
```bash
# 应用日志
journalctl -u spring-ai-demo -f

# Ollama日志
tail -f /var/log/ollama.log

# Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 2. 系统监控
```bash
# 内存使用
free -h

# 磁盘使用
df -h

# 进程状态
ps aux | grep -E "(java|ollama|nginx)"
```

### 3. 服务管理
```bash
# 重启应用
systemctl restart spring-ai-demo

# 重启Ollama
pkill ollama
ollama serve > /var/log/ollama.log 2>&1 &

# 重启Nginx
systemctl restart nginx
```

## 故障排除

### 1. 常见问题

#### Ollama连接失败
```bash
# 检查Ollama进程
ps aux | grep ollama

# 检查端口
netstat -tlnp | grep 11434

# 重启Ollama
pkill ollama
ollama serve > /var/log/ollama.log 2>&1 &
```

#### 模型加载失败
```bash
# 检查模型列表
ollama list

# 重新下载模型
ollama pull qwen:0.5b
```

#### 应用启动失败
```bash
# 检查Java版本
java -version

# 检查端口占用
netstat -tlnp | grep 8082

# 查看应用日志
journalctl -u spring-ai-demo -n 50
```

#### Nginx配置问题
```bash
# 测试配置
nginx -t

# 检查端口冲突
netstat -tlnp | grep 80

# 查看Nginx错误日志
tail -f /var/log/nginx/error.log
```

### 2. 性能优化

#### 内存优化
```bash
# 使用更小的模型
ollama pull qwen:0.5b  # 394MB

# 调整JVM参数
# 在start.sh中添加：-Xmx1g -Xms512m
```

#### 网络优化
```bash
# 配置Nginx缓存
# 在nginx配置中添加缓存规则

# 使用CDN加速（可选）
```

## 安全建议

### 1. 网络安全
- 配置SSL证书（Let's Encrypt）
- 限制访问IP
- 启用防火墙规则

### 2. 应用安全
- 定期更新依赖
- 监控异常访问
- 备份重要数据

### 3. 系统安全
- 定期更新系统
- 配置SSH密钥认证
- 禁用不必要的服务

## 备份和恢复

### 1. 备份配置
```bash
# 备份应用配置
tar -czf spring-ai-demo-backup-$(date +%Y%m%d).tar.gz \
  /opt/spring-ai-demo \
  /etc/systemd/system/spring-ai-demo.service \
  /etc/nginx/conf.d/spring-ai-demo.conf
```

### 2. 备份模型
```bash
# 备份Ollama模型
cp -r /root/.ollama /backup/ollama-$(date +%Y%m%d)
```

### 3. 恢复步骤
```bash
# 恢复应用
tar -xzf spring-ai-demo-backup-YYYYMMDD.tar.gz -C /

# 恢复模型
cp -r /backup/ollama-YYYYMMDD /root/.ollama

# 重启服务
systemctl restart spring-ai-demo
```

## 更新指南

### 1. 应用更新
```bash
# 停止服务
systemctl stop spring-ai-demo

# 备份当前版本
cp /opt/spring-ai-demo/spring-ai-demo-1.0-SNAPSHOT.jar /backup/

# 上传新版本
scp target/spring-ai-demo-1.0-SNAPSHOT.jar root@server:/opt/spring-ai-demo/

# 启动服务
systemctl start spring-ai-demo
```

### 2. 模型更新
```bash
# 下载新模型
ollama pull new-model:version

# 更新配置文件
sed -i 's/old-model/new-model/g' /opt/spring-ai-demo/application.yml

# 重启应用
systemctl restart spring-ai-demo
```

---

**注意**: 本指南基于CentOS 7环境，其他Linux发行版可能需要调整相应的命令和配置。 