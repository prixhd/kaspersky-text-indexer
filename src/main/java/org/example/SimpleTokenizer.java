package org.example;

import java.util.ArrayList;
import java.util.List;


public class SimpleTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String[] parts = text.split("\\s+");
        List<String> words = new ArrayList<>();

        for (String part : parts) {
            String clean = part.replaceAll("[^a-zA-Zа-яА-Я0-9]", "").toLowerCase();
            if (!clean.isEmpty()) {
                words.add(clean);
            }
        }
        return words;
    }
}