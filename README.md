# TaskManager

---

## Общее

Учебный проект в рамках летней практики 2025. Приложение для управления задачами с использованием микросервисной
архитектуры.

### Архитектура

![Architecture Diagram](docs/architecture.png)

### ⚙️ Стек проекта

- **Backend**:
    - <img src="docs/icons/java_logopng.png" width="16"> Java 21  
    - <img src="docs/icons/spring_logo.png" width="16"> Spring 3.5.0 (Boot, Jpa, Security)
    - <img src="docs/icons/spring_cloud_logo.png" width="16"> Spring Cloud (Eureka, Config, Gateway)  

- **Базы данных**:
    - <img src="docs/icons/postgres_logo.svg" width="16"> PostgreSQL 16  

- **Инфраструктура**:
    - <img src="docs/icons/docker_logo.png" width="16"> Docker  
    - <img src="docs/icons/kafka_logo.png" width="16"> Apache Kafka 
    - <img src="docs/icons/vault_logo.svg" width="16"> Vault 
    - <img src="docs/icons/zipkin_logo.svg" width="16"> Zipkin 

---

## Запуск проекта

Есть два варианта запуска приложения

* с полным использованием docker
* с частичным использованием docker<br>

В первом случае необходимо запустить `docker-compose` файлы в правильной последовательности, а именно:<br>

```text
'DC'.infrastructure.yml → Доп зависимости(см. раздел ниже) → 'DC'.core.yml → 'DC'.business-logic.yml <br>
```

где 'DC' - `docker-compose` <br><br>

### Подробнее про 'DC' файлы <br>

* _**.infrastructure.yml**_ - запускает контейнеры со сторонними сервисами(postgres, vault и т.д), создаёт общую
  docker-сеть
* _**.core.yml**_ - запускает "обслуживающие" микросервисы, без которых основные микросервисы с бизнес логикой
  либо будту работать некорректно, либо не будут работать вовсе(config-server, eureka-server, gateway)
* _**.business-logic.yml**_ - запускает микросервисы с бизнесс-логикой.(auth-ms, account-ms, task-ms)

### Дополнительные зависимости <br>

Также для корректной работы необходимо создать базы данных и секреты. Для этого используются `.sh` файлы
`db-initializer.sh` и `vault-initializer.sh`, которые монтируются в соответствующие контейнеры при их запуске.

```yaml

postgres:
  # ...
  volumes:
    - ./db-initializer.sh:/usr/local/bin/db-initializer.sh

vault:
  # ...
  volumes:
    - ./vault-initializer.sh:/usr/local/bin/vault-initializer.sh
```

После запуска контейнеров нужно перейти в каждом из них в консоль и выполнить эти скрипты

```shell
/bin/sh /usr/local/bin/specific_script.sh
```

![DB initialization](docs/init_db.png)
![Vault initialization](docs/init_vault.png)

Во втором случае процесс самую малость изменится:

```text
'DC'.infrastructure.yml → Доп зависимости(см. раздел ниже) → локальный запуск инфраструктуры и бизнесс-логики
```

### Замечания

В случае запуска в смешанном режиме(docker + локальный запуск микросервисов). В `docker-compose.infrastructure.yml`
возможно
понадобится внести небольшие изменения в kafka env. В частности в переменной `KAFKA_ADVERTISED_LISTENERS` в пути
заменить
kafka на localhost:

```yaml
  kafka:
    # ...
    environment:
      # ...
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_HOST://localhost:29092
      # ...
    # ...
```

---

## Работа с API

### Схема взаимодействия сервисов

### Основные эндпоинты

**`auth-ms`**

| Метод | Эндпоинт                | Описание                        | Тело запроса (JSON)                                                     |
|-------|-------------------------|---------------------------------|-------------------------------------------------------------------------|
| POST  | `/api/v1/auth/register` | Регистрация нового пользователя | `{"username":"user", "password":"root", "passwordConfirmation":"root"}` |
| POST  | `/api/v1/auth/login`    | Получение JWT-токены            | `{"username":"user", "password":"root"}`                                |
| POST  | `/api/v1/auth/refresh`  | Обновить JWT-токены             | `refresh_token`                                                         |

Также есть ручка для назначения

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

Тело у данного запроса отсутствует. Точно так же, нет и тела ответа. Статус код `204`

### Пример запросов

В директории http можно найти примеры взаимодействия с api для обращения либо напрямую к каждому микросервису, либо
через gateway.

Также к каждому микросервису подключен swagger. После успешного запуска приложения можно обращаться:

* напрямую к микросервису <br>
  ```curl
    http://localhost:<ms-port>/swagger-ui/index.html
  ```
* через gateway <br>
  microservice-name: auth, account, task. <br>
  ```curl
    http://localhost:<gateway-port>/<microservice-name>/swagger/index.html
  ```
