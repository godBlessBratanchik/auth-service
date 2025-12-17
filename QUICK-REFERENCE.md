# ⚡ Быстрая шпаргалка по командам

## 🚀 Запуск проекта

### Самый простой способ (рекомендуется)
```bash
start-local.bat
```

### Ручной запуск
```bash
# 1. Остановить если запущено
docker-compose down

# 2. Собрать образ auth-service
docker-compose build auth-service

# 3. Запустить PostgreSQL
docker-compose up -d postgres

# 4. Подождать 10 секунд
timeout /t 10 /nobreak

# 5. Запустить все сервисы
docker-compose up -d
```

---

## 🔍 Проверка состояния

```bash
# Посмотреть статус всех контейнеров
docker-compose ps

# Посмотреть логи auth-service
docker-compose logs auth-service

# Следить за логами в реальном времени
docker-compose logs -f auth-service

# Посмотреть логи PostgreSQL
docker-compose logs postgres

# Посмотреть логи Kafka
docker-compose logs kafka
```

---

## 🛑 Остановка

```bash
# Остановить все контейнеры (данные сохраняются)
docker-compose stop

# Остановить и удалить контейнеры (данные сохраняются)
docker-compose down

# Остановить и удалить контейнеры + volumes (ВСЕ ДАННЫЕ УДАЛЯЮТСЯ!)
docker-compose down -v
```

---

## 🗄️ Работа с базой данных

```bash
# Подключиться к PostgreSQL
docker exec -it auth-postgres psql -U postgres -d authdb

# Внутри PostgreSQL:
\dt              # Показать все таблицы
\d users         # Описание таблицы users
SELECT * FROM users;  # Выбрать всех пользователей
\q               # Выйти
```

---

## 🧪 Тестирование

```bash
# Запустить тесты локально
mvn clean test

# Запустить тесты с детальным выводом
mvn test -X

# Запустить конкретный тест
mvn test -Dtest=RegisterControllerTest

# Проверить что сервис отвечает
curl http://localhost:8080

# PowerShell:
Invoke-WebRequest http://localhost:8080
```

---

## 📦 Git команды для CI/CD

```bash
# Проверить статус
git status

# Посмотреть какая ветка
git branch

# Добавить все изменения
git add .

# Закоммитить
git commit -m "your message"

# Отправить на GitHub (запускает CI/CD)
git push origin main
```

---

## 🐋 Docker команды

```bash
# Посмотреть все образы
docker images

# Посмотреть запущенные контейнеры
docker ps

# Посмотреть все контейнеры (включая остановленные)
docker ps -a

# Удалить образ
docker rmi elbondarenko04121/auth-service:latest

# Скачать образ с DockerHub
docker pull elbondarenko04121/auth-service:latest

# Посмотреть использование ресурсов
docker stats

# Зайти внутрь контейнера
docker exec -it auth-service sh

# Удалить все неиспользуемые ресурсы
docker system prune -a
```

---

## 🔄 CI/CD проверка

```bash
# После push проверьте:
# 1. GitHub Actions
#    https://github.com/ваш-username/auth-service/actions

# 2. DockerHub
#    https://hub.docker.com/r/elbondarenko04121/auth-service

# 3. Скачайте образ
docker pull elbondarenko04121/auth-service:latest
```

---

## 🐛 Быстрое решение проблем

### Ошибка подключения к БД
```bash
docker-compose down
docker-compose up -d postgres
timeout /t 10 /nobreak
docker-compose up -d
```

### Порт 8080 занят
```bash
# Найти что использует порт
netstat -ano | findstr :8080

# Убить процесс (замените PID)
taskkill /PID <номер> /F
```

### Контейнер не запускается
```bash
# Посмотреть детальные логи
docker logs auth-service --tail 100

# Пересоздать все
docker-compose down -v
docker-compose build auth-service
docker-compose up -d
```

### Очистить все Docker данные
```bash
# ВНИМАНИЕ: Удалит ВСЕ Docker данные!
docker-compose down -v
docker system prune -a --volumes
```

---

## 📊 Полезные URL

```
Auth Service:    http://localhost:8080
PostgreSQL:      localhost:5432
Kafka:           localhost:9092

GitHub Actions:  https://github.com/ваш-username/auth-service/actions
DockerHub:       https://hub.docker.com/r/elbondarenko04121/auth-service
```

---

## 🔐 Переменные окружения

### Для локального запуска (PowerShell)
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/authdb"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
```

### Для локального запуска (CMD)
```cmd
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
set SPRING_DATASOURCE_USERNAME=postgres
set SPRING_DATASOURCE_PASSWORD=postgres
set SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

---

## 📁 Важные файлы

| Файл | Назначение |
|------|-----------|
| `docker-compose.yml` | Конфигурация всех сервисов |
| `.github/workflows/ci.yml` | CI/CD pipeline |
| `Dockerfile` | Сборка Docker образа |
| `pom.xml` | Maven зависимости |
| `src/main/resources/application.properties` | Основная конфигурация |
| `src/main/resources/application-local.properties` | Локальная конфигурация |

---

## 📚 Документация

| Файл | Когда читать |
|------|-------------|
| `TESTING-GUIDE.md` | Полное руководство по тестированию |
| `TROUBLESHOOTING-DATABASE.md` | Проблемы с базой данных |
| `QUICKSTART.md` | Быстрый старт проекта |
| `CI-CD-SETUP.md` | Детали CI/CD |
| `GITHUB-SECRETS-SETUP.md` | Настройка секретов |
| `SUMMARY.md` | Общий обзор работы |
| `QUICK-REFERENCE.md` | Эта шпаргалка |

---

## ⚡ Типичные сценарии

### Первый запуск
```bash
start-local.bat
docker-compose logs -f auth-service
```

### Разработка с изменениями кода
```bash
docker-compose down
docker-compose build auth-service
docker-compose up -d
docker-compose logs -f auth-service
```

### Деплой новой версии
```bash
git add .
git commit -m "feat: new feature"
git push origin main
# Дождитесь CI/CD (~5 минут)
# Образ автоматически появится на DockerHub
```

### Использование новой версии с DockerHub
```bash
docker-compose down
docker-compose pull
docker-compose up -d
```

### Полная очистка и перезапуск
```bash
docker-compose down -v
docker system prune -a
start-local.bat
```

---

*Сохраните эту шпаргалку - она содержит все основные команды!* 🚀

