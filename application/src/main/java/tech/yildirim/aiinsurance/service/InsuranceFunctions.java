package tech.yildirim.aiinsurance.service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/**
 * This class defines the functions (tools) that the AI model can invoke. Each function is
 * registered as a Spring Bean with a descriptive name and purpose. The AI uses these descriptions
 * to decide which function to call based on the user's prompt.
 */
@Configuration
@RequiredArgsConstructor
public class InsuranceFunctions {

  public static final Set<String> FUNCTIONS = Set.of("getCustomerById", "getPoliciesByCustomerId");



  /**
   * A function that retrieves a customer's details by their unique ID. The AI will use this
   * function when asked for specific information about a customer.
   *
   * @return A function that takes a customer ID and returns the customer's data.
   */
  @Bean
  @Description("Get customer details by customer ID")
  public Function<GetCustomerByIdRequest, CustomerDto> getCustomerById() {
    return null;
  }

  /**
   * A function that retrieves all policies for a given customer ID. The AI will use this when the
   * user asks about the policies of a specific customer.
   *
   * @return A function that takes a customer ID and returns a list of their policies.
   */
  @Bean
  @Description("Get all policies for a given customer ID")
  public Function<GetPoliciesByCustomerIdRequest, List<PolicyDto>> getPoliciesByCustomerId() {
    return null;
  }

  /**
   * Record to encapsulate the request for getting a customer by ID.
   *
   * @param customerId The unique identifier of the customer.
   */
  public record GetCustomerByIdRequest(Long customerId) {}

  /**
   * Record to encapsulate the request for getting policies by customer ID.
   *
   * @param customerId The unique identifier of the customer.
   */
  public record GetPoliciesByCustomerIdRequest(Long customerId) {}
}
