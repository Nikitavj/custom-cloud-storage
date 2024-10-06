# File cloud storage

# Overview
Многопользовательское файловое облако.
Сервис предназначен для загрузки, хранения и скачивания файлов и папок пользователя.

Приложение построено c использованием модуля Spring Boot и архитекурного паттерна MVC.
Доступ пользователей к сервису настроен с помощью фреймворка Spring Security.
Обращение клиента к серверу осуществляется посредством REST запросов.
Предствление для пользователя генерируется Thymeleaf шаблонизатором.
Данные пользователей хранятся в MySQL.
Для хранения текущих сессий пользователей применяется кеширование с помощью Redis.
Файлы и папки загружаемые пользователем хранятся в S3 Minio хранилище,
взаимодействие с которым построено на Java Client API.
Приложение и базы данных запускаются посредством контейнера Docker-compose.

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
- Docker-compose

# Installation
1. Указать переменные окружения:
   + SPRING_PROFILES_ACTIVE=prod
+ MYSQL_URL=jdbc:mysql://mysql:3306/storage_db
+ MYSQL_USER=user
+ MYSQL_PASSWORD=root
+ MYSQL_ROOT_PASSWORD=root
+ REDIS_HOST=redis
+ MINIO_URL=http://minio:9000
+ MINIO_USER=user
+ MINIO_PASSWORD=rootroot
2. Собрать c помощью Maven war артефакт приложения.
3. Развернуть war артефакт в Tomcat.

# Usage
### 1. Главная страница.
Адрес - /home.

![image](https://github.com/Nikitavj/WeatherForecast/assets/134765675/c1d0cad1-0a53-4e95-bd07-2d0dc8281072)

### 2. Страница поиска локаций.
Адрес - /locations.  
GET параметр name содержит название запрашиваемой локации.

![image](https://github.com/Nikitavj/WeatherForecast/assets/134765675/10595404-4f92-4f10-a88b-65ce36539e7b)

+ Добавление локации.  
  Адрес - /locations.  
  POST параметр name содержит название, latitude и longitude содержат координаты локации.

+ Удаление локации.   
  Адрес - /locations.  
  POST параметр id содержит порядковый номер локации юзера,  
    _method содержит значение DELETE.

### 3. Страница прогноза погоды для локации.
  Адрес - /forecast.     
  GET параметр id содержит порядковый номер локации юзера.

![image](https://github.com/Nikitavj/WeatherForecast/assets/134765675/b99eec27-aeef-436f-88e0-950c06e85315)

### 4. Страница авторизации.
  Адрес - /logup.  
  POST параметр user_name сожержит email, password и repeat_password содержат пароли.

![image](https://github.com/Nikitavj/WeatherForecast/assets/134765675/09a20c6f-ea05-4d1e-ab0c-44784eae8ca8)

### 5. Страница входа.
  Адрес - /login.  
  POST параметр user_name сожержит email, password содержит пароль.

![image](https://github.com/Nikitavj/WeatherForecast/assets/134765675/9a372bf1-f5b1-4bf8-82d3-0623b8b71011)

### 6. Страница выхода.
  Адрес - /logout.
