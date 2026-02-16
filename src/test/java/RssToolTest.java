import com.adk_demo.service.RssTool;
import com.adk_demo.service.RssTool.RssItem;
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
public class RssToolTest {

  @SystemStub
  private EnvironmentVariables environmentVariables;

  @BeforeEach
  void setUp() throws Exception {
    environmentVariables.set("SERVICE_API_KEY", "test-key");
    environmentVariables.set("MODEL_NAME", "test-model");
    environmentVariables.set("API_ENDPOINT", "http://test-endpoint");
    resetRssToolStaticState();
  }

  @AfterEach
  void tearDown() throws Exception {
    resetRssToolStaticState();
  }

  private void resetRssToolStaticState() throws Exception {
    Field initializedField = RssTool.class.getDeclaredField("initialized");
    initializedField.setAccessible(true);
    initializedField.setBoolean(null, false);

    Field rssEndpointField = RssTool.class.getDeclaredField("rssEndpoint");
    rssEndpointField.setAccessible(true);
    rssEndpointField.set(null, null);
  }

  @Test
  void testRssItemFromMapWithValidFields() {
    Map<String, Object> map = Map.of(
        "title", "Test Title",
        "description", "Test Description",
        "link", "http://test.com/link");

    RssItem rssItem = RssItem.fromMap(map);
    assertNotNull(rssItem);
    assertEquals("Test Title", rssItem.title());
    assertEquals("Test Description", rssItem.description());
    assertEquals("http://test.com/link", rssItem.link());
  }

  @Test
  void testRssItemFromMapWithMissingFields() {
    Map<String, Object> map = Map.of();

    RssItem rssItem = RssItem.fromMap(map);
    assertNotNull(rssItem);
    assertEquals("No title", rssItem.title());
    assertEquals("No description", rssItem.description());
    assertEquals("No link", rssItem.link());
  }

  @Test
  void testRssItemFromMapWithPartialFields() {
    Map<String, Object> map = Map.of("title", "Partial Title");

    RssItem rssItem = RssItem.fromMap(map);
    assertNotNull(rssItem);
    assertEquals("Partial Title", rssItem.title());
    assertEquals("No description", rssItem.description());
    assertEquals("No link", rssItem.link());
  }

  @Test
  void testTruncateDescriptionShort() {
    String shortDesc = "Short description";
    String result = RssTool.truncateDescription(shortDesc);
    assertEquals("Short description", result);
  }

  @Test
  void testTruncateDescriptionExactLength() {
    String exactDesc = "A".repeat(100);
    String result = RssTool.truncateDescription(exactDesc);
    assertEquals(exactDesc, result);
    assertEquals(100, result.length());
  }

  @Test
  void testTruncateDescriptionLong() {
    String longDesc = "A".repeat(150);
    String result = RssTool.truncateDescription(longDesc);
    assertEquals(103, result.length());
    assertTrue(result.endsWith("..."));
  }

  @Test
  void testTruncateDescriptionNull() {
    String result = RssTool.truncateDescription(null);
    assertNull(result);
  }

  @Test
  void testTruncateDescriptionEmpty() {
    String result = RssTool.truncateDescription("");
    assertEquals("", result);
  }

  @Test
  void testConstructor() {
    RssTool tool = new RssTool();
    assertNotNull(tool, "RssTool instance should not be null");
    assertNotNull(tool.GetCustomTool(), "FunctionTool should not be null");
    assertEquals("rssTool", tool.GetCustomToolName());
    assertEquals("fetchRssFeed", tool.GetCustomMethodName());
  }

  @Test
  void testFetchRssFeedWithoutEndpoint() {
    environmentVariables.set("RSS_INDEXER_ENDPOINT", null);
    Map<String, Object> result = RssTool.fetchRssFeed();
    assertTrue(result.containsKey("result"), "Result map should contain 'result' key");
    String resultString = (String) result.get("result");
    assertTrue(resultString.contains("RSS Indexer endpoint not configured"));
  }

  @Test
  void testFetchRssFeedWithInvalidEndpoint() {
    environmentVariables.set("RSS_INDEXER_ENDPOINT", "http://invalid-endpoint-that-does-not-exist.local");

    Map<String, Object> result = RssTool.fetchRssFeed();
    assertTrue(result.containsKey("result"), "Result map should contain 'result' key");
    String resultString = (String) result.get("result");
    assertTrue(resultString.contains("Failed to fetch RSS feed") || resultString.contains("Error fetching RSS feed"));
  }

  @Test
  void testFetchRssFeedReturnsMapWithResultKey() {
    environmentVariables.set("RSS_INDEXER_ENDPOINT", "");
    Map<String, Object> result = RssTool.fetchRssFeed();
    assertTrue(result.containsKey("result"), "Result map should contain 'result' key");
    assertNotNull(result.get("result"), "Result value should not be null");
  }

  @Test
  void testToolNameConstant() {
    RssTool tool = new RssTool();
    assertEquals("rssTool", tool.GetCustomToolName());
  }

  @Test
  void testMethodNameConstant() {
    RssTool tool = new RssTool();
    assertEquals("fetchRssFeed", tool.GetCustomMethodName());
  }
}
