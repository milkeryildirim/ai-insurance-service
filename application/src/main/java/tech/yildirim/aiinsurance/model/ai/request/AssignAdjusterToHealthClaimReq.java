package tech.yildirim.aiinsurance.model.ai.request;

import tech.yildirim.aiinsurance.api.generated.model.AssignAdjusterRequestDto;

public record AssignAdjusterToHealthClaimReq(
    Long claimId, AssignAdjusterRequestDto assignAdjusterRequestDto) {}
