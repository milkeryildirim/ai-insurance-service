package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;

public record UpdateAutoClaimReq(Long claimId, AutoClaimDto autoClaimDto) implements IClaimReq {}
