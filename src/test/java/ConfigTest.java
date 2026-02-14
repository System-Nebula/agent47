import com.adk_demo.config.Config;
import com.adk_demo.config.Config.configInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    private configInterface config;

    @BeforeEach
    public void setUp() {
        config = new configInterface() {
            private Config configInstance;

            @Override
            public void Config() {
                try {
                    configInstance = new Config();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            public Config getConfigInstance() {
                return configInstance;
            }
        };
    }

    @Test
    public void testConfigSetterNoEnvars() {
        EnvironmentVariables envVars = new EnvironmentVariables()
            .set("SERVICE_API_KEY", (String) null)
            .set("MODEL_NAME", (String) null)
            .set("API_ENDPOINT", (String) null);

        Exception exception = assertThrows(Exception.class, () -> {
            envVars.execute(() -> {
                config.Config();
            });
        });

        assertNotNull(exception);
    }

    @Test
    public void testConfigSetterMissingEnvar() {
        EnvironmentVariables envVars = new EnvironmentVariables()
            .set("SERVICE_API_KEY", "test-key")
            .set("MODEL_NAME", "gpt-4")
            .set("API_ENDPOINT", (String) null);

        Exception exception = assertThrows(Exception.class, () -> {
            envVars.execute(() -> {
                config.Config();
            });
        });

        assertNotNull(exception);
    }

    @Test
    public void testConfigOK() {
        EnvironmentVariables envVars = new EnvironmentVariables()
            .set("SERVICE_API_KEY", "test-api-key")
            .set("MODEL_NAME", "gpt-4")
            .set("API_ENDPOINT", "https://api.example.com");

        assertDoesNotThrow(() -> {
            envVars.execute(() -> {
                config.Config();
            });
        });
    }

}
