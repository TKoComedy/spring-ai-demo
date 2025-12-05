package org.example.controller;

import org.example.service.BidExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/bid-export")
public class BidExportController {

    @Autowired
    private BidExportService bidExportService;

    /**
     * 导出标书为指定格式
     */
    @PostMapping("/export")
    public ResponseEntity<?> exportBidDocument(@RequestBody Map<String, String> request) {
        String bidDocument = request.get("bidDocument");
        String format = request.get("format");
        String templateStyle = request.get("templateStyle");
        String fileName = request.get("fileName");
        
        if (bidDocument == null || bidDocument.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("标书内容不能为空");
        }
        
        if (format == null || format.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("导出格式不能为空");
        }
        
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "标书文档";
        }
        
        if (templateStyle == null || templateStyle.trim().isEmpty()) {
            templateStyle = "默认样式";
        }
        
        try {
            switch (format.toLowerCase()) {
                case "word":
                    return exportAsWord(bidDocument, templateStyle, fileName);
                case "pdf":
                    return exportAsPdf(bidDocument, templateStyle, fileName);
                case "html":
                    return exportAsHtml(bidDocument, templateStyle, fileName);
                case "text":
                    return exportAsText(bidDocument, templateStyle, fileName);
                default:
                    return ResponseEntity.badRequest().body("不支持的导出格式: " + format);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出为Word格式
     */
    private ResponseEntity<byte[]> exportAsWord(String bidDocument, String templateStyle, String fileName) {
        byte[] documentBytes = bidExportService.exportToWord(bidDocument, templateStyle);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileName + ".doc", StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            headers.setContentDispositionFormData("attachment", fileName + ".doc");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(documentBytes);
    }

    /**
     * 导出为PDF格式
     */
    private ResponseEntity<byte[]> exportAsPdf(String bidDocument, String templateStyle, String fileName) {
        byte[] documentBytes = bidExportService.exportToPdf(bidDocument, templateStyle);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        try {
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileName + ".pdf", StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            headers.setContentDispositionFormData("attachment", fileName + ".pdf");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(documentBytes);
    }

    /**
     * 导出为HTML格式
     */
    private ResponseEntity<String> exportAsHtml(String bidDocument, String templateStyle, String fileName) {
        String htmlContent = bidExportService.exportToHtml(bidDocument, templateStyle);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        try {
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileName + ".html", StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            headers.setContentDispositionFormData("attachment", fileName + ".html");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(htmlContent);
    }

    /**
     * 导出为纯文本格式
     */
    private ResponseEntity<String> exportAsText(String bidDocument, String templateStyle, String fileName) {
        String textContent = bidExportService.exportToText(bidDocument, templateStyle);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        try {
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileName + ".txt", StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            headers.setContentDispositionFormData("attachment", fileName + ".txt");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(textContent);
    }

    /**
     * 获取支持的导出格式列表
     */
    @GetMapping("/formats")
    public Map<String, Object> getSupportedFormats() {
        List<String> formats = bidExportService.getSupportedFormats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("supportedFormats", formats);
        return result;
    }

    /**
     * 生成标书封面
     */
    @PostMapping("/cover-page")
    public Map<String, String> generateCoverPage(@RequestBody Map<String, String> request) {
        String projectName = request.get("projectName");
        String companyName = request.get("companyName");
        String templateStyle = request.get("templateStyle");
        
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("公司名称不能为空");
        }
        
        if (templateStyle == null || templateStyle.trim().isEmpty()) {
            templateStyle = "默认样式";
        }
        
        String coverPage = bidExportService.generateCoverPage(projectName, companyName, templateStyle);
        
        Map<String, String> result = new HashMap<>();
        result.put("coverPage", coverPage);
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "标书导出服务正常运行");
        result.put("features", "Word导出,PDF导出,HTML导出,文本导出,封面生成");
        return result;
    }
}