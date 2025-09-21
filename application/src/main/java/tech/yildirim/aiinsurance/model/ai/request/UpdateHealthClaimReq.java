package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;

public record UpdateHealthClaimReq(Long claimId, HealthClaimDto healthClaimDto)
    implements IClaimReq {}
