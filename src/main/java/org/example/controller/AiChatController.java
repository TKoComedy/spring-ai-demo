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

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return Flux.just("data: {\"type\":\"error\",\"message\":\"消息不能为空\"}\n\n");
        }
        
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

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "AI服务正常运行");
        return result;
    }
} 