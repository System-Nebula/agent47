import com.adk_demo.util.HttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpClientTest {

    @Test
    void testDefaultConstructor() {
        HttpClient client = new HttpClient();
        assertNotNull(client, "HttpClient instance should not be null");
        assertNull(client.GetUrl(), "URL should be null after default constructor");
    }

    @Test
    void testConstructorWithUrl() {
        HttpClient client = new HttpClient("http://example.com");
        assertNotNull(client, "HttpClient instance should not be null");
        assertEquals("http://example.com", client.GetUrl());
    }

    @Test
    void testSetUrl() {
        HttpClient client = new HttpClient();
        client.SetUrl("http://example.com");
        assertEquals("http://example.com", client.GetUrl());
    }

    @Test
    void testSetUrlMultipleTimes() {
        HttpClient client = new HttpClient("http://first.com");
        assertEquals("http://first.com", client.GetUrl());
        client.SetUrl("http://second.com");
        assertEquals("http://second.com", client.GetUrl());
    }

    @Test
    void testGetUrl() {
        HttpClient client = new HttpClient();
        assertNull(client.GetUrl());
        
        client.SetUrl("http://test.com");
        assertEquals("http://test.com", client.GetUrl());
    }

    @Test
    void testGetStatusCodeWithNullUrl() {
        HttpClient client = new HttpClient();
        int statusCode = client.GetStatusCode();
        assertEquals(-1, statusCode);
        assertNotNull(client.GetLastError(), "Error message should be set");
        assertTrue(client.GetLastError().contains("URL is null or empty"));
    }

    @Test
    void testGetResponseBodyWithNullUrl() {
        HttpClient client = new HttpClient();
        String responseBody = client.GetResponseBody();
        assertNull(responseBody);
        assertNotNull(client.GetLastError(), "Error message should be set");
        assertTrue(client.GetLastError().contains("URL is null or empty"));
    }

    @Test
    void testGetLastError() {
        HttpClient client = new HttpClient();
        String error = client.GetLastError();
        assertNull(error, "Error should be null before any request");
        
        client.GetStatusCode();
        assertNotNull(client.GetLastError(), "Error should be set after failed request");
    }

    @Test
    void testGetStatusCodeWithInvalidUrl() {
        HttpClient client = new HttpClient("http://invalid-url-that-does-not-exist.local.invalid");
        int statusCode = client.GetStatusCode();
        assertTrue(statusCode == -1, "Status code should be -1 for invalid URL");
        assertNotNull(client.GetLastError(), "Error message should be set");
    }

    @Test
    void testGetResponseBodyWithInvalidUrl() {
        HttpClient client = new HttpClient("http://invalid-url-that-does-not-exist.local.invalid");
        String responseBody = client.GetResponseBody();
        assertNull(responseBody, "Response body should be null for invalid URL");
        assertNotNull(client.GetLastError(), "Error message should be set");
    }

    @Test
    void testSetUrlClearsPreviousError() {
        HttpClient client = new HttpClient("http://invalid-url.local.invalid");
        client.GetStatusCode();
        String firstError = client.GetLastError();
        assertNotNull(firstError);
        
        client.SetUrl("http://another-invalid-url.local.invalid");
        String errorAfterSetUrl = client.GetLastError();
        assertNull(errorAfterSetUrl, "Error should be cleared after SetUrl");
    }

    @Test
    void testSetUrlClearsPreviousResponse() {
        HttpClient client = new HttpClient("http://invalid-url.local.invalid");
        client.GetStatusCode();
        
        client.SetUrl("http://another-url.local.invalid");
        assertEquals(-1, client.GetStatusCode(), "Status code should be reset to -1 after SetUrl");
    }
}
