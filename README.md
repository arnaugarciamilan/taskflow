# ⚡ TaskFlow

> A professional fullstack task manager inspired by Trello — built with Spring Boot 3 & Angular 17.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red?logo=angular)](https://angular.io)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://mysql.com)
[![JWT](https://img.shields.io/badge/Auth-JWT-orange)](https://jwt.io)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)](https://docker.com)

---

## 📋 Description

TaskFlow is a production-ready fullstack application that allows teams to organize work into **Projects** and **Tasks**, with a visual Kanban board experience. Users can register, create projects, and manage tasks through intuitive status columns (To Do → In Progress → Done).

---

## 🛠️ Technologies

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 3.2 | Core framework |
| Spring Security | 6 | Authentication & authorization |
| Spring Data JPA | 3.2 | ORM / data access |
| JWT (jjwt) | 0.12 | Token-based auth |
| BCrypt | - | Password hashing |
| Flyway | 10 | Database migrations |
| MySQL | 8.0 | Database |
| Lombok | 1.18 | Boilerplate reduction |
| JUnit 5 + Mockito | - | Unit testing |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| Angular | 17 | SPA framework |
| RxJS | 7.8 | Reactive programming |
| Angular Router | 17 | SPA navigation |
| Angular Forms | 17 | Reactive forms |
| SCSS | - | Styling |

### DevOps
| Technology | Purpose |
|---|---|
| Docker | Backend containerization |
| Docker Compose | Multi-container orchestration |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                   FRONTEND (Angular 17)              │
│  ┌──────────┐  ┌───────────┐  ┌────────────────┐   │
│  │ AuthGuard│  │JwtIntercep│  │  Components    │   │
│  │GuestGuard│  │tor        │  │  - Login       │   │
│  └──────────┘  └───────────┘  │  - Register    │   │
│                                │  - Dashboard   │   │
│  ┌─────────────────────────┐  │  - ProjectView │   │
│  │  Services               │  └────────────────┘   │
│  │  AuthService            │                        │
│  │  ProjectService         │                        │
│  │  TaskService            │                        │
│  └─────────────────────────┘                        │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP + JWT
┌──────────────────────▼──────────────────────────────┐
│                 BACKEND (Spring Boot 3)              │
│                                                      │
│  ┌─────────────────────────────────────────────┐   │
│  │  Spring Security + JWT Filter                │   │
│  └───────────────────┬─────────────────────────┘   │
│                       │                              │
│  ┌─────────┐  ┌───────▼───────┐  ┌─────────────┐  │
│  │  REST   │  │  Controllers  │  │  @Valid +   │  │
│  │  API    │  │  /api/auth    │  │  DTOs       │  │
│  │  :8080  │  │  /api/users   │  │             │  │
│  └─────────┘  │  /api/projects│  └─────────────┘  │
│               │  /api/tasks   │                     │
│               └───────┬───────┘                     │
│                       │                              │
│               ┌───────▼───────┐                     │
│               │   Services    │                      │
│               │  AuthService  │                      │
│               │  UserService  │                      │
│               │  ProjectSvc   │                      │
│               │  TaskService  │                      │
│               └───────┬───────┘                     │
│                       │                              │
│               ┌───────▼───────┐                     │
│               │ Repositories  │                      │
│               │  (JPA)        │                      │
│               └───────┬───────┘                     │
│                       │                              │
└───────────────────────┼─────────────────────────────┘
                        │ JDBC
┌───────────────────────▼─────────────────────────────┐
│                    MySQL 8.0                         │
│  ┌──────────┐  ┌───────────┐  ┌───────────────┐    │
│  │  users   │  │ projects  │  │    tasks      │    │
│  └──────────┘  └───────────┘  └───────────────┘    │
│                 Flyway Migrations                    │
└─────────────────────────────────────────────────────┘
```

### 📁 Project Structure

```
taskflow/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/taskflow/
│   │   │   │   ├── config/          # SecurityConfig, CorsConfig
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── dto/             # Request/Response DTOs
│   │   │   │   ├── entity/          # JPA Entities
│   │   │   │   ├── exception/       # Custom exceptions + @ControllerAdvice
│   │   │   │   ├── repository/      # JPA Repositories
│   │   │   │   ├── security/        # JWT Service + Filter
│   │   │   │   └── service/         # Business logic
│   │   │   └── resources/
│   │   │       ├── db/migration/    # Flyway SQL migrations
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/com/taskflow/service/
│   │           ├── TaskServiceTest.java
│   │           └── AuthServiceTest.java
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   └── src/app/
│       ├── core/
│       │   ├── guards/           # AuthGuard, GuestGuard
│       │   ├── interceptors/     # JwtInterceptor
│       │   ├── models/           # TypeScript interfaces
│       │   └── services/         # AuthService, ProjectService, TaskService
│       └── features/
│           ├── auth/             # Login, Register components
│           ├── dashboard/        # Projects dashboard
│           └── projects/         # Project detail + Kanban board
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 🔐 Security

- Passwords encrypted with **BCrypt** (strength 10)
- Stateless authentication via **JWT** (HS256)
- Role-based access control: `ROLE_USER`, `ROLE_ADMIN`
- JWT filter validates every protected request
- CORS configured for Angular dev server

---

## 📦 API Reference

### Authentication
```
POST /api/auth/register    Register new user
POST /api/auth/login       Login + get JWT token
```

### Projects
```
GET    /api/projects        Get current user's projects
GET    /api/projects/{id}   Get project by ID
POST   /api/projects        Create project
PUT    /api/projects/{id}   Update project
DELETE /api/projects/{id}   Delete project
```

### Tasks
```
GET    /api/tasks/project/{projectId}   Get tasks (supports ?status= &priority=)
GET    /api/tasks/{id}                  Get task by ID
POST   /api/tasks                       Create task
PUT    /api/tasks/{id}                  Update task
DELETE /api/tasks/{id}                  Delete task
```

### Users (Admin)
```
GET    /api/users           Get all users (ADMIN only)
GET    /api/users/{id}      Get user by ID
PUT    /api/users/{id}      Update user
DELETE /api/users/{id}      Delete user (ADMIN only)
```

---

## 🚀 Installation & Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0 (or use Docker)

---

### 🐳 Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/taskflow.git
cd taskflow

# Configure environment
cp .env.example .env
# Edit .env with your secrets

# Build and start all services
docker compose up -d --build

# Check logs
docker compose logs -f backend
```

Services will be available at:
- **Backend API**: http://localhost:8080
- **MySQL**: localhost:3306

---

### 🖥️ Option 2: Local Development

**Backend:**
```bash
cd backend

# Make sure MySQL is running on port 3306
# Update src/main/resources/application.properties if needed

mvn clean install
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend

npm install
ng serve
```

Open http://localhost:4200

---

### 🧪 Running Tests

```bash
cd backend

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TaskServiceTest
mvn test -Dtest=AuthServiceTest

# Generate test report
mvn surefire-report:report
```

---

## 🎯 Default Admin Account

After startup, a default admin user is seeded via Flyway:

```
Email:    admin@taskflow.com
Password: admin123
Role:     ADMIN
```

> ⚠️ Change this password immediately in production!

---

## 🌐 Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | *(see .env.example)* | JWT signing key (min 32 chars) |
| `MYSQL_ROOT_PASSWORD` | `rootpassword` | MySQL root password (Docker) |

---

## 🎨 Features

- ✅ JWT Authentication (register + login)
- ✅ Role-based access control (USER / ADMIN)
- ✅ Project CRUD with ownership validation
- ✅ Task CRUD with status + priority
- ✅ Kanban board view (To Do / In Progress / Done)
- ✅ Filter tasks by status and priority
- ✅ Reactive Angular forms with validation
- ✅ Global error handling (@ControllerAdvice)
- ✅ Database migrations with Flyway
- ✅ JWT Interceptor (Angular)
- ✅ Route protection (AuthGuard)
- ✅ Docker + Docker Compose
- ✅ Unit tests (JUnit 5 + Mockito)

---

## 📝 License

MIT License — feel free to use this project as a template for your own fullstack applications.
