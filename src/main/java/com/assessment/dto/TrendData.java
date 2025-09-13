package com.assessment.dto;

/**
 * 趋势数据传输对象
 * 保存路径: src/main/java/com/assessment/dto/TrendData.java
 */
public class TrendData {
    private String date;
    private Double value;
    
    public TrendData() {}
    
    public TrendData(String date, Double value) {
        this.date = date;
        this.value = value;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TrendData{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}