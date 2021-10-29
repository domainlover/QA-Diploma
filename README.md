# Дипломный проект профессии «Тестировщик»
Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.

![](https://github.com/domainlover/QA-diploma/blob/master/webservice.png)

Приложение предлагает купить тур по определённой цене с помощью двух способов:

1. Произвести 100% оплату по банковской карте.
1. Произвести оплату в кредит с помощью кредитной карты.

## Инструкция по подключению БД и запуску SUT
Для запуска проекта необходим установленный на ПК пользователя **[Docker](https://www.docker.com/)**.

1. Склонировать проект из репозитория командой: ``` git clone https://github.com/domainlover/QA-Diploma.git ```
1. Для запуска контейнеров с MySql, PostgreSQL и Node.js использовать команду:

``` docker-compose up -d --force-recreate ```

3. Запуск SUT:
- для интеграции с MySQL ввести в терминале команду:

``` java -Dspring.datasource.url=jdbc:mysql://localhost:3306/app  -jar artifacts/aqa-shop.jar```

- для интеграции с PostgreSQL ввести в терминале команду:

``` java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app  -jar artifacts/aqa-shop.jar ```

4. Запуск тестов (Allure):
-  для запуска на MySQL ввести в терминале команду:

``` ./gradlew clean test -Ddb.url=jdbc:mysql://localhost:3306/app allureReport ```

- для запуска на PostgreSQL ввести в терминале команду:

``` ./gradlew clean test -Ddb.url=jdbc:postgresql://localhost:5432/app allureReport ```

5. Для получения браузерного отчета Allure ввести в терминале команду:

``` ./gradlew allureServe ```

6. После окончания тестов:
- завершить работу приложения в терминале (Ctrl+C)
- остановить контейнеры командой: ``` docker-compose down ```

