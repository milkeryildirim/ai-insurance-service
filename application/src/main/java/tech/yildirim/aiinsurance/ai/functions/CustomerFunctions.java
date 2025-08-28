package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/** Defines all AI-callable functions related to customer management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for retrieving customer and policy information")
public class CustomerFunctions {

  private final CustomersApiClient customersApiClient;

  // --- Request Records for Type Safety ---
  public record GetCustomerByPolicyNumberRequest(String policyNumber) {}

  public record GetPoliciesByCustomerIdRequest(Long customerId) {}

  @Bean(Functions.GET_CUSTOMER_BY_POLICY_NUMBER)
  @Description(
      "Finds a customer by their unique policy number. Use this to identify a customer when they provide a policy number.")
  public Function<GetCustomerByPolicyNumberRequest, CustomerDto> getCustomerByPolicyNumber() {
    return request ->
        Objects.requireNonNull(
            customersApiClient.getCustomerByPolicyNumber(request.policyNumber()).getBody());
  }

  @Bean(Functions.GET_POLICIES_BY_CUSTOMER_ID)
  @Description("Get all policies for a given customer ID.")
  public Function<GetPoliciesByCustomerIdRequest, List<PolicyDto>> getPoliciesByCustomerId() {
    return request ->
        Objects.requireNonNull(
            customersApiClient.getPoliciesByCustomerId(request.customerId()).getBody());
  }
}
