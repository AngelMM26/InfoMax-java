package com.example.InfoMax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final Map<String, Map<String, Integer>> invertedIndex;
    private final Map<String, String> documents;
    private final Map<String, Integer> df;
    private final Set<String> stopWords;
    private final Map<String, Double> pageRanks;
    private final Double N;

    // Spring Injection
    public HomeController(Indexer indexer, PageRank ranker) {
        this.invertedIndex = indexer.getIndex();
        this.documents = indexer.getDocuments();
        this.df = indexer.getDf();
        this.stopWords = indexer.getStopwords();
        this.pageRanks = ranker.getPageranks();
        this.N = ranker.getTotaldocs();
    }

    @GetMapping("/search")
    public SearchResponse search(@RequestParam("q") String query) {
        query = query.toLowerCase();
        // Keeps letters, apostrophes, and whitespace, but removes any digits
        query = query.replaceAll("[^a-z'\\s]", "").replaceAll("//d", "");
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

        Map<String, String> completeResults = new HashMap<>();
        for (String result : results) {
            completeResults.put(result, documents.get(result));
        }

        System.out.println(completeResults);

        return new SearchResponse(completeResults);
    }

}
