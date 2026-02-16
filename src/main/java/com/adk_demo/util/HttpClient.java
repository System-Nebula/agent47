package com.adk_demo.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HttpClient {

  private static final Logger logger = Logger.getLogger(HttpClient.class.getName());
  private static final CloseableHttpClient httpClient;
  
  static {
    try {
      SSLContext sslContext = SSLContexts.custom()
          .loadTrustMaterial(null, (chain, authType) -> true)
          .build();
      
      SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
          sslContext, NoopHostnameVerifier.INSTANCE);
      
      PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
          .setSSLSocketFactory(sslSocketFactory)
          .build();
      
      httpClient = HttpClients.custom()
          .setConnectionManager(connectionManager)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize HTTP client", e);
    }
  }
  
  private String urlString;
  private int lastStatusCode = -1;
  private String lastResponseBody;
  private String lastError;
  private Map<String, String> headers = new HashMap<>();

  public HttpClient() {}

  public HttpClient(String urlString) {
    this.urlString = urlString;
  }

  public void SetUrl(String urlString) {
    this.urlString = urlString;
    this.lastStatusCode = -1;
    this.lastResponseBody = null;
    this.lastError = null;
  }

  public String GetUrl() {
    return this.urlString;
  }

  private void fetch() {
    if (urlString == null || urlString.isEmpty()) {
      lastError = "URL is null or empty";
      return;
    }
    if (lastResponseBody != null) {
      return;
    }
    
    try {
      logger.info("Making HTTP request to: " + urlString);
      HttpGet httpGet = new HttpGet(urlString);
      
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        httpGet.setHeader(entry.getKey(), entry.getValue());
        logger.info("Adding header: " + entry.getKey() + " = " + entry.getValue());
      }
      
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        lastStatusCode = response.getCode();
        logger.info("HTTP response code: " + lastStatusCode);
        lastResponseBody = EntityUtils.toString(response.getEntity());
        logger.info("Response body length: " + (lastResponseBody != null ? lastResponseBody.length() : 0));
      }
    } catch (Exception e) {
      lastError = e.getClass().getName() + ": " + e.getMessage();
      logger.severe("HTTP request failed: " + lastError);
      if (e.getCause() != null) {
        logger.severe("Caused by: " + e.getCause().getMessage());
      }
    }
  }

  public int GetStatusCode() {
    fetch();
    return lastStatusCode;
  }

  public String GetResponseBody() {
    fetch();
    return lastResponseBody;
  }

  public String GetLastError() {
    return lastError;
  }

  public void SetHeader(String name, String value) {
    headers.put(name, value);
  }

  public void ClearHeaders() {
    headers.clear();
  }
}