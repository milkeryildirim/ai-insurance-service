package tech.yildirim.aiinsurance.ai.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.CreatePolicyRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetAllPoliciesRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetAutoClaimsByPolicyIdRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetHealthClaimsByPolicyIdRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetHomeClaimsByPolicyIdRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetPolicyByIdRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetPolicyByPolicyNumberRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.GetPolicyConditionsRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.UpdatePolicyConditionsRequest;
import tech.yildirim.aiinsurance.ai.functions.PolicyFunctions.UpdatePolicyRequest;
import tech.yildirim.aiinsurance.api.generated.clients.PoliciesApiClient;
import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.CancellationPenaltyRuleDto;
import tech.yildirim.aiinsurance.api.generated.model.HealthClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.HomeClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyConditionsDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/**
 * Unit tests for {@link PolicyFunctions}.
 *
 * <p>Tests the AI-callable functions for policy management, including policy creation, retrieval,
 * updates, claims retrieval by policy, and policy conditions management.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyFunctions Tests")
class PolicyFunctionsTest {

  @Mock private PoliciesApiClient policiesApiClient;

  private PolicyFunctions policyFunctions;

  @BeforeEach
  void setUp() {
    policyFunctions = new PolicyFunctions(policiesApiClient);
  }

  @Test
  @DisplayName("Should create policy when valid policy data is provided")
  void createPolicy_WithValidPolicyData_ShouldReturnCreatedPolicy() {
    // Given
    PolicyDto inputPolicy = createSamplePolicy();
    PolicyDto expectedPolicy = createSamplePolicy();
    expectedPolicy.setId(1L);

    when(policiesApiClient.createPolicy(inputPolicy)).thenReturn(ResponseEntity.ok(expectedPolicy));

    // When
    Function<CreatePolicyRequest, PolicyDto> function = policyFunctions.createPolicy();
    PolicyDto result = function.apply(new CreatePolicyRequest(inputPolicy));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getPolicyNumber()).isEqualTo("POL-2024-001");
    assertThat(result.getType()).isEqualTo(PolicyDto.TypeEnum.AUTO);
    verify(policiesApiClient).createPolicy(inputPolicy);
  }

  @Test
  @DisplayName("Should throw exception when create policy API returns null body")
  void createPolicy_WithNullResponse_ShouldThrowException() {
    // Given
    PolicyDto inputPolicy = createSamplePolicy();
    when(policiesApiClient.createPolicy(inputPolicy)).thenReturn(ResponseEntity.ok(null));

    // When & Then
    Function<CreatePolicyRequest, PolicyDto> function = policyFunctions.createPolicy();
    CreatePolicyRequest request = new CreatePolicyRequest(inputPolicy);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should return policy when valid policy ID is provided")
  void getPolicyById_WithValidPolicyId_ShouldReturnPolicy() {
    // Given
    Long policyId = 1L;
    PolicyDto expectedPolicy = createSamplePolicy();
    expectedPolicy.setId(policyId);

    when(policiesApiClient.getPolicyById(policyId)).thenReturn(ResponseEntity.ok(expectedPolicy));

    // When
    Function<GetPolicyByIdRequest, PolicyDto> function = policyFunctions.getPolicyById();
    PolicyDto result = function.apply(new GetPolicyByIdRequest(policyId));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(policyId);
    assertThat(result.getPolicyNumber()).isEqualTo("POL-2024-001");
    verify(policiesApiClient).getPolicyById(policyId);
  }

  @Test
  @DisplayName("Should throw exception when get policy by ID API returns null body")
  void getPolicyById_WithNullResponse_ShouldThrowException() {
    // Given
    Long policyId = 1L;
    when(policiesApiClient.getPolicyById(policyId)).thenReturn(ResponseEntity.ok(null));

    // When & Then
    Function<GetPolicyByIdRequest, PolicyDto> function = policyFunctions.getPolicyById();
    GetPolicyByIdRequest request = new GetPolicyByIdRequest(policyId);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should return policy when valid policy number is provided")
  void getPolicyByPolicyNumber_WithValidPolicyNumber_ShouldReturnPolicy() {
    // Given
    String policyNumber = "POL-2024-001";
    PolicyDto expectedPolicy = createSamplePolicy();
    expectedPolicy.setPolicyNumber(policyNumber);

    when(policiesApiClient.getPolicyByPolicyNumber(policyNumber))
        .thenReturn(ResponseEntity.ok(expectedPolicy));

    // When
    Function<GetPolicyByPolicyNumberRequest, PolicyDto> function =
        policyFunctions.getPolicyByPolicyNumber();
    PolicyDto result = function.apply(new GetPolicyByPolicyNumberRequest(policyNumber));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getPolicyNumber()).isEqualTo(policyNumber);
    verify(policiesApiClient).getPolicyByPolicyNumber(policyNumber);
  }

  @Test
  @DisplayName("Should return all policies when requested")
  void getAllPolicies_WithValidRequest_ShouldReturnPolicies() {
    // Given
    List<PolicyDto> expectedPolicies = createSamplePolicies();

    when(policiesApiClient.getAllPolicies()).thenReturn(ResponseEntity.ok(expectedPolicies));

    // When
    Function<GetAllPoliciesRequest, List<PolicyDto>> function = policyFunctions.getAllPolicies();
    List<PolicyDto> result = function.apply(new GetAllPoliciesRequest());

    // Then
    assertThat(result).isNotNull().hasSize(3);
    assertThat(result.get(0).getType()).isEqualTo(PolicyDto.TypeEnum.AUTO);
    assertThat(result.get(1).getType()).isEqualTo(PolicyDto.TypeEnum.HOME);
    assertThat(result.get(2).getType()).isEqualTo(PolicyDto.TypeEnum.HEALTH);
    verify(policiesApiClient).getAllPolicies();
  }

  @Test
  @DisplayName("Should return empty list when no policies exist")
  void getAllPolicies_WithNoPolicies_ShouldReturnEmptyList() {
    // Given
    List<PolicyDto> emptyPolicies = List.of();

    when(policiesApiClient.getAllPolicies()).thenReturn(ResponseEntity.ok(emptyPolicies));

    // When
    Function<GetAllPoliciesRequest, List<PolicyDto>> function = policyFunctions.getAllPolicies();
    List<PolicyDto> result = function.apply(new GetAllPoliciesRequest());

    // Then
    assertThat(result).isNotNull().isEmpty();
    verify(policiesApiClient).getAllPolicies();
  }

  @Test
  @DisplayName("Should update policy when valid data is provided")
  void updatePolicy_WithValidData_ShouldReturnUpdatedPolicy() {
    // Given
    Long policyId = 1L;
    PolicyDto inputPolicy = createSamplePolicy();
    PolicyDto expectedPolicy = createSamplePolicy();
    expectedPolicy.setId(policyId);
    expectedPolicy.setPremium(new BigDecimal("1500.00"));

    when(policiesApiClient.updatePolicy(policyId, inputPolicy))
        .thenReturn(ResponseEntity.ok(expectedPolicy));

    // When
    Function<UpdatePolicyRequest, PolicyDto> function = policyFunctions.updatePolicy();
    PolicyDto result = function.apply(new UpdatePolicyRequest(policyId, inputPolicy));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(policyId);
    assertThat(result.getPremium()).isEqualTo(new BigDecimal("1500.00"));
    verify(policiesApiClient).updatePolicy(policyId, inputPolicy);
  }

  @Test
  @DisplayName("Should return auto claims when valid policy ID is provided")
  void getAutoClaimsByPolicyId_WithValidPolicyId_ShouldReturnClaims() {
    // Given
    Long policyId = 1L;
    Integer page = 0;
    Integer size = 20;
    String status = "PENDING";
    List<AutoClaimDto> expectedClaims = createSampleAutoClaims();

    when(policiesApiClient.getAutoClaimsByPolicyId(policyId, page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetAutoClaimsByPolicyIdRequest, List<AutoClaimDto>> function =
        policyFunctions.getAutoClaimsByPolicyId();
    List<AutoClaimDto> result =
        function.apply(new GetAutoClaimsByPolicyIdRequest(policyId, page, size, status));

    // Then
    assertThat(result).isNotNull().hasSize(2);
    assertThat(result.get(0).getLicensePlate()).isEqualTo("ABC-123");
    assertThat(result.get(1).getLicensePlate()).isEqualTo("XYZ-789");
    verify(policiesApiClient).getAutoClaimsByPolicyId(policyId, page, size, status);
  }

  @Test
  @DisplayName("Should return home claims when valid policy ID is provided")
  void getHomeClaimsByPolicyId_WithValidPolicyId_ShouldReturnClaims() {
    // Given
    Long policyId = 1L;
    Integer page = 0;
    Integer size = 20;
    String status = "APPROVED";
    List<HomeClaimDto> expectedClaims = createSampleHomeClaims();

    when(policiesApiClient.getHomeClaimsByPolicyId(policyId, page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetHomeClaimsByPolicyIdRequest, List<HomeClaimDto>> function =
        policyFunctions.getHomeClaimsByPolicyId();
    List<HomeClaimDto> result =
        function.apply(new GetHomeClaimsByPolicyIdRequest(policyId, page, size, status));

    // Then
    assertThat(result).isNotNull().hasSize(2);
    assertThat(result.get(0).getTypeOfDamage()).isEqualTo("Fire damage");
    assertThat(result.get(1).getTypeOfDamage()).isEqualTo("Water damage");
    verify(policiesApiClient).getHomeClaimsByPolicyId(policyId, page, size, status);
  }

  @Test
  @DisplayName("Should return health claims when valid policy ID is provided")
  void getHealthClaimsByPolicyId_WithValidPolicyId_ShouldReturnClaims() {
    // Given
    Long policyId = 1L;
    Integer page = 0;
    Integer size = 20;
    String status = "IN_REVIEW";
    List<HealthClaimDto> expectedClaims = createSampleHealthClaims();

    when(policiesApiClient.getHealthClaimsByPolicyId(policyId, page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetHealthClaimsByPolicyIdRequest, List<HealthClaimDto>> function =
        policyFunctions.getHealthClaimsByPolicyId();
    List<HealthClaimDto> result =
        function.apply(new GetHealthClaimsByPolicyIdRequest(policyId, page, size, status));

    // Then
    assertThat(result).isNotNull().hasSize(2);
    assertThat(result.get(0).getMedicalProvider()).isEqualTo("City General Hospital");
    assertThat(result.get(1).getMedicalProvider()).isEqualTo("Family Care Clinic");
    verify(policiesApiClient).getHealthClaimsByPolicyId(policyId, page, size, status);
  }

  @Test
  @DisplayName("Should return policy conditions when requested")
  void getPolicyConditions_WithValidRequest_ShouldReturnConditions() {
    // Given
    PolicyConditionsDto expectedConditions = createSamplePolicyConditions();

    when(policiesApiClient.getPolicyConditions()).thenReturn(ResponseEntity.ok(expectedConditions));

    // When
    Function<GetPolicyConditionsRequest, PolicyConditionsDto> function =
        policyFunctions.getPolicyConditions();
    PolicyConditionsDto result = function.apply(new GetPolicyConditionsRequest());

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFreeCancellationDays()).isEqualTo(14);
    assertThat(result.getNoClaimBonusPercentage()).isEqualTo(new BigDecimal("0.05"));
    assertThat(result.getCancellationRules()).hasSize(1);
    verify(policiesApiClient).getPolicyConditions();
  }

  @Test
  @DisplayName("Should throw exception when get policy conditions API returns null body")
  void getPolicyConditions_WithNullResponse_ShouldThrowException() {
    // Given
    when(policiesApiClient.getPolicyConditions()).thenReturn(ResponseEntity.ok(null));

    // When & Then
    Function<GetPolicyConditionsRequest, PolicyConditionsDto> function =
        policyFunctions.getPolicyConditions();
    GetPolicyConditionsRequest request = new GetPolicyConditionsRequest();

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should update policy conditions when valid data is provided")
  void updatePolicyConditions_WithValidData_ShouldReturnUpdatedConditions() {
    // Given
    PolicyConditionsDto inputConditions = createSamplePolicyConditions();
    PolicyConditionsDto expectedConditions = createSamplePolicyConditions();
    expectedConditions.setFreeCancellationDays(7); // Updated value

    when(policiesApiClient.updatePolicyConditions(inputConditions))
        .thenReturn(ResponseEntity.ok(expectedConditions));

    // When
    Function<UpdatePolicyConditionsRequest, PolicyConditionsDto> function =
        policyFunctions.updatePolicyConditions();
    PolicyConditionsDto result = function.apply(new UpdatePolicyConditionsRequest(inputConditions));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFreeCancellationDays()).isEqualTo(7);
    assertThat(result.getNoClaimBonusPercentage()).isEqualTo(new BigDecimal("0.05"));
    verify(policiesApiClient).updatePolicyConditions(inputConditions);
  }

  @Test
  @DisplayName("Should throw exception when update policy conditions API returns null body")
  void updatePolicyConditions_WithNullResponse_ShouldThrowException() {
    // Given
    PolicyConditionsDto inputConditions = createSamplePolicyConditions();
    when(policiesApiClient.updatePolicyConditions(inputConditions))
        .thenReturn(ResponseEntity.ok(null));

    // When & Then
    Function<UpdatePolicyConditionsRequest, PolicyConditionsDto> function =
        policyFunctions.updatePolicyConditions();
    UpdatePolicyConditionsRequest request = new UpdatePolicyConditionsRequest(inputConditions);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should return empty list when policy has no claims")
  void getClaimsByPolicyId_WithNoClaimsPolicy_ShouldReturnEmptyList() {
    // Given
    Long policyId = 1L;
    Integer page = 0;
    Integer size = 20;
    String status = null;
    List<AutoClaimDto> emptyClaims = List.of();

    when(policiesApiClient.getAutoClaimsByPolicyId(policyId, page, size, status))
        .thenReturn(ResponseEntity.ok(emptyClaims));

    // When
    Function<GetAutoClaimsByPolicyIdRequest, List<AutoClaimDto>> function =
        policyFunctions.getAutoClaimsByPolicyId();
    List<AutoClaimDto> result =
        function.apply(new GetAutoClaimsByPolicyIdRequest(policyId, page, size, status));

    // Then
    assertThat(result).isNotNull().isEmpty();
    verify(policiesApiClient).getAutoClaimsByPolicyId(policyId, page, size, status);
  }

  @Test
  @DisplayName("Request records should be properly constructed")
  void requestRecords_ShouldBeProperlyConstructed() {
    // Given & When
    PolicyDto policy = createSamplePolicy();
    PolicyConditionsDto conditions = createSamplePolicyConditions();

    CreatePolicyRequest createRequest = new CreatePolicyRequest(policy);
    GetPolicyByIdRequest getByIdRequest = new GetPolicyByIdRequest(1L);
    GetPolicyByPolicyNumberRequest getByNumberRequest =
        new GetPolicyByPolicyNumberRequest("POL-2024-001");
    GetAllPoliciesRequest getAllRequest = new GetAllPoliciesRequest();
    UpdatePolicyRequest updateRequest = new UpdatePolicyRequest(1L, policy);
    GetAutoClaimsByPolicyIdRequest autoClaimsRequest =
        new GetAutoClaimsByPolicyIdRequest(1L, 0, 20, "PENDING");
    GetHomeClaimsByPolicyIdRequest homeClaimsRequest =
        new GetHomeClaimsByPolicyIdRequest(1L, 0, 20, "APPROVED");
    GetHealthClaimsByPolicyIdRequest healthClaimsRequest =
        new GetHealthClaimsByPolicyIdRequest(1L, 0, 20, "IN_REVIEW");
    GetPolicyConditionsRequest conditionsRequest = new GetPolicyConditionsRequest();
    UpdatePolicyConditionsRequest updateConditionsRequest =
        new UpdatePolicyConditionsRequest(conditions);

    // Then
    assertThat(createRequest.policyDto()).isEqualTo(policy);
    assertThat(getByIdRequest.policyId()).isEqualTo(1L);
    assertThat(getByNumberRequest.policyNumber()).isEqualTo("POL-2024-001");
    assertThat(getAllRequest).isNotNull();
    assertThat(updateRequest.policyId()).isEqualTo(1L);
    assertThat(updateRequest.policyDto()).isEqualTo(policy);
    assertThat(autoClaimsRequest.policyId()).isEqualTo(1L);
    assertThat(autoClaimsRequest.page()).isZero();
    assertThat(autoClaimsRequest.size()).isEqualTo(20);
    assertThat(autoClaimsRequest.status()).isEqualTo("PENDING");
    assertThat(homeClaimsRequest.policyId()).isEqualTo(1L);
    assertThat(homeClaimsRequest.status()).isEqualTo("APPROVED");
    assertThat(healthClaimsRequest.policyId()).isEqualTo(1L);
    assertThat(healthClaimsRequest.status()).isEqualTo("IN_REVIEW");
    assertThat(conditionsRequest).isNotNull();
    assertThat(updateConditionsRequest.policyConditionsDto()).isEqualTo(conditions);
  }

  /** Creates a sample policy for testing. */
  private PolicyDto createSamplePolicy() {
    PolicyDto policy = new PolicyDto();
    policy.setPolicyNumber("POL-2024-001");
    policy.setType(PolicyDto.TypeEnum.AUTO);
    policy.setStatus(PolicyDto.StatusEnum.ACTIVE);
    policy.setCustomerId(1L);
    policy.setAgencyId(1L);
    policy.setPremium(new BigDecimal("1200.00"));
    policy.setStartDate(LocalDate.of(2024, 1, 1));
    policy.setEndDate(LocalDate.of(2024, 12, 31));
    return policy;
  }

  /** Creates sample policies for testing. */
  private List<PolicyDto> createSamplePolicies() {
    PolicyDto autoPolicy = createSamplePolicy();
    autoPolicy.setId(1L);
    autoPolicy.setType(PolicyDto.TypeEnum.AUTO);

    PolicyDto homePolicy = createSamplePolicy();
    homePolicy.setId(2L);
    homePolicy.setPolicyNumber("POL-2024-002");
    homePolicy.setType(PolicyDto.TypeEnum.HOME);

    PolicyDto healthPolicy = createSamplePolicy();
    healthPolicy.setId(3L);
    healthPolicy.setPolicyNumber("POL-2024-003");
    healthPolicy.setType(PolicyDto.TypeEnum.HEALTH);

    return List.of(autoPolicy, homePolicy, healthPolicy);
  }

  /** Creates sample auto claims for testing. */
  private List<AutoClaimDto> createSampleAutoClaims() {
    AutoClaimDto claim1 = new AutoClaimDto();
    claim1.setId(1L);
    claim1.setPolicyId(1L);
    claim1.setLicensePlate("ABC-123");
    claim1.setDescription("Vehicle collision");

    AutoClaimDto claim2 = new AutoClaimDto();
    claim2.setId(2L);
    claim2.setPolicyId(1L);
    claim2.setLicensePlate("XYZ-789");
    claim2.setDescription("Parking accident");

    return List.of(claim1, claim2);
  }

  /** Creates sample home claims for testing. */
  private List<HomeClaimDto> createSampleHomeClaims() {
    HomeClaimDto claim1 = new HomeClaimDto();
    claim1.setId(1L);
    claim1.setPolicyId(1L);
    claim1.setTypeOfDamage("Fire damage");
    claim1.setDescription("Kitchen fire");

    HomeClaimDto claim2 = new HomeClaimDto();
    claim2.setId(2L);
    claim2.setPolicyId(1L);
    claim2.setTypeOfDamage("Water damage");
    claim2.setDescription("Pipe burst");

    return List.of(claim1, claim2);
  }

  /** Creates sample health claims for testing. */
  private List<HealthClaimDto> createSampleHealthClaims() {
    HealthClaimDto claim1 = new HealthClaimDto();
    claim1.setId(1L);
    claim1.setPolicyId(1L);
    claim1.setMedicalProvider("City General Hospital");
    claim1.setDescription("Emergency visit");

    HealthClaimDto claim2 = new HealthClaimDto();
    claim2.setId(2L);
    claim2.setPolicyId(1L);
    claim2.setMedicalProvider("Family Care Clinic");
    claim2.setDescription("Routine checkup");

    return List.of(claim1, claim2);
  }

  /** Creates sample policy conditions for testing. */
  private PolicyConditionsDto createSamplePolicyConditions() {
    PolicyConditionsDto conditions = new PolicyConditionsDto();
    conditions.setFreeCancellationDays(14);
    conditions.setNoClaimBonusPercentage(new BigDecimal("0.05"));

    CancellationPenaltyRuleDto penaltyRule = new CancellationPenaltyRuleDto();
    penaltyRule.setMonthsRemainingThreshold(6);
    penaltyRule.setPenaltyPercentage(new BigDecimal("5.0"));
    conditions.setCancellationRules(List.of(penaltyRule));

    return conditions;
  }
}
