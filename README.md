# Text Indexer

Библиотека для индексации текстовых файлов по словам с поддержкой многопоточной индексации и автоматического отслеживания изменений файлов на диске.

## Требования

- Java 17+
- Maven 3.8+

## Сборка проекта

```bash
git clone https://github.com/prixhd/kaspersky-text-indexer
cd kaspersky-text-indexer
mvn clean package
```

## Запуск приложеня
```
java -cp target/kaspersky-text-indexer-1.0-SNAPSHOT.jar org.example.App
```

## Команды приложения
```text
Команды: add 'путь', search 'слово', list, exit

-> add /home/user/documents
Папка добавлена. Файлов: 5

-> search hello
Найдено в 2 файлах:
  /home/user/documents/readme.txt
  /home/user/documents/notes.txt

-> list
Файлы (5):
  /С/documents/readme.txt
  /С/documents/notes.txt
  /С/documents/todo.txt
  /С/documents/log.txt
  /С/documents/config.txt

-> exit
До свидания
```

## Запуск тестов
```bash

mvn test
```