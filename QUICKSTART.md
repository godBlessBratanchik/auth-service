# 🚀 Quick Start Guide

## Запуск через Docker Compose (рекомендуется)

### 1. Запуск всех сервисов

**Автоматический запуск (Windows):**
```bash
# Просто запустите скрипт
start-local.bat
```

**Автоматический запуск (Linux/Mac):**
```bash
# Дайте права на выполнение (только первый раз)
chmod +x start-local.sh

# Запустите скрипт
./start-local.sh
```

**Ручной запуск:**
```bash
# Запуск в фоновом режиме
docker-compose up -d

# Или с логами в консоли
docker-compose up
```

Это автоматически запустит:
- ✅ Auth Service (порт 8080)
- ✅ PostgreSQL (порт 5432)
- ✅ Kafka + Zookeeper (порт 9092)

### 2. Проверка статуса

```bash
# Посмотреть статус контейнеров
docker-compose ps

# Посмотреть логи
docker-compose logs -f auth-service
```

### 3. Тестирование API

```bash
# Health check (если есть actuator)
curl http://localhost:8080/actuator/health

# Регистрация пользователя (пример)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 4. Остановка сервисов

```bash
# Остановить контейнеры (данные сохраняются)
docker-compose stop

# Остановить и удалить контейнеры
docker-compose down

# Остановить и удалить контейнеры + volumes (все данные)
docker-compose down -v
```

## Запуск только Auth Service

Если у вас уже есть PostgreSQL и Kafka:

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  elbondarenko04121/auth-service:latest
```

## Локальная разработка (без Docker)

### Требования:
- Java 17+
- Maven 3.6+
- PostgreSQL
- Kafka

### Шаги:

1. **Установить зависимости**
```bash
mvn clean install
```

2. **Настроить application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.kafka.bootstrap-servers=localhost:9092
```

3. **Запустить приложение**
```bash
mvn spring-boot:run
```

4. **Запустить тесты**
```bash
mvn test
```

## Сборка собственного образа

```bash
# Сборка
docker build -t my-auth-service:local .

# Запуск
docker run -p 8080:8080 my-auth-service:local
```

## Переменные окружения

### Обязательные:

| Переменная | Описание | Пример |
|------------|----------|--------|
| `SPRING_DATASOURCE_URL` | JDBC URL базы данных | `jdbc:postgresql://localhost:5432/authdb` |
| `SPRING_DATASOURCE_USERNAME` | Имя пользователя БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `postgres` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka серверы | `localhost:9092` |

### Опциональные:

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `JWT_SECRET` | Секретный ключ для JWT | - |
| `JWT_EXPIRATION` | Время жизни токена (мс) | - |
| `JAVA_OPTS` | JVM опции | `-Xmx512m -Xms256m` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Стратегия Hibernate | `update` |

## Проверка работоспособности

### 1. Проверка что сервис запустился

```bash
# Linux/Mac
curl http://localhost:8080

# Windows PowerShell
Invoke-WebRequest http://localhost:8080
```

### 2. Проверка подключения к БД

```bash
# Подключиться к PostgreSQL контейнеру
docker exec -it auth-postgres psql -U postgres -d authdb

# Посмотреть таблицы
\dt

# Выйти
\q
```

### 3. Проверка Kafka

```bash
# Список топиков
docker exec auth-kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Troubleshooting

### ❌ Ошибка: "unable to obtain isolated JDBC connection"

**Причина:** Spring Boot не может подключиться к PostgreSQL.

**Решение:**
```bash
# 1. Остановить все контейнеры
docker-compose down

# 2. Убедиться что PostgreSQL запускается первым
docker-compose up -d postgres

# 3. Подождать 10 секунд
timeout /t 10 /nobreak   # Windows
sleep 10                 # Linux/Mac

# 4. Запустить все сервисы
docker-compose up -d

# 5. Проверить логи
docker-compose logs -f auth-service
```

**Или используйте готовый скрипт:**
- Windows: `start-local.bat`
- Linux/Mac: `./start-local.sh`

📖 **Подробнее:** См. файл `TROUBLESHOOTING-DATABASE.md`

---

### Порт 8080 занят

```bash
# Изменить порт в docker-compose.yml
ports:
  - "8081:8080"  # внешний:внутренний
```

### База данных не инициализируется

```bash
# Пересоздать volume
docker-compose down -v
docker-compose up -d
```

### Контейнер падает при старте

```bash
# Посмотреть логи
docker-compose logs auth-service

# Посмотреть детальные логи
docker logs auth-service --tail 100
```

### Нет подключения к Kafka

```bash
# Проверить что Kafka запущена
docker-compose ps kafka

# Проверить логи Kafka
docker-compose logs kafka
```

## Полезные команды

```bash
# Пересобрать образы
docker-compose build

# Пересобрать и запустить
docker-compose up --build

# Запустить определенный сервис
docker-compose up auth-service

# Посмотреть использование ресурсов
docker stats

# Очистить все неиспользуемые ресурсы Docker
docker system prune -a

# Зайти внутрь контейнера
docker exec -it auth-service sh
```

## Дальнейшие шаги

После успешного запуска:

1. 📖 Изучить API документацию (если есть Swagger)
2. 🧪 Запустить integration тесты
3. 🔐 Настроить JWT секреты для production
4. 📊 Настроить мониторинг (Prometheus/Grafana)
5. 🚀 Развернуть в production (Kubernetes/Cloud)

## Полезные ссылки

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

