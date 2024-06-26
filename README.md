# Java Project "ShareIt"
ShareIt - cервис для шеринга вещей.

## Основные возможности
Сервис обеспечивает пользователям возможность:

* рассказывать, какими вещами они готовы поделиться;
* находить нужную вещь и брать её в аренду на какое-то время.
 
При бронировании вещи на определённые даты, сервис позволяет закрывать к ней доступ на время бронирования от других желающих. На случай, если нужной вещи в сервисе нет, у пользователей есть возможность оставлять запросы. Есть возможность отвечать на запрос другого пользователя.  По запросу можно добавлять новые вещи для шеринга. Пользователи могут оставлять отзывы на вещь после того, как взяли её в аренду.

## Технологии
* JDK Amazon Coretto version 11 - кроссплатформенный дистрибутив OpenJDK с долгосрочной поддержкой;
* Spring Boot 2.7.2 - модуль фреймворка Spring для создания приложений с использованием Java;
* Maven - инструмент для автоматической сборки проектов на Java;
* Lombok - основанная на аннотациях библиотека Java, позволяющая сократить шаблонный код;
* JDBC - API для взаимодействия с базами данных;
* PostgreSQL - свободная объектно-реляционная система управления базами данных.
* Docker.

## Запуск приложения
* Выполнить команду mvn clean install
* Далее команда docker-compose up
* Используемая версия языка Java - 11
