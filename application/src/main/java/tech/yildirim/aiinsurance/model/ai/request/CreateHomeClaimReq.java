package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;

public record CreateHomeClaimReq(HomeClaimDto homeClaimDto) implements IPolicyIDIdentifiableReq {

  @Override
  public Long policyId() {
    return homeClaimDto.getPolicyId();
  }
}
