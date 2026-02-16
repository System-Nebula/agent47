package com.adk_demo.service;

import com.adk_demo.config.Config;
import com.adk_demo.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.tools.FunctionTool;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RssTool extends ToolClass {

  private static final Logger logger = Logger.getLogger(StockTool.class.getName());
  private static final String TOOL_NAME = "rssTool";
  private static final String METHOD_NAME = "fetchRssFeed";
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static String rssEndpoint = null;
  private static boolean initialized = false;
  private static final int MAX_DESCRIPTION_LENGTH = 100;

  // Java Records are a transparent carrier for inmutable data
  public record RssItem(String title, String description, String link) {
    public static RssItem fromMap(Map<String, Object> map) {
      return new RssItem(
          (String) map.getOrDefault("title", "No title"),
          (String) map.getOrDefault("description", "No description"),
          (String) map.getOrDefault("link", "No link"));
    }
  }

  public static String truncateDescription(String description) {
    if (description == null || description.length() <= MAX_DESCRIPTION_LENGTH) {
      return description;
    }
    return description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
  }

  public RssTool() {
    super(TOOL_NAME, METHOD_NAME, FunctionTool.create(StockTool.class, METHOD_NAME));
    initEndpoint();
  }

  // Using thread syncrhonization for initEndpoint
  private static synchronized void initEndpoint() {
    if (initialized)
      return;
    try {
      Config config = new Config();
      rssEndpoint = config.RssIndexerEndpoint;
      logger.info("RSS endpoint initialized: " + rssEndpoint);
    } catch (Exception e) {
      rssEndpoint = null;
      logger.warning("Failed to initialize RSS endpoint: " + e.getMessage());
    }
    initialized = true;
  }

  public static Map<String, Object> fetchRssFeed() {
    if (!initialized) {
      initEndpoint();
    }
    if (rssEndpoint == null || rssEndpoint.isEmpty()) {
      return Map.of("result", "RSS Indexer endpoint not configured. Set RSS_INDEXER_ENDPOINT environment variable.");
    }

    try {
      HttpClient httpClient = new HttpClient(rssEndpoint);
      int statusCode = httpClient.GetStatusCode();
      logger.info("HTTP status code: " + statusCode);

      String responseBody = httpClient.GetResponseBody();
      logger.info("Response body length: " + (responseBody != null ? responseBody.length() : "null"));

      if (responseBody == null) {
        String error = httpClient.GetLastError();
        return Map.of("result",
            "Failed to fetch RSS feed from " + rssEndpoint + " (status: " + statusCode + ", error: " + error + ")");
      }

      List<Map<String, Object>> items = objectMapper.readValue(responseBody, List.class);

      String summary = IntStream.range(0, items.size())
          .mapToObj(i -> {
            RssItem item = RssItem.fromMap(items.get(i));
            return String.format("%d. %s\n   Description: %s\n   Link: %s\n\n",
                i + 1, item.title(), truncateDescription(item.description()), item.link());
          })
          .collect(Collectors.joining());

      return Map.of("result", "RSS Feed Summary (" + items.size() + " items):\n\n" + summary);

    } catch (Exception e) {
      logger.severe("Error fetching RSS feed: " + e.getMessage());
      return Map.of("result", "Error fetching RSS feed: " + e.getMessage());
    }
  }
}
