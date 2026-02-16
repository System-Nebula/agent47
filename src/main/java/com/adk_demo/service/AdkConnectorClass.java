package com.adk_demo.service;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.adk_demo.config.Config;
import com.google.adk.models.langchain4j.LangChain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public class AdkConnectorClass {
  public static BaseAgent ROOT_AGENT = initAgent();

  private static BaseAgent initAgent() {
    Config cfg = null;
    try {
      cfg = new Config();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    ChatModel localModel = OpenAiChatModel.builder()
        .baseUrl(cfg.ApiEndpoint)
        .apiKey(cfg.ApiKey)
        .modelName(cfg.ModelName)
        .logRequests(true)
        .build();

    HelloTool helloTool = new HelloTool();
    RssTool rssTool = new RssTool();
    StockTool stockTool = new StockTool();

    return LlmAgent.builder().name("ADK_DEMO")
        .model(new LangChain4j(localModel))
        .instruction(
            "You are a helpful assistant. When you receive tool results, always present the information clearly to the user in a readable format.")
        .tools(helloTool.GetCustomTool(), rssTool.GetCustomTool(), stockTool.GetCustomTool())
        .build();
  }

  public interface BaseAgentInterface {
    @Contract(pure = true)
    public static @Nullable BaseAgent initAgent() {
      return null;
    }
  }

}
