package org.example.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;
    
    @Value("${ollama.model:qwen:0.5b}")
    private String model;
    
    private final WebClient webClient;
    
    public OllamaService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }
    
    public String chat(String message) {
        try {
            GenerateRequest request = new GenerateRequest();
            request.setModel(model);
            request.setPrompt(message);
            request.setStream(false);
            
            return webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GenerateResponse.class)
                    .map(response -> response.getResponse())
                    .block();
        } catch (Exception e) {
            return "抱歉，AI服务暂时不可用: " + e.getMessage();
        }
    }
    
    public String chatWithSystemPrompt(String userMessage, String systemPrompt) {
        try {
            String fullPrompt = systemPrompt + "\n\n用户: " + userMessage + "\n\n助手: ";
            
            GenerateRequest request = new GenerateRequest();
            request.setModel(model);
            request.setPrompt(fullPrompt);
            request.setStream(false);
            
            return webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GenerateResponse.class)
                    .map(response -> response.getResponse())
                    .block();
        } catch (Exception e) {
            return "抱歉，AI服务暂时不可用: " + e.getMessage();
        }
    }
    
    // 内部类用于JSON序列化
    public static class GenerateRequest {
        private String model;
        private String prompt;
        private boolean stream;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public boolean isStream() { return stream; }
        public void setStream(boolean stream) { this.stream = stream; }
    }
    
    public static class GenerateResponse {
        private String response;
        
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }
} 