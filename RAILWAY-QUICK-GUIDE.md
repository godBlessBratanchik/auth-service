# ⚡ Railway - Быстрая шпаргалка

## 🎯 Что делать ВАМ (по порядку):

### 1. Railway проект (railway.app)
- Login через GitHub
- New Project → GitHub repo → `auth-service`

### 2. PostgreSQL
- В проекте: + New → Database → PostgreSQL

### 3. Переменные (Variables вкладка)
Добавьте:
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JWT_SECRET=your-secret-key-12345
SPRING_KAFKA_ENABLED=false
```

### 4. URL
- Settings → Networking → Generate Domain
- Скопируйте URL

### 5. Railway токен
- Профиль → Account Settings → Tokens → Create Token
- Имя: `github-actions`
- Скопируйте токен

### 6. GitHub Secret
- https://github.com/godBlessBratanchik/auth-service/settings/secrets/actions
- New secret: `RAILWAY_TOKEN` = ваш токен

### 7. Скажите мне - я сделаю push!

---

## 🤖 Что делаю Я:

✅ Создал `.github/workflows/deploy-railway.yml`  
✅ Создал `src/main/resources/application-prod.properties`  
✅ Создал `railway.json`  
✅ Создал документацию  
✅ Готов сделать push когда вы настроите Railway  

---

## ✅ Checklist:

- [ ] Railway проект создан
- [ ] PostgreSQL добавлен
- [ ] 8 переменных добавлены
- [ ] Domain сгенерирован
- [ ] Railway токен создан
- [ ] RAILWAY_TOKEN в GitHub
- [ ] **Готово? → Скажите мне!**

---

## 🚀 После push:

1. GitHub Actions задеплоит автоматически (~3-5 мин)
2. Проверьте: `https://ваш-url.up.railway.app/actuator/health`
3. Должно быть: `{"status":"UP"}`

**Готово!** 🎉

