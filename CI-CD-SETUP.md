# CI/CD Setup Documentation

## 📋 Обзор

Этот проект использует GitHub Actions для автоматической сборки, тестирования и публикации Docker образов в DockerHub.

## 🔄 Workflow Pipeline

### 1. Триггеры

Pipeline запускается при:
- **Push** в ветки: `main`, `master`, `develop`
- **Pull Request** в эти же ветки

### 2. Этапы (Jobs)

#### Job 1: Test (Тестирование)
- Выполняется на: `ubuntu-latest`
- Действия:
  1. Checkout кода из репозитория
  2. Установка JDK 17 (Eclipse Temurin)
  3. Кэширование Maven зависимостей
  4. Запуск тестов: `mvn clean test`
  5. Загрузка результатов тестов как артефакты

#### Job 2: Build and Push (Сборка и публикация)
- Выполняется на: `ubuntu-latest`
- Зависимость: требует успешного завершения `test`
- Условие: только для push событий (не для PR)
- Действия:
  1. Checkout кода
  2. Настройка Docker Buildx (для advanced функций)
  3. Авторизация в DockerHub
  4. Генерация метаданных и тегов для образа
  5. Сборка и публикация Docker образа
  6. Вывод digest собранного образа

## 🏷️ Docker Теги

Автоматическая генерация тегов:

| Тег | Описание | Пример |
|-----|----------|--------|
| `latest` | Последняя версия из default branch | `elbondarenko04121/auth-service:latest` |
| `{branch}-{sha}` | SHA коммита с префиксом ветки | `elbondarenko04121/auth-service:main-abc1234` |
| `{branch}` | Имя ветки | `elbondarenko04121/auth-service:develop` |

## 🔐 Секреты GitHub

### Необходимые секреты:

1. **DOCKER_USERNAME**
   - Значение: `elbondarenko04121`
   - Использование: авторизация в DockerHub

2. **DOCKER_TOKEN**
   - Значение: Personal Access Token из DockerHub
   - Использование: безопасная авторизация (вместо пароля)

### Как настроить секреты:

1. Перейти в Settings репозитория на GitHub
2. Выбрать Secrets and variables → Actions
3. Нажать New repository secret
4. Добавить каждый секрет с соответствующим именем и значением

## 🐋 Docker Optimization

### Multi-stage Build

Dockerfile использует multi-stage сборку для оптимизации размера:

```dockerfile
Stage 1: Build (maven:3.9-eclipse-temurin-17)
- Сборка JAR файла с помощью Maven

Stage 2: Runtime (eclipse-temurin:17-jre-alpine)
- Только JRE (не весь JDK)
- Alpine Linux для минимального размера
- Копирование только JAR файла
```

### Build Cache

Pipeline использует layer caching для ускорения сборки:
- `cache-from`: чтение кэша из registry
- `cache-to`: сохранение кэша в registry
- Кэш хранится как отдельный tag: `buildcache`

### .dockerignore

Исключает ненужные файлы из Docker context:
- Git файлы и история
- IDE конфигурации
- Локальные сборки Maven (target/)
- Документация
- Логи и временные файлы

## 📊 Мониторинг Pipeline

### Где посмотреть результаты:

1. **GitHub Actions**
   - Вкладка "Actions" в репозитории
   - История всех запусков
   - Логи каждого шага

2. **DockerHub**
   - https://hub.docker.com/r/elbondarenko04121/auth-service
   - Список всех опубликованных образов
   - История обновлений

### Артефакты

После каждого запуска доступны:
- **test-results**: XML отчеты Surefire с результатами тестов
- Хранятся 90 дней (по умолчанию GitHub)

## 🔧 Локальная разработка

### Тестирование workflow локально

Можно использовать [act](https://github.com/nektos/act):

```bash
# Установка act
# Windows (через chocolatey):
choco install act-cli

# Запуск workflow локально
act push
```

### Ручная сборка и push образа

```bash
# Авторизация в DockerHub
docker login -u elbondarenko04121

# Сборка образа
docker build -t elbondarenko04121/auth-service:local .

# Push в DockerHub
docker push elbondarenko04121/auth-service:local
```

## 🚀 Использование образа

### Базовый запуск

```bash
docker run -p 8080:8080 elbondarenko04121/auth-service:latest
```

### С переменными окружения

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/authdb \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  elbondarenko04121/auth-service:latest
```

### Docker Compose пример

```yaml
version: '3.8'

services:
  auth-service:
    image: elbondarenko04121/auth-service:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/authdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - postgres
      - kafka
  
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: authdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
  
  kafka:
    image: confluentinc/cp-kafka:latest
    # ... kafka configuration
```

## 🔍 Troubleshooting

### Pipeline падает на тестах

```bash
# Запустить тесты локально
mvn clean test

# Посмотреть детальные логи
mvn clean test -X
```

### Не удается push в DockerHub

Проверить:
1. ✅ Секреты DOCKER_USERNAME и DOCKER_TOKEN корректны
2. ✅ Токен имеет права на Read & Write
3. ✅ Репозиторий `elbondarenko04121/auth-service` существует на DockerHub

### Docker образ слишком большой

Текущий подход уже оптимизирован:
- ✅ Multi-stage build
- ✅ Alpine Linux base
- ✅ Только JRE (не JDK)
- ✅ .dockerignore

Размер образа: ~200-300 MB

## 📈 Метрики успеха

Успешная CI/CD pipeline означает:
- ✅ Все тесты проходят
- ✅ Docker образ успешно собирается
- ✅ Образ публикуется в DockerHub
- ✅ Время сборки < 5 минут (с кэшем)
- ✅ Образ запускается без ошибок

## 🎯 Следующие шаги (опционально)

Возможные улучшения:
1. Добавить security scanning (Trivy, Snyk)
2. Добавить code coverage отчеты
3. Настроить deployment в Kubernetes
4. Добавить staging окружение
5. Настроить rollback механизм
6. Добавить release versioning (semantic versioning)
7. Интеграция с Slack/Discord для уведомлений

