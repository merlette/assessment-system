package com.assessment.entity;

import javax.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 评估数据实体类
 * 保存路径: src/main/java/com/assessment/entity/Assessment.java
 */
@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "student_name")
    private String studentName;
    
    @Column(nullable = false, name = "assessment_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessmentDate;
    
    // 行为表现 - 纪律遵守度 (1-5分)
    @Column(nullable = false, name = "discipline_score")
    private Integer disciplineScore;
    
    // 能力发展 - 技能达标率 (0-100%)
    @Column(nullable = false, name = "skill_completion_rate")
    private Double skillCompletionRate;
    
    // 参与状态 - 任务完成数
    @Column(nullable = false, name = "tasks_completed")
    private Integer tasksCompleted;
    
    @Column(nullable = false, name = "total_tasks")
    private Integer totalTasks;
    
    // 无参构造函数
    public Assessment() {}
    
    // 带参构造函数
    public Assessment(String studentName, Integer disciplineScore, 
                     Double skillCompletionRate, Integer tasksCompleted, Integer totalTasks) {
        this.studentName = studentName;
        this.assessmentDate = LocalDate.now();
        this.disciplineScore = disciplineScore;
        this.skillCompletionRate = skillCompletionRate;
        this.tasksCompleted = tasksCompleted;
        this.totalTasks = totalTasks;
    }
    
    // 计算任务完成率
    public Double getTaskCompletionRate() {
        if (totalTasks == null || totalTasks == 0) return 0.0;
        return (double) tasksCompleted / totalTasks * 100;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }
    
    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
    
    public Integer getDisciplineScore() {
        return disciplineScore;
    }
    
    public void setDisciplineScore(Integer disciplineScore) {
        this.disciplineScore = disciplineScore;
    }
    
    public Double getSkillCompletionRate() {
        return skillCompletionRate;
    }
    
    public void setSkillCompletionRate(Double skillCompletionRate) {
        this.skillCompletionRate = skillCompletionRate;
    }
    
    public Integer getTasksCompleted() {
        return tasksCompleted;
    }
    
    public void setTasksCompleted(Integer tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }
    
    public Integer getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", assessmentDate=" + assessmentDate +
                ", disciplineScore=" + disciplineScore +
                ", skillCompletionRate=" + skillCompletionRate +
                ", tasksCompleted=" + tasksCompleted +
                ", totalTasks=" + totalTasks +
                '}';
    }
}