package tech.yildirim.aiinsurance.model.ai.request;

public record GetHomeClaimsByPolicyIdReq(Long policyId, Integer page, Integer size, String status)
    implements IPolicyIDIdentifiableReq {}
