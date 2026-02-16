import com.adk_demo.service.StockTool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.extension.ExtendWith(SystemStubsExtension.class)
public class StockToolTest {

  @SystemStub
  private EnvironmentVariables environmentVariables;

  @BeforeEach
  void setUp() throws Exception {
    environmentVariables.set("SERVICE_API_KEY", "test-key");
    environmentVariables.set("MODEL_NAME", "test-model");
    environmentVariables.set("API_ENDPOINT", "http://test-endpoint");
    resetStockToolStaticState();
  }

  @AfterEach
  void tearDown() throws Exception {
    resetStockToolStaticState();
  }

  private void resetStockToolStaticState() throws Exception {
    Field initializedField = StockTool.class.getDeclaredField("initialized");
    initializedField.setAccessible(true);
    initializedField.setBoolean(null, false);

    Field finnhubEndpointField = StockTool.class.getDeclaredField("finnhubEndpoint");
    finnhubEndpointField.setAccessible(true);
    finnhubEndpointField.set(null, null);

    Field finnhubKeyField = StockTool.class.getDeclaredField("finnhubKey");
    finnhubKeyField.setAccessible(true);
    finnhubKeyField.set(null, null);
  }

  @Test
  void testFetchStockPricesWithoutSymbol() {
    var result = StockTool.fetchStockPrices(null);
    assertNotNull(result);
    assertTrue(result.containsKey("result"));
    String message = (String) result.get("result");
    System.out.println("Result for null symbol: " + message);
    assertTrue(message.contains("Stock symbol is required"), "Should require a symbol when null");
  }

  @Test
  void testFetchStockPricesWithEmptySymbol() {
    var result = StockTool.fetchStockPrices("");
    assertNotNull(result);
    assertTrue(result.containsKey("result"));
    String message = (String) result.get("result");
    System.out.println("Result for empty symbol: " + message);
    assertTrue(message.contains("Stock symbol is required"), "Should require a symbol when empty");
  }

  @Test
  void testFetchStockPricesWithoutEndpoint() {
    environmentVariables.set("FINNHUB_ENDPOINT", null);
    environmentVariables.set("FINNHUB_API_KEY", "test-api-key");

    Map<String, Object> result = StockTool.fetchStockPrices("AAPL");
    assertTrue(result.containsKey("result"), "Result map should contain 'result' key");
    String resultString = (String) result.get("result");
    assertTrue(resultString.contains("Finnhub endpoint not configured"));
  }

  @Test
  void testFetchStockPricesWithValidSymbol() {
    var result = StockTool.fetchStockPrices("AAPL");
    assertNotNull(result);
    assertTrue(result.containsKey("result"));
    String message = (String) result.get("result");
    System.out.println("Result for AAPL: " + message);
  }

  @Test
  void testFetchStockPricesWithMetaSymbol() {
    var result = StockTool.fetchStockPrices("META");
    assertNotNull(result);
    assertTrue(result.containsKey("result"));
    String message = (String) result.get("result");
    System.out.println("Result for META: " + message);
  }

  @Test
  void testFetchStockPricesWithInvalidSymbol() {
    var result = StockTool.fetchStockPrices("INVALID_SYMBOL_12345");
    assertNotNull(result);
    assertTrue(result.containsKey("result"));
    String message = (String) result.get("result");
    System.out.println("Result for invalid symbol: " + message);
  }
}
