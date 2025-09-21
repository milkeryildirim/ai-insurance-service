package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;

public record CreateHealthClaimReq(HealthClaimDto healthClaimDto)
    implements IPolicyIDIdentifiableReq {

  @Override
  public Long policyId() {
    return healthClaimDto.getPolicyId();
  }
}
