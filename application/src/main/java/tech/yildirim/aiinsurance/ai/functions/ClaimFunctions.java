package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.ClaimsApiClient;
import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.AssignAdjusterToAutoClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.AssignAdjusterToHealthClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.AssignAdjusterToHomeClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.CreateAutoClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.CreateHealthClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.CreateHomeClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.DeleteAutoClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.DeleteHealthClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.DeleteHomeClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAllAutoClaimsReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAllHealthClaimsReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAllHomeClaimsReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAutoClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHealthClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.GetHomeClaimByIdReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateAutoClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateHealthClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateHomeClaimReq;
import tech.yildirim.aiinsurance.security.SecuredAI;

/** Defines all AI-callable functions related to claim management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for managing insurance claims (auto, home, health)")
public class ClaimFunctions {

  private final ClaimsApiClient claimsApiClient;

  // --- AUTO CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_AUTO_CLAIM)
  @SecuredAI
  @Description(
      "Creates a new auto insurance claim for vehicle accidents or damages. Use this function when: "
          + "1) Customer reports a car accident, collision, or vehicle damage, "
          + "2) Customer says 'I need to file an auto claim', 'my car was damaged', or 'I had an accident', "
          + "3) Vehicle-related incidents like theft, vandalism, or natural disaster damage, "
          + "4) Customer provides details about vehicle damage, license plate, and incident. "
          + "IMPORTANT: For the claim to be created correctly, you MUST set claimType to 'AutoClaimDto' (as string). "
          + "CRITICAL: Use the technical policy ID (Long number) for policyId field, NOT the policy number (string like POL-12345). "
          + "Set: claimType='AutoClaimDto', policyId=<technical_policy_id_number>, description, dateOfIncident, licensePlate, and optional fields like vehicleVin, accidentLocation, estimatedAmount. "
          + "Returns the created auto claim with assigned claim ID and initial status.")
  public Function<CreateAutoClaimReq, ResponseWrapper<AutoClaimDto>> createAutoClaim() {
    return request ->
        ResponseWrapper.<AutoClaimDto>builder()
            .success(true)
            .data(
                Objects.requireNonNull(
                    claimsApiClient.createAutoClaim(request.autoClaimDto()).getBody()))
            .build();
  }

  @Bean(Functions.GET_AUTO_CLAIM_BY_ID)
  @SecuredAI
  @Description(
      "Retrieves detailed auto claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their auto claim number or ID, "
          + "2) Customer asks 'what's the status of my auto claim', 'check my car claim', "
          + "3) You need to review specific auto claim details during conversation, "
          + "4) Following up on previously discussed auto claims. "
          + "Returns complete auto claim details including status, vehicle information, damages, and adjuster assignment.")
  public Function<GetAutoClaimByIdReq, ResponseWrapper<AutoClaimDto>> getAutoClaimById() {
    return request ->
        ResponseWrapper.<AutoClaimDto>builder()
            .success(true)
            .data(claimsApiClient.getAutoClaimById(request.claimId()).getBody())
            .build();
  }

  @Bean(Functions.GET_ALL_AUTO_CLAIMS)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Retrieves all auto claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all auto claims, "
          + "2) Customer service representatives need to search auto claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple auto claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of auto claims matching criteria.")
  public Function<GetAllAutoClaimsReq, ResponseWrapper<List<AutoClaimDto>>> getAllAutoClaims() {
    return request ->
        ResponseWrapper.<List<AutoClaimDto>>builder()
            .success(true)
            .data(
                claimsApiClient
                    .getAllAutoClaims(request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.UPDATE_AUTO_CLAIM)
  @SecuredAI
  @Description(
      "Updates existing auto claim information. Use this function when: "
          + "1) Customer provides additional information about the auto accident or damage, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my auto claim', 'add more details to my car claim', "
          + "4) Correction of auto claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated auto claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateAutoClaimReq, ResponseWrapper<AutoClaimDto>> updateAutoClaim() {
    return request ->
        ResponseWrapper.<AutoClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .updateAutoClaim(request.claimId(), request.autoClaimDto())
                    .getBody())
            .build();
  }

  @Bean(Functions.DELETE_AUTO_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Permanently deletes an auto claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests auto claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my auto claim', 'withdraw my car claim', 'delete my claim', "
          + "3) Duplicate or erroneous auto claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteAutoClaimReq, ResponseWrapper<String>> deleteAutoClaim() {
    return request -> {
      claimsApiClient.deleteAutoClaim(request.claimId());
      return ResponseWrapper.<String>builder()
          .success(true)
          .data("Auto claim deleted successfully.")
          .build();
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_AUTO_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Assigns an insurance adjuster to an auto claim for investigation and assessment. Use this function when: "
          + "1) Auto claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my auto claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex auto claims need expert evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated auto claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToAutoClaimReq, ResponseWrapper<AutoClaimDto>>
      assignAdjusterToAutoClaim() {
    return request ->
        ResponseWrapper.<AutoClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .assignAdjusterToAutoClaim(
                        request.claimId(), request.assignAdjusterRequestDto())
                    .getBody())
            .build();
  }

  // --- HOME CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_HOME_CLAIM)
  @SecuredAI
  @Description(
      "Creates a new home insurance claim for property damage or loss. Use this function when: "
          + "1) Customer reports home damage from fire, water, storm, or other covered perils, "
          + "2) Customer says 'I need to file a home claim', 'my house was damaged', 'property damage claim', "
          + "3) Home-related incidents like burglary, vandalism, or natural disasters, "
          + "4) Customer provides details about property damage, location, and incident. "
          + "IMPORTANT: For the claim to be created correctly, you MUST set claimType to 'HomeClaimDto' (as string). "
          + "CRITICAL: Use the technical policy ID (Long number) for policyId field, NOT the policy number (string like POL-12345). "
          + "Set: claimType='HomeClaimDto', policyId=<technical_policy_id_number>, description, dateOfIncident, and optional fields like typeOfDamage, damagedItems, estimatedAmount. "
          + "Returns the created home claim with assigned claim ID and initial status.")
  public Function<CreateHomeClaimReq, ResponseWrapper<HomeClaimDto>> createHomeClaim() {
    return request ->
        ResponseWrapper.<HomeClaimDto>builder()
            .success(true)
            .data(claimsApiClient.createHomeClaim(request.homeClaimDto()).getBody())
            .build();
  }

  @Bean(Functions.GET_HOME_CLAIM_BY_ID)
  @SecuredAI
  @Description(
      "Retrieves detailed home claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their home claim number or ID, "
          + "2) Customer asks 'what's the status of my home claim', 'check my property claim', "
          + "3) You need to review specific home claim details during conversation, "
          + "4) Following up on previously discussed home claims. "
          + "Returns complete home claim details including status, property information, damages, and adjuster assignment.")
  public Function<GetHomeClaimByIdReq, ResponseWrapper<HomeClaimDto>> getHomeClaimById() {
    return request ->
        ResponseWrapper.<HomeClaimDto>builder()
            .success(true)
            .data(claimsApiClient.getHomeClaimById(request.claimId()).getBody())
            .build();
  }

  @Bean(Functions.GET_ALL_HOME_CLAIMS)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Retrieves all home claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all home claims, "
          + "2) Customer service representatives need to search home claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple home claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of home claims matching criteria.")
  public Function<GetAllHomeClaimsReq, ResponseWrapper<List<HomeClaimDto>>> getAllHomeClaims() {
    return request ->
        ResponseWrapper.<List<HomeClaimDto>>builder()
            .success(true)
            .data(
                claimsApiClient
                    .getAllHomeClaims(request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.UPDATE_HOME_CLAIM)
  @SecuredAI
  @Description(
      "Updates existing home claim information. Use this function when: "
          + "1) Customer provides additional information about the property damage, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my home claim', 'add more details to my property claim', "
          + "4) Correction of home claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated home claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateHomeClaimReq, ResponseWrapper<HomeClaimDto>> updateHomeClaim() {
    return request ->
        ResponseWrapper.<HomeClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .updateHomeClaim(request.claimId(), request.homeClaimDto())
                    .getBody())
            .build();
  }

  @Bean(Functions.DELETE_HOME_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Permanently deletes a home claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests home claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my home claim', 'withdraw my property claim', 'delete my claim', "
          + "3) Duplicate or erroneous home claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteHomeClaimReq, ResponseWrapper<String>> deleteHomeClaim() {
    return request -> {
      claimsApiClient.deleteHomeClaim(request.claimId());
      return ResponseWrapper.<String>builder()
          .success(true)
          .data("Home claim deleted successfully.")
          .build();
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_HOME_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Assigns an insurance adjuster to a home claim for investigation and assessment. Use this function when: "
          + "1) Home claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my home claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex home claims need expert evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated home claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToHomeClaimReq, ResponseWrapper<HomeClaimDto>>
      assignAdjusterToHomeClaim() {
    return request ->
        ResponseWrapper.<HomeClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .assignAdjusterToHomeClaim(
                        request.claimId(), request.assignAdjusterRequestDto())
                    .getBody())
            .build();
  }

  // --- HEALTH CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_HEALTH_CLAIM)
  @SecuredAI
  @Description(
      "Creates a new health insurance claim for medical expenses or treatments. Use this function when: "
          + "1) Customer needs to file a medical expense claim or reimbursement request, "
          + "2) Customer says 'I need to file a health claim', 'submit medical bills', 'claim reimbursement', "
          + "3) Medical treatments, hospital visits, or healthcare services need coverage, "
          + "4) Customer provides medical provider details, treatment information, and expenses. "
          + "IMPORTANT: For the claim to be created correctly, you MUST set claimType to 'HealthClaimDto' (as string). "
          + "CRITICAL: Use the technical policy ID (Long number) for policyId field, NOT the policy number (string like POL-12345). "
          + "Set: claimType='HealthClaimDto', policyId=<technical_policy_id_number>, description, dateOfIncident, and optional fields like medicalProvider, procedureCode, estimatedAmount. "
          + "Returns the created health claim with assigned claim ID and initial status.")
  public Function<CreateHealthClaimReq, ResponseWrapper<HealthClaimDto>> createHealthClaim() {
    return request ->
        ResponseWrapper.<HealthClaimDto>builder()
            .success(true)
            .data(claimsApiClient.createHealthClaim(request.healthClaimDto()).getBody())
            .build();
  }

  @Bean(Functions.GET_HEALTH_CLAIM_BY_ID)
  @SecuredAI
  @Description(
      "Retrieves detailed health claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their health claim number or ID, "
          + "2) Customer asks 'what's the status of my health claim', 'check my medical claim', "
          + "3) You need to review specific health claim details during conversation, "
          + "4) Following up on previously discussed health claims. "
          + "Returns complete health claim details including status, medical provider, treatment information, and reimbursement status.")
  public Function<GetHealthClaimByIdReq, ResponseWrapper<HealthClaimDto>> getHealthClaimById() {
    return request ->
        ResponseWrapper.<HealthClaimDto>builder()
            .success(true)
            .data(claimsApiClient.getHealthClaimById(request.claimId()).getBody())
            .build();
  }

  @Bean(Functions.GET_ALL_HEALTH_CLAIMS)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Retrieves all health claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all health claims, "
          + "2) Customer service representatives need to search health claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple health claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of health claims matching criteria.")
  public Function<GetAllHealthClaimsReq, ResponseWrapper<List<HealthClaimDto>>>
      getAllHealthClaims() {
    return request ->
        ResponseWrapper.<List<HealthClaimDto>>builder()
            .success(true)
            .data(
                claimsApiClient
                    .getAllHealthClaims(request.page(), request.size(), request.status())
                    .getBody())
            .build();
  }

  @Bean(Functions.UPDATE_HEALTH_CLAIM)
  @SecuredAI
  @Description(
      "Updates existing health claim information. Use this function when: "
          + "1) Customer provides additional medical documentation or information, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my health claim', 'add more medical details', "
          + "4) Correction of health claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated health claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateHealthClaimReq, ResponseWrapper<HealthClaimDto>> updateHealthClaim() {
    return request ->
        ResponseWrapper.<HealthClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .updateHealthClaim(request.claimId(), request.healthClaimDto())
                    .getBody())
            .build();
  }

  @Bean(Functions.DELETE_HEALTH_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Permanently deletes a health claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests health claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my health claim', 'withdraw my medical claim', 'delete my claim', "
          + "3) Duplicate or erroneous health claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteHealthClaimReq, ResponseWrapper<String>> deleteHealthClaim() {
    return request -> {
      claimsApiClient.deleteHealthClaim(request.claimId());
      return ResponseWrapper.<String>builder()
          .success(true)
          .data("Health claim deleted successfully.")
          .build();
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_HEALTH_CLAIM)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Assigns an insurance adjuster to a health claim for investigation and assessment. Use this function when: "
          + "1) Health claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my health claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex health claims need expert medical evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated health claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToHealthClaimReq, ResponseWrapper<HealthClaimDto>>
      assignAdjusterToHealthClaim() {
    return request ->
        ResponseWrapper.<HealthClaimDto>builder()
            .success(true)
            .data(
                claimsApiClient
                    .assignAdjusterToHealthClaim(
                        request.claimId(), request.assignAdjusterRequestDto())
                    .getBody())
            .build();
  }
}
