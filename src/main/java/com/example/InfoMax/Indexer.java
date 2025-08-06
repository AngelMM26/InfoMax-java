package com.example.InfoMax;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Indexer {

    private Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();
    private Map<String, String> documents = new HashMap<>();
    private Map<String, Integer> df = new HashMap<>();
    private Set<String> stopWords = new HashSet<>();

    public void index(String content, String url, String title) {
        documents.put(url, title);
        String[] tokens = content.split("\\s+");

        Set<String> visited = new HashSet<>();
        for (String token : tokens) {
            token = token.toLowerCase();
            // Keeps letters, apostrophes, and whitespace, but removes any digits
            token = token.replaceAll("[^a-z'\\s]", "").replaceAll("//d", "");

            if (token.isEmpty()) {
                continue;
            }

            if (!stopWords.contains(token)) {
                if (invertedIndex.containsKey(token)) {
                    if (invertedIndex.get(token).containsKey(url)) {
                        Integer newFreq = invertedIndex.get(token).get(url) + 1;
                        invertedIndex.get(token).put(url, newFreq);
                    } else {
                        invertedIndex.get(token).put(url, 1);
                    }
                    Integer newFreq = invertedIndex.get(token).get(url) + 1;
                    invertedIndex.get(token).put(url, newFreq);
                } else {
                    Map<String, Integer> freq = new HashMap<>();
                    freq.put(url, 1);
                    invertedIndex.put(token, freq);
                }
            }

            if (!visited.contains(token)) {
                if (df.containsKey(token)) {
                    df.put(token, df.get(token) + 1);
                } else {
                    df.put(token, 1);
                }
            }
        }

    }

    public void loadStopWords(InputStream filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePath));) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    stopWords.add(line);
                }
            }
        } catch (IOException err) {
            System.err.println("Error reading stop_words.txt");
        }
    }

    public Map<String, Map<String, Integer>> getIndex() {
        return invertedIndex;
    }

    public Map<String, String> getDocuments() {
        return documents;
    }

    public Map<String, Integer> getDf() {
        return df;
    }

    public Set<String> getStopwords() {
        return stopWords;
    }

    public void outputIndex() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File("InfoMax/src/main/resources/data/invertedIndex.json"),
                    invertedIndex);
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File("InfoMax/src/main/resources/data/documents.json"),
                    documents);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("InfoMax/src/main/resources/data/df.json"), df);
        } catch (IOException err) {
            System.err.println("Error writing invertedIndex.json/documents.json file: " + err.getMessage());
        }
    }
}
