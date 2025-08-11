package com.example.InfoMax;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Repository
public class IndexRepository {
    private final ObjectMapper mapper;
    private Map<String, Map<String, Integer>> invertedIndex;
    private Map<String, String> documents;
    private Map<String, Integer> df;
    private Set<String> stopWords = new HashSet<>();
    private Map<String, Double> pageRanks;

    public IndexRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void load() {
        try {
            invertedIndex = mapper.readValue(new File("InfoMax/src/main/resources/data/invertedIndex.json"),
                    new TypeReference<Map<String, Map<String, Integer>>>() {
                    });
            documents = mapper.readValue(new File("InfoMax/src/main/resources/data/documents.json"),
                    new TypeReference<Map<String, String>>() {
                    });
            df = mapper.readValue(new File("InfoMax/src/main/resources/data/df.json"),
                    new TypeReference<Map<String, Integer>>() {
                    });
            pageRanks = mapper.readValue(new File("InfoMax/src/main/resources/data/pagerank.json"),
                    new TypeReference<Map<String, Double>>() {
                    });

            InputStream filePath = getClass().getClassLoader().getResourceAsStream("data/stop_words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    stopWords.add(line);
                }
            }
        } catch (IOException err) {
            System.err.println("Error loading: " + err.getMessage());
        }
    }

    public Map<String, Map<String, Integer>> getIndex() {
        return invertedIndex;
    }

    public Map<String, String> getDocs() {
        return documents;
    }

    public Map<String, Integer> getDf() {
        return df;
    }

    public Set<String> getStopwords() {
        return stopWords;
    }

    public Map<String, Double> getPageranks() {
        return pageRanks;
    }
}
