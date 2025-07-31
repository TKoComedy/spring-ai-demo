package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class ImageGenerationService {
    
    @Value("${image.generation.provider:pixabay}")
    private String provider;
    
    @Value("${stable.diffusion.api.url:https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image}")
    private String stableDiffusionUrl;
    
    @Value("${stable.diffusion.api.key:}")
    private String stableDiffusionKey;
    
    @Value("${openai.api.key:}")
    private String openaiKey;
    
    @Value("${unsplash.api.key:}")
    private String unsplashKey;
    
    @Value("${pixabay.api.key:}")
    private String pixabayKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final WebClient webClient;
    
    public ImageGenerationService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
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
                case "unsplash":
                    return generateWithUnsplash(prompt);
                case "pixabay":
                    return generateWithPixabay(prompt);
                case "ollama":
                    return generateWithOllama(prompt);
                default:
                    return generateWithPixabay(prompt); // 默认使用Pixabay
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
     * 使用Pixabay API搜索图像
     */
    private String generateWithPixabay(String prompt) {
        // 使用配置的Pixabay API密钥
        String apiKey = pixabayKey.isEmpty() ? "51553097-1aae0b00d0d99daaae20d1909" : pixabayKey;
        
        // 将中文关键词转换为英文，提高搜索成功率
        String searchTerm = translateToEnglish(prompt);
        
        // 构建API URL
        String url;
        try {
            url = "https://pixabay.com/api/?key=" + apiKey + 
                  "&q=" + java.net.URLEncoder.encode(searchTerm, "UTF-8");
        } catch (Exception e) {
            return "URL编码失败: " + e.getMessage();
        }
        
        try {
            
            // 使用HttpURLConnection (Java 8兼容)
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Spring-AI-Demo/1.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode != 200) {
                return "Pixabay搜索失败: HTTP " + responseCode + "\n\n" +
                       "调试信息:\n" +
                       "- 搜索关键词: " + searchTerm + "\n" +
                       "- 原文: " + prompt + "\n" +
                       "- API URL: " + url + "\n\n" +
                       "提示: 可以尝试配置Pixabay API密钥以获得更好的搜索结果。";
            }
            
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            connection.disconnect();
            
            if (responseBody.length() > 0) {
                JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
                JsonNode hits = jsonNode.get("hits");
                
                if (hits != null && hits.isArray() && hits.size() > 0) {
                    JsonNode firstImage = hits.get(0);
                    String imageUrl = firstImage.get("webformatURL").asText();
                    String largeImageUrl = firstImage.get("largeImageURL").asText();
                    String user = firstImage.get("user").asText();
                    String pixabayUrl = firstImage.get("pageURL").asText();
                    
                    return String.format("找到相关图像:\n\n" +
                            "搜索关键词: %s (原文: %s)\n" +
                            "图像URL: %s\n" +
                            "高清图像URL: %s\n" +
                            "摄影师: %s\n" +
                            "Pixabay链接: %s\n\n" +
                            "注意: 这是从Pixabay搜索到的相关图像，不是AI生成的。",
                            searchTerm, prompt, imageUrl, largeImageUrl, user, pixabayUrl);
                }
            }
            
            return "未找到相关图像，请尝试其他关键词。";
        } catch (Exception e) {
            return "Pixabay搜索失败: " + e.getMessage() + "\n\n" +
                   "调试信息:\n" +
                   "- 搜索关键词: " + searchTerm + "\n" +
                   "- 原文: " + prompt + "\n" +
                   "- API URL: " + url + "\n\n" +
                   "提示: 可以尝试配置Pixabay API密钥以获得更好的搜索结果。";
        }
    }
    
    /**
     * 使用Unsplash API搜索图像
     */
    private String generateWithUnsplash(String prompt) {
        try {
            // 如果没有配置API密钥，使用免费访问
            String url = "https://api.unsplash.com/search/photos?query=" + 
                        java.net.URLEncoder.encode(prompt, "UTF-8") + 
                        "&per_page=1&orientation=landscape";
            
            String response = webClient.get()
                    .uri(url)
                    .header("Authorization", "Client-ID " + (unsplashKey.isEmpty() ? "demo" : unsplashKey))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode results = jsonNode.get("results");
                
                if (results != null && results.isArray() && results.size() > 0) {
                    JsonNode firstImage = results.get(0);
                    String imageUrl = firstImage.get("urls").get("regular").asText();
                    String photographer = firstImage.get("user").get("name").asText();
                    String unsplashUrl = firstImage.get("links").get("html").asText();
                    
                    return String.format("找到相关图像:\n\n" +
                            "图像URL: %s\n" +
                            "摄影师: %s\n" +
                            "Unsplash链接: %s\n\n" +
                            "注意: 这是从Unsplash搜索到的相关图像，不是AI生成的。",
                            imageUrl, photographer, unsplashUrl);
                }
            }
            
            return "未找到相关图像，请尝试其他关键词。";
        } catch (Exception e) {
            return "Unsplash搜索失败: " + e.getMessage() + "\n\n提示: 可以尝试配置Unsplash API密钥以获得更好的搜索结果。";
        }
    }
    
    /**
     * 简单的中文到英文关键词转换
     */
    private String translateToEnglish(String prompt) {
        // 简单的关键词映射，可以根据需要扩展
        String lowerPrompt = prompt.toLowerCase();
        
        if (lowerPrompt.contains("猫") || lowerPrompt.contains("猫咪")) {
            return "cat";
        } else if (lowerPrompt.contains("狗") || lowerPrompt.contains("狗狗")) {
            return "dog";
        } else if (lowerPrompt.contains("风景") || lowerPrompt.contains("景色")) {
            return "landscape";
        } else if (lowerPrompt.contains("城市") || lowerPrompt.contains("都市")) {
            return "city";
        } else if (lowerPrompt.contains("花") || lowerPrompt.contains("花朵")) {
            return "flower";
        } else if (lowerPrompt.contains("树") || lowerPrompt.contains("树木")) {
            return "tree";
        } else if (lowerPrompt.contains("山") || lowerPrompt.contains("山峰")) {
            return "mountain";
        } else if (lowerPrompt.contains("海") || lowerPrompt.contains("海洋")) {
            return "ocean";
        } else if (lowerPrompt.contains("天空") || lowerPrompt.contains("云")) {
            return "sky";
        } else if (lowerPrompt.contains("太阳") || lowerPrompt.contains("阳光")) {
            return "sun";
        } else if (lowerPrompt.contains("月亮") || lowerPrompt.contains("夜晚")) {
            return "moon";
        } else if (lowerPrompt.contains("汽车") || lowerPrompt.contains("车")) {
            return "car";
        } else if (lowerPrompt.contains("建筑") || lowerPrompt.contains("房子")) {
            return "building";
        } else if (lowerPrompt.contains("人") || lowerPrompt.contains("人物")) {
            return "person";
        } else if (lowerPrompt.contains("动物")) {
            return "animal";
        } else if (lowerPrompt.contains("自然") || lowerPrompt.contains("大自然")) {
            return "nature";
        } else {
            // 如果没有匹配的中文关键词，直接返回原词（可能是英文）
            return prompt;
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