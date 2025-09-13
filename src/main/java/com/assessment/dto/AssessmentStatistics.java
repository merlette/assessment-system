package com.assessment.dto;

import java.util.List;

/**
 * 评估统计数据传输对象
 * 保存路径: src/main/java/com/assessment/dto/AssessmentStatistics.java
 */
public class AssessmentStatistics {
    private Double averageDisciplineScore;
    private Double averageSkillCompletionRate;
    private Double averageTaskCompletionRate;
    private Long totalAssessments;
    private List<TrendData> disciplineTrend;
    private List<TrendData> skillTrend;
    private List<TrendData> taskTrend;
    
    public AssessmentStatistics() {}
    
    // Getter和Setter方法
    public Double getAverageDisciplineScore() {
        return averageDisciplineScore;
    }
    
    public void setAverageDisciplineScore(Double averageDisciplineScore) {
        this.averageDisciplineScore = averageDisciplineScore;
    }
    
    public Double getAverageSkillCompletionRate() {
        return averageSkillCompletionRate;
    }
    
    public void setAverageSkillCompletionRate(Double averageSkillCompletionRate) {
        this.averageSkillCompletionRate = averageSkillCompletionRate;
    }
    
    public Double getAverageTaskCompletionRate() {
        return averageTaskCompletionRate;
    }
    
    public void setAverageTaskCompletionRate(Double averageTaskCompletionRate) {
        this.averageTaskCompletionRate = averageTaskCompletionRate;
    }
    
    public Long getTotalAssessments() {
        return totalAssessments;
    }
    
    public void setTotalAssessments(Long totalAssessments) {
        this.totalAssessments = totalAssessments;
    }
    
    public List<TrendData> getDisciplineTrend() {
        return disciplineTrend;
    }
    
    public void setDisciplineTrend(List<TrendData> disciplineTrend) {
        this.disciplineTrend = disciplineTrend;
    }
    
    public List<TrendData> getSkillTrend() {
        return skillTrend;
    }
    
    public void setSkillTrend(List<TrendData> skillTrend) {
        this.skillTrend = skillTrend;
    }
    
    public List<TrendData> getTaskTrend() {
        return taskTrend;
    }
    
    public void setTaskTrend(List<TrendData> taskTrend) {
        this.taskTrend = taskTrend;
    }
    
    @Override
    public String toString() {
        return "AssessmentStatistics{" +
                "averageDisciplineScore=" + averageDisciplineScore +
                ", averageSkillCompletionRate=" + averageSkillCompletionRate +
                ", averageTaskCompletionRate=" + averageTaskCompletionRate +
                ", totalAssessments=" + totalAssessments +
                ", disciplineTrend=" + disciplineTrend +
                ", skillTrend=" + skillTrend +
                ", taskTrend=" + taskTrend +
                '}';
    }
}