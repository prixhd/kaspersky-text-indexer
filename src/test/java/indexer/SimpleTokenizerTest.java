package indexer;

import org.example.SimpleTokenizer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTokenizerTest {

    private final SimpleTokenizer tokenizer = new SimpleTokenizer();

    @Test
    void simpleText() {
        assertEquals(List.of("hello", "world"), tokenizer.tokenize("Hello World"));
    }

    @Test
    void withPunctuation() {
        assertEquals(List.of("hello", "world"), tokenizer.tokenize("Hello, world!"));
    }

    @Test
    void emptyAndNull() {
        assertTrue(tokenizer.tokenize("").isEmpty());
        assertTrue(tokenizer.tokenize(null).isEmpty());
    }

    @Test
    void withNumbers() {
        assertEquals(List.of("java", "17"), tokenizer.tokenize("Java 17"));
    }
}