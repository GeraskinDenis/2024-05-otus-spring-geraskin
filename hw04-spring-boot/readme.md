# Программа тестирования
Программа демонстрирует работу 'Spring Shell' и использования механизма локализации.

## Настройка
* [Файл настроек](./src/main/resources/application.yml)
  + 'test: rightAnswersCountToPass: 3' - указывается количество правильных ответов для прохождения теста
  + 'test: locale: en-US' - for English
  + 'test: locale: ru-RU' - Русский
* [List of questions on English](./src/main/resources/questions.csv)
* [Список вопосов на Русском](./src/main/resources/questions_ru.csv)
* [Баннер при старте](./src/main/resources/banner.txt)

## Используемые технологии
* Spring Boot 3
* Spring Shell