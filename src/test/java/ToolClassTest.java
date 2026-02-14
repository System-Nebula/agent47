import com.adk_demo.service.HelloTool;
import com.adk_demo.service.ToolClass;
import com.google.adk.tools.FunctionTool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolClassTest {

    @Test
    void testGetCustomToolName() {
        ToolClass tool = new HelloTool();
        assertEquals("helloTool", tool.GetCustomToolName());
    }

    @Test
    void testSetToolName() {
        ToolClass tool = new HelloTool();
        tool.setToolName("newToolName");
        assertEquals("newToolName", tool.GetCustomToolName());
    }

    @Test
    void testGetCustomMethodName() {
        ToolClass tool = new HelloTool();
        assertEquals("sayHello", tool.GetCustomMethodName());
    }

    @Test
    void testSetCustomMethodName() {
        ToolClass tool = new HelloTool();
        tool.setCustomMethodName("newMethodName");
        assertEquals("newMethodName", tool.GetCustomMethodName());
    }

    @Test
    void testGetCustomTool() {
        ToolClass tool = new HelloTool();
        FunctionTool functionTool = tool.GetCustomTool();
        assertNotNull(functionTool, "FunctionTool should not be null");
    }
}