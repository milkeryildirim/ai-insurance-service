package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;

public record CreateAutoClaimReq(AutoClaimDto autoClaimDto) implements IPolicyIDIdentifiableReq {

  @Override
  public Long policyId() {
    return autoClaimDto.getPolicyId();
  }
}
