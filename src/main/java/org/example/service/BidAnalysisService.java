package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

@Service
public class BidAnalysisService {

    @Autowired
    private OllamaService ollamaService;

    /**
     * 智能解析招标文件，提取关键信息
     *
     * @param bidDocument 招标文件内容
     * @return 解析结果
     */
    public Map<String, Object> analyzeBidDocument(String bidDocument) {
        String systemPrompt = "你是一位专业的招标文件分析师，具有丰富的招投标经验。你的任务是分析一份招标文件，提取其中的关键信息。\n\n" +
                "请从以下维度分析招标文件：\n" +
                "1. 项目基本信息：项目名称、招标人、项目规模、预算金额等\n" +
                "2. 技术要求：关键技术指标、性能要求、质量标准等\n" +
                "3. 商务要求：付款方式、交付时间、质保期等\n" +
                "4. 评分标准：各部分的权重分配、评分细则等\n" +
                "5. 重要时间节点：投标截止时间、开标时间等\n" +
                "6. 资质要求：投标人需具备的资质、业绩要求等\n" +
                "7. 风险点识别：潜在的风险条款、需要注意的事项等\n\n" +
                "请以结构化的方式输出分析结果，便于后续标书编写使用。";

        String userPrompt = String.format("请分析以下招标文件：\n\n%s", bidDocument);

        String analysisResult = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalDocument", bidDocument);
        result.put("analysis", analysisResult);
        
        // 提取关键信息
        result.putAll(extractKeyInformation(bidDocument));
        
        return result;
    }

    /**
     * 提取招标文件中的关键信息
     *
     * @param bidDocument 招标文件内容
     * @return 关键信息Map
     */
    private Map<String, Object> extractKeyInformation(String bidDocument) {
        Map<String, Object> info = new HashMap<>();
        
        // 提取项目名称
        Pattern projectNamePattern = Pattern.compile("(?:项目名称|工程名称)[\\s:：]*([^\n\r]+)");
        Matcher projectNameMatcher = projectNamePattern.matcher(bidDocument);
        if (projectNameMatcher.find()) {
            info.put("projectName", projectNameMatcher.group(1).trim());
        }
        
        // 提取预算金额
        Pattern budgetPattern = Pattern.compile("(?:预算金额|项目预算|投资估算)[\\s:：]*([\\d,.]+\\s*万?元?)");
        Matcher budgetMatcher = budgetPattern.matcher(bidDocument);
        if (budgetMatcher.find()) {
            info.put("budget", budgetMatcher.group(1).trim());
        }
        
        // 提取投标截止时间
        Pattern deadlinePattern = Pattern.compile("(?:投标截止时间|递交投标文件截止时间)[\\s:：]*([^\n\r]+)");
        Matcher deadlineMatcher = deadlinePattern.matcher(bidDocument);
        if (deadlineMatcher.find()) {
            info.put("deadline", deadlineMatcher.group(1).trim());
        }
        
        // 提取评分标准
        Pattern scoringPattern = Pattern.compile("(?:评分标准|评标办法)[\\s:：]*([^\n\r]+(?:\n[^\n\r]*){0,10})");
        Matcher scoringMatcher = scoringPattern.matcher(bidDocument);
        if (scoringMatcher.find()) {
            info.put("scoringCriteria", scoringMatcher.group(1).trim());
        }
        
        return info;
    }

    /**
     * 识别招标文件中的风险点
     *
     * @param bidDocument 招标文件内容
     * @return 风险点列表
     */
    public List<String> identifyRiskPoints(String bidDocument) {
        String systemPrompt = "你是一位专业的招标文件风险评估师。你的任务是识别招标文件中的潜在风险点。\n\n" +
                "请重点关注以下类型的风险：\n" +
                "1. 法律风险：不合规条款、法律障碍等\n" +
                "2. 技术风险：过高或不明确的技术要求\n" +
                "3. 商务风险：不利的付款条件、过短的交付时间等\n" +
                "4. 资质风险：过高的资质要求、不合理的业绩要求等\n" +
                "5. 时间风险：过于紧张的时间安排\n\n" +
                "请列出具体的风险点，并给出简要说明。";

        String userPrompt = String.format("请识别以下招标文件中的风险点：\n\n%s", bidDocument);

        String riskAnalysis = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        // 将分析结果拆分为列表
        String[] risks = riskAnalysis.split("\n");
        List<String> riskList = new ArrayList<>();
        for (String risk : risks) {
            if (risk.trim().length() > 0) {
                riskList.add(risk.trim());
            }
        }
        
        return riskList;
    }

    /**
     * 提取招标文件中的评分标准
     *
     * @param bidDocument 招标文件内容
     * @return 评分标准详情
     */
    public Map<String, Object> extractScoringCriteria(String bidDocument) {
        String systemPrompt = "你是一位专业的评标专家。你的任务是从招标文件中提取评分标准。\n\n" +
                "请按照以下格式输出：\n" +
                "1. 总体评分构成（如：技术部分40分，商务部分30分，价格部分30分）\n" +
                "2. 技术部分评分细则\n" +
                "3. 商务部分评分细则\n" +
                "4. 价格部分评分细则\n" +
                "5. 其他评分因素\n\n" +
                "请以结构化的方式呈现评分标准，便于投标方针对性响应。";

        String userPrompt = String.format("请提取以下招标文件中的评分标准：\n\n%s", bidDocument);

        String criteria = ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("criteriaDetails", criteria);
        
        return result;
    }
}