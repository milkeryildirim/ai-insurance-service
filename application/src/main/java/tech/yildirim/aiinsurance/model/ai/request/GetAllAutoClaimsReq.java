package tech.yildirim.aiinsurance.model.ai.request;

public record GetAllAutoClaimsReq(Integer page, Integer size, String status) implements IClaimReq {}
