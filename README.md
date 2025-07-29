# TaskManager

---

## Общее

Учебный проект в рамках летней практики 2025. Приложение для управления задачами с использованием микросервисной
архитектуры.

### Архитектура

![Architecture Diagram](docs/architecture.png)

### ⚙️ Стек проекта

- **Backend**:
    - <img src="docs/icons/java_logo.png" width="16" alt="#"> Java 21  
    - <img src="docs/icons/spring_logo.png" width="16" alt="#"> Spring 3.5.0 (Boot, Jpa, Security)
    - <img src="docs/icons/spring_cloud_logo.png" width="16" alt="#"> Spring Cloud (Eureka, Config, Gateway)  

- **База данных**:
    - <img src="docs/icons/postgres_logo.svg" width="16" alt="#"> PostgreSQL 16  

- **Инфраструктура**:
    - <img src="docs/icons/zipkin_logo.svg" width="16" alt="#"> Zipkin 
    - <img src="docs/icons/kafka_logo.png" width="16" alt="#"> Apache Kafka 
    - <img src="docs/icons/docker_logo.png" width="16" alt="#"> Docker  
    - <img src="docs/icons/vault_logo.svg" width="16" alt="#"> Vault 

---

## Запуск проекта

Для запуска необходимо использовать Docker. Весь проект разбит на несколько `docker-compose`

### Подробнее про `docker-compose` файлы <br>

* _**.infrastructure.yml**_ - запускает контейнеры со сторонними сервисами(postgres, vault и т.д), создаёт общую
  docker-сеть
* _**.core.yml**_ - запускает "обслуживающие" микросервисы, без которых основные микросервисы с бизнес логикой
  либо будту работать некорректно, либо не будут работать вовсе(config-server, eureka-server, gateway)
* _**.business-logic.yml**_ - запускает микросервисы с бизнесс-логикой.(auth-ms, account-ms, task-ms)

### Сам запуск 

Можно либо запускать `docker-compose` файлы один за другим в правильной последовательности `infrastructure -> core -> ms` 
(однако в таком случае необходимо будет предварительно создать общую сеть `docker network create task-manager`), либо просто
перейти в директорию `scripts` и запустить файл `./start.sh`. Этот файл автоматически создаёт сеть и запускает `docker-compose`
файлы в правильном порядке.

### Замечания
В случае, если config-server провалил healthcheck во время запуска `./start.sh` может потребоваться повторно руками запустить 
`docker-compose.core.yml`

---

## Работа с API

### Где найти примеры работы API?

В директории `http` можно найти примеры взаимодействия с api. Обращение либо напрямую к каждому микросервису, либо
через `gateway`.

### Схема взаимодействия сервисов

**Важно:** для работы с api account-ms и task-ms необходим jwt-token. Без него не получится
использовать ни одну ручку (без исключений. Токен необходим даже для создания account'а).

### Основные эндпоинты

**`auth-ms`**
-

| Метод | Эндпоинт                | Описание                        | Тело запроса (JSON)                                                     |
|-------|-------------------------|---------------------------------|-------------------------------------------------------------------------|
| POST  | `/api/v1/auth/register` | Регистрация нового пользователя | `{"username":"user", "password":"root", "passwordConfirmation":"root"}` |
| POST  | `/api/v1/auth/login`    | Получение JWT-токены            | `{"username":"user", "password":"root"}`                                |
| POST  | `/api/v1/auth/refresh`  | Обновить JWT-токены             | `refresh_token`                                                         |

**Пример ответа (успех):**
<br>Логин/Refresh

```json
{
  "id": "5bc388a6-f95d-459d-b5b5-51b63e01c533",
  "username": "admin1",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjEiLCJpZCI6IjViYzM4OGE2LWY5NWQtNDU5ZC1iNWI1LTUxYjYzZTAxYzUzMyIsInJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXSwiZXhwIjoxNzUxMzAwNzA1fQ.0C1Nc82AKpZRP2U3SYwt3yThWLr3gLDocxL8UFuJeiz5GqF3tbwLjSIfUFkYfv_yctIuR7FaaJoQfHXTdufHGQ",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjEiLCJpZCI6IjViYzM4OGE2LWY5NWQtNDU5ZC1iNWI1LTUxYjYzZTAxYzUzMyIsImV4cCI6MTc1MTkwNDYwNX0.-D6xj0U3wjxxiM-kTPZenAgpYe3i4WHFm170rvhFwdH1szSothir2oW4lQPXQ-l8uq9uZooNBuiKqCPU9uiKGg"
}
```

<br>Регистрация

```json
{
  "id": "7f095e62-7574-42bf-9f6c-fa599b86233a",
  "username": "Danil",
  "isActive": true
}
```

**Пример ответа (40x):**
<br>Логин

```json
{
  "status": 404,
  "error": "Not found",
  "path": "/api/v1/auth/login",
  "message": "User not found",
  "timestamp": "2025-06-30T19:15:05.6597855"
}
```

<br>Регистрация

```json
{
  "status": 400,
  "error": "Bad request",
  "path": "/api/v1/auth/register",
  "message": "Validation failed",
  "timestamp": "2025-06-30T19:13:58.7175083",
  "errors": {
    "username": "User with such username already exists"
  }
}

```

<br>Refresh

```json
{
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/v1/auth/refresh",
  "message": "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.",
  "timestamp": "2025-06-30T19:39:42.666316"
}
```

Также в auth-ms есть ручка для назначения за пользователем роли ADMIN.

| Метод | Эндпоинт                  | Описание                               | Тело запроса (JSON) |
|-------|---------------------------|----------------------------------------|---------------------|
| PUT   | `/api/v1/user/{{userId}}` | Назначение за пользователем роли ADMIN | Не требуется.       |

**Важно:** данная ручка требует jwt для выполнения. Также сделать из пользователя админа может только пользователь,
который уже является админом.

Тело у данного запроса отсутствует. Точно так же, нет тела и у ответа. Статус код `204`


`account-ms`
- 

| Метод  | Эндпоинт                      | Описание                                                                        | Права                                                                                                                                                                                  |
|--------|-------------------------------|---------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/v1/account`             | Регистрация нового аккаунта                                                     | Пользователь может создать аккаунт только самому себе, <br/>его ID автоматически извлекается из jwt и назначается за аккаунтом. <br/>Де факто получается связь 1 к 1.(User - Account). |
| GET    | `/api/v1/account`             | Получить все аккаунты                                                           | Доступ есть только для пользователей с ролью `ADMIN`.                                                                                                                                  |
| GET    | `/api/v1/account/{accountId}` | Получить конкретный аккаунт с прикреплённым задачами(делается запрос к task-ms) | Обычный `USER` может так получить только доступ к своему аккаунту. `ADMIN` как к своему, так и к любому другому существующему аккаунту.                                                |
| GET    | `/api/v1/account/search`      | Поиск пользователя по name или surname                                          | Доступ только для `ADMIN`.                                                                                                                                                             |
| PUT    | `/api/v1/account/{accountId}` | Редактирование аккаунта                                                         | Отредактировать аккаунт может только его владелец.                                                                                                                                     |
| DELETE | `/api/v1/account/{accountId}` | Удаление аккаунта                                                               | Удалить аккаунт может либо его владелец, либо пользователь с ролью `ADMIN`.                                                                                                            |

* Детальнее с примерами запросов можно ознакомиться через `crud-test.http`<br>
* В файле `error-handling-test.http` можно найти примеры обработки ошибок невалидных запросов<br>
* Файл `paggination-test.http` демонстрирует примеры работы пагинации для `/api/v1/account`<br>
* Пример поиска (`/api/v1/account/search`) можно найти в `search-test.http`

`task-ms`
-

| Метод  | Эндпоинт                         | Описание                                                            | Права                                                                                                               |
|--------|----------------------------------|---------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/v1/task`                   | Создание task                                                       | `USER` может создать задачу только самому себе. `ADMIN` может создать как себе, так и любому другому пользователю.  |
| GET    | `/api/v1/task`                   | Получить все задачи по accountId (указывается как параметр запроса) | `USER` может посмотреть только свои задачи. `ADMIN` может посмотреть как свои задачи, так и по любому пользователю. |
| GET    | `/api/v1/task/{taskId}`          | Получить конкретную задачу по её ID                                 | `USER` если является владельцем задачи. `ADMIN` любую задачу.                                                       |
| PUT    | `/api/v1/task/{taskId}`          | Обновление задачи                                                   | Отредактировать задачу может только её владелец.                                                                    |
| DELETE | `/api/v1/task/{taskId}`          | Удаление задачи                                                     | `USER` может удалить только свою. `ADMIN` как свою, так и любого другого пользователя.                              |
| PUT    | `/api/v1/task/reassign/{taskId}` | Назначить другого пользователя                                      | Только `ADMIN`.                                                                                                     |

* Детальнее с примерами запросов можно ознакомиться через `crud-test.http`<br>
* В файле `error-handling-test.http` можно найти примеры обработки ошибок невалидных запросов<br>
* В файле `reassign-task-test.http` можно протестировать переназначение задачи

### Swagger

Также к каждому микросервису с бизнес-логикой подключен _swagger_. После успешного запуска приложения можно обращаться:

* напрямую к микросервису: `http://localhost:<ms-port>/swagger-ui/index.html`
* через gateway: `http://localhost:<gateway-port>/<microservice-name>/swagger/index.html` <br>
  microservice-name: auth, account, task. <br>
  