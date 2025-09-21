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
import tech.yildirim.aiinsurance.api.generated.clients.ClaimsApiClient;
import tech.yildirim.aiinsurance.api.generated.model.AssignAdjusterRequestDto;
import tech.yildirim.aiinsurance.api.generated.model.AutoClaimDto;
import tech.yildirim.aiinsurance.api.generated.model.ClaimDto;
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

/**
 * Unit tests for {@link ClaimFunctions}.
 *
 * <p>Tests the AI-callable functions for claim management, including creation, retrieval, updates,
 * deletion, and adjuster assignment for auto, home, and health claims.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimFunctions Tests")
class ClaimFunctionsTest {

  @Mock private ClaimsApiClient claimsApiClient;

  private ClaimFunctions claimFunctions;

  @BeforeEach
  void setUp() {
    claimFunctions = new ClaimFunctions(claimsApiClient);
  }

  // --- AUTO CLAIM TESTS ---

  @Test
  @DisplayName("Should create auto claim when valid claim data is provided")
  void createAutoClaim_WithValidClaimData_ShouldReturnCreatedClaim() {
    // Given
    AutoClaimDto inputClaim = createSampleAutoClaim();
    AutoClaimDto expectedClaim = createSampleAutoClaim();
    expectedClaim.setId(1L);

    when(claimsApiClient.createAutoClaim(inputClaim)).thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<CreateAutoClaimReq, ResponseWrapper<AutoClaimDto>> function =
        claimFunctions.createAutoClaim();
    ResponseWrapper<AutoClaimDto> result = function.apply(new CreateAutoClaimReq(inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(1L);
    assertThat(result.getData().getLicensePlate()).isEqualTo("ABC-123");
    assertThat(result.getData().getDescription()).isEqualTo("Vehicle collision on highway");
    verify(claimsApiClient).createAutoClaim(inputClaim);
  }

  @Test
  @DisplayName("Should throw exception when create auto claim API returns null body")
  void createAutoClaim_WithNullResponse_ShouldThrowException() {
    // Given
    AutoClaimDto inputClaim = createSampleAutoClaim();
    when(claimsApiClient.createAutoClaim(inputClaim)).thenReturn(ResponseEntity.ok(null));

    // When & Then
    Function<CreateAutoClaimReq, ResponseWrapper<AutoClaimDto>> function =
        claimFunctions.createAutoClaim();
    CreateAutoClaimReq request = new CreateAutoClaimReq(inputClaim);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should return auto claim when valid claim ID is provided")
  void getAutoClaimById_WithValidClaimId_ShouldReturnClaim() {
    // Given
    Long claimId = 1L;
    AutoClaimDto expectedClaim = createSampleAutoClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.getAutoClaimById(claimId)).thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<GetAutoClaimByIdReq, ResponseWrapper<AutoClaimDto>> function =
        claimFunctions.getAutoClaimById();
    ResponseWrapper<AutoClaimDto> result = function.apply(new GetAutoClaimByIdReq(claimId));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getLicensePlate()).isEqualTo("ABC-123");
    verify(claimsApiClient).getAutoClaimById(claimId);
  }

  @Test
  @DisplayName("Should return all auto claims when requested")
  void getAllAutoClaims_WithValidRequest_ShouldReturnClaims() {
    // Given
    Integer page = 0;
    Integer size = 20;
    String status = "PENDING";
    List<AutoClaimDto> expectedClaims = createSampleAutoClaims();

    when(claimsApiClient.getAllAutoClaims(page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetAllAutoClaimsReq, ResponseWrapper<List<AutoClaimDto>>> function =
        claimFunctions.getAllAutoClaims();
    ResponseWrapper<List<AutoClaimDto>> result =
        function.apply(new GetAllAutoClaimsReq(page, size, status));

    // Then
    assertThat(result.getData()).isNotNull().hasSize(2);
    assertThat(result.getData().get(0).getLicensePlate()).isEqualTo("ABC-123");
    assertThat(result.getData().get(1).getLicensePlate()).isEqualTo("XYZ-789");
    verify(claimsApiClient).getAllAutoClaims(page, size, status);
  }

  @Test
  @DisplayName("Should update auto claim when valid data is provided")
  void updateAutoClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    AutoClaimDto inputClaim = createSampleAutoClaim();
    AutoClaimDto expectedClaim = createSampleAutoClaim();
    expectedClaim.setId(claimId);
    expectedClaim.setDescription("Updated accident description");

    when(claimsApiClient.updateAutoClaim(claimId, inputClaim))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<UpdateAutoClaimReq, ResponseWrapper<AutoClaimDto>> function =
        claimFunctions.updateAutoClaim();
    ResponseWrapper<AutoClaimDto> result =
        function.apply(new UpdateAutoClaimReq(claimId, inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getDescription()).isEqualTo("Updated accident description");
    verify(claimsApiClient).updateAutoClaim(claimId, inputClaim);
  }

  @Test
  @DisplayName("Should delete auto claim when valid claim ID is provided")
  void deleteAutoClaim_WithValidClaimId_ShouldReturnSuccessMessage() {
    // Given
    Long claimId = 1L;

    when(claimsApiClient.deleteAutoClaim(claimId)).thenReturn(ResponseEntity.noContent().build());

    // When
    Function<DeleteAutoClaimReq, ResponseWrapper<String>> function =
        claimFunctions.deleteAutoClaim();
    ResponseWrapper<String> result = function.apply(new DeleteAutoClaimReq(claimId));

    // Then
    assertThat(result.getData()).contains("Auto claim deleted successfully");
    assertThat(result.isSuccess()).isTrue();
    verify(claimsApiClient).deleteAutoClaim(claimId);
  }

  @Test
  @DisplayName("Should assign adjuster to auto claim when valid data is provided")
  void assignAdjusterToAutoClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    AssignAdjusterRequestDto adjusterRequest = createSampleAdjusterRequest();
    AutoClaimDto expectedClaim = createSampleAutoClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.assignAdjusterToAutoClaim(claimId, adjusterRequest))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<AssignAdjusterToAutoClaimReq, ResponseWrapper<AutoClaimDto>> function =
        claimFunctions.assignAdjusterToAutoClaim();
    ResponseWrapper<AutoClaimDto> result =
        function.apply(new AssignAdjusterToAutoClaimReq(claimId, adjusterRequest));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    verify(claimsApiClient).assignAdjusterToAutoClaim(claimId, adjusterRequest);
  }

  // --- HOME CLAIM TESTS ---

  @Test
  @DisplayName("Should create home claim when valid claim data is provided")
  void createHomeClaim_WithValidClaimData_ShouldReturnCreatedClaim() {
    // Given
    HomeClaimDto inputClaim = createSampleHomeClaim();
    HomeClaimDto expectedClaim = createSampleHomeClaim();
    expectedClaim.setId(1L);

    when(claimsApiClient.createHomeClaim(inputClaim)).thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<CreateHomeClaimReq, ResponseWrapper<HomeClaimDto>> function =
        claimFunctions.createHomeClaim();
    ResponseWrapper<HomeClaimDto> result = function.apply(new CreateHomeClaimReq(inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(1L);
    assertThat(result.getData().getTypeOfDamage()).isEqualTo("Fire damage");
    assertThat(result.getData().getDescription())
        .isEqualTo("Kitchen fire caused significant damage");
    verify(claimsApiClient).createHomeClaim(inputClaim);
  }

  @Test
  @DisplayName("Should return home claim when valid claim ID is provided")
  void getHomeClaimById_WithValidClaimId_ShouldReturnClaim() {
    // Given
    Long claimId = 1L;
    HomeClaimDto expectedClaim = createSampleHomeClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.getHomeClaimById(claimId)).thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<GetHomeClaimByIdReq, ResponseWrapper<HomeClaimDto>> function =
        claimFunctions.getHomeClaimById();
    ResponseWrapper<HomeClaimDto> result = function.apply(new GetHomeClaimByIdReq(claimId));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getTypeOfDamage()).isEqualTo("Fire damage");
    verify(claimsApiClient).getHomeClaimById(claimId);
  }

  @Test
  @DisplayName("Should return all home claims when requested")
  void getAllHomeClaims_WithValidRequest_ShouldReturnClaims() {
    // Given
    Integer page = 0;
    Integer size = 20;
    String status = "APPROVED";
    List<HomeClaimDto> expectedClaims = createSampleHomeClaims();

    when(claimsApiClient.getAllHomeClaims(page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetAllHomeClaimsReq, ResponseWrapper<List<HomeClaimDto>>> function =
        claimFunctions.getAllHomeClaims();
    ResponseWrapper<List<HomeClaimDto>> result =
        function.apply(new GetAllHomeClaimsReq(page, size, status));

    // Then
    assertThat(result.getData()).isNotNull().hasSize(2);
    assertThat(result.getData().get(0).getTypeOfDamage()).isEqualTo("Fire damage");
    assertThat(result.getData().get(1).getTypeOfDamage()).isEqualTo("Water damage");
    verify(claimsApiClient).getAllHomeClaims(page, size, status);
  }

  @Test
  @DisplayName("Should update home claim when valid data is provided")
  void updateHomeClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    HomeClaimDto inputClaim = createSampleHomeClaim();
    HomeClaimDto expectedClaim = createSampleHomeClaim();
    expectedClaim.setId(claimId);
    expectedClaim.setDescription("Updated damage assessment");

    when(claimsApiClient.updateHomeClaim(claimId, inputClaim))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<UpdateHomeClaimReq, ResponseWrapper<HomeClaimDto>> function =
        claimFunctions.updateHomeClaim();
    ResponseWrapper<HomeClaimDto> result =
        function.apply(new UpdateHomeClaimReq(claimId, inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getDescription()).isEqualTo("Updated damage assessment");
    verify(claimsApiClient).updateHomeClaim(claimId, inputClaim);
  }

  @Test
  @DisplayName("Should delete home claim when valid claim ID is provided")
  void deleteHomeClaim_WithValidClaimId_ShouldReturnSuccessMessage() {
    // Given
    Long claimId = 1L;

    when(claimsApiClient.deleteHomeClaim(claimId)).thenReturn(ResponseEntity.noContent().build());

    // When
    Function<DeleteHomeClaimReq, ResponseWrapper<String>> function =
        claimFunctions.deleteHomeClaim();
    ResponseWrapper<String> result = function.apply(new DeleteHomeClaimReq(claimId));

    // Then
    assertThat(result.getData()).contains("Home claim deleted successfully");
    assertThat(result.isSuccess()).isTrue();
    verify(claimsApiClient).deleteHomeClaim(claimId);
  }

  @Test
  @DisplayName("Should assign adjuster to home claim when valid data is provided")
  void assignAdjusterToHomeClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    AssignAdjusterRequestDto adjusterRequest = createSampleAdjusterRequest();
    HomeClaimDto expectedClaim = createSampleHomeClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.assignAdjusterToHomeClaim(claimId, adjusterRequest))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<AssignAdjusterToHomeClaimReq, ResponseWrapper<HomeClaimDto>> function =
        claimFunctions.assignAdjusterToHomeClaim();
    ResponseWrapper<HomeClaimDto> result =
        function.apply(new AssignAdjusterToHomeClaimReq(claimId, adjusterRequest));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    verify(claimsApiClient).assignAdjusterToHomeClaim(claimId, adjusterRequest);
  }

  // --- HEALTH CLAIM TESTS ---

  @Test
  @DisplayName("Should create health claim when valid claim data is provided")
  void createHealthClaim_WithValidClaimData_ShouldReturnCreatedClaim() {
    // Given
    HealthClaimDto inputClaim = createSampleHealthClaim();
    HealthClaimDto expectedClaim = createSampleHealthClaim();
    expectedClaim.setId(1L);

    when(claimsApiClient.createHealthClaim(inputClaim))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<CreateHealthClaimReq, ResponseWrapper<HealthClaimDto>> function =
        claimFunctions.createHealthClaim();
    ResponseWrapper<HealthClaimDto> result = function.apply(new CreateHealthClaimReq(inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(1L);
    assertThat(result.getData().getMedicalProvider()).isEqualTo("City General Hospital");
    assertThat(result.getData().getDescription()).isEqualTo("Emergency room visit for chest pain");
    verify(claimsApiClient).createHealthClaim(inputClaim);
  }

  @Test
  @DisplayName("Should return health claim when valid claim ID is provided")
  void getHealthClaimById_WithValidClaimId_ShouldReturnClaim() {
    // Given
    Long claimId = 1L;
    HealthClaimDto expectedClaim = createSampleHealthClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.getHealthClaimById(claimId)).thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<GetHealthClaimByIdReq, ResponseWrapper<HealthClaimDto>> function =
        claimFunctions.getHealthClaimById();
    ResponseWrapper<HealthClaimDto> result = function.apply(new GetHealthClaimByIdReq(claimId));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getMedicalProvider()).isEqualTo("City General Hospital");
    verify(claimsApiClient).getHealthClaimById(claimId);
  }

  @Test
  @DisplayName("Should return all health claims when requested")
  void getAllHealthClaims_WithValidRequest_ShouldReturnClaims() {
    // Given
    Integer page = 0;
    Integer size = 20;
    String status = "IN_REVIEW";
    List<HealthClaimDto> expectedClaims = createSampleHealthClaims();

    when(claimsApiClient.getAllHealthClaims(page, size, status))
        .thenReturn(ResponseEntity.ok(expectedClaims));

    // When
    Function<GetAllHealthClaimsReq, ResponseWrapper<List<HealthClaimDto>>> function =
        claimFunctions.getAllHealthClaims();
    ResponseWrapper<List<HealthClaimDto>> result =
        function.apply(new GetAllHealthClaimsReq(page, size, status));

    // Then
    assertThat(result.getData()).isNotNull().hasSize(2);
    assertThat(result.getData().get(0).getMedicalProvider()).isEqualTo("City General Hospital");
    assertThat(result.getData().get(1).getMedicalProvider()).isEqualTo("Family Care Clinic");
    verify(claimsApiClient).getAllHealthClaims(page, size, status);
  }

  @Test
  @DisplayName("Should update health claim when valid data is provided")
  void updateHealthClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    HealthClaimDto inputClaim = createSampleHealthClaim();
    HealthClaimDto expectedClaim = createSampleHealthClaim();
    expectedClaim.setId(claimId);
    expectedClaim.setDescription("Updated medical assessment");

    when(claimsApiClient.updateHealthClaim(claimId, inputClaim))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When
    Function<UpdateHealthClaimReq, ResponseWrapper<HealthClaimDto>> function =
        claimFunctions.updateHealthClaim();
    ResponseWrapper<HealthClaimDto> result =
        function.apply(new UpdateHealthClaimReq(claimId, inputClaim));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    assertThat(result.getData().getDescription()).isEqualTo("Updated medical assessment");
    verify(claimsApiClient).updateHealthClaim(claimId, inputClaim);
  }

  @Test
  @DisplayName("Should delete health claim when valid claim ID is provided")
  void deleteHealthClaim_WithValidClaimId_ShouldReturnSuccessMessage() {
    // Given
    Long claimId = 1L;

    when(claimsApiClient.deleteHealthClaim(claimId)).thenReturn(ResponseEntity.noContent().build());

    // When
    Function<DeleteHealthClaimReq, ResponseWrapper<String>> function =
        claimFunctions.deleteHealthClaim();
    ResponseWrapper<String> result = function.apply(new DeleteHealthClaimReq(claimId));

    // Then
    assertThat(result.getData()).contains("Health claim deleted successfully");
    assertThat(result.isSuccess()).isTrue();
    verify(claimsApiClient).deleteHealthClaim(claimId);
  }

  @Test
  @DisplayName("Should assign adjuster to health claim when valid data is provided")
  void assignAdjusterToHealthClaim_WithValidData_ShouldReturnUpdatedClaim() {
    // Given
    Long claimId = 1L;
    AssignAdjusterRequestDto adjusterRequest = createSampleAdjusterRequest();
    HealthClaimDto expectedClaim = createSampleHealthClaim();
    expectedClaim.setId(claimId);

    when(claimsApiClient.assignAdjusterToHealthClaim(claimId, adjusterRequest))
        .thenReturn(ResponseEntity.ok(expectedClaim));

    // When

    Function<AssignAdjusterToHealthClaimReq, ResponseWrapper<HealthClaimDto>> function =
        claimFunctions.assignAdjusterToHealthClaim();
    ResponseWrapper<HealthClaimDto> result =
        function.apply(new AssignAdjusterToHealthClaimReq(claimId, adjusterRequest));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(claimId);
    verify(claimsApiClient).assignAdjusterToHealthClaim(claimId, adjusterRequest);
  }

  // --- REQUEST RECORDS TESTS ---

  @Test
  @DisplayName("Request records should be properly constructed")
  void requestRecords_ShouldBeProperlyConstructed() {
    // Given & When
    AutoClaimDto autoClaim = createSampleAutoClaim();
    HomeClaimDto homeClaim = createSampleHomeClaim();
    HealthClaimDto healthClaim = createSampleHealthClaim();
    AssignAdjusterRequestDto adjusterRequest = createSampleAdjusterRequest();

    CreateAutoClaimReq createAutoRequest = new CreateAutoClaimReq(autoClaim);
    GetAutoClaimByIdReq getAutoByIdRequest = new GetAutoClaimByIdReq(1L);
    GetAllAutoClaimsReq getAllAutoRequest = new GetAllAutoClaimsReq(0, 20, "PENDING");
    UpdateAutoClaimReq updateAutoRequest = new UpdateAutoClaimReq(1L, autoClaim);
    DeleteAutoClaimReq deleteAutoRequest = new DeleteAutoClaimReq(1L);
    AssignAdjusterToAutoClaimReq assignAutoRequest =
        new AssignAdjusterToAutoClaimReq(1L, adjusterRequest);

    CreateHomeClaimReq createHomeRequest = new CreateHomeClaimReq(homeClaim);
    GetHomeClaimByIdReq getHomeByIdRequest = new GetHomeClaimByIdReq(1L);
    GetAllHomeClaimsReq getAllHomeRequest = new GetAllHomeClaimsReq(0, 20, "APPROVED");
    UpdateHomeClaimReq updateHomeRequest = new UpdateHomeClaimReq(1L, homeClaim);
    DeleteHomeClaimReq deleteHomeRequest = new DeleteHomeClaimReq(1L);
    AssignAdjusterToHomeClaimReq assignHomeRequest =
        new AssignAdjusterToHomeClaimReq(1L, adjusterRequest);

    CreateHealthClaimReq createHealthRequest = new CreateHealthClaimReq(healthClaim);
    GetHealthClaimByIdReq getHealthByIdRequest = new GetHealthClaimByIdReq(1L);
    GetAllHealthClaimsReq getAllHealthRequest = new GetAllHealthClaimsReq(0, 20, "IN_REVIEW");
    UpdateHealthClaimReq updateHealthRequest = new UpdateHealthClaimReq(1L, healthClaim);
    DeleteHealthClaimReq deleteHealthRequest = new DeleteHealthClaimReq(1L);
    AssignAdjusterToHealthClaimReq assignHealthRequest =
        new AssignAdjusterToHealthClaimReq(1L, adjusterRequest);

    // Then
    assertThat(createAutoRequest.autoClaimDto()).isEqualTo(autoClaim);
    assertThat(getAutoByIdRequest.claimId()).isEqualTo(1L);
    assertThat(getAllAutoRequest.page()).isZero();
    assertThat(getAllAutoRequest.size()).isEqualTo(20);
    assertThat(getAllAutoRequest.status()).isEqualTo("PENDING");
    assertThat(updateAutoRequest.claimId()).isEqualTo(1L);
    assertThat(updateAutoRequest.autoClaimDto()).isEqualTo(autoClaim);
    assertThat(deleteAutoRequest.claimId()).isEqualTo(1L);
    assertThat(assignAutoRequest.claimId()).isEqualTo(1L);
    assertThat(assignAutoRequest.assignAdjusterRequestDto()).isEqualTo(adjusterRequest);

    assertThat(createHomeRequest.homeClaimDto()).isEqualTo(homeClaim);
    assertThat(getHomeByIdRequest.claimId()).isEqualTo(1L);
    assertThat(getAllHomeRequest.status()).isEqualTo("APPROVED");
    assertThat(updateHomeRequest.claimId()).isEqualTo(1L);
    assertThat(deleteHomeRequest.claimId()).isEqualTo(1L);
    assertThat(assignHomeRequest.assignAdjusterRequestDto()).isEqualTo(adjusterRequest);

    assertThat(createHealthRequest.healthClaimDto()).isEqualTo(healthClaim);
    assertThat(getHealthByIdRequest.claimId()).isEqualTo(1L);
    assertThat(getAllHealthRequest.status()).isEqualTo("IN_REVIEW");
    assertThat(updateHealthRequest.claimId()).isEqualTo(1L);
    assertThat(deleteHealthRequest.claimId()).isEqualTo(1L);
    assertThat(assignHealthRequest.assignAdjusterRequestDto()).isEqualTo(adjusterRequest);
  }

  // --- HELPER METHODS ---

  /** Creates a sample auto claim for testing. */
  private AutoClaimDto createSampleAutoClaim() {
    AutoClaimDto claim = new AutoClaimDto();
    claim.setClaimType(ClaimDto.ClaimTypeEnum.AUTO_CLAIM_DTO);
    claim.setPolicyId(1L);
    claim.setLicensePlate("ABC-123");
    claim.setDescription("Vehicle collision on highway");
    claim.setEstimatedAmount(new BigDecimal("5000.00"));
    claim.setDateOfIncident(LocalDate.now().minusDays(3));
    claim.setVehicleVin("1HGBH41JXMN109186");
    claim.setAccidentLocation("Highway 101, Mile Marker 45");
    return claim;
  }

  /** Creates sample auto claims for testing. */
  private List<AutoClaimDto> createSampleAutoClaims() {
    AutoClaimDto claim1 = createSampleAutoClaim();
    claim1.setId(1L);
    claim1.setLicensePlate("ABC-123");

    AutoClaimDto claim2 = createSampleAutoClaim();
    claim2.setId(2L);
    claim2.setLicensePlate("XYZ-789");
    claim2.setDescription("Parking lot accident");
    claim2.setAccidentLocation("Shopping Mall Parking Lot");

    return List.of(claim1, claim2);
  }

  /** Creates a sample home claim for testing. */
  private HomeClaimDto createSampleHomeClaim() {
    HomeClaimDto claim = new HomeClaimDto();
    claim.setClaimType(ClaimDto.ClaimTypeEnum.HOME_CLAIM_DTO);
    claim.setPolicyId(1L);
    claim.setTypeOfDamage("Fire damage");
    claim.setDescription("Kitchen fire caused significant damage");
    claim.setEstimatedAmount(new BigDecimal("15000.00"));
    claim.setDateOfIncident(LocalDate.now().minusDays(5));
    claim.setDamagedItems("Kitchen cabinets, appliances, flooring");
    return claim;
  }

  /** Creates sample home claims for testing. */
  private List<HomeClaimDto> createSampleHomeClaims() {
    HomeClaimDto claim1 = createSampleHomeClaim();
    claim1.setId(1L);
    claim1.setTypeOfDamage("Fire damage");

    HomeClaimDto claim2 = createSampleHomeClaim();
    claim2.setId(2L);
    claim2.setTypeOfDamage("Water damage");
    claim2.setDescription("Pipe burst in basement");
    claim2.setDamagedItems("Basement flooring, drywall, furniture");

    return List.of(claim1, claim2);
  }

  /** Creates a sample health claim for testing. */
  private HealthClaimDto createSampleHealthClaim() {
    HealthClaimDto claim = new HealthClaimDto();
    claim.setClaimType(ClaimDto.ClaimTypeEnum.HEALTH_CLAIM_DTO);
    claim.setPolicyId(1L);
    claim.setMedicalProvider("City General Hospital");
    claim.setDescription("Emergency room visit for chest pain");
    claim.setEstimatedAmount(new BigDecimal("2500.00"));
    claim.setDateOfIncident(LocalDate.now().minusDays(7));
    claim.setProcedureCode("CPT-99281");
    return claim;
  }

  /** Creates sample health claims for testing. */
  private List<HealthClaimDto> createSampleHealthClaims() {
    HealthClaimDto claim1 = createSampleHealthClaim();
    claim1.setId(1L);
    claim1.setMedicalProvider("City General Hospital");

    HealthClaimDto claim2 = createSampleHealthClaim();
    claim2.setId(2L);
    claim2.setMedicalProvider("Family Care Clinic");
    claim2.setDescription("Routine checkup and blood work");
    claim2.setProcedureCode("CPT-99213");

    return List.of(claim1, claim2);
  }

  /** Creates a sample adjuster assignment request for testing. */
  private AssignAdjusterRequestDto createSampleAdjusterRequest() {
    AssignAdjusterRequestDto request = new AssignAdjusterRequestDto();
    request.setEmployeeId(101L);
    return request;
  }
}
