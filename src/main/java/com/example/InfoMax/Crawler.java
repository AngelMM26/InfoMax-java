package com.example.InfoMax;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler {
    public void crawl() {
        int crawlLimit = 500;
        int crawls = 0;

        Queue<String> urls = new LinkedList<>();
        urls.add("https://en.wikipedia.org/wiki/New_York_City");

        Set<String> visited = new HashSet<>();

        // Regex to filter out "utility" pages, i.e wiki/Wikipedia:Community_portal,
        // wiki/Help:Introduction, /wiki/Special:Random
        String regex = "^/wiki/(?!Wikipedia:|Portal:|Special:|Category:|File:|Help:|Template:|User:)[^:/]+$";
        Pattern pattern = Pattern.compile(regex);

        Indexer indexer = new Indexer();
        InputStream input = getClass().getClassLoader().getResourceAsStream("data/stop_words.txt");
        indexer.loadStopWords(input);

        Map<String, Set<String>> graph = new HashMap<>(); // node = {outgoing link)
        Map<String, Set<String>> graphInbound = new HashMap<>(); // node = {inbound
        // links}

        while (urls.size() > 0 && crawls < crawlLimit) {
            String url = urls.remove();
            if (visited.contains(url)) {
                continue;
            }

            try {
                int delay = 1000 + new Random().nextInt(2000);
                Thread.sleep(delay);

                // HTTP GET request to the currURL in an attempt to retrieve data
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                Elements links = doc.select("a");

                Set<String> outgoingLinks = new HashSet<>();
                for (int i = 0; i < links.size(); ++i) {
                    String link = links.get(i).attr("href");
                    if (pattern.matcher(link).matches()) {
                        String fixedLink = "https://en.wikipedia.org" + link;
                        urls.add(fixedLink);
                        outgoingLinks.add(fixedLink);

                        if (graphInbound.containsKey(fixedLink)) {
                            graphInbound.get(fixedLink).add(url);
                        } else {
                            Set<String> inboundLinks = new HashSet<>();
                            inboundLinks.add(url);
                            graphInbound.put(fixedLink, inboundLinks);
                        }
                    }
                }
                graph.put(url, outgoingLinks);
                visited.add(url);

                String content = doc.text();
                indexer.index(content, url, title);

            } catch (IOException | InterruptedException err) {
                continue;
            }
            crawls += 1;
        }

        PageRank ranker = new PageRank();
        ranker.rank(graph, graphInbound);

        indexer.outputIndex();
        ranker.outputRanks();
    }

}
