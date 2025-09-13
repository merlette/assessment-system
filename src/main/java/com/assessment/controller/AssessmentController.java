package com.assessment.controller;

import com.assessment.dto.AssessmentStatistics;
import com.assessment.entity.Assessment;
import com.assessment.service.AssessmentService;
import com.assessment.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评估系统REST API控制器
 * 保存路径: src/main/java/com/assessment/controller/AssessmentController.java
 */
@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "*") // 允许跨域请求
public class AssessmentController {
    
    @Autowired
    private AssessmentService assessmentService;
    
    @Autowired
    private ReportService reportService;
    
    /**
     * 获取所有评估数据
     */
    @GetMapping
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        try {
            List<Assessment> assessments = assessmentService.getAllAssessments();
            return ResponseEntity.ok(assessments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取评估记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {
        try {
            Assessment assessment = assessmentService.getAssessmentById(id);
            if (assessment != null) {
                return ResponseEntity.ok(assessment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 创建新的评估记录
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAssessment(@RequestBody Assessment assessment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 数据验证
            if (assessment.getStudentName() == null || assessment.getStudentName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "学生姓名不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (assessment.getDisciplineScore() == null || 
                assessment.getDisciplineScore() < 1 || assessment.getDisciplineScore() > 5) {
                response.put("success", false);
                response.put("message", "纪律遵守度必须在1-5分之间");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (assessment.getSkillCompletionRate() == null || 
                assessment.getSkillCompletionRate() < 0 || assessment.getSkillCompletionRate() > 100) {
                response.put("success", false);
                response.put("message", "技能达标率必须在0-100%之间");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (assessment.getTasksCompleted() == null || assessment.getTotalTasks() == null ||
                assessment.getTasksCompleted() < 0 || assessment.getTotalTasks() <= 0 ||
                assessment.getTasksCompleted() > assessment.getTotalTasks()) {
                response.put("success", false);
                response.put("message", "任务完成数据无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 如果评估日期为空，设置为当前日期
            if (assessment.getAssessmentDate() == null) {
                assessment.setAssessmentDate(LocalDate.now());
            }
            
            // 保存评估记录
            Assessment savedAssessment = assessmentService.saveAssessment(assessment);
            
            response.put("success", true);
            response.put("message", "评估记录创建成功");
            response.put("data", savedAssessment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 更新评估记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAssessment(@PathVariable Long id, 
                                                              @RequestBody Assessment assessment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Assessment existingAssessment = assessmentService.getAssessmentById(id);
            if (existingAssessment == null) {
                response.put("success", false);
                response.put("message", "评估记录不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 更新字段
            existingAssessment.setStudentName(assessment.getStudentName());
            existingAssessment.setDisciplineScore(assessment.getDisciplineScore());
            existingAssessment.setSkillCompletionRate(assessment.getSkillCompletionRate());
            existingAssessment.setTasksCompleted(assessment.getTasksCompleted());
            existingAssessment.setTotalTasks(assessment.getTotalTasks());
            
            Assessment updatedAssessment = assessmentService.saveAssessment(existingAssessment);
            
            response.put("success", true);
            response.put("message", "评估记录更新成功");
            response.put("data", updatedAssessment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除评估记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAssessment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Assessment existingAssessment = assessmentService.getAssessmentById(id);
            if (existingAssessment == null) {
                response.put("success", false);
                response.put("message", "评估记录不存在");
                return ResponseEntity.notFound().build();
            }
            
            assessmentService.deleteAssessment(id);
            
            response.put("success", true);
            response.put("message", "评估记录删除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据学生姓名获取评估记录
     */
    @GetMapping("/student/{studentName}")
    public ResponseEntity<List<Assessment>> getAssessmentsByStudent(@PathVariable String studentName) {
        try {
            List<Assessment> assessments = assessmentService.getAssessmentsByStudent(studentName);
            return ResponseEntity.ok(assessments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 搜索学生评估记录
     */
    @GetMapping("/search")
    public ResponseEntity<List<Assessment>> searchAssessments(@RequestParam String keyword) {
        try {
            List<Assessment> assessments = assessmentService.searchAssessmentsByStudentName(keyword);
            return ResponseEntity.ok(assessments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<AssessmentStatistics> getStatistics() {
        try {
            AssessmentStatistics stats = assessmentService.getStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            // 返回空的统计数据而不是错误
            AssessmentStatistics emptyStats = new AssessmentStatistics();
            emptyStats.setAverageDisciplineScore(0.0);
            emptyStats.setAverageSkillCompletionRate(0.0);
            emptyStats.setAverageTaskCompletionRate(0.0);
            emptyStats.setTotalAssessments(0L);
            return ResponseEntity.ok(emptyStats);
        }
    }
    
    /**
     * Excel文件导入
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 文件验证
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要导入的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                response.put("success", false);
                response.put("message", "请选择Excel文件（.xlsx或.xls格式）");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 文件大小检查（限制10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "文件大小不能超过10MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 导入数据
            List<Assessment> assessments = assessmentService.importFromExcel(file);
            
            response.put("success", true);
            response.put("message", "成功导入 " + assessments.size() + " 条记录");
            response.put("count", assessments.size());
            response.put("data", assessments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "导入失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取优秀学生列表
     */
    @GetMapping("/excellent")
    public ResponseEntity<List<Assessment>> getExcellentStudents() {
        try {
            List<Assessment> excellentStudents = assessmentService.getExcellentStudents();
            return ResponseEntity.ok(excellentStudents);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * PDF报告生成和下载
     */
    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> generatePDFReport() {
        try {
            byte[] pdfBytes = reportService.generatePDFReport();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "assessment_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("PDF生成失败: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}