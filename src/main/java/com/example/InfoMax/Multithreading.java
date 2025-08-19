package com.example.InfoMax;

import java.util.regex.Pattern;

public class Multithreading implements Runnable {

    private Crawler crawler;
    private Indexer indexer;
    private Pattern pattern;

    public Multithreading(Crawler crawler, Indexer indexer, Pattern pattern) {
        this.crawler = crawler;
        this.indexer = indexer;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        crawler.crawl(indexer, pattern);
    }

}
