package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BidSectionService {

    @Autowired
    private OllamaService ollamaService;

    /**
     * 根据章节标题和要求生成章节内容
     *
     * @param sectionTitle 章节标题
     * @param sectionRequirements 章节要求
     * @param bidContext 标书上下文信息
     * @return 章节内容
     */
    public Map<String, Object> generateSectionContent(String sectionTitle, String sectionRequirements, Map<String, Object> bidContext) {
        String systemPrompt = "你是一位专业的标书撰写专家，具有丰富的投标经验。你的任务是根据章节标题和要求，编写高质量的标书章节内容。\n\n" +
                "撰写要求：\n" +
                "1. 紧密围绕章节标题和要求\n" +
                "2. 内容详实，逻辑清晰\n" +
                "3. 突出本公司优势\n" +
                "4. 符合标书编写规范\n" +
                "5. 语言专业，表达准确\n\n" +
                "请输出符合要求的章节内容。";

        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append(String.format("章节标题：%s\n\n", sectionTitle));
        userPromptBuilder.append(String.format("章节要求：%s\n\n", sectionRequirements));
        
        if (bidContext != null && !bidContext.isEmpty()) {
            userPromptBuilder.append("标书上下文信息：\n");
            for (Map.Entry<String, Object> entry : bidContext.entrySet()) {
                userPromptBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        String userPrompt = userPromptBuilder.toString();

        String content = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sectionTitle", sectionTitle);
        result.put("sectionContent", content);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成技术方案章节
     *
     * @param technicalRequirements 技术要求
     * @param projectInfo 项目信息
     * @return 技术方案内容
     */
    public Map<String, Object> generateTechnicalSolution(String technicalRequirements, String projectInfo) {
        String systemPrompt = "你是一位专业的技术方案设计师，具有丰富的项目实施经验。你的任务是根据技术要求和项目信息，设计一份详细的技术方案。\n\n" +
                "技术方案应包括以下内容：\n" +
                "1. 技术路线和实施方案\n" +
                "2. 关键技术难点及解决方案\n" +
                "3. 项目实施计划和进度安排\n" +
                "4. 质量保证措施\n" +
                "5. 安全保障措施\n" +
                "6. 风险控制措施\n\n" +
                "请输出详细的技术方案内容。";

        String userPrompt = String.format("技术要求：%s\n\n项目信息：%s", technicalRequirements, projectInfo);

        String solution = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sectionTitle", "技术方案");
        result.put("sectionContent", solution);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成项目管理章节
     *
     * @param projectInfo 项目信息
     * @param managementRequirements 管理要求
     * @return 项目管理内容
     */
    public Map<String, Object> generateProjectManagement(String projectInfo, String managementRequirements) {
        String systemPrompt = "你是一位专业的项目经理，具有丰富的项目管理经验。你的任务是根据项目信息和管理要求，制定详细的项目管理方案。\n\n" +
                "项目管理方案应包括以下内容：\n" +
                "1. 项目组织架构\n" +
                "2. 人员配置和职责分工\n" +
                "3. 项目实施计划\n" +
                "4. 进度控制措施\n" +
                "5. 质量管理措施\n" +
                "6. 风险管理措施\n" +
                "7. 沟通协调机制\n\n" +
                "请输出详细的项目管理方案内容。";

        String userPrompt = String.format("项目信息：%s\n\n管理要求：%s", projectInfo, managementRequirements);

        String managementPlan = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sectionTitle", "项目管理");
        result.put("sectionContent", managementPlan);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成质量保证章节
     *
     * @param qualityRequirements 质量要求
     * @param projectInfo 项目信息
     * @return 质量保证内容
     */
    public Map<String, Object> generateQualityAssurance(String qualityRequirements, String projectInfo) {
        String systemPrompt = "你是一位专业的质量管理专家，具有丰富的质量体系建设经验。你的任务是根据质量要求和项目信息，制定详细的质量保证方案。\n\n" +
                "质量保证方案应包括以下内容：\n" +
                "1. 质量管理体系\n" +
                "2. 质量控制措施\n" +
                "3. 质量检测方法\n" +
                "4. 质量验收标准\n" +
                "5. 质量改进措施\n" +
                "6. 质量责任制度\n\n" +
                "请输出详细的质量保证方案内容。";

        String userPrompt = String.format("质量要求：%s\n\n项目信息：%s", qualityRequirements, projectInfo);

        String assurancePlan = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sectionTitle", "质量保证");
        result.put("sectionContent", assurancePlan);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成售后服务章节
     *
     * @param serviceRequirements 服务要求
     * @param projectInfo 项目信息
     * @return 售后服务内容
     */
    public Map<String, Object> generateAfterSalesService(String serviceRequirements, String projectInfo) {
        String systemPrompt = "你是一位专业的客户服务经理，具有丰富的售后服务经验。你的任务是根据服务要求和项目信息，制定详细的售后服务方案。\n\n" +
                "售后服务方案应包括以下内容：\n" +
                "1. 服务承诺\n" +
                "2. 服务内容和标准\n" +
                "3. 服务响应时间\n" +
                "4. 服务团队配置\n" +
                "5. 服务流程\n" +
                "6. 服务监督机制\n" +
                "7. 培训服务安排\n\n" +
                "请输出详细的售后服务方案内容。";

        String userPrompt = String.format("服务要求：%s\n\n项目信息：%s", serviceRequirements, projectInfo);

        String servicePlan = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sectionTitle", "售后服务");
        result.put("sectionContent", servicePlan);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 自定义章节内容优化
     *
     * @param currentContent 当前章节内容
     * @param optimizationRequirements 优化要求
     * @return 优化后的章节内容
     */
    public Map<String, Object> optimizeSectionContent(String currentContent, String optimizationRequirements) {
        String systemPrompt = "你是一位专业的标书优化师，具有丰富的标书改进经验。你的任务是根据优化要求，对标书章节内容进行改进。\n\n" +
                "优化要求：\n" +
                "1. 保持内容的准确性和完整性\n" +
                "2. 提升内容的专业性和说服力\n" +
                "3. 突出评分重点\n" +
                "4. 语言表达更加精准\n" +
                "5. 逻辑结构更加清晰\n\n" +
                "请输出优化后的章节内容。";

        String userPrompt = String.format("当前章节内容：\n%s\n\n优化要求：\n%s", currentContent, optimizationRequirements);

        String optimizedContent = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("optimizedContent", optimizedContent);
        result.put("optimizedAt", new Date());
        
        return result;
    }
}