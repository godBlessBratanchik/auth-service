# 🔧 Решение проблем с подключением к базе данных

## ❌ Ошибка: "unable to obtain isolated JDBC connection"

### Причина
Spring Boot не может подключиться к PostgreSQL базе данных.

---

## 🎯 Решения в зависимости от способа запуска

### ✅ Вариант 1: Запуск через Docker Compose (РЕКОМЕНДУЕТСЯ)

Это самый простой способ - все сервисы запускаются автоматически.

```bash
# Остановить все контейнеры если они запущены
docker-compose down

# Удалить старые volumes (опционально, если нужна чистая БД)
docker-compose down -v

# Запустить все сервисы
docker-compose up -d

# Проверить что все контейнеры запущены
docker-compose ps

# Посмотреть логи auth-service
docker-compose logs -f auth-service
```

**Что происходит:**
- PostgreSQL запускается в контейнере `postgres`
- Auth Service подключается к `postgres:5432`
- Переменные окружения в `docker-compose.yml` переопределяют настройки

**Проверка подключения к БД:**
```bash
# Подключиться к PostgreSQL контейнеру
docker exec -it auth-postgres psql -U postgres -d authdb

# Внутри PostgreSQL:
\dt    # Посмотреть таблицы
\q     # Выйти
```

---

### ✅ Вариант 2: Запуск локально с Docker PostgreSQL

Если хотите запустить приложение локально, но использовать PostgreSQL из Docker:

**Шаг 1:** Запустите только PostgreSQL
```bash
# Запустить только postgres и зависимые сервисы
docker-compose up -d postgres

# Проверить что PostgreSQL запущен
docker-compose ps postgres
```

**Шаг 2:** Установите переменные окружения
```bash
# Windows PowerShell:
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/authdb"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"

# Windows CMD:
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
set SPRING_DATASOURCE_USERNAME=postgres
set SPRING_DATASOURCE_PASSWORD=postgres

# Linux/Mac:
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/authdb"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="postgres"
```

**Шаг 3:** Запустите приложение
```bash
mvn spring-boot:run
```

---

### ✅ Вариант 3: Использование удаленной БД (текущая конфигурация)

Если хотите использовать удаленную БД на `95.165.27.159:5433`:

**Проверка 1:** Доступна ли база данных?
```bash
# Windows (PowerShell):
Test-NetConnection -ComputerName 95.165.27.159 -Port 5433

# Linux/Mac:
nc -zv 95.165.27.159 5433
# или
telnet 95.165.27.159 5433
```

**Проверка 2:** Правильные ли credentials?
```bash
# Попробовать подключиться через psql (если установлен):
psql -h 95.165.27.159 -p 5433 -U postgres -d auth_db
# Пароль: 123
```

**Если удаленная БД недоступна:**
- ❌ Сервер выключен
- ❌ Firewall блокирует порт 5433
- ❌ PostgreSQL не слушает на внешнем интерфейсе
- ❌ Неверные credentials

**Решение:** Используйте Docker Compose (Вариант 1) для локальной разработки

---

## 🔍 Диагностика проблемы

### Проверка 1: Какой способ запуска вы используете?

```bash
# Docker Compose?
docker-compose ps

# Локальный запуск?
ps aux | grep java    # Linux/Mac
tasklist | findstr java    # Windows
```

### Проверка 2: Какие переменные окружения установлены?

```bash
# Windows PowerShell:
Get-ChildItem Env: | Where-Object {$_.Name -like "SPRING*"}

# Windows CMD:
set | findstr SPRING

# Linux/Mac:
env | grep SPRING
```

### Проверка 3: Доступна ли PostgreSQL?

```bash
# Если используете Docker:
docker-compose ps postgres
docker-compose logs postgres

# Если локальный PostgreSQL:
# Windows:
Get-Service postgresql*

# Linux:
sudo systemctl status postgresql
```

---

## 📝 Настройка для разных окружений

### Создание application-local.properties

Для локальной разработки создайте файл `src/main/resources/application-local.properties`:

```properties
# Local development with Docker Compose PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

**Использование:**
```bash
# Windows PowerShell:
$env:SPRING_PROFILES_ACTIVE="local"
mvn spring-boot:run

# Linux/Mac:
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```

### Создание application-dev.properties

Для использования удаленной БД:

```properties
# Remote development database
spring.datasource.url=jdbc:postgresql://95.165.27.159:5433/auth_db
spring.datasource.username=postgres
spring.datasource.password=123
```

---

## 🚀 Быстрое решение (TL;DR)

**Самый простой способ запустить проект:**

```bash
# 1. Остановить все если запущено
docker-compose down

# 2. Запустить через Docker Compose
docker-compose up -d

# 3. Проверить логи
docker-compose logs -f auth-service

# 4. Проверить что работает
curl http://localhost:8080
```

**Если нужно использовать существующую БД:**

Создайте файл `.env` в корне проекта:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://95.165.27.159:5433/auth_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123
```

И обновите docker-compose.yml чтобы использовать `.env`:
```yaml
auth-service:
  env_file:
    - .env
```

---

## ⚠️ Важные замечания

### 1. Docker Compose использует внутреннюю сеть
Когда сервисы запущены через docker-compose:
- ✅ Используйте имя сервиса: `postgres:5432`
- ❌ НЕ используйте: `localhost:5432` (не будет работать внутри контейнера)

### 2. Локальный запуск использует localhost
Когда приложение запущено локально:
- ✅ Используйте: `localhost:5432`
- ❌ НЕ используйте: `postgres:5432` (это имя Docker сети)

### 3. Hibernate DDL Auto
```properties
# Для разработки:
spring.jpa.hibernate.ddl-auto=update    # Обновляет схему

# Для production:
spring.jpa.hibernate.ddl-auto=validate  # Только проверяет
```

### 4. Connection Pool
Если БД медленно отвечает, настройте таймауты:
```properties
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

---

## 🆘 Если ничего не помогло

### 1. Проверьте логи детально

```bash
# Docker Compose:
docker-compose logs --tail=100 auth-service
docker-compose logs --tail=100 postgres

# Локальный запуск с debug:
mvn spring-boot:run -Ddebug
```

### 2. Проверьте network connectivity

```bash
# Из контейнера auth-service:
docker exec -it auth-service sh
ping postgres
nc -zv postgres 5432
exit
```

### 3. Проверьте PostgreSQL готовность

```bash
# Дождитесь пока PostgreSQL будет готов
docker-compose logs postgres | grep "ready to accept connections"
```

### 4. Пересоздайте все с нуля

```bash
# Полная очистка
docker-compose down -v
docker system prune -a

# Заново запустить
docker-compose up --build -d
```

---

## 📞 Получение помощи

Если проблема не решается, соберите следующую информацию:

```bash
# 1. Версии
java -version
mvn -version
docker --version
docker-compose --version

# 2. Логи
docker-compose logs auth-service > auth-service.log
docker-compose logs postgres > postgres.log

# 3. Конфигурация
docker-compose ps
docker network ls
```

---

## ✅ Проверка что все работает

После исправления проблемы:

```bash
# 1. Проверить что контейнеры запущены
docker-compose ps
# Все должны быть "Up"

# 2. Проверить логи без ошибок
docker-compose logs auth-service | grep -i error

# 3. Проверить health
curl http://localhost:8080

# 4. Проверить подключение к БД
docker exec -it auth-postgres psql -U postgres -d authdb -c "\dt"
```

**Если видите таблицы Hibernate (users, etc.) - все работает! ✅**

---

*Последнее обновление: 17 декабря 2025*

