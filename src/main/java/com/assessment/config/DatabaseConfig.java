package com.assessment.config;

import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class DatabaseConfig {
    
    @PostConstruct
    public void init() {
        // 创建数据库目录（相对于当前工作目录）
        File dbDir = new File("database");
        
        if (!dbDir.exists()) {
            boolean created = dbDir.mkdirs();
            if (created) {
                System.out.println("评估系统: 创建数据库目录 - " + dbDir.getAbsolutePath());
            } else {
                System.err.println("评估系统: 无法创建数据库目录 - " + dbDir.getAbsolutePath());
            }
        } else {
            System.out.println("评估系统: 使用数据库目录 - " + dbDir.getAbsolutePath());
        }
    }
}