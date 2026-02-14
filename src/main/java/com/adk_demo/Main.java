package com.adk_demo;

import com.adk_demo.service.AdkConnectorClass;
import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    InMemoryRunner runner = new InMemoryRunner(AdkConnectorClass.ROOT_AGENT, "test-app");
    RunConfig runConfig = RunConfig.builder().build();
    Scanner scanner = new Scanner(System.in);
    System.out.println("Chat ready. Type 'exit' to quit.");

    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
        System.out.println("Goodbye!");
        break;
      }

      if (input.isEmpty())
        continue;

      Session session = runner.sessionService()
          .createSession(runner.appName(), "user-123", null, null)
          .blockingGet();

      Content content = Content.fromParts(Part.fromText(input));
      Flowable<Event> events = runner.runAsync(session.userId(), session.id(), content, runConfig);
      events.blockingForEach(event -> {
        if (event.finalResponse()) {
          System.out.println(event.stringifyContent());
        }
      });
    }

    scanner.close();
  }
}
