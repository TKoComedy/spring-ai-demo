# Spring AI Demo 流式输出测试脚本

Write-Host "=== Spring AI Demo 流式输出测试 ===" -ForegroundColor Green

# 测试基础健康检查
Write-Host "`n1. 测试基础健康检查..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/ai/health" -Method GET
    Write-Host "基础API状态: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "响应: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "基础API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试流式健康检查
Write-Host "`n2. 测试流式健康检查..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/streaming/health" -Method GET
    Write-Host "流式API状态: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "响应: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "流式API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试图像生成
Write-Host "`n3. 测试图像生成..." -ForegroundColor Yellow
try {
    $body = @{
        prompt = "一只可爱的小猫在花园里玩耍"
    } | ConvertTo-Json
    
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/advanced-ai/generate-image" -Method POST -ContentType "application/json" -Body $body
    Write-Host "图像生成状态: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "响应: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "图像生成测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试流式聊天（简化版本）
Write-Host "`n4. 测试流式聊天..." -ForegroundColor Yellow
try {
    $body = @{
        message = "你好，请简单介绍一下自己"
    } | ConvertTo-Json
    
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/streaming/chat" -Method POST -ContentType "application/json" -Body $body
    Write-Host "流式聊天状态: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "响应类型: $($response.Headers['Content-Type'])" -ForegroundColor Gray
    Write-Host "响应内容: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "流式聊天测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "访问 http://localhost:8082/streaming-test.html 进行完整的流式输出测试" -ForegroundColor Cyan 