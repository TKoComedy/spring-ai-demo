package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Spring AI 学习演示");
        return "index";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute("title", "AI 聊天");
        return "chat";
    }

    @GetMapping("/advanced")
    public String advanced(Model model) {
        model.addAttribute("title", "高级 AI 功能");
        return "advanced";
    }

    @GetMapping("/demo")
    public String demo(Model model) {
        model.addAttribute("title", "AI 功能演示");
        return "demo";
    }
} 