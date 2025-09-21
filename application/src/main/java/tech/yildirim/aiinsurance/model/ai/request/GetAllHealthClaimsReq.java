package tech.yildirim.aiinsurance.model.ai.request;

public record GetAllHealthClaimsReq(Integer page, Integer size, String status)
    implements IClaimReq {}
