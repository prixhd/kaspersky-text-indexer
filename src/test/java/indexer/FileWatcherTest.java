package indexer;

import org.example.FileWatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

class FileWatcherTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsNewFile() throws IOException, InterruptedException {
        AtomicBoolean detected = new AtomicBoolean(false);

        FileWatcher watcher = new FileWatcher(
                file -> detected.set(true),
                file -> {}
        );

        watcher.watchDirectory(tempDir);
        watcher.start();

        Files.writeString(tempDir.resolve("new.txt"), "hello");

        waitUntil(detected::get);

        assertTrue(detected.get(), "Watcher должен был заметить новый файл");
        watcher.stop();
    }

    @Test
    void detectsDeletedFile() throws IOException, InterruptedException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "hello");

        AtomicBoolean deleted = new AtomicBoolean(false);

        FileWatcher watcher = new FileWatcher(
                f -> {},
                f -> deleted.set(true)
        );

        watcher.watchDirectory(tempDir);
        watcher.start();

        Files.delete(file);

        waitUntil(deleted::get);

        assertTrue(deleted.get(), "Watcher должен был заметить удаление");
        watcher.stop();
    }

    private void waitUntil(BooleanSupplier condition) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            if (condition.getAsBoolean()) return;
            Thread.sleep(100);
        }
    }
}