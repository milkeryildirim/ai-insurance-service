package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.PoliciesApiClient;
import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyConditionsDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.CreatePolicyReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAllPoliciesReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAutoClaimsByPolicyIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHealthClaimsByPolicyIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHomeClaimsByPolicyIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetPolicyByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetPolicyByPolicyNumberReq;
import tech.yildirim.aiinsurance.model.ai.request.GetPolicyConditionsReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdatePolicyConditionsReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdatePolicyReq;
import tech.yildirim.aiinsurance.security.SecuredAI;

/** Defines all AI-callable functions related to policy management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for retrieving policy details")
public class PolicyFunctions {

  private final PoliciesApiClient policiesApiClient;

  @Bean(Functions.GET_POLICY_BY_ID)
  @SecuredAI
  @Description(
      "Retrieves detailed policy information using the policy's unique internal ID. Use this function when: "
          + "1) You have a policy ID from previous customer or policy lookups, "
          + "2) You need to get complete policy details after identifying the policy, "
          + "3) Following up on policy information where ID is already known from system operations, "
          + "4) You need to refresh policy data during ongoing conversation. "
          + "This is more efficient than searching by policy number when ID is available. "
          + "Returns comprehensive policy details including coverage, premiums, dates, and status.")
  public Function<GetPolicyByIdReq, ResponseWrapper<PolicyDto>> getPolicyById() {
    return request ->
        ResponseWrapper.<PolicyDto>builder()
            .success(true)
            .data(
                Objects.requireNonNull(
                    policiesApiClient.getPolicyById(request.policyId()).getBody()))
            .build();
  }

  @Bean(Functions.GET_POLICY_BY_POLICY_NUMBER)
  @SecuredAI
  @Description(
      "Retrieves policy information by searching with the policy number. Use this function when: "
          + "1) Customer provides their policy number (format: POL-XXXXX, POLICY-12345, or similar), "
          + "2) Customer asks about 'my policy POL-12345' or references a specific policy number, "
          + "3) You need to look up policy details when customer mentions their policy number, "
          + "4) Verifying policy existence and details for customer inquiries. "
          + "This is the primary way to identify and retrieve specific policy information. "
          + "Returns complete policy details including type, coverage amounts, premium, effective dates, and current status.")
  public Function<GetPolicyByPolicyNumberReq, ResponseWrapper<PolicyDto>>
      getPolicyByPolicyNumber() {
    return request ->
        ResponseWrapper.<PolicyDto>builder()
            .success(true)
            .data(
                Objects.requireNonNull(
                    policiesApiClient.getPolicyByPolicyNumber(request.policyNumber()).getBody()))
            .build();
  }

  @Bean(Functions.CREATE_POLICY)
  @SecuredAI
  @Description(
      "Creates a new insurance policy in the system. Use this function when: "
          + "1) Customer wants to purchase a new insurance policy, "
          + "2) Customer says 'I want to buy insurance', 'create a new policy', or 'get coverage', "
          + "3) Sales process requires creating a new policy for an existing customer, "
          + "4) Converting a quote into an active policy. "
          + "Requires complete policy information including coverage details, premiums, and effective dates. "
          + "This is typically used during new policy sales process. "
          + "Returns the created policy object with assigned policy ID and number.")
  public Function<CreatePolicyReq, ResponseWrapper<PolicyDto>> createPolicy() {
    return request ->
        ResponseWrapper.<PolicyDto>builder()
            .success(true)
            .data(
                Objects.requireNonNull(
                    policiesApiClient.createPolicy(request.policyDto()).getBody()))
            .build();
  }

  @Bean(Functions.GET_ALL_POLICIES)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Retrieves all policies in the insurance system. Use this function when: "
          + "1) Administrative tasks require a complete policy list, "
          + "2) Customer service representative needs to browse all policies, "
          + "3) Reporting or analysis tasks need policy overview, "
          + "4) System maintenance or audit processes require policy enumeration. "
          + "WARNING: This may return a large dataset depending on system size. "
          + "Use carefully and consider if more specific searches would be more appropriate. "
          + "Returns a list of all policies with basic information including policy numbers, types, and status.")
  public Function<GetAllPoliciesReq, ResponseWrapper<List<PolicyDto>>> getAllPolicies() {
    return request ->
        ResponseWrapper.<List<PolicyDto>>builder()
            .success(true)
            .data(policiesApiClient.getAllPolicies().getBody())
            .build();
  }

  @Bean(Functions.UPDATE_POLICY)
  @SecuredAI
  @Description(
      "Updates existing policy information in the system. Use this function when: "
          + "1) Customer requests policy modifications like coverage changes or beneficiary updates, "
          + "2) Customer says 'change my policy', 'update my coverage', or 'modify my insurance', "
          + "3) Policy renewals require updated terms or premiums, "
          + "4) Administrative corrections to policy details are needed, "
          + "5) Endorsements or riders need to be added to existing policies. "
          + "Requires policy ID and updated policy object with new information. "
          + "Always verify changes with customer and explain any premium implications. "
          + "Returns the updated policy object with all modifications applied.")
  public Function<UpdatePolicyReq, ResponseWrapper<PolicyDto>> updatePolicy() {
    return request ->
        ResponseWrapper.<PolicyDto>builder()
            .success(true)
            .data(policiesApiClient.updatePolicy(request.policyId(), request.policyDto()).getBody())
            .build();
  }

  @Bean(Functions.GET_AUTO_CLAIMS_BY_POLICY_ID)
  @SecuredAI
  @Description(
      "Retrieves all auto claims associated with a specific policy from policy perspective. Use this function when: "
          + "1) Customer asks 'show me auto claims for my policy', 'what car claims do I have', or 'policy claim history', "
          + "2) You need to display auto claim history for a specific policy during policy discussions, "
          + "3) Customer wants to see vehicle-related claims while reviewing their policy details, "
          + "4) Policy-focused conversations that need to include auto claim information. "
          + "This provides the same data as auto claim functions but from a policy management perspective. "
          + "Supports pagination and status filtering. Returns list of auto claims for the policy.")
  public Function<GetAutoClaimsByPolicyIdReq, ResponseWrapper<List<AutoClaimDto>>>
      getAutoClaimsByPolicyId() {
    return request ->
        ResponseWrapper.<List<AutoClaimDto>>builder()
            .success(true)
            .data(
                policiesApiClient
                    .getAutoClaimsByPolicyId(
                        request.policyId(), request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.GET_HOME_CLAIMS_BY_POLICY_ID)
  @SecuredAI
  @Description(
      "Retrieves all home claims associated with a specific policy from policy perspective. Use this function when: "
          + "1) Customer asks 'show me home claims for my policy', 'what property claims do I have', or 'policy claim history', "
          + "2) You need to display home claim history for a specific policy during policy discussions, "
          + "3) Customer wants to see property-related claims while reviewing their policy details, "
          + "4) Policy-focused conversations that need to include home claim information. "
          + "This provides the same data as home claim functions but from a policy management perspective. "
          + "Supports pagination and status filtering. Returns list of home claims for the policy.")
  public Function<GetHomeClaimsByPolicyIdReq, ResponseWrapper<List<HomeClaimDto>>>
      getHomeClaimsByPolicyId() {
    return request ->
        ResponseWrapper.<List<HomeClaimDto>>builder()
            .success(true)
            .data(
                policiesApiClient
                    .getHomeClaimsByPolicyId(
                        request.policyId(), request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.GET_HEALTH_CLAIMS_BY_POLICY_ID)
  @SecuredAI
  @Description(
      "Retrieves all health claims associated with a specific policy from policy perspective. Use this function when: "
          + "1) Customer asks 'show me health claims for my policy', 'what medical claims do I have', or 'policy claim history', "
          + "2) You need to display health claim history for a specific policy during policy discussions, "
          + "3) Customer wants to see medical-related claims while reviewing their policy details, "
          + "4) Policy-focused conversations that need to include health claim information. "
          + "This provides the same data as health claim functions but from a policy management perspective. "
          + "Supports pagination and status filtering. Returns list of health claims for the policy.")
  public Function<GetHealthClaimsByPolicyIdReq, ResponseWrapper<List<HealthClaimDto>>>
      getHealthClaimsByPolicyId() {
    return request ->
        ResponseWrapper.<List<HealthClaimDto>>builder()
            .success(true)
            .data(
                policiesApiClient
                    .getHealthClaimsByPolicyId(
                        request.policyId(), request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.GET_POLICY_CONDITIONS)
  @Description(
      "Retrieves the current set of policy conditions and terms for the insurance system. Use this function when: "
          + "1) Customer asks 'what are the policy conditions?', 'show me the terms', or 'policy rules', "
          + "2) Customer wants to understand cancellation policies, penalty rules, or coverage terms, "
          + "3) You need to explain policy conditions during sales or customer service conversations, "
          + "4) Customer inquires about coverage limitations, deductibles, or policy restrictions. "
          + "Returns comprehensive policy conditions including cancellation penalties, coverage terms, "
          + "and general policy rules that apply across the insurance system.")
  public Function<GetPolicyConditionsReq, ResponseWrapper<PolicyConditionsDto>>
      getPolicyConditions() {
    return request ->
        ResponseWrapper.<PolicyConditionsDto>builder()
            .success(true)
            .data(Objects.requireNonNull(policiesApiClient.getPolicyConditions().getBody()))
            .build();
  }

  @Bean(Functions.UPDATE_POLICY_CONDITIONS)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Updates the system-wide policy conditions and terms. Use this function when: "
          + "1) Administrative changes to policy terms are required, "
          + "2) Legal or regulatory updates necessitate policy condition modifications, "
          + "3) Business rule changes need to be applied to policy conditions, "
          + "4) Management requests updates to cancellation penalties or coverage terms. "
          + "WARNING: This is an admin-only operation that affects all policies system-wide. "
          + "Requires complete policy conditions object with updated terms. "
          + "Always verify changes with appropriate authorization and explain impact. "
          + "Returns the updated policy conditions with all modifications applied.")
  public Function<UpdatePolicyConditionsReq, ResponseWrapper<PolicyConditionsDto>>
      updatePolicyConditions() {
    return request ->
        ResponseWrapper.<PolicyConditionsDto>builder()
            .success(true)
            .data(
                Objects.requireNonNull(
                    policiesApiClient
                        .updatePolicyConditions(request.policyConditionsDto())
                        .getBody()))
            .build();
  }
}
