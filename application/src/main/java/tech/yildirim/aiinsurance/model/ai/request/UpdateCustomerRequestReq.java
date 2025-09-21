package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;

public record UpdateCustomerRequestReq(Long customerId, CustomerDto customerDto)
    implements ICustomerIdentifiableReq {}
