package org.example.controller;

import org.example.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 添加知识条目
     */
    @PostMapping("/entries")
    public Map<String, String> addKnowledgeEntry(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String category = (String) request.get("category");
        @SuppressWarnings("unchecked")
        List<String> keywordsList = (List<String>) request.get("keywords");
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        
        if (category == null || category.trim().isEmpty()) {
            category = "未分类";
        }
        
        String[] keywords = keywordsList != null ? keywordsList.toArray(new String[0]) : new String[0];
        String id = knowledgeBaseService.addKnowledgeEntry(content, category, keywords);
        
        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("message", "知识条目添加成功");
        return result;
    }

    /**
     * 获取知识条目
     */
    @GetMapping("/entries/{id}")
    public Map<String, Object> getKnowledgeEntry(@PathVariable String id) {
        KnowledgeBaseService.KnowledgeEntry entry = knowledgeBaseService.getKnowledgeEntry(id);
        
        if (entry == null) {
            throw new IllegalArgumentException("未找到ID为 " + id + " 的知识条目");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", entry.getId());
        result.put("content", entry.getContent());
        result.put("category", entry.getCategory());
        result.put("keywords", entry.getKeywords());
        result.put("createdAt", entry.getCreatedAt());
        result.put("updatedAt", entry.getUpdatedAt());
        
        return result;
    }

    /**
     * 更新知识条目
     */
    @PutMapping("/entries/{id}")
    public Map<String, String> updateKnowledgeEntry(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String category = (String) request.get("category");
        @SuppressWarnings("unchecked")
        List<String> keywordsList = (List<String>) request.get("keywords");
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        
        if (category == null || category.trim().isEmpty()) {
            category = "未分类";
        }
        
        String[] keywords = keywordsList != null ? keywordsList.toArray(new String[0]) : new String[0];
        boolean success = knowledgeBaseService.updateKnowledgeEntry(id, content, category, keywords);
        
        if (!success) {
            throw new IllegalArgumentException("未找到ID为 " + id + " 的知识条目");
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("message", "知识条目更新成功");
        return result;
    }

    /**
     * 删除知识条目
     */
    @DeleteMapping("/entries/{id}")
    public Map<String, String> deleteKnowledgeEntry(@PathVariable String id) {
        boolean success = knowledgeBaseService.deleteKnowledgeEntry(id);
        
        if (!success) {
            throw new IllegalArgumentException("未找到ID为 " + id + " 的知识条目");
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("message", "知识条目删除成功");
        return result;
    }

    /**
     * 根据关键词搜索知识条目
     */
    @GetMapping("/search/keyword")
    public Map<String, Object> searchByKeyword(@RequestParam String keyword) {
        List<KnowledgeBaseService.KnowledgeEntry> entries = knowledgeBaseService.searchByKeyword(keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword);
        result.put("count", entries.size());
        result.put("entries", convertEntriesToList(entries));
        
        return result;
    }

    /**
     * 根据分类获取知识条目
     */
    @GetMapping("/category/{category}")
    public Map<String, Object> getByCategory(@PathVariable String category) {
        List<KnowledgeBaseService.KnowledgeEntry> entries = knowledgeBaseService.getByCategory(category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("count", entries.size());
        result.put("entries", convertEntriesToList(entries));
        
        return result;
    }

    /**
     * 语义搜索
     */
    @GetMapping("/search/semantic")
    public Map<String, Object> semanticSearch(@RequestParam String query, @RequestParam(defaultValue = "5") int topK) {
        List<KnowledgeBaseService.KnowledgeEntry> entries = knowledgeBaseService.semanticSearch(query, topK);
        
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("topK", topK);
        result.put("count", entries.size());
        result.put("entries", convertEntriesToList(entries));
        
        return result;
    }

    /**
     * 获取知识库统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return knowledgeBaseService.getStatistics();
    }

    /**
     * 清空知识库
     */
    @DeleteMapping("/clear")
    public Map<String, String> clearKnowledgeBase() {
        knowledgeBaseService.clearKnowledgeBase();
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "知识库已清空");
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "知识库服务正常运行");
        result.put("features", "知识条目管理,关键词搜索,语义搜索,统计信息");
        return result;
    }

    /**
     * 将知识条目列表转换为Map列表
     */
    private List<Map<String, Object>> convertEntriesToList(List<KnowledgeBaseService.KnowledgeEntry> entries) {
        return entries.stream().map(entry -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getId());
            map.put("content", entry.getContent());
            map.put("category", entry.getCategory());
            map.put("keywords", entry.getKeywords());
            map.put("createdAt", entry.getCreatedAt());
            map.put("updatedAt", entry.getUpdatedAt());
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}