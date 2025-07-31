package org.example.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

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
    
    /**
     * 流式聊天
     */
    public void streamChat(String message, FluxSink<String> sink) {
        try {
            GenerateRequest request = new GenerateRequest();
            request.setModel(model);
            request.setPrompt(message);
            request.setStream(true);
            
            webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(GenerateStreamResponse.class)
                    .doOnNext(response -> {
                        // 调试信息
                        System.out.println("Received response: " + response.getResponse() + ", done: " + response.isDone());
                    })
                    .subscribe(
                        response -> {
                            if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                                String jsonChunk = "{\"type\":\"chunk\",\"content\":\"" + 
                                    response.getResponse().replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                                sink.next("data: " + jsonChunk + "\n\n");
                            }
                        },
                        error -> {
                            System.err.println("Stream error: " + error.getMessage());
                            String errorJson = "{\"type\":\"error\",\"message\":\"" + error.getMessage() + "\"}";
                            sink.next("data: " + errorJson + "\n\n");
                            sink.complete();
                        },
                        () -> {
                            System.out.println("Stream completed");
                            // 发送结束标记
                            sink.next("data: {\"type\":\"end\"}\n\n");
                            sink.complete();
                        }
                    );
        } catch (Exception e) {
            System.err.println("Stream exception: " + e.getMessage());
            String errorJson = "{\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
            sink.next("data: " + errorJson + "\n\n");
            sink.complete();
        }
    }
    
    /**
     * 流式聊天 - 响应式版本
     */
    public Flux<String> streamChatReactive(String message) {
        try {
            GenerateRequest request = new GenerateRequest();
            request.setModel(model);
            request.setPrompt(message);
            request.setStream(true);
            
            return webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(GenerateStreamResponse.class)
                    .map(response -> response.getResponse() != null ? response.getResponse() : "")
                    .filter(chunk -> !chunk.isEmpty())
                    .onErrorResume(e -> Flux.just("错误: " + e.getMessage()));
        } catch (Exception e) {
            return Flux.just("错误: " + e.getMessage());
        }
    }
    
    /**
     * 流式聊天（带系统提示）
     */
    public void streamChatWithSystemPrompt(String userMessage, String systemPrompt, FluxSink<String> sink) {
        try {
            String fullPrompt = systemPrompt + "\n\n用户: " + userMessage + "\n\n助手: ";
            
            GenerateRequest request = new GenerateRequest();
            request.setModel(model);
            request.setPrompt(fullPrompt);
            request.setStream(true);
            
            webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(GenerateStreamResponse.class)
                    .subscribe(
                        response -> {
                            if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                                String jsonChunk = "{\"type\":\"chunk\",\"content\":\"" + 
                                    response.getResponse().replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                                sink.next("data: " + jsonChunk + "\n\n");
                            }
                        },
                        error -> {
                            String errorJson = "{\"type\":\"error\",\"message\":\"" + error.getMessage() + "\"}";
                            sink.next("data: " + errorJson + "\n\n");
                            sink.complete();
                        },
                        () -> {
                            // 发送结束标记
                            sink.next("data: {\"type\":\"end\"}\n\n");
                            sink.complete();
                        }
                    );
        } catch (Exception e) {
            String errorJson = "{\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
            sink.next("data: " + errorJson + "\n\n");
            sink.complete();
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
    
    public static class GenerateStreamResponse {
        private String response;
        private boolean done;
        private String model;
        private String created_at;
        private String done_reason;
        
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        
        public boolean isDone() { return done; }
        public void setDone(boolean done) { this.done = done; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }
        
        public String getDone_reason() { return done_reason; }
        public void setDone_reason(String done_reason) { this.done_reason = done_reason; }
    }
} 