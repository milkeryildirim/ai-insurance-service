package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;

public record UpdateHomeClaimReq(Long claimId, HomeClaimDto homeClaimDto) implements IClaimReq {}
