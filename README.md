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
---
## Запуск проекта
Есть два варианта запуска приложения
* с полным использованием docker
* с частичным использованием docker<br>

В первом случае необходимо запустить docker-compose файлы в правильной последовательности, а именно:<br>
```text
'DC'.infrastructure.yml → Доп зависимости(см. раздел ниже) → 'DC'.core.yml → 'DC'.business-logic.yml <br>
```
где 'DC' - docker-compose <br><br>

### Подробнее про 'DC' файлы <br>
* _**.infrastructure.yml**_ - запускает контейнеры со сторонними сервисами(postgres, vault и т.д), создаёт общую docker-сеть
* _**.core.yml**_ - запускает "обслуживающие" микросервисы, без которых основные микросервисы с бизнес логикой 
либо будту работать некорректно, либо не будут работать вовсе(config-server, eureka-server, gateway)
* _**.business-logic.yml**_ - запускает микросервисы с бизнесс-логикой.(auth-ms, account-ms, task-ms)

### Дополнительные зависимости <br>
Также для корректной работы необходимо создать базы данных и секреты. Для этого используются .sh файлы 
db-initializer.sh и vault-initializer.sh, которые монтируются в соответствующие контейнеры при их запуске.
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
---
