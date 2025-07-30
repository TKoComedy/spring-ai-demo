package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageGenerationService {
    
    @Value("${image.generation.provider:stable-diffusion}")
    private String provider;
    
    @Value("${stable.diffusion.api.url:https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image}")
    private String stableDiffusionUrl;
    
    @Value("${stable.diffusion.api.key:}")
    private String stableDiffusionKey;
    
    @Value("${openai.api.key:}")
    private String openaiKey;
    
    private final WebClient webClient;
    
    public ImageGenerationService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * 生成图像并返回URL
     */
    public String generateImage(String prompt) {
        try {
            switch (provider.toLowerCase()) {
                case "stable-diffusion":
                    return generateWithStableDiffusion(prompt);
                case "dalle":
                    return generateWithDalle(prompt);
                case "ollama":
                    return generateWithOllama(prompt);
                default:
                    return generateWithOllama(prompt); // 默认使用Ollama
            }
        } catch (Exception e) {
            return "图像生成失败: " + e.getMessage();
        }
    }
    
    /**
     * 使用Stable Diffusion生成图像
     */
    private String generateWithStableDiffusion(String prompt) {
        if (stableDiffusionKey.isEmpty()) {
            return "请配置Stable Diffusion API密钥";
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> textPrompt = new HashMap<>();
        textPrompt.put("text", prompt);
        textPrompt.put("weight", 1.0);
        requestBody.put("text_prompts", new Object[]{ textPrompt });
        requestBody.put("cfg_scale", 7);
        requestBody.put("height", 1024);
        requestBody.put("width", 1024);
        requestBody.put("samples", 1);
        requestBody.put("steps", 30);
        
        try {
            String response = webClient.post()
                    .uri(stableDiffusionUrl)
                    .header("Authorization", "Bearer " + stableDiffusionKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // 这里需要解析响应获取图像URL
            // 实际实现中需要根据API响应格式解析
            return "https://example.com/generated-image.jpg"; // 示例URL
        } catch (Exception e) {
            return "Stable Diffusion生成失败: " + e.getMessage();
        }
    }
    
    /**
     * 使用DALL-E生成图像
     */
    private String generateWithDalle(String prompt) {
        if (openaiKey.isEmpty()) {
            return "请配置OpenAI API密钥";
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "dall-e-3");
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        
        try {
            String response = webClient.post()
                    .uri("https://api.openai.com/v1/images/generations")
                    .header("Authorization", "Bearer " + openaiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // 解析响应获取图像URL
            return "https://example.com/dalle-image.jpg"; // 示例URL
        } catch (Exception e) {
            return "DALL-E生成失败: " + e.getMessage();
        }
    }
    
    /**
     * 使用Ollama生成图像描述（作为备选方案）
     */
    private String generateWithOllama(String prompt) {
        // 由于Ollama不支持图像生成，我们返回一个描述
        // 在实际应用中，可以集成其他图像生成服务
        return "图像描述: " + prompt + "\n\n注意: 当前使用Ollama无法生成真实图像，请配置Stable Diffusion或DALL-E API。";
    }
} 