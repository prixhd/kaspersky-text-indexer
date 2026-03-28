package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class FileWatcher {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();
    private final Consumer<Path> onChanged;
    private final Consumer<Path> onDeleted;
    private volatile boolean running = true;
    private Thread thread;

    public FileWatcher(Consumer<Path> onChanged, Consumer<Path> onDeleted) {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать WatchService", e);
        }
        this.onChanged = onChanged;
        this.onDeleted = onDeleted;
    }


    public void watchDirectory(Path dir) {
        try {
            WatchKey key = dir.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );
            keys.put(key, dir);
        } catch (IOException e) {
            System.err.println("Не удалось следить за папкой: " + dir);
        }
    }


    public void start() {
        thread = new Thread(() -> {
            while (running) {
                WatchKey key;
                try {
                    key = watchService.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                } catch (InterruptedException | ClosedWatchServiceException e) {
                    return;
                }

                if (key == null) {
                    continue;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path file = dir.resolve(pathEvent.context());

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE
                            || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        if (Files.isRegularFile(file)) {
                            onChanged.accept(file);
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        onDeleted.accept(file);
                    }
                }

                key.reset();
            }
        }, "file-watcher");

        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
        try {
            watchService.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии WatchService");
        }
    }
}