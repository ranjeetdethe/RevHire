# RevHire - Job Portal Application 🚀

A modern, full-stack job portal application built with **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, and **MySQL**. RevHire connects job seekers with employers, providing a seamless platform for job posting, searching, and application management.

## ✨ Features

### For Job Seekers
- 🔐 **Secure Registration & Login** - Create account with email and password (BCrypt encrypted)
- 🔍 **Job Search** - Browse and search jobs by keywords, location, and requirements
- 📄 **Resume Management** - Upload and manage your resume (PDF support)
- 📊 **Application Tracking** - View all your job applications and their statuses
- 👤 **Profile Management** - Update your professional information

### For Employers
- 🏢 **Company Profile** - Create and manage your company profile
- ✍️ **Job Posting** - Post new job openings with detailed descriptions
- 👥 **Application Management** - Review and manage candidate applications
- 📈 **Dashboard** - View statistics and manage all your job postings
- ✏️ **Job Management** - Edit or close job postings

### Security & Authentication
- 🔒 **Spring Security Integration** - Enterprise-grade authentication and authorization
- 🛡️ **BCrypt Password Hashing** - Secure password storage
- 👮 **Role-Based Access Control** - SEEKER and EMPLOYER roles
- 🔑 **Session Management** - Secure user sessions
- 🚪 **Password Recovery** - Security question-based password reset

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data access and ORM
- **Spring Security 6.2.0** - Authentication and authorization
- **Hibernate 6.3.1** - ORM implementation
- **MySQL 8** - Relational database
- **Jakarta Validation** - Bean validation

### Frontend
- **Thymeleaf 3.1.2** - Server-side template engine
- **Thymeleaf Spring Security** - Security dialect
- **Vanilla CSS** - Styling
- **Font Awesome** - Icons
- **Google Fonts** - Typography

### Development Tools
- **Maven** - Build automation
- **Lombok** - Boilerplate code reduction
- **HikariCP** - Connection pooling
- **SLF4J & Logback** - Logging

## 📋 Prerequisites

- **JDK 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Docker & Docker Compose** (optional, for containerized deployment)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd RevHire
```

### 2. Set Up Database
```sql
-- Create database
CREATE DATABASE revhire_db;

-- MySQL should be running on port 3307 (or update application.properties)
```

### 3. Configure Application
Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/revhire_db
spring.datasource.username=root
spring.datasource.password=Ranjeet@123
```

### 4. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Alternative: Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app
```

## 📂 Project Structure

```
RevHire/
├── src/
│   ├── main/
│   │   ├── java/com/revhire/
│   │   │   ├── controller/       # REST & MVC Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Global exception handling
│   │   │   ├── model/            # JPA Entities
│   │   │   ├── repository/       # Spring Data repositories
│   │   │   ├── security/         # Security configuration
│   │   │   ├── service/          # Business logic
│   │   │   │   └── impl/         # Service implementations
│   │   │   └── RevHireApplication.java
│   │   └── resources/
│   │       ├── static/           # CSS, JS, images
│   │       ├── templates/        # Thymeleaf templates
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/                     # Unit & Integration tests
├── docker-compose.yml
├── Dockerfile
├── DEPLOYMENT.md                  # Deployment guide
├── pom.xml
└── README.md
```

## 🗄️ Database Schema

### Core Entities
- **users** - User accounts (SEEKER/EMPLOYER)
- **job_seekers** - Job seeker profiles
- **employers** - Employer profiles
- **jobs** - Job postings
- **applications** - Job applications
- **resumes** - Resume files

### Relationships
- User (1) ↔ (1) JobSeeker/Employer
- Employer (1) ↔ (N) Jobs
- Job (1) ↔ (N) Applications
- JobSeeker (1) ↔ (N) Applications
- JobSeeker (1) ↔ (1) Resume

## 🔌 API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /register` - Registration form
- `POST /register` - Process registration
- `GET /login` - Login page
- `POST /login` - Process login (Spring Security)
- `GET /jobs` - Browse jobs
- `GET /jobs/{id}` - View job details

### Job Seeker Endpoints (Authenticated - SEEKER role)
- `GET /seeker/dashboard` - Job seeker dashboard
- `POST /applications/apply` - Apply for a job
- `GET /applications/my-applications` - View my applications
- `GET /seeker/resume/upload` - Upload resume

### Employer Endpoints (Authenticated - EMPLOYER role)
- `GET /employer/dashboard` - Employer dashboard
- `GET /employer/jobs/new` - New job form
- `POST /employer/jobs` - Create job posting
- `GET /employer/jobs/{id}/applications` - View applications

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn clean verify
```

## 🏗️ Architecture

The application follows a **layered architecture**:

1. **Presentation Layer** - Controllers & Thymeleaf templates
2. **Service Layer** - Business logic
3. **Data Access Layer** - Repositories & JPA Entities
4. **Security Layer** - Spring Security configuration

### Design Patterns Used
- **Repository Pattern** - Data access abstraction
- **Service Pattern** - Business logic encapsulation
- **DTO Pattern** - Data transfer between layers
- **MVC Pattern** - Web layer structure
- **Dependency Injection** - Spring's IoC container

## 🔐 Security Features

- **Password Encryption** - BCrypt with cost factor 10
- **CSRF Protection** - Enabled by default
- **Session Management** - HTTP session-based
- **Role-Based Access** - Method-level security
- **SQL Injection Prevention** - Parameterized queries (JPA)
- **XSS Protection** - Thymeleaf auto-escaping

## 📝 Configuration Profiles

### Development (`application.properties`)
- Show SQL queries
- Hibernate DDL auto-update
- Thymeleaf cache disabled

### Production (`application-prod.properties`)
- SQL logging disabled
- Hibernate validation only
- Thymeleaf cache enabled
- Environment variable configuration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java conventions
- Use meaningful variable names
- Add JavaDoc for public methods
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Ranjeet Dethe** - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Thymeleaf for the template engine
- MySQL for the database
- Font Awesome for icons

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Email: <your-email@example.com>

## 🗺️ Roadmap

### Version 1.1 (Planned)
- [ ] Email notifications for applications
- [ ] Advanced search filters
- [ ] Resume parsing with AI
- [ ] Real-time chat between employers and seekers
- [ ] Mobile responsive design improvements

### Version 2.0 (Future)
- [ ] REST API for mobile app
- [ ] OAuth2 login (Google, LinkedIn)
- [ ] Payment integration for premium features
- [ ] Analytics dashboard
- [ ] Multi-language support

---

**Made with ❤️ using Spring Boot**

For deployment instructions, see [DEPLOYMENT.md](DEPLOYMENT.md)
