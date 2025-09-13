package com.assessment.repository;

import com.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评估数据访问层
 * 保存路径: src/main/java/com/assessment/repository/AssessmentRepository.java
 */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    /**
     * 根据学生姓名查找评估记录，按日期升序排列
     */
    List<Assessment> findByStudentNameOrderByAssessmentDateAsc(String studentName);
    
    /**
     * 根据学生姓名模糊搜索
     */
    List<Assessment> findByStudentNameContainingIgnoreCase(String studentName);
    
    /**
     * 根据日期范围查询评估记录
     */
    @Query("SELECT a FROM Assessment a WHERE a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * 计算平均纪律遵守度
     */
    @Query("SELECT AVG(a.disciplineScore) FROM Assessment a")
    Double getAverageDisciplineScore();
    
    /**
     * 计算平均技能达标率
     */
    @Query("SELECT AVG(a.skillCompletionRate) FROM Assessment a")
    Double getAverageSkillCompletionRate();
    
    /**
     * 计算平均任务完成率
     */
    @Query("SELECT AVG(a.tasksCompleted * 100.0 / a.totalTasks) FROM Assessment a")
    Double getAverageTaskCompletionRate();
    
    /**
     * 获取最近的评估记录
     */
    @Query("SELECT a FROM Assessment a ORDER BY a.assessmentDate DESC")
    List<Assessment> findRecentAssessments();
    
    /**
     * 根据纪律分数范围查询
     */
    List<Assessment> findByDisciplineScoreBetween(Integer minScore, Integer maxScore);
    
    /**
     * 根据技能达标率范围查询
     */
    List<Assessment> findBySkillCompletionRateBetween(Double minRate, Double maxRate);
}