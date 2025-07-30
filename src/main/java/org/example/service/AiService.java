package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class AiService {
    
    @Value("${ollama.model:deepseek-r1}")
    private String model;
    
    @Autowired
    private OllamaService ollamaService;
    
    @Autowired
    private ImageGenerationService imageGenerationService;

    public String simpleChat(String message) {
        return ollamaService.chat(message);
    }

    public String chatWithSystemPrompt(String userMessage, String systemPrompt) {
        return ollamaService.chatWithSystemPrompt(userMessage, systemPrompt);
    }

    public String chatWithTemplate(String topic, String style) {
        String template = "请以" + style + "的风格，写一篇关于" + topic + "的文章。\n" +
                "要求：\n" +
                "1. 内容要生动有趣\n" +
                "2. 字数控制在200字左右\n" +
                "3. 结构清晰";
        return ollamaService.chat(template);
    }

    /**
     * 代码生成助手
     */
    public String generateCode(String requirement, String language) {
        String systemPrompt = "你是一个专业的程序员，请根据用户的需求生成高质量的代码。\n" +
                "要求：\n" +
                "1. 代码要规范，有适当的注释\n" +
                "2. 考虑边界情况和错误处理\n" +
                "3. 遵循最佳实践";
        
        String userPrompt = String.format("请用%s语言实现以下需求：%s", language, requirement);
        
        return chatWithSystemPrompt(userPrompt, systemPrompt);
    }

    /**
     * 文档摘要
     */
    public String summarizeDocument(String content) {
        String systemPrompt = "你是一个专业的文档摘要专家。\n" +
                "请对给定的文档内容进行摘要，要求：\n" +
                "1. 提取关键信息\n" +
                "2. 保持逻辑清晰\n" +
                "3. 字数控制在原文的1/3以内";
        
        return chatWithSystemPrompt(content, systemPrompt);
    }

    /**
     * 图像生成 (支持多种图像生成服务)
     */
    public String generateImage(String prompt) {
        try {
            return imageGenerationService.generateImage(prompt);
        } catch (Exception e) {
            return "图像生成失败: " + e.getMessage();
        }
    }

    /**
     * 文本嵌入 (简化版本，返回模拟数据)
     */
    public List<Double> getEmbedding(String text) {
        try {
            // 这里可以调用Ollama的embeddings API，暂时返回模拟数据
            return Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5);
        } catch (Exception e) {
            return Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0);
        }
    }

    /**
     * 批量文本嵌入
     */
    public List<List<Double>> getBatchEmbeddings(List<String> texts) {
        try {
            // 批量处理文本嵌入，暂时返回模拟数据
            List<List<Double>> results = Arrays.asList();
            for (String text : texts) {
                results.add(getEmbedding(text));
            }
            return results;
        } catch (Exception e) {
            return Arrays.asList(
                Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5),
                Arrays.asList(0.2, 0.3, 0.4, 0.5, 0.6)
            );
        }
    }
} 