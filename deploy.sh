#!/bin/bash

# Spring AI Demo 自动化部署脚本
# 服务器IP: 152.136.229.42
# 域名: tkocomedy.site

set -e  # 遇到错误立即退出

echo "🚀 开始部署 Spring AI Demo 到服务器..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    log_error "请使用root用户运行此脚本"
    exit 1
fi

# 检测操作系统
detect_os() {
    if [ -f /etc/redhat-release ]; then
        echo "centos"
    elif [ -f /etc/debian_version ]; then
        echo "debian"
    else
        echo "unknown"
    fi
}

OS_TYPE=$(detect_os)
log_info "检测到操作系统: $OS_TYPE"

if [ "$OS_TYPE" = "centos" ]; then
    log_info "使用CentOS/RHEL包管理器..."
    
    log_info "第一步：更新系统..."
    yum update -y
    
    log_info "第二步：安装必要软件..."
    yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel nginx docker git unzip curl
    
    # 启动Docker
    systemctl start docker
    systemctl enable docker
    
elif [ "$OS_TYPE" = "debian" ]; then
    log_info "使用Debian/Ubuntu包管理器..."
    
    log_info "第一步：更新系统..."
    apt update && apt upgrade -y
    
    log_info "第二步：安装必要软件..."
    apt install -y openjdk-8-jdk nginx docker.io git unzip curl
    
    # 启动Docker
    systemctl start docker
    systemctl enable docker
    
else
    log_error "不支持的操作系统"
    exit 1
fi

log_info "第三步：安装Ollama..."
curl -fsSL https://ollama.com/install.sh | sh

# 启动Ollama服务
systemctl start ollama
systemctl enable ollama

log_info "第四步：下载AI模型..."
ollama pull deepseek-r1:1.5b

log_info "第五步：创建应用目录..."
mkdir -p /opt/spring-ai-demo
cd /opt/spring-ai-demo

log_info "第六步：创建应用配置文件..."
cat > application.yml << 'EOF'
server:
  port: 8080

spring:
  application:
    name: spring-ai-demo

# Ollama 配置
ollama:
  base-url: http://localhost:11434
  model: deepseek-r1:1.5b

# 日志配置
logging:
  level:
    org.example: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
EOF

log_info "第七步：创建启动脚本..."
cat > start.sh << 'EOF'
#!/bin/bash
cd /opt/spring-ai-demo
java -jar spring-ai-demo-1.0-SNAPSHOT.jar --spring.config.location=file:./application.yml
EOF

chmod +x start.sh

log_info "第八步：创建systemd服务..."
cat > /etc/systemd/system/spring-ai-demo.service << 'EOF'
[Unit]
Description=Spring AI Demo Application
After=network.target ollama.service

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

log_info "第九步：配置Nginx..."
cat > /etc/nginx/conf.d/spring-ai-demo.conf << 'EOF'
server {
    listen 80;
    server_name tkocomedy.site www.tkocomedy.site;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
EOF

# 启动Nginx
systemctl start nginx
systemctl enable nginx

# 测试Nginx配置
nginx -t

log_info "第十步：配置防火墙..."
if [ "$OS_TYPE" = "centos" ]; then
    # CentOS使用firewalld
    yum install -y firewalld
    systemctl start firewalld
    systemctl enable firewalld
    
    firewall-cmd --permanent --add-service=ssh
    firewall-cmd --permanent --add-service=http
    firewall-cmd --permanent --add-service=https
    firewall-cmd --permanent --add-port=8080/tcp
    firewall-cmd --reload
    
elif [ "$OS_TYPE" = "debian" ]; then
    # Debian使用ufw
    apt install -y ufw
    ufw default deny incoming
    ufw default allow outgoing
    ufw allow ssh
    ufw allow 80
    ufw allow 443
    ufw allow 8080
    ufw --force enable
fi

log_info "第十一步：创建监控脚本..."
cat > /opt/monitor.sh << 'EOF'
#!/bin/bash
echo "=== 系统状态检查 ==="
echo "时间: $(date)"
echo "内存使用: $(free -h | grep Mem)"
echo "磁盘使用: $(df -h /)"
echo ""

echo "=== 服务状态 ==="
echo "Spring Boot: $(systemctl is-active spring-ai-demo)"
echo "Ollama: $(systemctl is-active ollama)"
echo "Nginx: $(systemctl is-active nginx)"
echo ""

echo "=== 端口检查 ==="
echo "8080端口: $(netstat -tlnp | grep :8080 || echo '未监听')"
echo "11434端口: $(netstat -tlnp | grep :11434 || echo '未监听')"
echo "80端口: $(netstat -tlnp | grep :80 || echo '未监听')"
EOF

chmod +x /opt/monitor.sh

log_warn "⚠️  重要提醒："
echo "1. 请将 target/spring-ai-demo-1.0-SNAPSHOT.jar 上传到 /opt/spring-ai-demo/ 目录"
echo "2. 在腾讯云控制台将域名 tkocomedy.site 的A记录指向 152.136.229.42"
echo "3. 然后运行以下命令启动服务："

echo ""
echo "启动命令："
echo "systemctl start spring-ai-demo"
echo "systemctl enable spring-ai-demo"
echo "systemctl restart nginx"
echo ""

echo "测试命令："
echo "curl http://localhost:8080/api/ai/health"
echo "curl -X POST http://localhost:8080/api/ai/chat -H 'Content-Type: application/json' -d '{\"message\":\"你好\"}'"
echo ""

echo "监控命令："
echo "/opt/monitor.sh"
echo ""

log_info "✅ 部署脚本执行完成！"
log_info "📝 详细部署说明请查看 DEPLOYMENT_GUIDE.md" 