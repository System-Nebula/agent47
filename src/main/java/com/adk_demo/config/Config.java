package com.adk_demo.config;

import java.util.logging.Logger;

public class Config {

  public String ApiKey;
  public String ModelName;
  public String ApiEndpoint;
  public String RssIndexerEndpoint;

  public Config() throws Exception {
    Logger logger = Logger.getLogger(Config.class.getName());
    this.ApiKey = System.getenv("SERVICE_API_KEY");
    this.ModelName = System.getenv("MODEL_NAME");
    this.ApiEndpoint = System.getenv("API_ENDPOINT");
    this.RssIndexerEndpoint = System.getenv("RSS_INDEXER_ENDPOINT");
    if (ApiKey == null || ModelName == null || ApiEndpoint == null) {
      logger.severe("The following envars are needed: SERVICE_API_KEY MODEL_NAME API_ENDPOINT");
      throw new Exception();
    }
    if (RssIndexerEndpoint == null) {
      logger.warning("RSS_INDEXER_ENDPOINT not set - RSS tool will be unavailable");
    }
  }

  public interface configInterface {
    public void Config();
  }
}
