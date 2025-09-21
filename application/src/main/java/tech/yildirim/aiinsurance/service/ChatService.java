package tech.yildirim.aiinsurance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import tech.yildirim.aiinsurance.ai.functions.Functions;

/**
 * Service layer responsible for handling chat interactions with the AI model. This class acts as a
 * bridge between the controller and the Spring AI ChatClient.
 */
@Service
@Slf4j
public class ChatService {

  private final ChatClient chatClient;

  public ChatService(
      ChatClient.Builder builder, @Value("${config.default-prompt}") String defaultPrompt) {

    PromptChatMemoryAdvisor promptChatMemoryAdvisor =
        PromptChatMemoryAdvisor.builder(
                MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .maxMessages(100)
                    .build())
            .build();
    VertexAiGeminiChatOptions vertexAiGeminiChatOptions =
        VertexAiGeminiChatOptions.builder().toolNames(Functions.ALL_FUNCTIONS).build();

    this.chatClient =
        builder
            .defaultAdvisors(promptChatMemoryAdvisor)
            .defaultOptions(vertexAiGeminiChatOptions)
            .defaultSystem(defaultPrompt)
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

  /**
   * Generates a personalized welcome message for the authenticated user. Extracts the customer ID
   * from the OIDC token and uses AI to create a greeting.
   *
   * @param oidcUser The authenticated OIDC user with ID token
   * @return A personalized welcome message from the AI assistant
   */
  public String generateWelcomeMessage(OidcUser oidcUser) {
    try {
      // Extract customer ID from the insurance_user_id claim
      Long customerId = extractCustomerIdFromToken(oidcUser);

      if (customerId != null) {
        // Create a prompt for AI to generate welcome message with customer context
        String welcomePrompt =
            String.format(
                "Generate a warm, professional welcome message for the customer. "
                    + "Use the getCustomerById function with customer ID %d to get their information. "
                    + "Address them by their first name and introduce yourself as Martin, their AI insurance assistant. "
                    + "Keep it friendly and ask how you can help them today. "
                    + "Respond in the same language as the customer's preferred language if available, otherwise use English.",
                customerId);

        return chatClient.prompt().user(welcomePrompt).call().content();
      } else {
        // Fallback welcome message when customer ID is not available
        return "Hello! I'm Martin, your AI insurance assistant. How can I help you today?";
      }
    } catch (Exception e) {
      log.error("Error generating welcome message", e);
      return "Hello! I'm Martin, your AI insurance assistant. How can I help you today?";
    }
  }

  /**
   * Extracts the insurance customer ID from the OIDC user's ID token.
   *
   * @param oidcUser The authenticated OIDC user
   * @return The customer ID if found, null otherwise
   */
  private Long extractCustomerIdFromToken(OidcUser oidcUser) {
    if (oidcUser == null || oidcUser.getIdToken() == null) {
      log.warn("OIDC user or ID token is null");
      return null;
    }

    final Object customerIdClaim = oidcUser.getIdToken().getClaim("insurance_user_id");
    if (customerIdClaim == null) {
      log.warn("insurance_user_id claim not found in ID token");
      return null;
    }

    try {
      return switch (customerIdClaim) {
        case Integer customerId -> customerId.longValue();
        case Long customerId -> customerId;
        case String customerId -> Long.parseLong(customerId);
        default -> {
          log.warn("insurance_user_id claim has unexpected type: {}", customerIdClaim.getClass());
          yield null;
        }
      };
    } catch (NumberFormatException e) {
      log.error("Failed to parse insurance_user_id claim: {}", customerIdClaim, e);
      return null;
    }
  }
}
