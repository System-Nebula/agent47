import com.adk_demo.service.HelloTool;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HelloToolTest {

    @Test
    void testSayHelloWithValidName() {
        Map<String, Object> result = HelloTool.sayHello("Alice");
        assertEquals("Hello, Alice! Welcome to ADK Demo!", result.get("result"));
    }

    @Test
    void testSayHelloWithAnotherName() {
        Map<String, Object> result = HelloTool.sayHello("Bob");
        assertEquals("Hello, Bob! Welcome to ADK Demo!", result.get("result"));
    }

    @Test
    void testSayHelloWithNullName() {
        Map<String, Object> result = HelloTool.sayHello(null);
        assertEquals("Hello, friend!", result.get("result"));
    }

    @Test
    void testSayHelloWithEmptyName() {
        Map<String, Object> result = HelloTool.sayHello("");
        assertEquals("Hello, friend!", result.get("result"));
    }

    @Test
    void testSayHelloReturnsMapWithResultKey() {
        Map<String, Object> result = HelloTool.sayHello("Test");
        assertTrue(result.containsKey("result"), "Result map should contain 'result' key");
    }

    @Test
    void testHelloToolConstructor() {
        HelloTool tool = new HelloTool();
        assertNotNull(tool, "HelloTool instance should not be null");
        assertNotNull(tool.GetCustomTool(), "FunctionTool should not be null");
    }
}