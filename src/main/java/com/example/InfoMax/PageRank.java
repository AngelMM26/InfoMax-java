package com.example.InfoMax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PageRank {

    private Map<String, Double> ranks = new HashMap<>();
    private Double totalDocs;

    public void rank(Map<String, Set<String>> graph, Map<String, Set<String>> graphInbound, Integer totalCrawls) {

        Double dampningFactor = 0.85;
        Integer iterations = 100;

        totalDocs = (double) totalCrawls;
        Set<String> nodes = new HashSet<>();
        for (String entry : graph.keySet()) {
            nodes.add(entry);
            nodes.addAll(graph.get(entry));
        }
        Integer N = nodes.size();

        // Initialize rankings
        for (String node : nodes) {
            ranks.put(node, 1.0 / N);
        }

        // Nodes with no outgoing links or that have not been crawled
        ArrayList<String> sinks = new ArrayList<>();
        for (String node : nodes) {
            if (!graphInbound.containsKey(node) || graph.getOrDefault(node, new HashSet<>()).size() == 0) {
                sinks.add(node);
            }
        }

        // Redistribute rank from sinks equally to all nodes during each iteration
        for (Integer i = 0; i < iterations; ++i) {
            Double sinkSum = 0.0;
            for (String sink : sinks) {
                sinkSum += ranks.get(sink);
            }
            sinkSum /= N;

            Map<String, Double> newRanks = new HashMap<>();
            for (String node : nodes) {
                Double sumPR = 0.0;
                for (String link : graphInbound.get(node)) {
                    Integer numOutgoing = graph.get(link).size();
                    if (numOutgoing > 0) {
                        sumPR += ranks.get(link) / numOutgoing;
                    }
                }
                newRanks.put(node, (1 - dampningFactor) / N + dampningFactor * (sumPR + sinkSum));
            }
            ranks = newRanks;
        }

    }

    public Map<String, Double> getPageranks() {
        return ranks;
    }

    public Double getTotaldocs() {
        return totalDocs;
    }

    public void outputRanks() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<Map.Entry<String, Double>> sortedList = new ArrayList<>(ranks.entrySet());
            sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            Map<String, Double> sortedRanks = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : sortedList) {
                sortedRanks.put(entry.getKey(), entry.getValue());
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File("InfoMax/src/main/resources/data/pagerank.json"),
                    sortedRanks);
        } catch (IOException err) {
            System.err.println("Error writing pagerank.json file: " + err.getMessage());
        }
    }
}
