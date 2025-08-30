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
      "Escalates the conversation to a human operator when AI cannot handle the request. Use this function ONLY when: "
          + "1) Customer requests something that cannot be fulfilled with available functions, "
          + "2) Customer asks for complex policy changes, claims processing, or underwriting decisions, "
          + "3) Customer is frustrated or explicitly asks to speak with a human agent, "
          + "4) Technical issues prevent you from accessing required information, "
          + "5) Customer needs assistance with sensitive matters like fraud reporting or legal issues, "
          + "6) You encounter errors or system limitations that prevent helping the customer. "
          + "IMPORTANT: This should be your last resort. Always try to help with available functions first. "
          + "When using this function, provide a clear reason explaining why human intervention is needed. "
          + "This will create an alert for human operators to take over the conversation.")
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
