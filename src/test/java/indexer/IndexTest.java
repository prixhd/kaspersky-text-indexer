package indexer;

import org.example.Index;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class IndexTest {

    private final Index index = new Index();

    @Test
    void addAndSearch() {
        Path file = Path.of("/test.txt");
        index.add(file, List.of("hello", "world"));

        assertEquals(Set.of(file), index.search("hello"));
        assertTrue(index.search("missing").isEmpty());
    }

    @Test
    void multipleFiles() {
        Path a = Path.of("/a.txt");
        Path b = Path.of("/b.txt");

        index.add(a, List.of("hello", "world"));
        index.add(b, List.of("hello", "java"));

        assertEquals(Set.of(a, b), index.search("hello"));
        assertEquals(Set.of(a), index.search("world"));
        assertEquals(Set.of(b), index.search("java"));
    }

    @Test
    void removeFile() {
        Path file = Path.of("/test.txt");
        index.add(file, List.of("hello"));

        index.remove(file);
        assertTrue(index.search("hello").isEmpty());
    }
}