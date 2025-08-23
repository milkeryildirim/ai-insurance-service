package tech.yildirim.aiinsurance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yildirim.aiinsurance.service.ChatService;

/**
 * REST Controller for handling chat requests from the user interface.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  /**
   * Represents the incoming request from the chat interface.
   *
   * @param message The user's message content.
   */
  public record ChatRequest(String message) {

  }

  /**
   * Represents the outgoing response to the chat interface.
   *
   * @param response The AI's response content.
   */
  public record ChatResponse(String response) {

  }

  /**
   * Receives a chat message from the user, processes it via the ChatService, and returns the AI's
   * response.
   *
   * @param chatRequest The request object containing the user's message.
   * @return A response object containing the AI's reply.
   */
  @PostMapping
  public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
    String aiResponse = chatService.getAiResponse(chatRequest.message());
    return new ChatResponse(aiResponse);
  }

}
