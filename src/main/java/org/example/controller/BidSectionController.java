package org.example.controller;

import org.example.service.BidSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bid-section")
public class BidSectionController {

    @Autowired
    private BidSectionService bidSectionService;

    /**
     * 生成章节内容
     */
    @PostMapping("/generate")
    public Map<String, Object> generateSectionContent(@RequestBody Map<String, Object> request) {
        String sectionTitle = (String) request.get("sectionTitle");
        String sectionRequirements = (String) request.get("sectionRequirements");
        @SuppressWarnings("unchecked")
        Map<String, Object> bidContext = (Map<String, Object>) request.get("bidContext");
        
        if (sectionTitle == null || sectionTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("章节标题不能为空");
        }
        
        if (sectionRequirements == null || sectionRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("章节要求不能为空");
        }
        
        return bidSectionService.generateSectionContent(sectionTitle, sectionRequirements, bidContext);
    }

    /**
     * 生成技术方案章节
     */
    @PostMapping("/generate-technical-solution")
    public Map<String, Object> generateTechnicalSolution(@RequestBody Map<String, String> request) {
        String technicalRequirements = request.get("technicalRequirements");
        String projectInfo = request.get("projectInfo");
        
        if (technicalRequirements == null || technicalRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("技术要求不能为空");
        }
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        return bidSectionService.generateTechnicalSolution(technicalRequirements, projectInfo);
    }

    /**
     * 生成项目管理章节
     */
    @PostMapping("/generate-project-management")
    public Map<String, Object> generateProjectManagement(@RequestBody Map<String, String> request) {
        String projectInfo = request.get("projectInfo");
        String managementRequirements = request.get("managementRequirements");
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        if (managementRequirements == null || managementRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("管理要求不能为空");
        }
        
        return bidSectionService.generateProjectManagement(projectInfo, managementRequirements);
    }

    /**
     * 生成质量保证章节
     */
    @PostMapping("/generate-quality-assurance")
    public Map<String, Object> generateQualityAssurance(@RequestBody Map<String, String> request) {
        String qualityRequirements = request.get("qualityRequirements");
        String projectInfo = request.get("projectInfo");
        
        if (qualityRequirements == null || qualityRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("质量要求不能为空");
        }
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        return bidSectionService.generateQualityAssurance(qualityRequirements, projectInfo);
    }

    /**
     * 生成售后服务章节
     */
    @PostMapping("/generate-after-sales-service")
    public Map<String, Object> generateAfterSalesService(@RequestBody Map<String, String> request) {
        String serviceRequirements = request.get("serviceRequirements");
        String projectInfo = request.get("projectInfo");
        
        if (serviceRequirements == null || serviceRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("服务要求不能为空");
        }
        
        if (projectInfo == null || projectInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        
        return bidSectionService.generateAfterSalesService(serviceRequirements, projectInfo);
    }

    /**
     * 优化章节内容
     */
    @PostMapping("/optimize")
    public Map<String, Object> optimizeSectionContent(@RequestBody Map<String, String> request) {
        String currentContent = request.get("currentContent");
        String optimizationRequirements = request.get("optimizationRequirements");
        
        if (currentContent == null || currentContent.trim().isEmpty()) {
            throw new IllegalArgumentException("当前章节内容不能为空");
        }
        
        if (optimizationRequirements == null || optimizationRequirements.trim().isEmpty()) {
            throw new IllegalArgumentException("优化要求不能为空");
        }
        
        return bidSectionService.optimizeSectionContent(currentContent, optimizationRequirements);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "标书章节生成服务正常运行");
        result.put("features", "章节生成,技术方案,项目管理,质量保证,售后服务,内容优化");
        return result;
    }
}