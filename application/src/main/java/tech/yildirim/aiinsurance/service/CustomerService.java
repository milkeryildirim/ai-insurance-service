package tech.yildirim.aiinsurance.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.yildirim.aiinsurance.api.generated.clients.ClaimsApiClient;
import tech.yildirim.aiinsurance.api.generated.clients.PoliciesApiClient;
import tech.yildirim.aiinsurance.api.generated.model.ClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;
import tech.yildirim.aiinsurance.model.ai.request.GetAutoClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHealthClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHomeClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateAutoClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateHealthClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateHomeClaimReq;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

  private final PoliciesApiClient policiesApiClient;
  private final ClaimsApiClient claimsApiClient;

  public Long getCustomerIdByPolicyId(Long policyId) {
    return Optional.ofNullable(policiesApiClient.getPolicyById(policyId).getBody())
        .map(PolicyDto::getCustomerId)
        .orElseThrow(() -> new IllegalArgumentException("Policy not found with ID: " + policyId));
  }

  public Long getCustomerIdByPolicyNumber(String policyNumber) {
    return Optional.ofNullable(policiesApiClient.getPolicyByPolicyNumber(policyNumber).getBody())
        .map(PolicyDto::getCustomerId)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Policy not found with PolicyNumber: " + policyNumber));
  }

  public Long getCustomerIdByClaimRequestType(Object request) {
    ClaimDto claim =
        switch (request) {
          case GetAutoClaimByIdReq(Long claimId) ->
              claimsApiClient.getAutoClaimById(claimId).getBody();
          case GetHomeClaimByIdReq(Long claimId) ->
              claimsApiClient.getHomeClaimById(claimId).getBody();
          case GetHealthClaimByIdReq(Long claimId) ->
              claimsApiClient.getHealthClaimById(claimId).getBody();
          case UpdateAutoClaimReq req -> claimsApiClient.getAutoClaimById(req.claimId()).getBody();
          case UpdateHomeClaimReq req -> claimsApiClient.getHomeClaimById(req.claimId()).getBody();
          case UpdateHealthClaimReq req ->
              claimsApiClient.getHealthClaimById(req.claimId()).getBody();
          default ->
              throw new IllegalArgumentException(
                  "Unknown claim request type: " + request.getClass());
        };

    if (claim == null) {
      throw new IllegalArgumentException("Claim not found for request: " + request);
    }

    return Optional.of(claim)
        .map(ClaimDto::getPolicyId)
        .map(this::getCustomerIdByPolicyId)
        .orElseThrow(() -> new IllegalArgumentException("Policy not found for claim: " + request));
  }
}
