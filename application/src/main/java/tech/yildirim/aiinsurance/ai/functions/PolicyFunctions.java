package tech.yildirim.aiinsurance.ai.functions;

import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.PoliciesApiClient;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/** Defines all AI-callable functions related to policy management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for retrieving policy details")
public class PolicyFunctions {

  private final PoliciesApiClient policiesApiClient;

  // --- Request Record for Type Safety ---

  /**
   * Record to encapsulate the request for getting policy details by ID.
   *
   * @param policyId The unique technical identifier of the policy.
   */
  public record GetPolicyByIdRequest(Long policyId) {}

  public record GetPolicyByPolicyNumberRequest(String policyNumber) {}

  @Bean(Functions.GET_POLICY_BY_ID)
  @Description(
      "Get policy details by its unique technical ID. Use this after identifying the user to get more details about a policy.")
  public Function<GetPolicyByIdRequest, PolicyDto> getPolicyById() {
    return request ->
        Objects.requireNonNull(policiesApiClient.getPolicyById(request.policyId()).getBody());
  }

  @Bean(Functions.GET_POLICY_BY_POLICY_NUMBER)
  @Description(
      "Get policy details by its unique policy number. Use this to identify a policy when it is provided to the user.")
  public Function<GetPolicyByPolicyNumberRequest, PolicyDto> getPolicyByPolicyNumber() {
    return request ->
        Objects.requireNonNull(
            policiesApiClient.getPolicyByPolicyNumber(request.policyNumber()).getBody());
  }
}
