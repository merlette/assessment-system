package com.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.io.File;

@SpringBootApplication
@EnableJpaRepositories
public class AssessmentSystemApplication {
    public static void main(String[] args) {
        System.out.println("正在启动评估系统...");
        SpringApplication.run(AssessmentSystemApplication.class, args);
        System.out.println("评估系统启动成功！访问地址: http://localhost:8080");
    }
}