package tech.yildirim.aiinsurance.ai.functions;

import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

/** Defines AI-callable functions for scenarios requiring human intervention. */
@Configuration
@Description("Functions for escalating issues to a human operator.")
@Slf4j
public class HandoffFunctions {

  /**
   * Record to encapsulate the reason for escalating to a human operator.
   *
   * @param reason A summary of the user's request that the AI could not handle.
   */
  public record InformHumanOperatorRequest(String reason) {}

  /**
   * A function that logs a request for human operator intervention. In a real-world scenario, this
   * could create a ticket in a CRM, send an email, or trigger a notification.
   *
   * @return A function that takes a reason for handoff and returns a confirmation message.
   */
  @Bean(name = Functions.INFORM_HUMAN_OPERATOR)
  @Description(
      "Use this function ONLY when you cannot fulfill a user's request with any other available tool. This will flag the conversation for a human agent.")
  public Function<InformHumanOperatorRequest, String> informHumanOperator() {
    return request -> {
      log.warn("--- HUMAN OPERATOR ALERT ---");
      log.warn("AI assistant could not handle the request. Reason: {}", request.reason());
      log.warn("Conversation details should be reviewed for follow-up.");
      log.warn("---------------------------");
      return "{\"status\": \"SUCCESS\", \"message\": \"Human operator has been notified.\"}";
    };
  }
}
