package com.example.InfoMax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PageRank {
    private Map<String, Double> ranks = new HashMap<>();
    private Double dampningFactor = 0.85;
    private Integer iterations = 100;
    private Double threshold = 1e-6;

    public void rank(Map<String, Set<String>> graph, Map<String, Set<String>> graphInbound) {
        ranks.clear();

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
        for (Integer iter = 0; iter < iterations; ++iter) {
            Double sinkSum = 0.0;
            for (String sink : sinks) {
                sinkSum += ranks.get(sink);
            }
            sinkSum /= N;

            Map<String, Double> newRanks = new HashMap<>();
            Double l1 = 0.0;
            for (String node : nodes) {
                Double sumPR = 0.0;
                for (String link : graphInbound.get(node)) {
                    Integer numOutgoing = graph.get(link).size();
                    if (numOutgoing > 0) {
                        sumPR += ranks.get(link) / numOutgoing;
                    }
                }
                newRanks.put(node, (1 - dampningFactor) / N + dampningFactor * (sumPR + sinkSum));
                l1 += Math.abs(ranks.get(node) - newRanks.get(node));
            }

            ranks = newRanks;

            // After 10 iterations check if change is less than the threshold for early stop
            if (iter >= 9) { // Zero based indexing, this is still 10 iterations
                if (11 < threshold) {
                    System.out.println("Converged after " + iter + " iterations");
                    break;
                }
            }
        }
        System.out.println("All iterations needed");

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
