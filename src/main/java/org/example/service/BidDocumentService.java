package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BidDocumentService {

    @Autowired
    private OllamaService ollamaService;

    /**
     * 生成主标书 - 追求最高评分
     *
     * @param projectInfo 项目信息
     * @param requirements 标书要求
     * @return 高质量标书内容
     */
    public String generateMainBidDocument(String projectInfo, String requirements) {
        String systemPrompt = "你是一位专业的标书撰写专家，具有丰富的投标经验。你的任务是为以下项目撰写一份能够获得最高评分的优质标书。\n\n" +
                "撰写要求：\n" +
                "1. 全面响应招标文件的所有要求\n" +
                "2. 突出本公司优势和技术实力\n" +
                "3. 方案详实可行，技术路线清晰\n" +
                "4. 表述严谨专业，逻辑性强\n" +
                "5. 格式规范，条理清晰\n" +
                "6. 对评分标准要点重点突出\n\n" +
                "请严格按照标书格式输出，包括但不限于：\n" +
                "- 投标函\n" +
                "- 法定代表人身份证明\n" +
                "- 授权委托书\n" +
                "- 联合体协议书（如有）\n" +
                "- 投标保证金\n" +
                "- 投标报价表\n" +
                "- 资格审查资料\n" +
                "- 技术规格偏离表\n" +
                "- 技术建议书\n" +
                "- 商务条款偏离表\n" +
                "- 项目管理机构\n" +
                "- 拟分包项目情况表\n" +
                "- 资格预审更新资料\n" +
                "- 其他资料";

        String userPrompt = String.format("项目信息：%s\n\n标书要求：%s\n\n请根据以上信息生成一份追求最高评分的主标书。", 
                projectInfo, requirements);

        return ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
    }

    /**
     * 生成陪标书 - 确保在及格线以上
     *
     * @param projectInfo 项目信息
     * @param requirements 标书要求
     * @return 及格线以上的标书内容
     */
    public String generateSecondaryBidDocument(String projectInfo, String requirements) {
        String systemPrompt = "你是一位专业的标书撰写专家。你的任务是为以下项目撰写一份能够通过评审但不会中标的陪标书。\n\n" +
                "撰写要求：\n" +
                "1. 满足招标文件的基本要求，确保通过符合性检查\n" +
                "2. 内容完整，格式正确\n" +
                "3. 技术方案基本可行，但不突出优势\n" +
                "4. 报价合理，略高于预期中标价格\n" +
                "5. 避免明显的低级错误\n" +
                "6. 不要过于优秀以免引起关注\n\n" +
                "请严格按照标书格式输出，包括必要的组成部分。";

        String userPrompt = String.format("项目信息：%s\n\n标书要求：%s\n\n请根据以上信息生成一份陪标书，确保在及格线以上但不会中标。", 
                projectInfo, requirements);

        return ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
    }

    /**
     * 生成多份陪标书
     *
     * @param projectInfo 项目信息
     * @param requirements 标书要求
     * @param count 生成数量
     * @return 陪标书列表
     */
    public List<String> generateMultipleSecondaryBidDocuments(String projectInfo, String requirements, int count) {
        List<String> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // 添加一些随机性以避免重复
            String variedRequirements = requirements + String.format("\n\n这是第%d份陪标书，请在细节上与其他陪标书略有差异。", i + 1);
            documents.add(generateSecondaryBidDocument(projectInfo, variedRequirements));
        }
        return documents;
    }

    /**
     * 评估标书质量
     *
     * @param bidDocument 标书内容
     * @return 质量评分和建议
     */
    public String evaluateBidDocument(String bidDocument) {
        String systemPrompt = "你是一位资深的标书评审专家，具有丰富的评标经验。你的任务是对一份标书进行专业评估。\n\n" +
                "评估要求：\n" +
                "1. 从以下几个维度进行评分（每项满分20分）：\n" +
                "   - 符合性（是否满足招标要求）\n" +
                "   - 技术方案（先进性、可行性）\n" +
                "   - 商务条款（合理性、竞争力）\n" +
                "   - 企业实力（资质、业绩、团队）\n" +
                "   - 格式规范（完整性、条理性）\n" +
                "2. 给出总分（满分100分）\n" +
                "3. 提供具体的改进建议\n" +
                "4. 判断该标书属于主标书还是陪标书水平";

        String userPrompt = String.format("请评估以下标书的质量：\n\n%s", bidDocument);

        return ollamaService.chatWithSystemPrompt(userPrompt, systemPrompt);
    }
}