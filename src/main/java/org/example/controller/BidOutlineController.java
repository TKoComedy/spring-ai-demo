package org.example.controller;

import org.example.service.BidOutlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bid-outline")
public class BidOutlineController {

    @Autowired
    private BidOutlineService bidOutlineService;

    /**
     * 生成标书目录
     */
    @PostMapping("/generate")
    public Map<String, Object> generateBidOutline(@RequestBody Map<String, Object> request) {
        String bidDocument = (String) request.get("bidDocument");
        @SuppressWarnings("unchecked")
        Map<String, Object> analysisResult = (Map<String, Object>) request.get("analysisResult");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("招标文件内容不能为空");
        }
        
        return bidOutlineService.generateBidOutline(bidDocument, analysisResult);
    }

    /**
     * 生成技术标目录
     */
    @PostMapping("/generate-technical")
    public Map<String, Object> generateTechnicalOutline(@RequestBody Map<String, String> request) {
        String technicalRequirements = request.get("technicalRequirements");
        
        if (technicalRequirements == null || technicalRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("技术要求不能为空");
        }
        
        return bidOutlineService.generateTechnicalOutline(technicalRequirements);
    }

    /**
     * 生成商务标目录
     */
    @PostMapping("/generate-commercial")
    public Map<String, Object> generateCommercialOutline(@RequestBody Map<String, String> request) {
        String commercialRequirements = request.get("commercialRequirements");
        
        if (commercialRequirements == null || commercialRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("商务要求不能为空");
        }
        
        return bidOutlineService.generateCommercialOutline(commercialRequirements);
    }

    /**
     * 自定义目录结构调整
     */
    @PostMapping("/customize")
    public Map<String, Object> customizeOutline(@RequestBody Map<String, String> request) {
        String currentOutline = request.get("currentOutline");
        String modifications = request.get("modifications");
        
        if (currentOutline == null || currentOutline.trim().isEmpty()) {
            throw new IllegalArgumentException("当前目录不能为空");
        }
        
        if (modifications == null || modifications.trim().isEmpty()) {
            throw new IllegalArgumentException("修改要求不能为空");
        }
        
        return bidOutlineService.customizeOutline(currentOutline, modifications);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "标书目录生成服务正常运行");
        result.put("features", "目录生成,技术标目录,商务标目录,目录定制");
        return result;
    }
}