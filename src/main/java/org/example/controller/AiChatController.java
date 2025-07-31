package org.example.controller;

import org.example.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private OllamaService ollamaService;

    @PostMapping(value = "/chat")
    public Flux<String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"消息不能为空\"}\n\n");
        }
        
        return Flux.create(sink -> {
            try {
                // 直接调用Ollama的流式API，不发送额外的开始标记
                ollamaService.streamChat(message, sink);
            } catch (Exception e) {
                sink.next("data: {\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}\n\n");
                sink.complete();
            }
        });
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "AI服务正常运行");
        return result;
    }
} 