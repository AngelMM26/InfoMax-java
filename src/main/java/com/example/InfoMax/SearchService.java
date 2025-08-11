package com.example.InfoMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final IndexRepository indexRepo;

    public SearchService(IndexRepository indexRepo) {
        this.indexRepo = indexRepo;
    }

    public Map<String, String> search(String query) {
        Map<String, Map<String, Integer>> invertedIndex = indexRepo.getIndex();
        Map<String, String> documents = indexRepo.getDocs();
        Map<String, Integer> df = indexRepo.getDf();
        Set<String> stopWords = indexRepo.getStopwords();
        Map<String, Double> pageRanks = indexRepo.getPageranks();
        Double N = (double) indexRepo.getDocs().size();

        query = query.toLowerCase();
        // Keeps letters, apostrophes, and whitespace, but removes any digits
        query = query.replaceAll("[^a-z'\\s]", "").replaceAll("\\d", "");
        String[] tokens = query.split("\\s++");

        Set<String> pages = new HashSet<>();
        Map<String, Double> idfScores = new HashMap<>();
        for (String token : tokens) {
            if (!stopWords.contains(token) && invertedIndex.containsKey(token)) {
                if (pages.isEmpty()) {
                    pages.addAll(invertedIndex.get(token).keySet());
                } else {
                    // Set intersection to find common pages
                    pages.retainAll(invertedIndex.get(token).keySet());
                }
                idfScores.put(token, Math.log(N / df.get(token)));
            }

        }

        List<String> results = new ArrayList<>(pages);
        Map<String, Double> finalRanks = new HashMap<>();
        for (String page : results) {
            Double tfIdf = 0.0;
            for (String token : tokens) {
                if (!stopWords.contains(token) && invertedIndex.containsKey(token)) {
                    Integer tf = invertedIndex.get(token).get(page);
                    tfIdf += tf * idfScores.get(token);
                }
            }
            finalRanks.put(page, pageRanks.get(page) * tfIdf);
        }

        results.sort((page1, page2) -> {
            Double rank1 = finalRanks.get(page1);
            Double rank2 = finalRanks.get(page2);
            return rank2.compareTo(rank1);
        });

        Map<String, String> completeResults = new LinkedHashMap<>();
        for (String result : results) {
            completeResults.put(result, documents.get(result));
        }

        return completeResults;
    }

}
