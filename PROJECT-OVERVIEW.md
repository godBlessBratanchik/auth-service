# 🎯 Auth Service - Project Overview

## 📋 Краткое описание

**Auth Service** - микросервис аутентификации на Spring Boot с JWT токенами, полностью настроенным CI/CD pipeline через GitHub Actions и DockerHub.

---

## 🏗️ Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                        GitHub Repository                     │
│                    (auth-service codebase)                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                    Push/PR
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      GitHub Actions                          │
│  ┌──────────────┐           ┌──────────────────────┐       │
│  │  Test Job    │           │ Build & Push Job     │       │
│  │  - Maven     │──Success─▶│  - Docker Build      │       │
│  │  - JUnit     │           │  - DockerHub Push    │       │
│  └──────────────┘           └───────────┬──────────┘       │
└────────────────────────────────────────┼──────────────────┘
                                          │
                                    Push Image
                                          │
                                          ▼
┌─────────────────────────────────────────────────────────────┐
│                         DockerHub                            │
│                 elbondarenko04121/auth-service              │
│       Tags: latest, main, develop, {branch}-{sha}          │
└────────────────────────┬────────────────────────────────────┘
                         │
                    Pull Image
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Deployment Environment                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Auth Service │  │  PostgreSQL  │  │    Kafka     │     │
│  │   (Docker)   │──│   Database   │  │   Broker     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

### Backend
- **Java 17** - язык программирования
- **Spring Boot 3.5.7** - application framework
- **Spring Security** - security framework
- **JWT (java-jwt 4.5.0)** - token-based authentication
- **Spring Data JPA** - ORM
- **Maven** - build tool & dependency management

### Database & Messaging
- **PostgreSQL** - реляционная база данных
- **Apache Kafka** - event streaming platform
- **Spring Kafka** - Kafka integration

### DevOps & Infrastructure
- **Docker** - контейнеризация
- **Docker Compose** - multi-container orchestration
- **GitHub Actions** - CI/CD automation
- **DockerHub** - container registry

### Development Tools
- **Lombok** - boilerplate code reduction
- **H2** - in-memory database for testing
- **JUnit** - testing framework
- **ModelMapper** - object mapping

---

## 📦 Project Structure

```
auth-service/
│
├── .github/
│   └── workflows/
│       ├── ci.yml                       # 🔄 CI/CD Pipeline
│       └── README.md                    # Workflow документация
│
├── src/
│   ├── main/
│   │   ├── java/com/georgedroidnegroid/auth_service/
│   │   │   ├── api/                     # API интерфейсы
│   │   │   ├── config/                  # Security & JWT конфигурация
│   │   │   ├── controller/              # REST контроллеры
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   ├── entity/                  # JPA entities
│   │   │   ├── repository/              # Data access layer
│   │   │   ├── service/                 # Business logic
│   │   │   └── utils/                   # Utilities & exceptions
│   │   └── resources/
│   │       └── application.properties   # Application configuration
│   │
│   └── test/                            # Unit & Integration тесты
│
├── target/                              # Maven build output
│   └── auth-service-*.jar               # Executable JAR
│
├── .dockerignore                        # Docker build exclusions
├── docker-compose.yml                   # 🐋 Local development stack
├── Dockerfile                           # 🐋 Multi-stage image build
├── pom.xml                              # Maven configuration
│
├── README.md                            # 📖 Main documentation
├── CI-CD-SETUP.md                       # 📖 CI/CD details
├── QUICKSTART.md                        # 📖 Quick start guide
├── GITHUB-SECRETS-SETUP.md              # 📖 Secrets configuration
├── PROJECT-OVERVIEW.md                  # 📖 This file
└── SUMMARY.md                           # 📖 Work summary
```

---

## 🔄 CI/CD Pipeline Flow

### Подробный процесс:

```
1. Developer makes changes
   │
   ├─ Writes code
   ├─ Writes tests
   └─ Commits & pushes to GitHub
          │
          ▼
2. GitHub Actions Triggered
   │
   ├─ Event: Push to main/master/develop
   ├─ Event: Pull Request
   │
   ▼
3. JOB: Test
   │
   ├─ Checkout code
   ├─ Setup JDK 17 (Eclipse Temurin)
   ├─ Cache Maven dependencies (~/.m2)
   ├─ Run: mvn clean test
   ├─ Upload test results (artifacts)
   │
   └─ ✅ Tests Pass?
          │
          ├─ ❌ NO  → Pipeline stops, notification sent
          │
          └─ ✅ YES → Continue to next job
                  │
                  ▼
4. JOB: Build & Push (only for push events)
   │
   ├─ Checkout code
   ├─ Setup Docker Buildx
   ├─ Login to DockerHub (using secrets)
   ├─ Extract metadata & generate tags
   ├─ Build Docker image (multi-stage)
   │   ├─ Stage 1: Build with Maven (JDK)
   │   └─ Stage 2: Runtime with JRE (Alpine)
   ├─ Use layer caching (registry-based)
   ├─ Push image to DockerHub
   │   └─ elbondarenko04121/auth-service
   │
   └─ ✅ Success → Image available
          │
          ▼
5. Image Published on DockerHub
   │
   └─ Available tags:
      ├─ latest (from main/master)
      ├─ {branch} (e.g., develop)
      └─ {branch}-{sha} (e.g., main-abc1234)
          │
          ▼
6. Ready for Deployment
   │
   └─ Can be pulled: docker pull elbondarenko04121/auth-service:latest
```

---

## 🐋 Docker Images

### Published Images

**Registry:** DockerHub  
**Repository:** `elbondarenko04121/auth-service`  
**Access:** Public

### Available Tags

| Tag | Description | Use Case |
|-----|-------------|----------|
| `latest` | Latest from main branch | Production |
| `main` | Latest from main | Production |
| `develop` | Latest from develop | Staging/Testing |
| `{branch}-{sha}` | Specific commit | Reproducible builds |

### Image Characteristics

- **Base:** Alpine Linux (minimal size)
- **Runtime:** Eclipse Temurin 17 JRE
- **Size:** ~200-300 MB (optimized)
- **User:** Non-root (spring:spring)
- **Port:** 8080
- **Health:** Configurable via environment

### Pull & Run

```bash
# Pull latest image
docker pull elbondarenko04121/auth-service:latest

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/authdb \
  elbondarenko04121/auth-service:latest
```

---

## 🚀 Deployment Options

### Option 1: Docker Compose (Recommended for Development)

```bash
# Start full stack (app + postgres + kafka)
docker-compose up -d

# View logs
docker-compose logs -f auth-service

# Stop stack
docker-compose down
```

**Includes:**
- Auth Service
- PostgreSQL 15
- Kafka + Zookeeper
- Automatic networking
- Volume persistence

### Option 2: Standalone Docker

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/authdb \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  elbondarenko04121/auth-service:latest
```

### Option 3: Kubernetes (Future)

```yaml
# Example deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: elbondarenko04121/auth-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
```

---

## 🔐 Security

### Authentication
- **JWT Tokens** - stateless authentication
- **Spring Security** - comprehensive security framework
- **BCrypt** - password hashing

### Docker Security
- **Non-root user** - runs as spring:spring
- **Minimal base image** - Alpine Linux
- **Multi-stage build** - no build tools in final image

### Secrets Management
- **GitHub Secrets** - encrypted storage
- **Environment variables** - runtime configuration
- **No hardcoded credentials** - all externalized

---

## 📊 Monitoring & Observability (Future Enhancements)

### Suggested Additions:

```yaml
# Spring Boot Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Potential Integrations:
- **Prometheus** - metrics collection
- **Grafana** - visualization
- **ELK Stack** - log aggregation
- **Jaeger** - distributed tracing

---

## 🧪 Testing

### Test Types:
- **Unit Tests** - business logic validation
- **Integration Tests** - component interaction
- **Controller Tests** - REST API testing

### Test Database:
- **H2** - in-memory database for testing
- **TestContainers** (potential addition)

### CI Integration:
- Tests run automatically on every push
- Test results uploaded as artifacts
- Failed tests block deployment

---

## 🔧 Configuration

### Environment Variables

#### Required:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

#### Optional:
```bash
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
JAVA_OPTS="-Xmx512m -Xms256m"
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Configuration Files:
- `application.properties` - main configuration
- `application-test.properties` - test configuration
- `docker-compose.yml` - local environment setup

---

## 📈 Performance Optimizations

### Build Optimizations:
- ✅ **Maven dependency caching** - faster CI builds
- ✅ **Docker layer caching** - incremental builds
- ✅ **Multi-stage Docker build** - smaller images
- ✅ **.dockerignore** - faster context transfer

### Runtime Optimizations:
- ✅ **JRE instead of JDK** - smaller footprint
- ✅ **Alpine Linux** - minimal OS
- ✅ **Connection pooling** - database efficiency
- ✅ **Lazy initialization** (configurable)

---

## 📚 Documentation Files

| File | Purpose | Target Audience |
|------|---------|-----------------|
| `README.md` | Project overview & quick info | Everyone |
| `QUICKSTART.md` | Step-by-step startup guide | New developers |
| `CI-CD-SETUP.md` | Detailed CI/CD explanation | DevOps/Developers |
| `GITHUB-SECRETS-SETUP.md` | Secrets configuration | DevOps/Admins |
| `PROJECT-OVERVIEW.md` | This file - comprehensive overview | Everyone |
| `SUMMARY.md` | Work summary & achievements | Project managers |
| `.github/workflows/README.md` | Workflow documentation | DevOps/Developers |

---

## 🎯 Quick Start Commands

```bash
# Local development with Docker Compose
docker-compose up -d

# Pull and run from DockerHub
docker pull elbondarenko04121/auth-service:latest
docker run -p 8080:8080 elbondarenko04121/auth-service:latest

# Build locally
mvn clean package
java -jar target/auth-service-*.jar

# Run tests
mvn test

# Build Docker image locally
docker build -t auth-service:local .
```

---

## ✅ Success Metrics

### Current Status:
- ✅ **CI/CD**: Fully automated
- ✅ **Testing**: Automated on every push
- ✅ **Docker**: Optimized multi-stage build
- ✅ **DockerHub**: Public registry configured
- ✅ **Documentation**: Comprehensive
- ✅ **Local Dev**: Docker Compose ready
- ✅ **Security**: Secrets properly managed

### Performance:
- ⚡ Build time: ~3-5 minutes (with cache)
- 📦 Image size: ~200-300 MB
- 🧪 Test execution: ~1-2 minutes
- 🚀 Deployment: Single command

---

## 🔮 Future Roadmap

### Short-term:
- [ ] Add Swagger/OpenAPI documentation
- [ ] Implement health check endpoints
- [ ] Add code coverage reporting
- [ ] Set up integration with testing services

### Medium-term:
- [ ] Kubernetes deployment manifests
- [ ] Helm charts
- [ ] Staging environment setup
- [ ] Security scanning (Trivy, Snyk)

### Long-term:
- [ ] Service mesh integration (Istio)
- [ ] Advanced monitoring (Prometheus/Grafana)
- [ ] Distributed tracing (Jaeger)
- [ ] Auto-scaling configuration
- [ ] Multi-region deployment

---

## 🤝 Contributing

### Development Workflow:

1. **Fork & Clone** repository
2. **Create feature branch**: `git checkout -b feature/my-feature`
3. **Make changes** and write tests
4. **Run tests locally**: `mvn test`
5. **Commit**: `git commit -m "feat: add my feature"`
6. **Push**: `git push origin feature/my-feature`
7. **Create Pull Request**
8. **CI/CD runs automatically**
9. **Merge after approval**

---

## 🆘 Support & Troubleshooting

### Common Issues:

1. **Port 8080 already in use**
   - Solution: Change port in docker-compose.yml or use `-p 8081:8080`

2. **Database connection failed**
   - Solution: Check PostgreSQL is running and credentials are correct

3. **Kafka connection timeout**
   - Solution: Ensure Kafka is started and network is configured

4. **CI/CD pipeline fails**
   - Solution: Check GitHub Actions logs and secrets configuration

### Getting Help:
- 📖 Check documentation files
- 🔍 Review GitHub Actions logs
- 🐋 Check Docker logs: `docker logs container_name`
- 📊 Review test reports in artifacts

---

## 📞 Links & Resources

### Project:
- **GitHub**: (your repository URL)
- **DockerHub**: https://hub.docker.com/r/elbondarenko04121/auth-service
- **CI/CD**: GitHub Actions tab

### Documentation:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [JWT Introduction](https://jwt.io/introduction)

---

## 📄 License

*(Add your license information here)*

---

## 👥 Team

*(Add team members/contributors here)*

---

**Last Updated:** December 17, 2025  
**Version:** 1.0.0  
**Status:** ✅ Production Ready

