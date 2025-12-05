package org.example.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KnowledgeBaseService {

    // 模拟向量数据库存储
    private final Map<String, KnowledgeEntry> knowledgeBase = new ConcurrentHashMap<>();
    
    // 模拟索引结构
    private final Map<String, List<String>> keywordIndex = new ConcurrentHashMap<>();

    /**
     * 知识条目类
     */
    public static class KnowledgeEntry {
        private String id;
        private String content;
        private String category;
        private String[] keywords;
        private float[] vector;
        private Date createdAt;
        private Date updatedAt;

        // 构造函数
        public KnowledgeEntry(String id, String content, String category, String[] keywords) {
            this.id = id;
            this.content = content;
            this.category = category;
            this.keywords = keywords;
            this.createdAt = new Date();
            this.updatedAt = new Date();
            // 在实际实现中，这里应该使用向量模型将内容转换为向量
            this.vector = new float[0]; // 简化处理
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; this.updatedAt = new Date(); }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String[] getKeywords() { return keywords; }
        public void setKeywords(String[] keywords) { this.keywords = keywords; }
        
        public float[] getVector() { return vector; }
        public void setVector(float[] vector) { this.vector = vector; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    }

    /**
     * 添加知识条目到知识库
     *
     * @param content 内容
     * @param category 分类
     * @param keywords 关键词
     * @return 条目ID
     */
    public String addKnowledgeEntry(String content, String category, String[] keywords) {
        String id = UUID.randomUUID().toString();
        KnowledgeEntry entry = new KnowledgeEntry(id, content, category, keywords);
        knowledgeBase.put(id, entry);
        
        // 更新关键词索引
        if (keywords != null) {
            for (String keyword : keywords) {
                keywordIndex.computeIfAbsent(keyword.toLowerCase(), k -> new ArrayList<>()).add(id);
            }
        }
        
        return id;
    }

    /**
     * 根据ID获取知识条目
     *
     * @param id 条目ID
     * @return 知识条目
     */
    public KnowledgeEntry getKnowledgeEntry(String id) {
        return knowledgeBase.get(id);
    }

    /**
     * 更新知识条目
     *
     * @param id 条目ID
     * @param content 新内容
     * @param category 新分类
     * @param keywords 新关键词
     * @return 是否更新成功
     */
    public boolean updateKnowledgeEntry(String id, String content, String category, String[] keywords) {
        KnowledgeEntry entry = knowledgeBase.get(id);
        if (entry == null) {
            return false;
        }
        
        entry.setContent(content);
        entry.setCategory(category);
        entry.setKeywords(keywords);
        entry.setUpdatedAt(new Date());
        
        // 更新关键词索引
        // 先删除旧的索引
        for (List<String> ids : keywordIndex.values()) {
            ids.remove(id);
        }
        
        // 添加新的索引
        if (keywords != null) {
            for (String keyword : keywords) {
                keywordIndex.computeIfAbsent(keyword.toLowerCase(), k -> new ArrayList<>()).add(id);
            }
        }
        
        return true;
    }

    /**
     * 删除知识条目
     *
     * @param id 条目ID
     * @return 是否删除成功
     */
    public boolean deleteKnowledgeEntry(String id) {
        KnowledgeEntry removed = knowledgeBase.remove(id);
        if (removed != null) {
            // 删除索引
            if (removed.getKeywords() != null) {
                for (String keyword : removed.getKeywords()) {
                    List<String> ids = keywordIndex.get(keyword.toLowerCase());
                    if (ids != null) {
                        ids.remove(id);
                        if (ids.isEmpty()) {
                            keywordIndex.remove(keyword.toLowerCase());
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 根据关键词搜索知识条目
     *
     * @param keyword 关键词
     * @return 匹配的知识条目列表
     */
    public List<KnowledgeEntry> searchByKeyword(String keyword) {
        List<KnowledgeEntry> results = new ArrayList<>();
        List<String> ids = keywordIndex.get(keyword.toLowerCase());
        
        if (ids != null) {
            for (String id : ids) {
                KnowledgeEntry entry = knowledgeBase.get(id);
                if (entry != null) {
                    results.add(entry);
                }
            }
        }
        
        return results;
    }

    /**
     * 根据分类获取知识条目
     *
     * @param category 分类
     * @return 匹配的知识条目列表
     */
    public List<KnowledgeEntry> getByCategory(String category) {
        List<KnowledgeEntry> results = new ArrayList<>();
        
        for (KnowledgeEntry entry : knowledgeBase.values()) {
            if (category.equals(entry.getCategory())) {
                results.add(entry);
            }
        }
        
        return results;
    }

    /**
     * 语义搜索（简化实现，实际应使用向量相似度计算）
     *
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 最相似的知识条目列表
     */
    public List<KnowledgeEntry> semanticSearch(String query, int topK) {
        // 简化实现：基于关键词匹配的数量来排序
        Map<KnowledgeEntry, Integer> scoreMap = new HashMap<>();
        
        String[] queryKeywords = extractKeywords(query);
        
        for (KnowledgeEntry entry : knowledgeBase.values()) {
            int score = 0;
            if (entry.getKeywords() != null) {
                for (String queryKeyword : queryKeywords) {
                    for (String entryKeyword : entry.getKeywords()) {
                        if (queryKeyword.equalsIgnoreCase(entryKeyword)) {
                            score++;
                        }
                    }
                }
            }
            scoreMap.put(entry, score);
        }
        
        // 按分数排序并返回topK
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<KnowledgeEntry, Integer>comparingByValue().reversed())
                .limit(topK)
                .map(Map.Entry::getKey)
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }

    /**
     * 简单的关键词提取（实际应使用NLP技术）
     *
     * @param text 文本
     * @return 关键词数组
     */
    private String[] extractKeywords(String text) {
        // 简化实现：按空格分割并过滤常用词
        Set<String> commonWords = new HashSet<>(Arrays.asList("的", "是", "在", "了", "和", "有", "我", "你", "他", "她", "它"));
        
        return Arrays.stream(text.split("[\\s,，.。;；:：]+"))
                .filter(word -> word.length() > 1 && !commonWords.contains(word))
                .toArray(String[]::new);
    }

    /**
     * 获取知识库统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEntries", knowledgeBase.size());
        stats.put("totalKeywords", keywordIndex.size());
        stats.put("categories", knowledgeBase.values().stream()
                .map(KnowledgeEntry::getCategory)
                .distinct()
                .count());
        
        // 按分类统计
        Map<String, Long> categoryStats = new HashMap<>();
        for (KnowledgeEntry entry : knowledgeBase.values()) {
            categoryStats.merge(entry.getCategory(), 1L, Long::sum);
        }
        stats.put("entriesByCategory", categoryStats);
        
        return stats;
    }

    /**
     * 清空知识库
     */
    public void clearKnowledgeBase() {
        knowledgeBase.clear();
        keywordIndex.clear();
    }
}