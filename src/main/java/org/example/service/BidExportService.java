package org.example.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BidExportService {

    /**
     * 导出为Word格式
     *
     * @param bidDocument 标书内容
     * @param templateStyle 模板样式
     * @return Word文档字节数组
     */
    public byte[] exportToWord(String bidDocument, String templateStyle) {
        // 这里应该使用Apache POI或其他库来生成真正的Word文档
        // 为了简化，我们返回一个模拟的Word文档内容
        StringBuilder wordContent = new StringBuilder();
        wordContent.append("标书文档\n\n");
        wordContent.append("模板样式: ").append(templateStyle).append("\n\n");
        wordContent.append(bidDocument);
        
        return wordContent.toString().getBytes();
    }

    /**
     * 导出为PDF格式
     *
     * @param bidDocument 标书内容
     * @param templateStyle 模板样式
     * @return PDF文档字节数组
     */
    public byte[] exportToPdf(String bidDocument, String templateStyle) {
        // 这里应该使用iText或其他库来生成真正的PDF文档
        // 为了简化，我们返回一个模拟的PDF文档内容
        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("PDF标书文档\n\n");
        pdfContent.append("模板样式: ").append(templateStyle).append("\n\n");
        pdfContent.append(bidDocument);
        
        return pdfContent.toString().getBytes();
    }

    /**
     * 导出为HTML格式
     *
     * @param bidDocument 标书内容
     * @param templateStyle 模板样式
     * @return HTML文档字符串
     */
    public String exportToHtml(String bidDocument, String templateStyle) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html>\n<head>\n");
        htmlContent.append("<meta charset=\"UTF-8\">\n");
        htmlContent.append("<title>标书文档</title>\n");
        htmlContent.append("<style>\n");
        htmlContent.append("body { font-family: Arial, sans-serif; }\n");
        htmlContent.append("h1, h2, h3 { color: #333; }\n");
        htmlContent.append(".header { background-color: #f0f0f0; padding: 10px; }\n");
        htmlContent.append(".content { margin: 20px; }\n");
        htmlContent.append("</style>\n");
        htmlContent.append("</head>\n<body>\n");
        htmlContent.append("<div class=\"header\">\n");
        htmlContent.append("<h1>标书文档</h1>\n");
        htmlContent.append("<p>模板样式: ").append(templateStyle).append("</p>\n");
        htmlContent.append("</div>\n");
        htmlContent.append("<div class=\"content\">\n");
        
        // 将标书内容转换为HTML格式
        String htmlFormattedContent = formatContentToHtml(bidDocument);
        htmlContent.append(htmlFormattedContent);
        
        htmlContent.append("</div>\n</body>\n</html>");
        
        return htmlContent.toString();
    }

    /**
     * 将标书内容格式化为HTML
     *
     * @param content 原始内容
     * @return HTML格式内容
     */
    private String formatContentToHtml(String content) {
        // 替换标题（以数字开头的行）
        Pattern titlePattern = Pattern.compile("^(\\d+(?:\\.\\d+)*)[\\s\u00A0]+(.+)$", Pattern.MULTILINE);
        Matcher titleMatcher = titlePattern.matcher(content);
        StringBuffer formattedContent = new StringBuffer();
        
        while (titleMatcher.find()) {
            int level = getTitleLevel(titleMatcher.group(1));
            String title = titleMatcher.group(2);
            titleMatcher.appendReplacement(formattedContent, "<h" + level + ">" + titleMatcher.group(1) + " " + title + "</h" + level + ">");
        }
        titleMatcher.appendTail(formattedContent);
        
        // 替换段落（空行分隔的文本块）
        String[] paragraphs = formattedContent.toString().split("\n\n");
        StringBuilder result = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                // 移除可能的HTML标签包裹
                String cleanParagraph = paragraph.replaceAll("^<h\\d>", "").replaceAll("</h\\d>$", "");
                if (!cleanParagraph.trim().isEmpty()) {
                    result.append("<p>").append(cleanParagraph).append("</p>\n");
                }
            }
        }
        
        return result.toString();
    }

    /**
     * 根据标题层级数字确定HTML标题级别
     *
     * @param number 标题编号
     * @return HTML标题级别（1-6）
     */
    private int getTitleLevel(String number) {
        String[] parts = number.split("\\.");
        int level = parts.length;
        return Math.min(level, 6); // HTML只支持h1-h6
    }

    /**
     * 导出为纯文本格式
     *
     * @param bidDocument 标书内容
     * @param templateStyle 模板样式
     * @return 纯文本字符串
     */
    public String exportToText(String bidDocument, String templateStyle) {
        StringBuilder textContent = new StringBuilder();
        textContent.append("标书文档\n\n");
        textContent.append("模板样式: ").append(templateStyle).append("\n\n");
        textContent.append(bidDocument);
        
        return textContent.toString();
    }

    /**
     * 获取支持的导出格式列表
     *
     * @return 格式列表
     */
    public List<String> getSupportedFormats() {
        return Arrays.asList("word", "pdf", "html", "text");
    }

    /**
     * 根据模板样式生成标书封面
     *
     * @param projectName 项目名称
     * @param companyName 公司名称
     * @param templateStyle 模板样式
     * @return 封面HTML
     */
    public String generateCoverPage(String projectName, String companyName, String templateStyle) {
        StringBuilder coverPage = new StringBuilder();
        coverPage.append("<div style=\"text-align: center; margin-top: 100px;\">\n");
        coverPage.append("<h1>").append(projectName).append("</h1>\n");
        coverPage.append("<h2>投标文件</h2>\n");
        coverPage.append("<h3>").append(companyName).append("</h3>\n");
        coverPage.append("<p style=\"margin-top: 50px;\">").append(new Date()).append("</p>\n");
        coverPage.append("</div>\n");
        
        return coverPage.toString();
    }
}