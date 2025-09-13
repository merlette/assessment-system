package com.assessment.service;

import com.assessment.dto.AssessmentStatistics;
import com.assessment.dto.TrendData;
import com.assessment.entity.Assessment;
import com.assessment.repository.AssessmentRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评估业务逻辑服务类
 * 保存路径: src/main/java/com/assessment/service/AssessmentService.java
 */
@Service
@Transactional
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    /**
     * 保存评估记录
     */
    public Assessment saveAssessment(Assessment assessment) {
        if (assessment.getAssessmentDate() == null) {
            assessment.setAssessmentDate(LocalDate.now());
        }
        return assessmentRepository.save(assessment);
    }

    /**
     * 获取所有评估记录
     */
    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    /**
     * 根据ID获取评估记录
     */
    public Assessment getAssessmentById(Long id) {
        return assessmentRepository.findById(id).orElse(null);
    }

    /**
     * 根据学生姓名获取评估记录
     */
    public List<Assessment> getAssessmentsByStudent(String studentName) {
        return assessmentRepository.findByStudentNameOrderByAssessmentDateAsc(studentName);
    }

    /**
     * 搜索学生评估记录
     */
    public List<Assessment> searchAssessmentsByStudentName(String keyword) {
        return assessmentRepository.findByStudentNameContainingIgnoreCase(keyword);
    }

    /**
     * 删除评估记录
     */
    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }

    /**
     * 获取统计数据
     */
    public AssessmentStatistics getStatistics() {
        AssessmentStatistics stats = new AssessmentStatistics();

        // 计算平均分，处理null值
        Double avgDiscipline = assessmentRepository.getAverageDisciplineScore();
        Double avgSkill = assessmentRepository.getAverageSkillCompletionRate();
        Double avgTask = assessmentRepository.getAverageTaskCompletionRate();

        stats.setAverageDisciplineScore(avgDiscipline != null ? avgDiscipline : 0.0);
        stats.setAverageSkillCompletionRate(avgSkill != null ? avgSkill : 0.0);
        stats.setAverageTaskCompletionRate(avgTask != null ? avgTask : 0.0);
        stats.setTotalAssessments(assessmentRepository.count());

        // 生成趋势数据
        List<Assessment> allAssessments = assessmentRepository.findRecentAssessments();
        if (!allAssessments.isEmpty()) {
            stats.setDisciplineTrend(generateTrendData(allAssessments, "discipline"));
            stats.setSkillTrend(generateTrendData(allAssessments, "skill"));
            stats.setTaskTrend(generateTrendData(allAssessments, "task"));
        } else {
            // 如果没有数据，返回空列表
            stats.setDisciplineTrend(new ArrayList<>());
            stats.setSkillTrend(new ArrayList<>());
            stats.setTaskTrend(new ArrayList<>());
        }

        return stats;
    }

    /**
     * 生成趋势数据
     */
    private List<TrendData> generateTrendData(List<Assessment> assessments, String type) {
        if (assessments == null || assessments.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<Double>> groupedData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Assessment assessment : assessments) {
            String dateKey = assessment.getAssessmentDate().format(formatter);
            groupedData.computeIfAbsent(dateKey, k -> new ArrayList<>());

            switch (type) {
                case "discipline":
                    groupedData.get(dateKey).add(assessment.getDisciplineScore().doubleValue());
                    break;
                case "skill":
                    groupedData.get(dateKey).add(assessment.getSkillCompletionRate());
                    break;
                case "task":
                    groupedData.get(dateKey).add(assessment.getTaskCompletionRate());
                    break;
            }
        }

        return groupedData.entrySet().stream()
                .map(entry -> new TrendData(
                        entry.getKey(),
                        entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                ))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    /**
     * Excel导入功能
     */
    public List<Assessment> importFromExcel(MultipartFile file) throws IOException {
        List<Assessment> assessments = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过标题行，从第二行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Assessment assessment = new Assessment();

                    // 读取每列数据
                    String studentName = getCellStringValue(row.getCell(0));
                    if (studentName.trim().isEmpty()) continue; // 跳过空行

                    assessment.setStudentName(studentName);
                    
                    // 读取评估时间
                    Cell dateCell = row.getCell(1);
                    if (dateCell != null) {
                        try {
                            LocalDate date;
                            if (dateCell.getCellType() == CellType.NUMERIC) {
                                date = dateCell.getLocalDateTimeCellValue().toLocalDate();
                            } else {
                                // 尝试解析字符串格式的日期
                                String dateStr = dateCell.getStringCellValue();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                date = LocalDate.parse(dateStr, formatter);
                            }
                            assessment.setAssessmentDate(date);
                        } catch (Exception e) {
                            // 如果日期解析失败，使用当前日期
                            assessment.setAssessmentDate(LocalDate.now());
                        }
                    } else {
                        assessment.setAssessmentDate(LocalDate.now());
                    }
                    
                    assessment.setDisciplineScore((int) getCellNumericValue(row.getCell(2)));
                    assessment.setSkillCompletionRate(getCellNumericValue(row.getCell(3)));
                    assessment.setTasksCompleted((int) getCellNumericValue(row.getCell(4)));
                    assessment.setTotalTasks((int) getCellNumericValue(row.getCell(5)));

                    // 数据验证
                    if (isValidAssessment(assessment)) {
                        assessments.add(assessment);
                    }
                } catch (Exception e) {
                    System.err.println("处理第" + (i + 1) + "行时出错: " + e.getMessage());
                    // 继续处理其他行
                }
            }
        }

        // 批量保存
        if (!assessments.isEmpty()) {
            return assessmentRepository.saveAll(assessments);
        }

        return assessments;
    }

    /**
     * 验证评估数据有效性
     */
    private boolean isValidAssessment(Assessment assessment) {
        return assessment.getStudentName() != null && !assessment.getStudentName().trim().isEmpty()
                && assessment.getAssessmentDate() != null  // 添加时间验证
                && assessment.getDisciplineScore() != null && assessment.getDisciplineScore() >= 1 && assessment.getDisciplineScore() <= 5
                && assessment.getSkillCompletionRate() != null && assessment.getSkillCompletionRate() >= 0 && assessment.getSkillCompletionRate() <= 100
                && assessment.getTasksCompleted() != null && assessment.getTasksCompleted() >= 0
                && assessment.getTotalTasks() != null && assessment.getTotalTasks() > 0
                && assessment.getTasksCompleted() <= assessment.getTotalTasks();
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 获取单元格数值
     */
    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0.0;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }

    /**
     * 根据日期范围获取评估记录
     */
    public List<Assessment> getAssessmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return assessmentRepository.findByDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    /**
     * 获取优秀学生（纪律分数4分及以上，技能达标率80%及以上）
     */
    public List<Assessment> getExcellentStudents() {
        return assessmentRepository.findAll().stream()
                .filter(a -> a.getDisciplineScore() >= 4 && a.getSkillCompletionRate() >= 80.0)
                .collect(Collectors.toList());
    }

    /**
     * 验证日期有效性
     */
    private boolean isValidDate(LocalDate date) {
        if (date == null) return false;
        int year = date.getYear();
        return year >= 1900 && year <= 9999;
    }
}