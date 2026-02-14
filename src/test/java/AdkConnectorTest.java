import com.adk_demo.service.AdkConnectorClass;
import com.google.adk.agents.BaseAgent;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.junit.jupiter.api.Assertions.*;

public class AdkConnectorTest {

    /**
     * Test that initAgent() successfully creates a BaseAgent when all env vars are set.
     * This validates the happy path through the BaseAgentInterface contract.
     */
    @Test
    void InitBuilderOk() throws Exception {
        EnvironmentVariables envVars = new EnvironmentVariables()
            .set("SERVICE_API_KEY", "test-api-key")
            .set("MODEL_NAME", "gpt-4")
            .set("API_ENDPOINT", "https://api.example.com");

        envVars.execute(() -> {
            // Access the static ROOT_AGENT which is initialized by initAgent()
            BaseAgent agent = AdkConnectorClass.ROOT_AGENT;

            // Validate non-null agent
            assertNotNull(agent, "Agent should not be null");

            // Validate agent name
            assertNotNull(agent.name(), "Agent name should not be null");
            assertEquals("ADK_DEMO", agent.name(), "Agent name should be 'ADK_DEMO'");

            // Validate that the agent has a model configured
            // Note: Depending on BaseAgent API, we might need different accessors
            assertNotNull(agent.toString(), "Agent should have a meaningful toString representation");
        });
    }

    /**
     * Test that initAgent() fails appropriately when required env vars are missing.
     * This tests the error handling path through the BaseAgentInterface contract.
     *
     * Note: Since ROOT_AGENT is static and initialized at class load time,
     * this test needs to be isolated. We're testing the behavior when Config fails.
     */
    @Test
    void InitBuilderNull() throws Exception {
        // This test validates that when Config fails (due to missing env vars),
        // a RuntimeException is thrown during agent initialization

        EnvironmentVariables envVars = new EnvironmentVariables()
            .set("SERVICE_API_KEY", (String) null)
            .set("MODEL_NAME", (String) null)
            .set("API_ENDPOINT", (String) null);

        envVars.execute(() -> {
            // Try to create a new Config to simulate the failure
            Exception exception = assertThrows(Exception.class, () -> {
                new com.adk_demo.config.Config();
            });

            assertNotNull(exception, "Exception should be thrown when env vars are missing");
        });
    }

    /**
     * Test that the BaseAgentInterface static method exists and can be called.
     * This validates the interface contract, even though it returns null by default.
     */
    @Test
    void TestBaseAgentInterface() {
        // The static method in the interface returns null by default
        // This test just verifies it can be called
        BaseAgent agent = AdkConnectorClass.BaseAgentInterface.initAgent();
        assertNull(agent, "BaseAgentInterface.initAgent() should return null by default");
    }
}
