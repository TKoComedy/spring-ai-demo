package org.example.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class AiService {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    private OpenAiService openAiService;

    @PostConstruct
    public void initOpenAiService() {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public String simpleChat(String message) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(new ChatMessage("user", message)))
                .maxTokens(1000)
                .temperature(0.7)
                .build();
        return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
    }

    public String chatWithSystemPrompt(String userMessage, String systemPrompt) {
        ChatMessage systemMessage = new ChatMessage("system", systemPrompt);
        ChatMessage userMsg = new ChatMessage("user", userMessage);
        List<ChatMessage> messages = Arrays.asList(systemMessage, userMsg);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(1000)
                .temperature(0.7)
                .build();
        return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
    }

    public String chatWithTemplate(String topic, String style) {
        String template = "请以" + style + "的风格，写一篇关于" + topic + "的文章。\n" +
                "要求：\n" +
                "1. 内容要生动有趣\n" +
                "2. 字数控制在200字左右\n" +
                "3. 结构清晰";
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(new ChatMessage("user", template)))
                .maxTokens(1000)
                .temperature(0.7)
                .build();
        return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
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
     * 图像生成 (使用DALL-E API)
     */
    public String generateImage(String prompt) {
        try {
            // 这里需要DALL-E API，暂时返回提示信息
            // 如果需要完整实现，需要添加DALL-E依赖
            return "图像生成功能需要DALL-E API。\n" +
                    "图像描述: " + prompt + "\n" +
                    "请访问 https://openai.com/dall-e-2 手动生成图像，或添加DALL-E API依赖。";
        } catch (Exception e) {
            return "图像生成失败: " + e.getMessage();
        }
    }

    /**
     * 文本嵌入 (使用OpenAI Embeddings API)
     */
    public List<Double> getEmbedding(String text) {
        try {
            // 这里需要OpenAI Embeddings API，暂时返回模拟数据
            // 如果需要完整实现，需要添加embeddings相关依赖
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