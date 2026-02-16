package com.adk_demo.service;

import com.adk_demo.config.Config;
import com.adk_demo.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import java.util.Map;
import java.util.logging.Logger;

public class StockTool extends ToolClass {

  private static final Logger logger = Logger.getLogger(StockTool.class.getName());
  private static final String TOOL_NAME = "stockTool";
  private static final String METHOD_NAME = "fetchStockPrices";
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static String finnhubEndpoint = null;
  private static String finnhubKey = null;
  private static boolean initialized = false;

  /*
   * c -> Current price
   * h -> highest price of the day
   * l -> lowest price of the day
   * o -> open price of the day
   * pc -> previous close price
   */
  public record QuoteItem(Float c, Float h, Float l, Float o, Float pc) {
    public static QuoteItem fromMap(Map<String, Object> map) {
      return new QuoteItem(
          map.containsKey("c") ? Float.parseFloat(map.get("c").toString()) : 0.01f,
          map.containsKey("h") ? Float.parseFloat(map.get("h").toString()) : 0.01f,
          map.containsKey("l") ? Float.parseFloat(map.get("l").toString()) : 0.01f,
          map.containsKey("o") ? Float.parseFloat(map.get("o").toString()) : 0.01f,
          map.containsKey("pc") ? Float.parseFloat(map.get("pc").toString()) : 0.01f);
    }
  }

  public StockTool() {
    super(TOOL_NAME, METHOD_NAME, FunctionTool.create(StockTool.class, METHOD_NAME));
    initEndpoint();
  }

  // Using thread syncrhonization for initEndpoint
  private static synchronized void initEndpoint() {
    if (initialized)
      return;
    try {
      Config config = new Config();
      if (config.FinnhubEndpoint != null && !config.FinnhubEndpoint.isEmpty()) {
        finnhubEndpoint = config.FinnhubEndpoint + "/quote?symbol=";
      } else {
        finnhubEndpoint = null;
      }
      finnhubKey = config.FinnhubApiKey;
      logger.info("Finnhub endpoint initialized: " + finnhubEndpoint);
      logger.info("Finnhub API key initialized: " + (finnhubKey != null ? "YES" : "NO"));
    } catch (Exception e) {
      finnhubEndpoint = null;
      finnhubKey = null;
      logger.warning("Failed to initialize Finnhub endpoint: " + e.getMessage());
    }
    initialized = true;
  }

  public static Map<String, Object> fetchStockPrices(
      @Schema(name = "symbol", description = "The stock symbol to fetch prices for (e.g., AAPL, META, ORCL)") String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      return Map.of("result", "Stock symbol is required. Please provide a valid stock symbol (e.g., AAPL, META, ORCL)");
    }
    if (!initialized) {
      initEndpoint();
    }
    if (finnhubEndpoint == null || finnhubEndpoint.isEmpty()) {
      return Map.of("result", "Finnhub endpoint not configured. Set FINNHUB_ENDPOINT environment variable.");
    }
    if (finnhubKey == null || finnhubKey.isEmpty()) {
      return Map.of("result", "Finnhub API KEY not configured. Set FINNHUB_API_KEY environment variable.");
    }

    try {
      String url = finnhubEndpoint + symbol;
      HttpClient httpClient = new HttpClient(url);
      if (finnhubKey != null && !finnhubKey.isEmpty()) {
        httpClient.SetHeader("X-Finnhub-Token", finnhubKey);
      }
      int statusCode = httpClient.GetStatusCode();
      logger.info("HTTP status code: " + statusCode);

      String responseBody = httpClient.GetResponseBody();
      logger.info("Response body length: " + (responseBody != null ? responseBody.length() : "null"));

      if (responseBody == null) {
        String error = httpClient.GetLastError();
        return Map.of("result",
            "Failed to fetch stock prices for " + symbol + " from " + url + " (status: " + statusCode + ", error: "
                + error
                + ")");
      }

      Map<String, Object> data = objectMapper.readValue(responseBody, Map.class);
      QuoteItem item = QuoteItem.fromMap(data);

      String prices = String.format(
          "Stock Quote Summary for %s:\n1. Current Price: $%.2f\n2. Day High: $%.2f\n3. Day Low: $%.2f\n4. Open: $%.2f\n5. Previous Close: $%.2f",
          symbol.toUpperCase(), item.c(), item.h(), item.l(), item.o(), item.pc());

      return Map.of("result", prices);

    } catch (Exception e) {
      logger.severe("Error fetching the stock prices: " + e.getMessage());
      return Map.of("result", "Error fetching stock prices for " + symbol + ": " + e.getMessage());
    }
  }
}
