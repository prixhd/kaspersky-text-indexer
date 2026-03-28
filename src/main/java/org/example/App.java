package org.example;

import java.nio.file.*;
import java.util.Scanner;
import java.util.Set;

public class App {

    public static void main(String[] args) {
        System.out.println("--- Text Indexer ---");
        System.out.println("Команды: add 'путь', search 'слово', list, exit");
        System.out.println();

        FileIndexer indexer = new FileIndexer();
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.print("-> ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2);
                String command = parts[0].toLowerCase();

                try {
                    switch (command) {
                        case "add" -> {
                            if (parts.length < 2) {
                                System.out.println("Укаажите путь: add 'путь'");
                                continue;
                            }
                            Path path = Paths.get(parts[1]);

                            if (Files.isDirectory(path)) {
                                indexer.addDirectory(path);

                                Thread.sleep(500);
                                System.out.println("Пaпка добавлена. Файлов: " + indexer.getFiles().size());
                            } else if (Files.isRegularFile(path)) {
                                indexer.addFile(path);
                                System.out.println("Файл добавлен");
                            } else {
                                System.out.println("Путь не найден: " + path);
                            }
                        }

                        case "search" -> {
                            if (parts.length < 2) {
                                System.out.println("Укажите слово: search 'слово'");
                                continue;
                            }
                            Set<Path> results = indexer.search(parts[1]);
                            if (results.isEmpty()) {
                                System.out.println("Ничего не найдено");
                            } else {
                                System.out.println("Найдено в " + results.size() + " файлах: ");
                                for (Path p : results) {
                                    System.out.println("  " + p);
                                }
                            }
                        }

                        case "list" -> {
                            Set<Path> files = indexer.getFiles();
                            if (files.isEmpty()) {
                                System.out.println("Индекс пуст");
                            } else {
                                System.out.println("Файлы (" + files.size() + "): ");
                                for (Path p : files) {
                                    System.out.println("  " + p);
                                }
                            }
                        }

                        case "exit" -> {
                            System.out.println("До свидания");
                            return;
                        }

                        default -> System.out.println("Неизвестная команда. Доступны: add, search, list, exit");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        } finally {
            indexer.close();
        }
    }
}