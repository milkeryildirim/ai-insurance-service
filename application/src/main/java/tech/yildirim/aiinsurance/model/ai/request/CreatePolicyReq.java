package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

public record CreatePolicyReq(PolicyDto policyDto) implements ICustomerIdentifiableReq {

  @Override
  public Long customerId() {
    return policyDto.getCustomerId();
  }
}
