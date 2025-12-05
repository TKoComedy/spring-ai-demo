package org.example.controller;

import org.example.service.BidAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bid-analysis")
public class BidAnalysisController {

    @Autowired
    private BidAnalysisService bidAnalysisService;

    /**
     * 智能解析招标文件
     */
    @PostMapping("/analyze")
    public Map<String, Object> analyzeBidDocument(@RequestBody Map<String, String> request) {
        String bidDocument = request.get("bidDocument");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("招标文件内容不能为空");
        }
        
        return bidAnalysisService.analyzeBidDocument(bidDocument);
    }

    /**
     * 识别招标文件风险点
     */
    @PostMapping("/identify-risks")
    public Map<String, Object> identifyRiskPoints(@RequestBody Map<String, String> request) {
        String bidDocument = request.get("bidDocument");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("招标文件内容不能为空");
        }
        
        List<String> risks = bidAnalysisService.identifyRiskPoints(bidDocument);
        
        Map<String, Object> result = new HashMap<>();
        result.put("bidDocument", bidDocument);
        result.put("risks", risks);
        return result;
    }

    /**
     * 提取评分标准
     */
    @PostMapping("/extract-scoring-criteria")
    public Map<String, Object> extractScoringCriteria(@RequestBody Map<String, String> request) {
        String bidDocument = request.get("bidDocument");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("招标文件内容不能为空");
        }
        
        return bidAnalysisService.extractScoringCriteria(bidDocument);
    }

    /**
     * 文件上传解析（预留接口）
     */
    @PostMapping("/upload-and-analyze")
    public Map<String, Object> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        try {
            String content = new String(file.getBytes(), "UTF-8");
            return bidAnalysisService.analyzeBidDocument(content);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "招标文件分析服务正常运行");
        result.put("features", "智能解析,风险识别,评分标准提取");
        return result;
    }
}