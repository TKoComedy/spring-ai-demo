package org.example.controller;

import org.example.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "消息不能为空");
                return error;
            }
            
            String aiReply = aiService.simpleChat(message);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", message);
            result.put("response", aiReply);
            return result;
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "处理请求时发生错误: " + e.getMessage());
            return error;
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "AI服务正常运行");
        return result;
    }
} 