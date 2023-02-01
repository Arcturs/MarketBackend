# Веб-сервис маркета
В данном репозитории представлен исходный код для курсовой работы по теме: *Использование Debezium в веб-сервисе для реализации шаблона разработки захвата изменения данных*.

## Использованные технологии:
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html);
- [Maven](https://maven.apache.org/download.cgi) последняя версия;
- [Spring boot 3.0.2](https://spring.io/projects/spring-boot);
- [PostgreSQL 15.1](https://www.postgresql.org/);
- [Liquibase](https://www.liquibase.org/);
- [Spring security 6.0.1](https://spring.io/projects/spring-security);
- [Json web tokens](https://jwt.io/);
- [Swagger](https://swagger.io/) документация;
- [Testcontainers](https://www.testcontainers.org/) для интеграционных тестов;
- [Docker](https://www.docker.com/).

## Что реализовано:
На данный момент практически полностью реализована логика сервиса маркета. Больше про сам сервис в [readme маркета](MarketServer/README.md).

## Запуск приложения
### Необходимые технологии:
Для запуска приложения понадобится только докер. Для запуска тестов необходимо установить JDK и maven. Стоит отметить, что на данный момент запуститься только сервис маркета, а для него настроен CI/CD в гитхабе. А значит на мастере лежит рабочая версия (надеюсь).

### Запуск
В папке приложения market в терминале запустить следующую команду:
```
docker-compose up -d
```

### Запуск тестов
Для запуска тестов необходимо перейти в папку MarketServer и следовать инструкции [readme](MarketServer/README.md).

## Документация
Чтобы просмотреть все эндпоинты маркета, необходимо перейти по ссылке: http://localhost:8080/api/v1/swagger-ui/index.html.
В дальнейшем будет добавлена папка со всеми схемами приложения.

## План действий:
Следующие шаги по подготовке курсовой работы:
0) Параллельно с разработкой идет написание текста курсовой;
1) Закончить реализацию маркета, включая подключение debezium;
2) Начать реализацию другого сервиса - склада (наверное, это уже за рамками курсовой);
3) Соединение работы маркета и склада, используя Kafka.