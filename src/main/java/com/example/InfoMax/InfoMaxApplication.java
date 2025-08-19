package com.example.InfoMax;

import java.time.LocalTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InfoMaxApplication {
	public static void main(String[] args) {
		Boolean crawl = false;
		if (crawl) {
			LocalTime start = LocalTime.now();
			System.out.println(start);
			Crawler.wikibot();
			LocalTime end = LocalTime.now();
			System.out.println(end);
		}
		SpringApplication.run(InfoMaxApplication.class, args);
	}

}
