# Elder Care Management System - Build Guide

## Prerequisites

### Backend (Spring Boot)
- **JDK**: Java 17 or higher (currently using Java 25)
- **Gradle**: 7.6 or higher
- **PostgreSQL**: 12 or higher
- **Ollama**: For AI integration (optional for development)

### Frontend (Web Dashboard)
- **Node.js**: 14.0 or higher
- **npm**: 6.0 or higher

### Mobile (Cordova)
- **Node.js**: 14.0 or higher
- **Cordova**: 12.0.0
- **Android SDK**: For Android builds (optional)
- **Xcode**: For iOS builds (macOS only, optional)

## Backend Build

### 1. Install Dependencies
```bash
# Navigate to backend directory
cd backend

# Install Gradle (if not installed)
# On Windows: Download from https://gradle.org/install/
# On macOS: brew install gradle
# On Linux: apt-get install gradle (or equivalent)

# Verify Gradle installation
gradle -v
```

### 2. Build the Project
```bash
# Build without running tests
gradle build -x test

# Build and run all tests
gradle build

# Run just the application
gradle bootRun

# Clean build
gradle clean build -x test
```

### 3. Database Setup

#### PostgreSQL Configuration
1. Start PostgreSQL server
2. Create database:
```sql
CREATE DATABASE eldercare_db;
```

3. Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/eldercare_db
    username: postgres
    password: your_password
```

#### Ollama Setup (for AI features)
```bash
# Download Ollama from https://ollama.ai/
# Start Ollama
ollama serve

# In another terminal, pull the Mistral model
ollama pull mistral
```

### 4. Run the Backend
```bash
# Development mode
gradle bootRun

# or run the JAR directly after building
java -jar build/libs/eldercare-app-1.0.0.jar
```

The backend will be available at: `http://localhost:8080`

## Frontend Build

### Web Dashboard
```bash
cd web

# No build required for vanilla JavaScript
# Simply open index.html in a web browser or serve with a local server

# Using Python's built-in server (Python 3)
python -m http.server 8000

# Using Node.js http-server
npm install -g http-server
http-server -p 8000
```

The web dashboard will be available at: `http://localhost:8000`

## Mobile Build

### Cordova Setup
```bash
# Navigate to mobile directory
cd mobile

# Install dependencies
npm install

# Add platforms
cordova platform add android
cordova platform add ios

# List installed platforms
cordova platform list
```

### Build for Different Platforms

#### Android Build
```bash
# Development build
cordova build android

# Release build
cordova build android --release

# Run on device/emulator
cordova run android
```

#### iOS Build (macOS only)
```bash
# Development build
cordova build ios

# Run on simulator
cordova emulate ios

# Run on device
cordova run ios
```

#### Browser Testing
```bash
# Quick testing in browser
cordova platform add browser
cordova run browser
```

## Project Structure

```
elder-care-app/
├── backend/                          # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/eldercare/
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controller/      # REST API controllers
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── repository/      # Data access layer
│   │   │   │   ├── service/         # Business logic
│   │   │   │   └── ElderCareApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml  # Configuration
│   │   │       └── schema.sql       # Database schema
│   │   └── test/
│   └── build.gradle                 # Gradle build config
│
├── web/                             # Web dashboard (frontend)
│   ├── index.html                   # Main page
│   ├── login.html                   # Login page
│   ├── css/
│   │   └── style.css               # Styles
│   └── js/
│       ├── main.js                 # Main logic
│       ├── api.js                  # API client
│       ├── chart.js                # Data visualization
│       └── dashboard.js            # Dashboard logic
│
├── mobile/                          # Cordova mobile app
│   ├── config.xml                  # App configuration
│   ├── package.json                # npm dependencies
│   └── www/                        # Web assets
│       ├── index.html
│       ├── css/
│       └── js/
│
└── README.md                        # Project documentation
```

## Development Configuration

### application.yml (Backend)
Key configuration properties:

```yaml
spring:
  application:
    name: eldercare-app
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/eldercare_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

spring-ai:
  ollama:
    base-url: http://localhost:11434

jwt:
  secret: your-secret-key-change-in-production
  expiration: 86400000    # 24 hours
```

### Environment Variables
Create `.env` file in project root (optional):

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/eldercare_db
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
OLLAMA_URL=http://localhost:11434
JWT_SECRET=your-secret-key
PORT=8080
```

## Running All Services

### Terminal 1: PostgreSQL
```bash
# Ensure PostgreSQL is running (platform-specific)
# Windows: Services > PostgreSQL > Start
# Linux: sudo service postgresql start
# macOS: brew services start postgresql
```

### Terminal 2: Ollama
```bash
ollama serve
```

### Terminal 3: Backend
```bash
cd backend
gradle bootRun
# Backend available at http://localhost:8080
```

### Terminal 4: Frontend
```bash
cd web
python -m http.server 8000
# Frontend available at http://localhost:8000
```

### Terminal 5: Mobile (Optional)
```bash
cd mobile
cordova run browser
```

## Testing

### Backend Tests
```bash
cd backend

# Run all tests
gradle test

# Run specific test class
gradle test --tests com.eldercare.controller.GuardianControllerTest

# Run with coverage
gradle test jacocoTestReport
```

### API Testing
- **Postman**: Import endpoints from API documentation
- **cURL**: Use provided curl examples in API docs
- **Thunder Client**: VS Code extension for API testing

## Troubleshooting

### Gradle Build Issues
```bash
# Clear gradle cache
gradle clean

# Rebuild with verbose output
gradle build --stacktrace

# Refresh dependencies
gradle build --refresh-dependencies
```

### PostgreSQL Connection Issues
```bash
# Check if PostgreSQL is running
psql --version

# Connect to test connection
psql -h localhost -U postgres -d eldercare_db
```

### Port Already in Use
```bash
# Linux/macOS: Find and kill process on port 8080
lsof -i :8080
kill -9 <PID>

# Windows: Find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## Production Build

### Backend Production Build
```bash
cd backend

# Build optimized JAR
gradle bootJar -Pproduction

# Build Docker image (requires Dockerfile)
docker build -t eldercare:1.0.0 .

# Run Docker container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://db:5432/eldercare_db \
  -e JWT_SECRET=your-production-secret \
  eldercare:1.0.0
```

### Frontend Production Build
```bash
cd web

# Minify CSS and JS (if using build tools)
npm install -g minify
minify js/*.js -o js/
minify css/*.css -o css/
```

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Gradle Build Tool](https://gradle.org/)
- [Cordova Documentation](https://cordova.apache.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Ollama Documentation](https://github.com/ollama/ollama)

---

**Last Updated**: 2026-04-30
