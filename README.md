# Симуляция животного мира

Веб-приложение для симуляции экосистемы с зайцами, волками, ягодами и деревьями.

## Технологии

- Java 21
- Spring Boot 3.2.5
- WebSocket для real-time обновлений
- Canvas API для анимаций
- Maven

## Локальный запуск

1. Убедитесь, что установлена Java 21
2. Скопируйте `.env.example` в `.env` и настройте параметры
3. Запустите:
```bash
mvn spring-boot:run
```
4. Откройте http://localhost:8080

## Деплой на Railway

### Через GitHub:

1. Запушьте проект на GitHub
2. Зайдите на [Railway.app](https://railway.app)
3. Нажмите "New Project" → "Deploy from GitHub repo"
4. Выберите ваш репозиторий
5. Railway автоматически обнаружит Dockerfile и соберет проект
6. Добавьте переменные окружения в Railway (Settings → Variables):
   - Скопируйте все переменные из `.env.example`
   - Или используйте Raw Editor и вставьте содержимое `.env.example`
7. Railway автоматически задеплоит приложение
8. Получите публичный URL в Settings → Networking → Generate Domain

### Переменные окружения для Railway:

```
WIDTH=15
HEIGHT=10
IWE_MAX_COUNT=2
TREE_MAX_COUNT=2
HARE_MAX_COUNT=2
WOLF_MAX_COUNT=1
BERRIES_MAX_COUNT=3
IWE_MAX_LEVELS_OUT=4
TREE_MAX_LEVELS_OUT=5
HARE_MAX_LEVELS_OUT=7
WOLF_MAX_LEVELS_OUT=10
BERRIES_MAX_LEVELS_OUT=9
IWE_MAX_LEVEL=1000
TREE_MAX_LEVEL=900
BERRIES_MAX_LEVEL=100
```

## Возможности

- ✅ Плавные анимации появления, движения и исчезновения
- ✅ Real-time обновления через WebSocket (с fallback на polling)
- ✅ Управление симуляцией (Старт/Пауза/Шаг/Сброс)
- ✅ Список всех объектов на карте
- ✅ Лог событий (кто кого съел)
- ✅ Настраиваемые параметры через .env

## Структура проекта

```
src/
├── main/
│   ├── java/simulation/
│   │   ├── Entity/          # Классы сущностей (Hare, Wolf, Tree, etc.)
│   │   ├── backend/         # Логика симуляции
│   │   ├── util/            # Утилиты и конфигурация
│   │   └── web/             # REST API и WebSocket
│   └── resources/
│       ├── static/          # HTML/CSS/JS для веб-интерфейса
│       └── application.properties
```

## Лицензия

MIT
