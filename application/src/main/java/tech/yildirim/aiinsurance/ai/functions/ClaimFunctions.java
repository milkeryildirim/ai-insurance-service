package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.ClaimsApiClient;
import tech.yildirim.aiinsurance.api.generated.model.AssignAdjusterRequestDto;
import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;

/** Defines all AI-callable functions related to claim management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for managing insurance claims (auto, home, health)")
public class ClaimFunctions {

  private final ClaimsApiClient claimsApiClient;

  // --- Request Records for Type Safety ---

  // Auto Claim Records
  public record CreateAutoClaimRequest(AutoClaimDto autoClaimDto) {}

  public record GetAutoClaimByIdRequest(Long claimId) {}

  public record GetAllAutoClaimsRequest(Integer page, Integer size, String status) {}

  public record UpdateAutoClaimRequest(Long claimId, AutoClaimDto autoClaimDto) {}

  public record DeleteAutoClaimRequest(Long claimId) {}

  public record AssignAdjusterToAutoClaimRequest(
      Long claimId, AssignAdjusterRequestDto assignAdjusterRequestDto) {}

  // Home Claim Records
  public record CreateHomeClaimRequest(HomeClaimDto homeClaimDto) {}

  public record GetHomeClaimByIdRequest(Long claimId) {}

  public record GetAllHomeClaimsRequest(Integer page, Integer size, String status) {}

  public record UpdateHomeClaimRequest(Long claimId, HomeClaimDto homeClaimDto) {}

  public record DeleteHomeClaimRequest(Long claimId) {}

  public record AssignAdjusterToHomeClaimRequest(
      Long claimId, AssignAdjusterRequestDto assignAdjusterRequestDto) {}

  // Health Claim Records
  public record CreateHealthClaimRequest(HealthClaimDto healthClaimDto) {}

  public record GetHealthClaimByIdRequest(Long claimId) {}

  public record GetAllHealthClaimsRequest(Integer page, Integer size, String status) {}

  public record UpdateHealthClaimRequest(Long claimId, HealthClaimDto healthClaimDto) {}

  public record DeleteHealthClaimRequest(Long claimId) {}

  public record AssignAdjusterToHealthClaimRequest(
      Long claimId, AssignAdjusterRequestDto assignAdjusterRequestDto) {}

  // --- AUTO CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_AUTO_CLAIM)
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
  public Function<CreateAutoClaimRequest, AutoClaimDto> createAutoClaim() {
    return request ->
        Objects.requireNonNull(claimsApiClient.createAutoClaim(request.autoClaimDto()).getBody());
  }

  @Bean(Functions.GET_AUTO_CLAIM_BY_ID)
  @Description(
      "Retrieves detailed auto claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their auto claim number or ID, "
          + "2) Customer asks 'what's the status of my auto claim', 'check my car claim', "
          + "3) You need to review specific auto claim details during conversation, "
          + "4) Following up on previously discussed auto claims. "
          + "Returns complete auto claim details including status, vehicle information, damages, and adjuster assignment.")
  public Function<GetAutoClaimByIdRequest, AutoClaimDto> getAutoClaimById() {
    return request ->
        Objects.requireNonNull(claimsApiClient.getAutoClaimById(request.claimId()).getBody());
  }

  @Bean(Functions.GET_ALL_AUTO_CLAIMS)
  @Description(
      "Retrieves all auto claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all auto claims, "
          + "2) Customer service representatives need to search auto claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple auto claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of auto claims matching criteria.")
  public Function<GetAllAutoClaimsRequest, List<AutoClaimDto>> getAllAutoClaims() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .getAllAutoClaims(request.page(), request.size(), request.status())
                .getBody());
  }

  @Bean(Functions.UPDATE_AUTO_CLAIM)
  @Description(
      "Updates existing auto claim information. Use this function when: "
          + "1) Customer provides additional information about the auto accident or damage, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my auto claim', 'add more details to my car claim', "
          + "4) Correction of auto claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated auto claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateAutoClaimRequest, AutoClaimDto> updateAutoClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient.updateAutoClaim(request.claimId(), request.autoClaimDto()).getBody());
  }

  @Bean(Functions.DELETE_AUTO_CLAIM)
  @Description(
      "Permanently deletes an auto claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests auto claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my auto claim', 'withdraw my car claim', 'delete my claim', "
          + "3) Duplicate or erroneous auto claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteAutoClaimRequest, String> deleteAutoClaim() {
    return request -> {
      claimsApiClient.deleteAutoClaim(request.claimId());
      return "{\"status\": \"SUCCESS\", \"message\": \"Auto claim deleted successfully.\"}";
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_AUTO_CLAIM)
  @Description(
      "Assigns an insurance adjuster to an auto claim for investigation and assessment. Use this function when: "
          + "1) Auto claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my auto claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex auto claims need expert evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated auto claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToAutoClaimRequest, AutoClaimDto> assignAdjusterToAutoClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .assignAdjusterToAutoClaim(request.claimId(), request.assignAdjusterRequestDto())
                .getBody());
  }

  // --- HOME CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_HOME_CLAIM)
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
  public Function<CreateHomeClaimRequest, HomeClaimDto> createHomeClaim() {
    return request ->
        Objects.requireNonNull(claimsApiClient.createHomeClaim(request.homeClaimDto()).getBody());
  }

  @Bean(Functions.GET_HOME_CLAIM_BY_ID)
  @Description(
      "Retrieves detailed home claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their home claim number or ID, "
          + "2) Customer asks 'what's the status of my home claim', 'check my property claim', "
          + "3) You need to review specific home claim details during conversation, "
          + "4) Following up on previously discussed home claims. "
          + "Returns complete home claim details including status, property information, damages, and adjuster assignment.")
  public Function<GetHomeClaimByIdRequest, HomeClaimDto> getHomeClaimById() {
    return request ->
        Objects.requireNonNull(claimsApiClient.getHomeClaimById(request.claimId()).getBody());
  }

  @Bean(Functions.GET_ALL_HOME_CLAIMS)
  @Description(
      "Retrieves all home claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all home claims, "
          + "2) Customer service representatives need to search home claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple home claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of home claims matching criteria.")
  public Function<GetAllHomeClaimsRequest, List<HomeClaimDto>> getAllHomeClaims() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .getAllHomeClaims(request.page(), request.size(), request.status())
                .getBody());
  }

  @Bean(Functions.UPDATE_HOME_CLAIM)
  @Description(
      "Updates existing home claim information. Use this function when: "
          + "1) Customer provides additional information about the property damage, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my home claim', 'add more details to my property claim', "
          + "4) Correction of home claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated home claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateHomeClaimRequest, HomeClaimDto> updateHomeClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient.updateHomeClaim(request.claimId(), request.homeClaimDto()).getBody());
  }

  @Bean(Functions.DELETE_HOME_CLAIM)
  @Description(
      "Permanently deletes a home claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests home claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my home claim', 'withdraw my property claim', 'delete my claim', "
          + "3) Duplicate or erroneous home claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteHomeClaimRequest, String> deleteHomeClaim() {
    return request -> {
      claimsApiClient.deleteHomeClaim(request.claimId());
      return "{\"status\": \"SUCCESS\", \"message\": \"Home claim deleted successfully.\"}";
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_HOME_CLAIM)
  @Description(
      "Assigns an insurance adjuster to a home claim for investigation and assessment. Use this function when: "
          + "1) Home claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my home claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex home claims need expert evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated home claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToHomeClaimRequest, HomeClaimDto> assignAdjusterToHomeClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .assignAdjusterToHomeClaim(request.claimId(), request.assignAdjusterRequestDto())
                .getBody());
  }

  // --- HEALTH CLAIM FUNCTIONS ---

  @Bean(Functions.CREATE_HEALTH_CLAIM)
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
  public Function<CreateHealthClaimRequest, HealthClaimDto> createHealthClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient.createHealthClaim(request.healthClaimDto()).getBody());
  }

  @Bean(Functions.GET_HEALTH_CLAIM_BY_ID)
  @Description(
      "Retrieves detailed health claim information using the unique claim ID. Use this function when: "
          + "1) Customer provides their health claim number or ID, "
          + "2) Customer asks 'what's the status of my health claim', 'check my medical claim', "
          + "3) You need to review specific health claim details during conversation, "
          + "4) Following up on previously discussed health claims. "
          + "Returns complete health claim details including status, medical provider, treatment information, and reimbursement status.")
  public Function<GetHealthClaimByIdRequest, HealthClaimDto> getHealthClaimById() {
    return request ->
        Objects.requireNonNull(claimsApiClient.getHealthClaimById(request.claimId()).getBody());
  }

  @Bean(Functions.GET_ALL_HEALTH_CLAIMS)
  @Description(
      "Retrieves all health claims in the system with optional filtering and pagination. Use this function when: "
          + "1) Administrative users need to view all health claims, "
          + "2) Customer service representatives need to search health claims by status, "
          + "3) You need to find claims with specific status like 'PENDING', 'APPROVED', 'REJECTED', "
          + "4) Generating reports or reviewing multiple health claims. "
          + "Supports pagination (page, size) and status filtering. Returns list of health claims matching criteria.")
  public Function<GetAllHealthClaimsRequest, List<HealthClaimDto>> getAllHealthClaims() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .getAllHealthClaims(request.page(), request.size(), request.status())
                .getBody());
  }

  @Bean(Functions.UPDATE_HEALTH_CLAIM)
  @Description(
      "Updates existing health claim information. Use this function when: "
          + "1) Customer provides additional medical documentation or information, "
          + "2) Claim status needs to be changed (approved, rejected, under review), "
          + "3) Customer says 'I need to update my health claim', 'add more medical details', "
          + "4) Correction of health claim information is needed, "
          + "5) Processing workflow requires claim updates. "
          + "Requires claim ID and updated health claim object. Returns the updated claim with modifications applied.")
  public Function<UpdateHealthClaimRequest, HealthClaimDto> updateHealthClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .updateHealthClaim(request.claimId(), request.healthClaimDto())
                .getBody());
  }

  @Bean(Functions.DELETE_HEALTH_CLAIM)
  @Description(
      "Permanently deletes a health claim from the system. Use this function ONLY when: "
          + "1) Customer explicitly requests health claim cancellation or withdrawal, "
          + "2) Customer says 'cancel my health claim', 'withdraw my medical claim', 'delete my claim', "
          + "3) Duplicate or erroneous health claims need to be removed, "
          + "4) Legal or compliance requirements mandate claim removal. "
          + "WARNING: This action is irreversible. Always confirm with customer before executing. "
          + "Consider business rules and data retention policies before deletion.")
  public Function<DeleteHealthClaimRequest, String> deleteHealthClaim() {
    return request -> {
      claimsApiClient.deleteHealthClaim(request.claimId());
      return "{\"status\": \"SUCCESS\", \"message\": \"Health claim deleted successfully.\"}";
    };
  }

  @Bean(Functions.ASSIGN_ADJUSTER_TO_HEALTH_CLAIM)
  @Description(
      "Assigns an insurance adjuster to a health claim for investigation and assessment. Use this function when: "
          + "1) Health claim requires professional assessment and investigation, "
          + "2) Customer asks 'when will an adjuster contact me', 'who is handling my health claim', "
          + "3) Claim processing workflow requires adjuster assignment, "
          + "4) Complex health claims need expert medical evaluation. "
          + "Requires claim ID and adjuster assignment details. "
          + "Returns updated health claim with assigned adjuster information and contact details.")
  public Function<AssignAdjusterToHealthClaimRequest, HealthClaimDto>
      assignAdjusterToHealthClaim() {
    return request ->
        Objects.requireNonNull(
            claimsApiClient
                .assignAdjusterToHealthClaim(request.claimId(), request.assignAdjusterRequestDto())
                .getBody());
  }
}
