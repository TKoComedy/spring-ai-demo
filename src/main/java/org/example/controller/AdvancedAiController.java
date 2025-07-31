package org.example.controller;

import org.example.service.OllamaService;
import org.example.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced-ai")
public class AdvancedAiController {

    @Autowired
    private OllamaService ollamaService;
    
    @Autowired
    private AiService aiService;

    /**
     * 基础聊天 - 流式输出
     */
    @PostMapping(value = "/chat")
    public Flux<String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"消息不能为空\"}\n\n");
        }
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"message\":\"" + escapeJsonString(message) + "\"}\n\n");
                ollamaService.streamChat(message, sink);
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 带系统提示的聊天 - 流式输出
     */
    @PostMapping(value = "/chat-with-system")
    public Flux<String> chatWithSystem(@RequestBody Map<String, String> request) {
        String userMessage = request.get("userMessage");
        String systemPrompt = request.get("systemPrompt");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"用户消息不能为空\"}\n\n");
        }
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"userMessage\":\"" + escapeJsonString(userMessage) + "\",\"systemPrompt\":\"" + escapeJsonString(systemPrompt) + "\"}\n\n");
                ollamaService.streamChatWithSystemPrompt(userMessage, systemPrompt, sink);
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 模板聊天 - 流式输出
     */
    @PostMapping(value = "/chat-template")
    public Flux<String> chatTemplate(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String style = request.get("style");
        
        if (topic == null || topic.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"主题不能为空\"}\n\n");
        }
        
        String template = "请以" + style + "的风格，写一篇关于" + topic + "的文章。\n" +
                "要求：\n" +
                "1. 内容要生动有趣\n" +
                "2. 字数控制在200字左右\n" +
                "3. 结构清晰";
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"topic\":\"" + escapeJsonString(topic) + "\",\"style\":\"" + escapeJsonString(style) + "\"}\n\n");
                ollamaService.streamChat(template, sink);
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 文本嵌入
     */
    @PostMapping("/embed")
    public Map<String, Object> embed(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        List<Double> embedding = aiService.getEmbedding(text);
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("embedding", embedding);
        return result;
    }

    /**
     * 批量文本嵌入
     */
    @PostMapping("/embed-batch")
    public Map<String, Object> embedBatch(@RequestBody Map<String, List<String>> request) {
        List<String> texts = request.get("texts");
        List<List<Double>> embeddings = aiService.getBatchEmbeddings(texts);
        Map<String, Object> result = new HashMap<>();
        result.put("texts", texts);
        result.put("embeddings", embeddings);
        return result;
    }

    /**
     * 图像生成
     */
    @PostMapping("/generate-image")
    public Map<String, String> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String imageUrl = aiService.generateImage(prompt);
        Map<String, String> result = new HashMap<>();
        result.put("prompt", prompt);
        result.put("imageUrl", imageUrl);
        return result;
    }

    /**
     * 代码生成 - 流式输出
     */
    @PostMapping(value = "/generate-code")
    public Flux<String> generateCode(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        String language = request.get("language");
        
        if (requirement == null || requirement.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"需求描述不能为空\"}\n\n");
        }
        
        String systemPrompt = "你是一个专业的程序员，请根据用户的需求生成高质量的代码。\n" +
                "要求：\n" +
                "1. 代码要规范，有适当的注释\n" +
                "2. 考虑边界情况和错误处理\n" +
                "3. 遵循最佳实践";
        
        String userPrompt = String.format("请用%s语言实现以下需求：%s", language, requirement);
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"requirement\":\"" + escapeJsonString(requirement) + "\",\"language\":\"" + escapeJsonString(language) + "\"}\n\n");
                ollamaService.streamChatWithSystemPrompt(userPrompt, systemPrompt, sink);
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    /**
     * 文档摘要 - 流式输出
     */
    @PostMapping(value = "/summarize")
    public Flux<String> summarize(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        
        if (content == null || content.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"文档内容不能为空\"}\n\n");
        }
        
        String systemPrompt = "你是一个专业的文档摘要专家。\n" +
                "请对给定的文档内容进行摘要，要求：\n" +
                "1. 提取关键信息\n" +
                "2. 保持逻辑清晰\n" +
                "3. 字数控制在原文的1/3以内";
        
        return Flux.create(sink -> {
            try {
                sink.next("data: {\"type\":\"start\",\"content_length\":\"" + content.length() + "\"}\n\n");
                ollamaService.streamChatWithSystemPrompt(content, systemPrompt, sink);
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
        Map<String, String> result = new HashMap<>();
        result.put("status", "高级AI服务正常运行");
        result.put("features", "聊天,嵌入,图像生成,代码生成,文档摘要");
        return result;
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
} 