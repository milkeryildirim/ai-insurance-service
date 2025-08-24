package tech.yildirim.aiinsurance.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for handling chat interactions with the AI model. This class acts as a
 * bridge between the controller and the Spring AI ChatClient.
 */
@Service
public class ChatService {

  private static final String DEFAULT_SYSTEM_PROMPT = """
        You are a helpful and polite virtual insurance agent named 'InsuranceAgent'.
        Your task is to assist customers with their insurance policies.
        You must always be professional and courteous.
        You must answer in the same language as the user's question.
        
        Before performing any action like updating an address or filing a claim,
        you must know the user's policy number. If you don't know it, you MUST ask for it first.
        Do not ask for any other personal identification information unless it's required for a specific tool.
        """;

  private final ChatClient chatClient;

  public ChatService(ChatClient.Builder builder) {
    PromptChatMemoryAdvisor promptChatMemoryAdvisor =
        PromptChatMemoryAdvisor.builder(
                MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .maxMessages(100)
                    .build())
            .build();
    VertexAiGeminiChatOptions vertexAiGeminiChatOptions =
        VertexAiGeminiChatOptions.builder().toolNames(InsuranceFunctions.FUNCTIONS).build();

    this.chatClient =
        builder
            .defaultAdvisors(promptChatMemoryAdvisor)
            .defaultOptions(vertexAiGeminiChatOptions)
            .defaultSystem(DEFAULT_SYSTEM_PROMPT)
            .build();
  }

  /**
   * Sends a user's message to the configured AI model and returns the response.
   *
   * @param message The text message from the user.
   * @return The generated response content from the AI model as a String.
   */
  public String getAiResponse(String message) {
    return chatClient.prompt().user(message).call().content();
  }
}
