package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FileIndexer {

    private final Index index = new Index();
    private final Tokenizer tokenizer;
    private final FileWatcher watcher;
    private final ExecutorService executor;

    public FileIndexer() {
        this(new SimpleTokenizer());
    }

    public FileIndexer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;

        this.executor = Executors.newFixedThreadPool(4);

        this.watcher = new FileWatcher(
                file -> executor.submit(() -> indexFile(file)),
                file -> index.remove(file.toAbsolutePath().normalize())
        );
        this.watcher.start();
    }

    public void addFile(Path file) {
        Path normalized = file.toAbsolutePath().normalize();
        indexFile(normalized);
    }

    public void addDirectory(Path dir) {
        Path normalized = dir.toAbsolutePath().normalize();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(normalized)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    executor.submit(() -> indexFile(entry));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения папки: " + e.getMessage());
        }

        watcher.watchDirectory(normalized);
    }

    public void removeFile(Path file) {
        index.remove(file.toAbsolutePath().normalize());
    }

    public Set<Path> search(String word) {
        if (word == null || word.isBlank()) return Set.of();
        return index.search(word);
    }

    public Set<Path> getFiles() {
        return index.getAllFiles();
    }

    public void close() {
        watcher.stop();
        executor.shutdown();
    }

    private void indexFile(Path file) {
        try {
            String content = Files.readString(file.toAbsolutePath().normalize());
            List<String> words = tokenizer.tokenize(content);
            index.add(file.toAbsolutePath().normalize(), words);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + file + " - " + e.getMessage());
        }
    }
}