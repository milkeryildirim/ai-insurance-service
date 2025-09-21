package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

public record UpdatePolicyReq(Long policyId, PolicyDto policyDto)
    implements ICustomerIdentifiableReq {

  @Override
  public Long customerId() {
    return policyDto.getCustomerId();
  }
}
