package tech.yildirim.aiinsurance.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for handling chat interactions with the AI model.
 * This class acts as a bridge between the controller and the Spring AI ChatClient.
 */
@Service

public class ChatService {

  private final ChatClient chatClient;

  public ChatService(ChatClient.Builder builder) {
    PromptChatMemoryAdvisor promptChatMemoryAdvisor = PromptChatMemoryAdvisor.builder(
        MessageWindowChatMemory.builder().chatMemoryRepository(new InMemoryChatMemoryRepository())
            .maxMessages(100).build()).build();
    this.chatClient = builder.defaultAdvisors(promptChatMemoryAdvisor).build();
  }

  /**
   * Sends a user's message to the configured AI model and returns the response.
   *
   * @param message The text message from the user.
   * @return The generated response content from the AI model as a String.
   */
  public String getAiResponse(String message) {
    return chatClient.prompt()
        .user(message)
        .call()
        .content();
  }

}
