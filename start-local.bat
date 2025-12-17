@echo off
REM Quick start script for Windows

echo ====================================
echo Starting Auth Service (Docker Compose)
echo ====================================
echo.

REM Stop any running containers
echo [1/5] Stopping existing containers...
docker-compose down

REM Build auth-service image
echo.
echo [2/5] Building auth-service image...
docker-compose build auth-service

REM Start PostgreSQL first
echo.
echo [3/5] Starting PostgreSQL...
docker-compose up -d postgres

REM Wait for PostgreSQL to be ready
echo.
echo [4/5] Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak

REM Start all services
echo.
echo [5/5] Starting all services...
docker-compose up -d

echo.
echo ====================================
echo Services started!
echo ====================================
echo.
echo Auth Service: http://localhost:8080
echo PostgreSQL: localhost:5432
echo Kafka: localhost:9092
echo.
echo Check status: docker-compose ps
echo View logs: docker-compose logs -f auth-service
echo.
pause

