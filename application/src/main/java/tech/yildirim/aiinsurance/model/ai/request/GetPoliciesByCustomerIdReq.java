package tech.yildirim.aiinsurance.model.ai.request;

public record GetPoliciesByCustomerIdReq(Long customerId) implements ICustomerIdentifiableReq {}
