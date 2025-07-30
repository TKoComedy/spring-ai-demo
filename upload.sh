#!/bin/bash

# 文件上传脚本
# 将JAR包上传到服务器

SERVER_IP="152.136.229.42"
SERVER_USER="root"
JAR_FILE="target/spring-ai-demo-1.0-SNAPSHOT.jar"
REMOTE_DIR="/opt/spring-ai-demo"

echo "📤 开始上传文件到服务器..."

# 检查JAR文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ 错误：找不到JAR文件 $JAR_FILE"
    echo "请先运行 mvn clean package 生成JAR文件"
    exit 1
fi

echo "📁 上传文件: $JAR_FILE"
echo "📍 目标服务器: $SERVER_USER@$SERVER_IP:$REMOTE_DIR"

# 上传文件
scp "$JAR_FILE" "$SERVER_USER@$SERVER_IP:$REMOTE_DIR/"

if [ $? -eq 0 ]; then
    echo "✅ 文件上传成功！"
    echo ""
    echo "🚀 接下来请在服务器上执行："
    echo "ssh $SERVER_USER@$SERVER_IP"
    echo "systemctl start spring-ai-demo"
    echo "systemctl enable spring-ai-demo"
    echo "systemctl restart nginx"
    echo ""
    echo "🧪 测试命令："
    echo "curl http://localhost:8080/api/ai/health"
else
    echo "❌ 文件上传失败！"
    echo "请检查："
    echo "1. 服务器是否可访问"
    echo "2. SSH密钥是否正确配置"
    echo "3. 服务器目录是否存在"
fi 