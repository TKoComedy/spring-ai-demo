package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BidOutlineService {

    @Autowired
    private OllamaService ollamaService;

    /**
     * 根据招标文件生成标书目录
     *
     * @param bidDocument 招标文件内容
     * @param analysisResult 招标文件分析结果（可选）
     * @return 标书目录结构
     */
    public Map<String, Object> generateBidOutline(String bidDocument, Map<String, Object> analysisResult) {
        String systemPrompt = "你是一位专业的标书目录设计师，具有丰富的投标经验。你的任务是根据招标文件的要求，设计一份结构清晰、符合要求的标书目录。\n\n" +
                "请遵循以下原则：\n" +
                "1. 全面响应招标文件的所有要求\n" +
                "2. 结构清晰，层次分明\n" +
                "3. 突出评分重点章节\n" +
                "4. 符合行业规范和惯例\n" +
                "5. 便于评审专家查阅\n\n" +
                "请输出详细的目录结构，包括：\n" +
                "1. 一级标题\n" +
                "2. 二级标题\n" +
                "3. 三级标题（如需要）\n" +
                "4. 各章节的简要说明";

        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append("请根据以下招标文件生成标书目录：\n\n");
        userPromptBuilder.append(bidDocument);
        
        if (analysisResult != null && !analysisResult.isEmpty()) {
            userPromptBuilder.append("\n\n招标文件分析结果：\n");
            for (Map.Entry<String, Object> entry : analysisResult.entrySet()) {
                userPromptBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        String userPrompt = userPromptBuilder.toString();

        String outline = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("outline", outline);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成技术标目录
     *
     * @param technicalRequirements 技术要求
     * @return 技术标目录
     */
    public Map<String, Object> generateTechnicalOutline(String technicalRequirements) {
        String systemPrompt = "你是一位专业的技术标书目录设计师。你的任务是根据技术要求，设计一份详细的技术标目录。\n\n" +
                "请考虑以下方面：\n" +
                "1. 技术方案设计\n" +
                "2. 实施计划和进度安排\n" +
                "3. 质量保证措施\n" +
                "4. 项目管理机构\n" +
                "5. 安全保障措施\n" +
                "6. 环境保护措施\n" +
                "7. 风险控制措施\n" +
                "8. 售后服务承诺\n\n" +
                "请输出详细的目录结构，并为每个章节提供简要说明。";

        String userPrompt = String.format("请根据以下技术要求生成技术标目录：\n\n%s", technicalRequirements);

        String outline = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("technicalOutline", outline);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 生成商务标目录
     *
     * @param commercialRequirements 商务要求
     * @return 商务标目录
     */
    public Map<String, Object> generateCommercialOutline(String commercialRequirements) {
        String systemPrompt = "你是一位专业的商务标书目录设计师。你的任务是根据商务要求，设计一份详细的商务标目录。\n\n" +
                "请考虑以下方面：\n" +
                "1. 投标函及附录\n" +
                "2. 法定代表人身份证明\n" +
                "3. 授权委托书\n" +
                "4. 联合体协议书（如有）\n" +
                "5. 投标保证金\n" +
                "6. 投标报价表\n" +
                "7. 资格审查资料\n" +
                "8. 商务条款偏离表\n" +
                "9. 项目管理机构\n" +
                "10. 拟分包项目情况表\n" +
                "11. 资格预审更新资料\n" +
                "12. 其他资料\n\n" +
                "请输出详细的目录结构，并为每个章节提供简要说明。";

        String userPrompt = String.format("请根据以下商务要求生成商务标目录：\n\n%s", commercialRequirements);

        String outline = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("commercialOutline", outline);
        result.put("generatedAt", new Date());
        
        return result;
    }

    /**
     * 自定义目录结构调整
     *
     * @param currentOutline 当前目录
     * @param modifications 调整要求
     * @return 调整后的目录
     */
    public Map<String, Object> customizeOutline(String currentOutline, String modifications) {
        String systemPrompt = "你是一位专业的标书目录编辑师。你的任务是根据用户的修改要求，对现有标书目录进行调整。\n\n" +
                "请遵循以下原则：\n" +
                "1. 保持目录结构的完整性和逻辑性\n" +
                "2. 确保修改符合标书编写规范\n" +
                "3. 突出用户要求的重点章节\n" +
                "4. 保持格式统一\n\n" +
                "请输出调整后的目录结构。";

        String userPrompt = String.format("当前目录：\n%s\n\n修改要求：\n%s", currentOutline, modifications);

        String outline = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("customizedOutline", outline);
        result.put("modifiedAt", new Date());
        
        return result;
    }
}