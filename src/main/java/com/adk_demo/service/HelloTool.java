package com.adk_demo.service;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import java.util.Map;

public class HelloTool extends ToolClass {

  private static final String TOOL_NAME = "helloTool";
  private static final String METHOD_NAME = "sayHello";

  public HelloTool() {
    super(TOOL_NAME, METHOD_NAME, FunctionTool.create(HelloTool.class, METHOD_NAME));
  }

  public static Map<String, Object> sayHello(
      @Schema(name = "name", description = "The name of the person to greet") String name) {
    String greeting;
    if (name == null || name.isEmpty()) {
      greeting = "Hello, friend!";
    } else {
      greeting = "Hello, " + name + "! Welcome to ADK Demo!";
    }
    return Map.of("result", greeting);
  }

}
