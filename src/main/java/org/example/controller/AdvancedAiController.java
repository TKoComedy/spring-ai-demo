package org.example.controller;

import org.example.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced-ai")
public class AdvancedAiController {

    @Autowired
    private AiService aiService;

    /**
     * 基础聊天
     */
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = aiService.simpleChat(message);
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        result.put("response", response);
        return result;
    }

    /**
     * 带系统提示的聊天
     */
    @PostMapping("/chat-with-system")
    public Map<String, String> chatWithSystem(@RequestBody Map<String, String> request) {
        String userMessage = request.get("userMessage");
        String systemPrompt = request.get("systemPrompt");
        String response = aiService.chatWithSystemPrompt(userMessage, systemPrompt);
        Map<String, String> result = new HashMap<>();
        result.put("userMessage", userMessage);
        result.put("systemPrompt", systemPrompt);
        result.put("response", response);
        return result;
    }

    /**
     * 模板聊天
     */
    @PostMapping("/chat-template")
    public Map<String, String> chatTemplate(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String style = request.get("style");
        String response = aiService.chatWithTemplate(topic, style);
        Map<String, String> result = new HashMap<>();
        result.put("topic", topic);
        result.put("style", style);
        result.put("response", response);
        return result;
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
     * 代码生成
     */
    @PostMapping("/generate-code")
    public Map<String, String> generateCode(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        String language = request.get("language");
        String code = aiService.generateCode(requirement, language);
        Map<String, String> result = new HashMap<>();
        result.put("requirement", requirement);
        result.put("language", language);
        result.put("code", code);
        return result;
    }

    /**
     * 文档摘要
     */
    @PostMapping("/summarize")
    public Map<String, String> summarize(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String summary = aiService.summarizeDocument(content);
        Map<String, String> result = new HashMap<>();
        result.put("content", content);
        result.put("summary", summary);
        return result;
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
} 