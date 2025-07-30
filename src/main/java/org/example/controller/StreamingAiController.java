package org.example.controller;

import org.example.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/streaming")
public class StreamingAiController {

    @Autowired
    private OllamaService ollamaService;

    /**
     * 流式聊天 - 使用Server-Sent Events
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        return Flux.create(sink -> {
            try {
                // 发送开始标记
                sink.next("data: {\"type\":\"start\",\"message\":\"" + message + "\"}\n\n");
                
                // 调用Ollama的流式API
                ollamaService.streamChat(message, sink);
                
                // 发送结束标记
                sink.next("data: {\"type\":\"end\"}\n\n");
                sink.complete();
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 流式聊天 - 使用WebFlux响应式流
     */
    @PostMapping(value = "/chat-reactive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatReactive(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        return Flux.just(message)
                .flatMap(msg -> ollamaService.streamChatReactive(msg))
                .map(chunk -> "data: " + chunk + "\n\n")
                .onErrorResume(e -> Flux.just("data: {\"error\":\"" + e.getMessage() + "\"}\n\n"));
    }

    /**
     * 流式代码生成
     */
    @PostMapping(value = "/generate-code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCodeGeneration(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        String language = request.get("language");
        
        String systemPrompt = "你是一个专业的程序员，请根据用户的需求生成高质量的代码。\n" +
                "要求：\n" +
                "1. 代码要规范，有适当的注释\n" +
                "2. 考虑边界情况和错误处理\n" +
                "3. 遵循最佳实践";
        
        String userPrompt = String.format("请用%s语言实现以下需求：%s", language, requirement);
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"requirement\":\"" + requirement + "\",\"language\":\"" + language + "\"}\n\n");
                ollamaService.streamChatWithSystemPrompt(userPrompt, systemPrompt, sink);
                sink.next("data: {\"type\":\"end\"}\n\n");
                sink.complete();
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 流式文档摘要
     */
    @PostMapping(value = "/summarize", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamSummarize(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        
        String systemPrompt = "你是一个专业的文档摘要专家。\n" +
                "请对给定的文档内容进行摘要，要求：\n" +
                "1. 提取关键信息\n" +
                "2. 保持逻辑清晰\n" +
                "3. 字数控制在原文的1/3以内";
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"content_length\":\"" + content.length() + "\"}\n\n");
                ollamaService.streamChatWithSystemPrompt(content, systemPrompt, sink);
                sink.next("data: {\"type\":\"end\"}\n\n");
                sink.complete();
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new java.util.HashMap<>();
        result.put("status", "流式AI服务正常运行");
        result.put("features", "流式聊天,流式代码生成,流式文档摘要");
        return result;
    }
} 