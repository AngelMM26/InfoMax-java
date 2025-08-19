package com.example.InfoMax;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler {
    private int crawlLimit = 500;
    private AtomicInteger crawls = new AtomicInteger(0);
    private BlockingQueue<String> urls = new LinkedBlockingQueue<>();
    private Set<String> visited = new HashSet<>();
    private Map<String, Set<String>> graph = new HashMap<>(); // node = {outgoing link)
    private Map<String, Set<String>> graphInbound = new HashMap<>(); // node = {inbound links}

    public void crawl(Indexer indexer, Pattern pattern) {
        while (true) {
            if (crawls.get() >= crawlLimit) {
                break;
            }

            String url;
            try {
                url = urls.poll(2000, TimeUnit.MILLISECONDS);
                if (visited.contains(url) || url == null) {
                    continue;
                }
            } catch (InterruptedException err) {
                break;
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
                if (crawls.incrementAndGet() >= crawlLimit) {
                    break;
                }

            } catch (InterruptedException | IOException err) {
                continue;
            }
        }
    }

    public void initiateQueue(Crawler crawler) {
        urls.add("https://en.wikipedia.org/wiki/New_York_City");
    }

    public void preload(Indexer indexer) {
        InputStream input = getClass().getClassLoader().getResourceAsStream("data/stop_words.txt");
        indexer.loadStopWords(input);
    }

    public void initiateRanking(PageRank ranker) {
        ranker.rank(graph, graphInbound);
    }

    public static void wikibot() {
        Crawler crawler = new Crawler();
        Indexer indexer = new Indexer();

        crawler.initiateQueue(crawler);
        crawler.preload(indexer);

        // Regex to filter out "utility" pages, i.e wiki/Wikipedia:Community_portal,
        // wiki/Help:Introduction, /wiki/Special:Random
        String regex = "^/wiki/(?!Wikipedia:|Portal:|Special:|Category:|File:|Help:|Template:|User:)[^:/]+$";
        Pattern pattern = Pattern.compile(regex);

        int n = 50;
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new Multithreading(crawler, indexer, pattern));
            threads[i].start();
        }

        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException err) {
                continue;
            }
        }

        PageRank ranker = new PageRank();
        crawler.initiateRanking(ranker);

        indexer.outputIndex();
        ranker.outputRanks();
    }

}
