package indexer;

import org.example.FileIndexer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

class FileIndexerTest {

    @TempDir
    Path tempDir;

    FileIndexer indexer;

    @BeforeEach
    void setUp() {
        indexer = new FileIndexer();
    }

    @AfterEach
    void tearDown() {
        indexer.close();
    }

    @Test
    void addFileAndSearch() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello world");

        indexer.addFile(file);

        assertFalse(indexer.search("hello").isEmpty());
        assertTrue(indexer.search("missing").isEmpty());
    }

    @Test
    void addDirectoryAndSearch() throws IOException, InterruptedException {
        Files.writeString(tempDir.resolve("a.txt"), "Hello");
        Files.writeString(tempDir.resolve("b.txt"), "World");

        indexer.addDirectory(tempDir);

        waitUntil(() -> indexer.getFiles().size() == 2);

        assertEquals(2, indexer.getFiles().size());
    }

    @Test
    void removeFile() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello");

        indexer.addFile(file);
        indexer.removeFile(file);

        assertTrue(indexer.search("hello").isEmpty());
    }

    @Test
    void detectsNewFileInWatchedDirectory() throws IOException, InterruptedException {
        indexer.addDirectory(tempDir);

        Thread.sleep(500);

        Files.writeString(tempDir.resolve("new.txt"), "surprise");

        waitUntil(() -> !indexer.search("surprise").isEmpty());

        assertFalse(indexer.search("surprise").isEmpty(),
                "Новый файл должен автоматически полностьж проиндексироваться");
    }

    @Test
    void searchEmptyOrNull() {
        assertTrue(indexer.search("").isEmpty());
        assertTrue(indexer.search(null).isEmpty());
        assertTrue(indexer.search("  ").isEmpty());
    }


    private void waitUntil(BooleanSupplier condition) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            if (condition.getAsBoolean()) return;
            Thread.sleep(100);
        }
    }
}