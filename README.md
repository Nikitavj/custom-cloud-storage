# File cloud storage

# Overview
Многопользовательское файловое облако.  
Сервис предназначен для загрузки, хранения и скачивания файлов и папок пользователя.  

Приложение построено c использованием модуля Spring Boot и архитектурного паттерна MVC.  
Доступ пользователей к сервису настроен с помощью фреймворка Spring Security.  
Обращение клиента к серверу осуществляется посредством REST запросов.  
Представление для пользователя генерируется Thymeleaf шаблонизатором.  
Данные пользователей хранятся в MySQL.  
Liquibase используется для изменения схемы БД.  
Для хранения текущих сессий пользователей применяется кеширование с помощью Redis.  
Файлы и папки загружаемые пользователем хранятся в S3 Minio хранилище,  
взаимодействие с которым построено на Java Client API.  
Приложение и базы данных запускаются в контейнерах посредством Docker Сompose.  

Проект выполнен в соответствии ТЗ https://zhukovsd.github.io/java-backend-learning-course/projects/cloud-file-storage/

# Technologies / tools used:
- Spring Boot
- Spring Sequrity
- Spring Sessions
- MySQL
- Redis
- S3 Minio
- Maven
- HTML
- Thymeleaf
- Bootstrap
- Lombok
- Docker Сompose
- Liquibase

# Installation
1. Указать переменные окружения:
```
SPRING_PROFILES_ACTIVE=prod
MYSQL_URL=jdbc:mysql://mysql:3306/storage_db
MYSQL_USER=user
MYSQL_PASSWORD=root
MYSQL_ROOT_PASSWORD=root
REDIS_HOST=redis
MINIO_URL=http://minio:9000
MINIO_USER=user
MINIO_PASSWORD=rootroot
```

2. Выполнить ```docker-compose -f docker-compose-prod.yml up```

# Usage
### 1. Главная страница.
Адрес - ```/```

![image](https://github.com/user-attachments/assets/816cfb7a-28a7-40f5-aff3-3f1a164c374d)

### 2. Страница поиска файлов.
Адрес - ```/search```  

![image](https://github.com/user-attachments/assets/45a3f6f8-9600-4c61-8d3a-95ff2be0d13d)

### 3. Страница авторизации.
  Адрес - ```/log-up```

![image](https://github.com/user-attachments/assets/fb228272-9252-4ed8-a182-cc2d74056c6b)

### 4. Страница входа.
  Адрес - ```/log-in```

![image](https://github.com/user-attachments/assets/ef8db1d1-3aef-4623-a677-e8594400c43d)


### 5. Страница выхода.
  Адрес - ```/log-out```
