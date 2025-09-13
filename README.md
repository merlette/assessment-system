# Student Assessment Management System 📊

🌟 A comprehensive student assessment management system built with Spring Boot, featuring a modern web interface and powerful data analytics capabilities.

[中文版 README](README_CN.md) | English README

## 🚀 Features

- **📈 Data Statistics**: Real-time statistics for discipline compliance, skill completion rates, task completion rates, and other key metrics
- **📝 Assessment Management**: Add, edit, and delete student assessment records
- **📊 Trend Analysis**: Visual charts showing trends in various metrics
- **📁 Bulk Import**: Support for Excel file bulk import of assessment data
- **📄 Report Export**: Generate professional PDF assessment reports
- **🔍 Smart Search**: Quick search for specific student assessment records
- **🌙 Dark Mode**: Support for light/dark theme switching

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot
- **Database**: H2 Database
- **Frontend**: Native HTML5 + CSS3 + JavaScript
- **Charts**: Chart.js
- **Document Processing**: Apache POI (Excel) + iText (PDF)
- **Build Tool**: Maven

## 🚀 Quick Start

### 1. Clone the project
```bash
git clone https://github.com/merlette/assessment-system.git
cd assessment-system
```

### 2. Build the project
```bash
mvn clean compile
```

### 3. Run the application
```bash
mvn spring-boot:run
```

### 4. Access the system
Open your browser and visit: http://localhost:8080

## 📁 Project Structure

```
assessment-system/
├── src/main/java/com/assessment/
│   ├── AssessmentSystemApplication.java    # Main startup class
│   ├── config/
│   │   └── DatabaseConfig.java            # Database configuration
│   ├── controller/
│   │   └── AssessmentController.java      # REST API controller
│   ├── dto/
│   │   ├── AssessmentStatistics.java      # Statistics data transfer object
│   │   └── TrendData.java                 # Trend data transfer object
│   ├── entity/
│   │   └── Assessment.java                # Assessment entity class
│   ├── repository/
│   │   └── AssessmentRepository.java      # Data access layer
│   └── service/
│       ├── AssessmentService.java         # Assessment business logic
│       └── ReportService.java             # Report generation service
├── src/main/resources/
│   ├── application.yml                    # Application configuration
│   └── static/
│       └── index.html                     # Frontend page
├── database/                              # Database file directory
├── pom.xml                               # Maven configuration
└── README.md                             # Project documentation
```

## 📊 API Endpoints

### Assessment Management
- `GET /api/assessments` - Get all assessment records
- `POST /api/assessments` - Create new assessment record
- `PUT /api/assessments/{id}` - Update assessment record
- `DELETE /api/assessments/{id}` - Delete assessment record

### Data Query
- `GET /api/assessments/statistics` - Get statistical data
- `GET /api/assessments/search?keyword={name}` - Search student records
- `GET /api/assessments/excellent` - Get excellent students list

### File Operations
- `POST /api/assessments/import` - Excel file import
- `GET /api/assessments/report/pdf` - Generate PDF report

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** - Powerful Java application framework
- **H2 Database** - Lightweight embedded database
- **Chart.js** - Excellent charting library
- **Apache POI** - Powerful Office document processing library
- **iText** - Professional PDF generation library

## 📞 Contact

For questions or suggestions, please contact us through:
- GitHub Issues: [Submit an issue](https://github.com/merlette/assessment-system/issues)
- Email: [Contact developer](mailto:merlette@aliyun.com)

---

⭐ If this project helps you, please give it a star!
