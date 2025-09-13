package com.assessment.service;

import com.assessment.dto.AssessmentStatistics;
import com.assessment.entity.Assessment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF报告生成服务
 * 保存路径: src/main/java/com/assessment/service/ReportService.java
 */
@Service
public class ReportService {
    
    @Autowired
    private AssessmentService assessmentService;
    
    /**
     * 生成PDF报告
     */
    public byte[] generatePDFReport() throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        try {
            // 添加中文字体支持
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font contentFont = new Font(baseFont, 10, Font.NORMAL);
            
            // 标题
            Paragraph title = new Paragraph("评估系统统计报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // 报告生成时间
            Paragraph reportTime = new Paragraph(
                "报告生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")), 
                contentFont
            );
            reportTime.setAlignment(Element.ALIGN_RIGHT);
            reportTime.setSpacingAfter(20);
            document.add(reportTime);
            
            // 获取统计数据
            AssessmentStatistics stats = assessmentService.getStatistics();
            
            // 概述信息
            addSummarySection(document, stats, headerFont, contentFont);
            
            // 详细数据表格
            addDetailTable(document, headerFont, contentFont);
            
            // 分析总结
            addAnalysisSection(document, stats, headerFont, contentFont);
            
        } catch (Exception e) {
            // 如果中文字体不可用，使用默认字体
            generateSimplePDFReport(document);
        }
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * 添加概述部分
     */
    private void addSummarySection(Document document, AssessmentStatistics stats, 
                                  Font headerFont, Font contentFont) throws DocumentException {
        
        // 概述标题
        Paragraph summaryTitle = new Paragraph("一、数据概述", headerFont);
        summaryTitle.setSpacingBefore(10);
        summaryTitle.setSpacingAfter(15);
        document.add(summaryTitle);
        
        // 概述表格
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);
        
        // 表头样式
        PdfPCell headerCell1 = new PdfPCell(new Phrase("评估项目", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("平均分/完成率", headerFont));
        headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell1.setPadding(8);
        headerCell2.setPadding(8);
        summaryTable.addCell(headerCell1);
        summaryTable.addCell(headerCell2);
        
        // 数据行
        addTableRow(summaryTable, "纪律遵守度", String.format("%.2f 分", stats.getAverageDisciplineScore()), contentFont);
        addTableRow(summaryTable, "技能达标率", String.format("%.1f%%", stats.getAverageSkillCompletionRate()), contentFont);
        addTableRow(summaryTable, "任务完成率", String.format("%.1f%%", stats.getAverageTaskCompletionRate()), contentFont);
        addTableRow(summaryTable, "评估总数", stats.getTotalAssessments().toString() + " 条", contentFont);
        
        document.add(summaryTable);
    }
    
    /**
     * 添加详细数据表格
     */
    private void addDetailTable(Document document, Font headerFont, Font contentFont) throws DocumentException {
        List<Assessment> assessments = assessmentService.getAllAssessments();
        
        if (assessments.isEmpty()) {
            Paragraph noData = new Paragraph("暂无评估数据", contentFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            document.add(noData);
            return;
        }
        
        // 详细数据标题
        Paragraph detailTitle = new Paragraph("二、详细评估记录", headerFont);
        detailTitle.setSpacingBefore(20);
        detailTitle.setSpacingAfter(15);
        document.add(detailTitle);
        
        // 详细数据表格
        PdfPTable detailTable = new PdfPTable(6);
        detailTable.setWidthPercentage(100);
        detailTable.setSpacingAfter(20);
        
        // 设置列宽
        float[] columnWidths = {2f, 2f, 1.5f, 1.5f, 1.5f, 1.5f};
        detailTable.setWidths(columnWidths);
        
        // 表头
        String[] headers = {"学生姓名", "评估日期", "纪律遵守度", "技能达标率", "已完成任务", "总任务数"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(cell);
        }
        
        // 数据行
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Assessment assessment : assessments) {
            detailTable.addCell(createDataCell(assessment.getStudentName(), contentFont));
            detailTable.addCell(createDataCell(assessment.getAssessmentDate().format(formatter), contentFont));
            detailTable.addCell(createDataCell(assessment.getDisciplineScore() + "/5", contentFont));
            detailTable.addCell(createDataCell(String.format("%.1f%%", assessment.getSkillCompletionRate()), contentFont));
            detailTable.addCell(createDataCell(assessment.getTasksCompleted().toString(), contentFont));
            detailTable.addCell(createDataCell(assessment.getTotalTasks().toString(), contentFont));
        }
        
        document.add(detailTable);
    }
    
    /**
     * 添加分析总结部分
     */
    private void addAnalysisSection(Document document, AssessmentStatistics stats, 
                                   Font headerFont, Font contentFont) throws DocumentException {
        
        // 分析标题
        Paragraph analysisTitle = new Paragraph("三、数据分析", headerFont);
        analysisTitle.setSpacingBefore(20);
        analysisTitle.setSpacingAfter(15);
        document.add(analysisTitle);
        
        // 分析内容
        StringBuilder analysis = new StringBuilder();
        analysis.append("基于当前").append(stats.getTotalAssessments()).append("条评估记录的分析结果：\n\n");
        
        // 纪律表现分析
        double disciplineScore = stats.getAverageDisciplineScore();
        analysis.append("1. 纪律表现：平均").append(String.format("%.2f", disciplineScore)).append("分");
        if (disciplineScore >= 4.0) {
            analysis.append("，整体表现优秀");
        } else if (disciplineScore >= 3.0) {
            analysis.append("，整体表现良好");
        } else {
            analysis.append("，需要改进");
        }
        analysis.append("\n\n");
        
        // 技能发展分析
        double skillRate = stats.getAverageSkillCompletionRate();
        analysis.append("2. 技能发展：平均达标率").append(String.format("%.1f", skillRate)).append("%");
        if (skillRate >= 90.0) {
            analysis.append("，技能掌握优秀");
        } else if (skillRate >= 80.0) {
            analysis.append("，技能掌握良好");
        } else if (skillRate >= 70.0) {
            analysis.append("，技能掌握一般");
        } else {
            analysis.append("，需要加强技能训练");
        }
        analysis.append("\n\n");
        
        // 任务执行分析
        double taskRate = stats.getAverageTaskCompletionRate();
        analysis.append("3. 任务执行：平均完成率").append(String.format("%.1f", taskRate)).append("%");
        if (taskRate >= 90.0) {
            analysis.append("，执行能力优秀");
        } else if (taskRate >= 80.0) {
            analysis.append("，执行能力良好");
        } else if (taskRate >= 70.0) {
            analysis.append("，执行能力一般");
        } else {
            analysis.append("，需要提升执行能力");
        }
        
        Paragraph analysisContent = new Paragraph(analysis.toString(), contentFont);
        analysisContent.setSpacingAfter(20);
        document.add(analysisContent);
        
        // 建议部分
        addRecommendations(document, stats, headerFont, contentFont);
    }
    
    /**
     * 添加改进建议
     */
    private void addRecommendations(Document document, AssessmentStatistics stats, 
                                  Font headerFont, Font contentFont) throws DocumentException {
        
        Paragraph recTitle = new Paragraph("四、改进建议", headerFont);
        recTitle.setSpacingBefore(10);
        recTitle.setSpacingAfter(15);
        document.add(recTitle);
        
        StringBuilder recommendations = new StringBuilder();
        
        // 根据数据生成针对性建议
        if (stats.getAverageDisciplineScore() < 4.0) {
            recommendations.append("• 加强纪律管理，建立更完善的行为规范体系\n");
        }
        
        if (stats.getAverageSkillCompletionRate() < 85.0) {
            recommendations.append("• 优化技能培训方案，增加实践操作机会\n");
        }
        
        if (stats.getAverageTaskCompletionRate() < 85.0) {
            recommendations.append("• 改进任务分配机制，提供更多支持和指导\n");
        }
        
        if (stats.getTotalAssessments() < 10) {
            recommendations.append("• 建议增加评估频次，获得更全面的数据支撑\n");
        }
        
        recommendations.append("• 定期回顾评估结果，持续优化管理策略\n");
        recommendations.append("• 建立激励机制，鼓励优秀表现");
        
        Paragraph recContent = new Paragraph(recommendations.toString(), contentFont);
        document.add(recContent);
    }
    
    /**
     * 创建数据单元格
     */
    private PdfPCell createDataCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
    
    /**
     * 添加表格行
     */
    private void addTableRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        labelCell.setPadding(8);
        valueCell.setPadding(8);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    /**
     * 生成简化版PDF报告（当中文字体不可用时）
     */
    private void generateSimplePDFReport(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        // 标题
        Paragraph title = new Paragraph("Assessment System Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // 获取统计数据
        AssessmentStatistics stats = assessmentService.getStatistics();
        
        // 基本统计信息
        Paragraph summary = new Paragraph("Summary Statistics:", headerFont);
        summary.setSpacingAfter(10);
        document.add(summary);
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);
        
        addTableRow(summaryTable, "Average Discipline Score", String.format("%.2f", stats.getAverageDisciplineScore()), contentFont);
        addTableRow(summaryTable, "Average Skill Rate", String.format("%.1f%%", stats.getAverageSkillCompletionRate()), contentFont);
        addTableRow(summaryTable, "Average Task Rate", String.format("%.1f%%", stats.getAverageTaskCompletionRate()), contentFont);
        addTableRow(summaryTable, "Total Records", stats.getTotalAssessments().toString(), contentFont);
        
        document.add(summaryTable);
        
        // 生成时间
        Paragraph footer = new Paragraph(
            "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
            contentFont
        );
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);
    }
}