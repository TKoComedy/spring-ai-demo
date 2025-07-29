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
        String message = request.get("message");
        String aiReply = aiService.simpleChat(message);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        result.put("response", aiReply);
        return result;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "AI服务正常运行");
        return result;
    }
} 