# InfoMax 🔍

**InfoMax** is a lightweight search engine that crawls Wikipedia pages, indexes content using an inverted index, ranks results with PageRank, and serves them through a sleek frontend built with JavaScript and styled CSS. It's a complete search engine implementation built from scratch using Java and Spring Boot for the backend. :)

---

## 📁 Project Structure

```
InfoMax/
│
├── src/
│   └── main/
│       ├── java/com/example/InfoMax/
│       │   ├── Crawler.java             # Crawls and collects pages from Wikipedia
│       │   ├── Indexer.java             # Builds the inverted index
│       │   ├── PageRank.java            # Implements PageRank algorithm
│       │   ├── IndexRepository.java     # Loads/saves JSON index data
│       │   ├── SearchService.java       # Query processing, scoring, results
│       │   ├── SearchController.java    # /search REST API
│       │   ├── HomeController.java      # Serves pages (if needed)
│       │   └── InfoMaxApplication.java  # Spring Boot entry point (+ crawl toggle)
│       │
│       └── resources/
│           ├── data/
│           │   ├── df.json              # JSON file storing document frequency (df)
│           │   ├── documents.json       # JSON file mapping URLS to website titles
│           │   ├── invertedIndex.json   # JSON file storing the inverted index
│           │   ├── pagerank.json        # JSON file storing the PageRank scores
│           │   └── stop_words.txt       # txt.file containing stopwords from https://countwordsfree.com/stopwords
│           └── static/
│               ├── index.html           # Search page interface
│               ├── results.html         # Result page interface
│               ├── style.css            # CSS styling
│               └── results.css          # CSS styling
│
└── README.md                            # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ (or the version your Spring Boot project supports)
- Maven (for build & dependency management)
- Spring Boot

### Install Dependencies
Spring Boot & other dependencies are managed via pom.xml.
From the project root, run:

```bash
mvn clean install
```

---

## 🧠 How It Works

### 1. **Crawling**
- `Crawler.java` uses Jsoup to fetch adn parse Wikipedia pages starting from a seed URL.
- It extracts internal Wikipedia links and visits them up to a defined `CRAWL_LIMIT`.

### 2. **Indexing**
- `Indexer.java` tokenizes and preprocesses the content of crawled pages.
- It builds an inverted index mapping terms to a set of URLs containing them.
- The index is stored in `invertedindex.json`.

### 3. **Ranking**
- `PageRank.java` implements the **PageRank** algorithm.
- Pages with more inbound links from other high ranked pages are scored higher.
- Results are saved to `pagerank.json`.

### 4. **Search Interface**
- Users enter a query into the search bar in `index.html`.
- `SearchController.java` processes the query, retrieves matching documents, ranks them, and sends JSON results back to the browser.
- Links are displayed dynamically in the browser.

---

## 🔎 Query Flow

1. **Preprocessing**: Stopwords are removed, punctuation is stripped, and text is lowercased.
2. **Intersection**: Only documents that match _all_ query tokens are returned.
3. **Ranking**: Results are sorted in descending order of PageRank.

---

## 🌐 Running the Project

1. Open InfoMaxApplication and set crawl to true (if not satisfied with sample data, may take a bit to run depending on desired CRAWL_LIMIT)

2. Start the Spring Boot server (Run InfoMaxApplication.java)

3. Open `http://localhost:8080` in your browser.

---

## 💡 Features

- Custom web crawler for Wikipedia
- Inverted index for efficient keyword lookup
- PageRank scoring system
- RESTful API built with Spring Boot
- Clean, responsive search interface using JavaScript and CSS

---

## 📌 Future Enhancements

- Introduce **multi-threaded crawling** for speed ✅ 

---

## 🧑‍💻 Author

Angel Mejia Martinez  
Computer Science Major, NYU  
[LinkedIn](https://www.linkedin.com/in/angel-mejia-martinez-3b0a09252/) · [GitHub](https://github.com/AngelMM26)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).