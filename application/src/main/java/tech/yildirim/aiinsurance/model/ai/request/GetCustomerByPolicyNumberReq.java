package tech.yildirim.aiinsurance.model.ai.request;

public record GetCustomerByPolicyNumberReq(String policyNumber)
    implements IPolicyNumberIdentifiableReq {}
