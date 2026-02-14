# Agent47

A Java project demonstrating the use of Google's Agent Development Kit (ADK) with custom tools including a greeting tool and an RSS feed fetcher.

## Prerequisites

- Java 21 or higher
- Maven 3.6+

## Environment Setup

Create a `.env` file in the project root based on `.env.example`:

```bash
export SERVICE_API_KEY="your-api-key"
export MODEL_NAME="kimi-k2.5:cloud"
export API_ENDPOINT="http://localhost:11434/v1"
export RSS_INDEXER_ENDPOINT="https://indexer_url/rss"
```

## Running the Project

1. Build the project:
```bash
mvn clean package
```

2. Run the application:
```bash
java -jar target/untitled-1.0-SNAPSHOT.jar
```

The application will start an interactive chat interface. Type a message to interact with the agent, or type `exit` or `quit` to close.

## Testing the Project

Run all tests:
```bash
mvn test
```

Run specific test classes:
```bash
mvn test -Dtest=HelloToolTest
mvn test -Dtest=RssToolTest
mvn test -Dtest=AdkConnectorTest
```

## Tools

### HelloTool

A simple greeting tool that generates personalized greetings.

**Method:** `sayHello(String name)`

**Parameters:**
- `name` (String, optional): The name of the person to greet

**Returns:** A Map containing a greeting message under the `result` key.

**Example:**
- Input: "Alice"
- Output: "Hello, Alice! Welcome to ADK Demo!"

### RssTool

Fetches RSS feed data from a configured endpoint and returns a formatted summary.

**Method:** `fetchRssFeed()`

**Parameters:** None (uses the `RSS_INDEXER_ENDPOINT` environment variable)

**Returns:** A Map containing a formatted RSS feed summary under the `result` key.

**Expected API Response JSON Structure:**

The RSS indexer API should return a JSON array with the following structure:

```json
[
  {
    "title": "Article Title 1",
    "description": "A brief description of the article content that may be longer than 100 characters...",
    "link": "https://example.com/article1"
  },
  {
    "title": "Article Title 2",
    "description": "Another article description providing context about the content.",
    "link": "https://example.com/article2"
  },
  {
    "title": "Article Title 3",
    "description": "Third article with its own unique description and link.",
    "link": "https://example.com/article3"
  }
]
```

**Field Descriptions:**
- `title` (String, required): The headline or title of the RSS item
- `description` (String, optional): A summary or description of the content (will be truncated to 100 characters in the output)
- `link` (String, required): The URL to the full article or content

**Example Output:**
```
RSS Feed Summary (3 items):

1. Article Title 1
   Description: A brief description of the article content that may be longer...
   Link: https://example.com/article1

2. Article Title 2
   Description: Another article description providing context about the content.
   Link: https://example.com/article2

3. Article Title 3
   Description: Third article with its own unique description and link.
   Link: https://example.com/article3
```

## Architecture

The project consists of:

- `Main.java`: Entry point with interactive chat loop
- `AdkConnectorClass.java`: Initializes the LLM agent with custom tools
- `HelloTool.java`: Simple greeting tool implementation
- `RssTool.java`: RSS feed fetching tool with HTTP client integration
- `ToolClass.java`: Base class for tool implementations
- `Config.java`: Environment variable configuration loader
- `HttpClient.java`: HTTP client utility for API requests

## Dependencies

- Google ADK (v0.5.0)
- LangChain4J (v1.1.0)
- JUnit Jupiter (v5.10.1) - Testing
- System Stubs Jupiter (v2.1.6) - Environment variable stubbing in tests
- Logback (v1.5.6) - Logging
