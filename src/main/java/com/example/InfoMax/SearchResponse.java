package com.example.InfoMax;

import java.util.Map;

public class SearchResponse {
    private Map<String, String> completeResults;

    public SearchResponse(Map<String, String> results) {
        this.completeResults = results;
    }

    public Map<String, String> getResults() {
        return completeResults;
    }

    public void setResults(Map<String, String> results) {
        this.completeResults = results;
    }
}
