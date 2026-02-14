package com.adk_demo.service;

import com.google.adk.tools.FunctionTool;

public class ToolClass {
  private String toolName;
  private final FunctionTool functionTool;
  private String customMethod;

  protected ToolClass(String toolName, String customMethod, FunctionTool functionTool) {
    this.toolName = toolName;
    this.functionTool = functionTool;
    this.customMethod = customMethod;
  }

  public String GetCustomToolName() {
    return toolName;
  }

  public void setToolName(String name) {
    this.toolName = name;
  }

  public String GetCustomMethodName() {
    return customMethod;
  }

  public void setCustomMethodName(String name) {
    this.customMethod = name;
  }

  public FunctionTool GetCustomTool() {
    return functionTool;
  }

}
