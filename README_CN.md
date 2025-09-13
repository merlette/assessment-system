# 学生评估管理系统 📊

🌟 一个功能完整的学生评估管理系统，基于Spring Boot构建，提供现代化的Web界面和强大的数据分析功能。

[English README](README.md) | 中文版 README

## 🚀 功能特性

- **📈 数据统计**: 实时统计纪律遵守度、技能达标率、任务完成率等关键指标
- **📝 评估管理**: 支持添加、编辑、删除学生评估记录
- **📊 趋势分析**: 可视化图表展示各项指标的变化趋势
- **📁 批量导入**: 支持Excel文件批量导入评估数据
- **📄 报告导出**: 生成专业的PDF评估报告
- **🔍 智能搜索**: 快速查找特定学生的评估记录
- **🌙 暗黑模式**: 支持明暗主题切换

## 🛠️ 技术栈

- **后端框架**: Spring Boot
- **数据库**: H2 Database
- **前端技术**: 原生HTML5 + CSS3 + JavaScript
- **图表库**: Chart.js
- **文档处理**: Apache POI (Excel) + iText (PDF)
- **构建工具**: Maven

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/merlette/assessment-system.git
cd assessment-system
```

### 2. 编译项目
```bash
mvn clean compile
```

### 3. 运行应用
```bash
mvn spring-boot:run
```

### 4. 访问系统
打开浏览器访问: http://localhost:8080

## 📁 项目结构

```
assessment-system/
├── src/main/java/com/assessment/
│   ├── AssessmentSystemApplication.java    # 主启动类
│   ├── config/
│   │   └── DatabaseConfig.java            # 数据库配置
│   ├── controller/
│   │   └── AssessmentController.java      # REST API控制器
│   ├── dto/
│   │   ├── AssessmentStatistics.java      # 统计数据传输对象
│   │   └── TrendData.java                 # 趋势数据传输对象
│   ├── entity/
│   │   └── Assessment.java                # 评估实体类
│   ├── repository/
│   │   └── AssessmentRepository.java      # 数据访问层
│   └── service/
│       ├── AssessmentService.java         # 评估业务逻辑
│       └── ReportService.java             # 报告生成服务
├── src/main/resources/
│   ├── application.yml                    # 应用配置
│   └── static/
│       └── index.html                     # 前端页面
├── database/                              # 数据库文件目录
├── pom.xml                               # Maven配置
└── README.md                             # 项目说明
```

## 📊 API接口

### 评估管理
- `GET /api/assessments` - 获取所有评估记录
- `POST /api/assessments` - 创建新评估记录
- `PUT /api/assessments/{id}` - 更新评估记录
- `DELETE /api/assessments/{id}` - 删除评估记录

### 数据查询
- `GET /api/assessments/statistics` - 获取统计数据
- `GET /api/assessments/search?keyword={name}` - 搜索学生记录
- `GET /api/assessments/excellent` - 获取优秀学生列表

### 文件操作
- `POST /api/assessments/import` - Excel文件导入
- `GET /api/assessments/report/pdf` - 生成PDF报告

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- **Spring Boot** - 强大的Java应用框架
- **H2 Database** - 轻量级嵌入式数据库
- **Chart.js** - 优秀的图表库
- **Apache POI** - 强大的Office文档处理库
- **iText** - 专业的PDF生成库

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- GitHub Issues: [提交问题](https://github.com/merlette/assessment-system/issues)
- Email: [联系开发者](mailto:merlette@aliyun.com)

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
