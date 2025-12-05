package org.example.controller;

import org.example.service.BidDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bid-document")
public class BidDocumentController {

    @Autowired
    private BidDocumentService bidDocumentService;

    /**
     * 生成主标书
     */
    @PostMapping("/generate-main")
    public Map<String, String> generateMainBidDocument(@RequestBody Map<String, String> request) {
        String projectInfo = request.get("projectInfo");
        String requirements = request.get("requirements");
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new IllegalArgumentException("标书要求不能为空");
        }
        
        String bidDocument = bidDocumentService.generateMainBidDocument(projectInfo, requirements);
        
        Map<String, String> result = new HashMap<>();
        result.put("projectInfo", projectInfo);
        result.put("requirements", requirements);
        result.put("bidDocument", bidDocument);
        return result;
    }

    /**
     * 生成陪标书
     */
    @PostMapping("/generate-secondary")
    public Map<String, String> generateSecondaryBidDocument(@RequestBody Map<String, String> request) {
        String projectInfo = request.get("projectInfo");
        String requirements = request.get("requirements");
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new IllegalArgumentException("标书要求不能为空");
        }
        
        String bidDocument = bidDocumentService.generateSecondaryBidDocument(projectInfo, requirements);
        
        Map<String, String> result = new HashMap<>();
        result.put("projectInfo", projectInfo);
        result.put("requirements", requirements);
        result.put("bidDocument", bidDocument);
        return result;
    }

    /**
     * 生成多份陪标书
     */
    @PostMapping("/generate-multiple-secondary")
    public Map<String, Object> generateMultipleSecondaryBidDocuments(@RequestBody Map<String, Object> request) {
        String projectInfo = (String) request.get("projectInfo");
        String requirements = (String) request.get("requirements");
        Integer count = (Integer) request.get("count");
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new IllegalArgumentException("标书要求不能为空");
        }
        
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        
        List<String> bidDocuments = bidDocumentService.generateMultipleSecondaryBidDocuments(projectInfo, requirements, count);
        
        Map<String, Object> result = new HashMap<>();
        result.put("projectInfo", projectInfo);
        result.put("requirements", requirements);
        result.put("count", count);
        result.put("bidDocuments", bidDocuments);
        return result;
    }

    /**
     * 评估标书质量
     */
    @PostMapping("/evaluate")
    public Map<String, String> evaluateBidDocument(@RequestBody Map<String, String> request) {
        String bidDocument = request.get("bidDocument");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("标书内容不能为空");
        }
        
        String evaluation = bidDocumentService.evaluateBidDocument(bidDocument);
        
        Map<String, String> result = new HashMap<>();
        result.put("bidDocument", bidDocument);
        result.put("evaluation", evaluation);
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "标书服务正常运行");
        result.put("features", "主标书生成,陪标书生成,批量生成,质量评估");
        return result;
    }
}