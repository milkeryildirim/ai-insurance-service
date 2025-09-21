package tech.yildirim.aiinsurance.model.ai.request;

public record GetPolicyByPolicyNumberReq(String policyNumber)
    implements IPolicyNumberIdentifiableReq {}
