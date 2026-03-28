package org.example;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Index {

    private final Map<String, Set<Path>> wordToFiles = new ConcurrentHashMap<>();

    private final Map<Path, Set<String>> fileToWords = new ConcurrentHashMap<>();


    public void add(Path file, List<String> words) {
        remove(file);

        Set<String> wordSet = new HashSet<>(words);
        fileToWords.put(file, wordSet);

        for (String word : wordSet) {
            wordToFiles.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet()).add(file);
        }
    }

    public void remove(Path file) {
        Set<String> words = fileToWords.remove(file);
        if (words == null) return;

        for (String word : words) {
            Set<Path> files = wordToFiles.get(word);
            if (files != null) {
                files.remove(file);
            }
        }
    }

    public Set<Path> search(String word) {
        Set<Path> files = wordToFiles.get(word.toLowerCase());
        if (files == null) return Set.of();
        return Set.copyOf(files);
    }

    public Set<Path> getAllFiles() {
        return Set.copyOf(fileToWords.keySet());
    }
}