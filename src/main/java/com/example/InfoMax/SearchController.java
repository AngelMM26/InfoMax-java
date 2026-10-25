package com.example.InfoMax;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    static class SearchResponse {
        public Map<String, String> results;

        public SearchResponse(Map<String, String> results) {
            this.results = results;
        }
    }

    @GetMapping("/api/search")
    public SearchResponse getResults(@RequestParam("q") String query) {
        long start = System.nanoTime();
        Map<String, String> results = searchService.search(query);
        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("Query \"%s\": %d results in %.2f ms%n",query, results.size(), elapsedMs);
        return new SearchResponse(results);
    }

}
