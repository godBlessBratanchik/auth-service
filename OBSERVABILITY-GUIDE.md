# 🔍 Observability Guide - Auth Service

## 📋 Обзор

Для auth-service настроены две технологии наблюдаемости:

1. **Мониторинг** - Prometheus + Grafana
2. **Логирование** - Loki + Promtail (централизованные логи)

---

## 🏗️ Архитектура Observability

```
┌─────────────────────────────────────────────────────────────────┐
│                     Auth Service                                 │
│  ┌─────────────────┐     ┌─────────────────┐                   │
│  │  /actuator/     │     │   JSON Logs     │                   │
│  │  prometheus     │     │   (Logback)     │                   │
│  └────────┬────────┘     └────────┬────────┘                   │
└───────────┼────────────────────────┼────────────────────────────┘
            │                        │
            ▼                        ▼
┌───────────────────┐    ┌───────────────────┐
│    Prometheus     │    │    Promtail       │
│   (Metrics DB)    │    │  (Log Collector)  │
│   Port: 9090      │    │   Port: 9080      │
└─────────┬─────────┘    └─────────┬─────────┘
          │                        │
          │                        ▼
          │              ┌───────────────────┐
          │              │      Loki         │
          │              │   (Log Storage)   │
          │              │   Port: 3100      │
          │              └─────────┬─────────┘
          │                        │
          ▼                        ▼
┌─────────────────────────────────────────────┐
│               Grafana                        │
│         (Visualization)                      │
│         Port: 3000                           │
│  ┌──────────────┐  ┌──────────────────┐    │
│  │  Dashboards  │  │   Log Explorer   │    │
│  │  (Metrics)   │  │   (Logs/Traces)  │    │
│  └──────────────┘  └──────────────────┘    │
└─────────────────────────────────────────────┘
```

---

## 🚀 Быстрый старт

### Запуск всего стека

```bash
# Запустить все сервисы включая observability
docker-compose up -d

# Проверить статус
docker-compose ps
```

### Доступ к интерфейсам

| Сервис | URL | Логин/Пароль |
|--------|-----|--------------|
| **Grafana** | http://localhost:3000 | admin / admin |
| **Prometheus** | http://localhost:9090 | - |
| **Loki** | http://localhost:3100 | - |
| **Auth Service Metrics** | http://localhost:8080/actuator/prometheus | - |
| **Auth Service Health** | http://localhost:8080/actuator/health | - |

---

## 📊 Мониторинг (Prometheus + Grafana)

### Что мониторится

#### Метрики приложения:
- **HTTP Requests** - количество, latency, ошибки
- **JVM Memory** - heap, non-heap, GC
- **JVM Threads** - количество потоков
- **Database Pool** - активные/idle соединения
- **Uptime** - время работы приложения

#### Метрики базы данных:
- **Connections** - количество соединений
- **Transactions** - commits, rollbacks
- **Query Performance** - время выполнения
- **Database Size** - размер БД

### Prometheus Endpoints

```bash
# Метрики приложения
curl http://localhost:8080/actuator/prometheus

# Health check
curl http://localhost:8080/actuator/health

# Info
curl http://localhost:8080/actuator/info

# All metrics
curl http://localhost:8080/actuator/metrics
```

### Grafana Dashboard

Преднастроенный дашборд включает:

1. **JVM Heap Used %** - использование памяти
2. **Active HTTP Requests** - активные запросы
3. **Health Status** - статус приложения
4. **Uptime** - время работы
5. **HTTP Request Rate** - график запросов/сек
6. **HTTP Response Time** - время ответа (p50, p95)
7. **JVM Memory Usage** - память во времени
8. **Database Connection Pool** - состояние пула соединений

### Полезные PromQL запросы

```promql
# Request Rate (requests/sec)
sum(rate(http_server_requests_seconds_count{application="auth-service"}[1m])) by (uri)

# Average Response Time
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application="auth-service"}[5m])) by (le))

# Error Rate
sum(rate(http_server_requests_seconds_count{application="auth-service",status=~"5.."}[5m]))

# JVM Memory Used
sum(jvm_memory_used_bytes{application="auth-service"}) by (area)

# Database Connections
hikaricp_connections_active{application="auth-service"}

# Uptime
process_uptime_seconds{application="auth-service"}
```

---

## 📝 Логирование (Loki + Promtail)

### Формат логов

Логи в JSON формате (для Loki):

```json
{
  "@timestamp": "2024-01-15T10:30:00.000Z",
  "level": "INFO",
  "logger_name": "c.g.a.controller.RegisterController",
  "thread_name": "http-nio-8080-exec-1",
  "message": "User registration attempt",
  "application": "auth-service",
  "environment": "docker"
}
```

### Просмотр логов в Grafana

1. Откройте http://localhost:3000
2. Перейдите в **Explore** (иконка компаса)
3. Выберите **Loki** как datasource
4. Используйте LogQL запросы:

```logql
# Все логи auth-service
{application="auth-service"}

# Только ошибки
{application="auth-service"} |= "ERROR"

# Логи по уровню
{application="auth-service",level="ERROR"}

# Поиск по тексту
{application="auth-service"} |~ "(?i)exception"

# Последние 100 логов
{application="auth-service"} | limit 100
```

### Уровни логирования

| Уровень | Цвет в Grafana | Описание |
|---------|----------------|----------|
| ERROR | Красный | Ошибки требующие внимания |
| WARN | Желтый | Предупреждения |
| INFO | Зеленый | Информационные сообщения |
| DEBUG | Синий | Отладочная информация |

---

## 🛠️ Конфигурация

### Файловая структура

```
observability/
├── prometheus/
│   └── prometheus.yml        # Конфигурация Prometheus
├── grafana/
│   └── provisioning/
│       ├── datasources/
│       │   └── datasources.yml    # Источники данных
│       └── dashboards/
│           ├── dashboards.yml     # Настройка дашбордов
│           └── json/
│               └── auth-service-dashboard.json
└── loki/
    ├── loki-config.yml       # Конфигурация Loki
    └── promtail-config.yml   # Конфигурация Promtail
```

### Spring Boot настройки

В `application.properties`:

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true

# Custom metrics tags
management.metrics.tags.application=${spring.application.name}
```

### Logback конфигурация

В `logback-spring.xml`:
- **CONSOLE** - человекочитаемый формат для разработки
- **JSON** - структурированные логи для Loki
- **FILE** - запись в файл с ротацией

---

## 🔧 Customization

### Добавление custom метрик

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

@Service
public class AuthService {
    private final Counter registrationCounter;
    
    public AuthService(MeterRegistry registry) {
        this.registrationCounter = Counter.builder("auth.registrations.total")
            .description("Total user registrations")
            .tag("type", "new_user")
            .register(registry);
    }
    
    public void registerUser(String email, String password) {
        // ... логика регистрации
        registrationCounter.increment();
    }
}
```

### Добавление custom логов с context

```java
import org.slf4j.MDC;

public void processRequest(String userId) {
    try {
        MDC.put("userId", userId);
        MDC.put("traceId", UUID.randomUUID().toString());
        
        log.info("Processing request for user");
        // ... логика
        
    } finally {
        MDC.clear();
    }
}
```

---

## 📈 Alerting (опционально)

### Prometheus Alert Rules

Создайте `observability/prometheus/alert-rules.yml`:

```yaml
groups:
  - name: auth-service
    rules:
      - alert: HighErrorRate
        expr: sum(rate(http_server_requests_seconds_count{status=~"5..",application="auth-service"}[5m])) > 0.1
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          
      - alert: ServiceDown
        expr: up{job="auth-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Auth Service is down"
          
      - alert: HighMemoryUsage
        expr: sum(jvm_memory_used_bytes{area="heap",application="auth-service"}) / sum(jvm_memory_max_bytes{area="heap",application="auth-service"}) > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High JVM memory usage"
```

---

## 🐛 Troubleshooting

### Prometheus не видит метрики

```bash
# Проверьте endpoint
curl http://localhost:8080/actuator/prometheus

# Проверьте targets в Prometheus
# http://localhost:9090/targets
```

### Grafana не показывает данные

1. Проверьте datasources в Grafana
2. Убедитесь что Prometheus работает
3. Проверьте время (Time Range) в дашборде

### Loki не получает логи

```bash
# Проверьте статус Promtail
docker-compose logs promtail

# Проверьте Loki
curl http://localhost:3100/ready
```

---

## 📊 Полезные команды

```bash
# Запустить только observability stack
docker-compose up -d prometheus grafana loki promtail postgres-exporter

# Посмотреть логи Prometheus
docker-compose logs -f prometheus

# Посмотреть логи Grafana
docker-compose logs -f grafana

# Перезапустить observability
docker-compose restart prometheus grafana loki promtail

# Очистить данные (для fresh start)
docker-compose down -v
docker volume rm auth-service_prometheus-data auth-service_grafana-data auth-service_loki-data
```

---

## 🎓 Что изучено

### Мониторинг:
- ✅ Spring Boot Actuator
- ✅ Micrometer + Prometheus Registry
- ✅ Prometheus для сбора метрик
- ✅ Grafana для визуализации
- ✅ PromQL запросы
- ✅ PostgreSQL экспортер

### Логирование:
- ✅ Структурированное логирование (JSON)
- ✅ Logback конфигурация
- ✅ Loki для агрегации логов
- ✅ Promtail для сбора логов
- ✅ LogQL запросы
- ✅ MDC (Mapped Diagnostic Context)

---

## 📚 Дополнительные ресурсы

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Loki Documentation](https://grafana.com/docs/loki/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)

---

## ✅ Checklist

После настройки проверьте:

- [ ] `http://localhost:8080/actuator/health` - возвращает UP
- [ ] `http://localhost:8080/actuator/prometheus` - возвращает метрики
- [ ] `http://localhost:9090/targets` - auth-service в состоянии UP
- [ ] `http://localhost:3000` - Grafana открывается
- [ ] Dashboard "Auth Service Dashboard" - показывает данные
- [ ] Explore → Loki - показывает логи

**Готово! Observability настроен! 🎉**

