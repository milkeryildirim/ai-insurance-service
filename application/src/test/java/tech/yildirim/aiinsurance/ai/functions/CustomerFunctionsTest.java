package tech.yildirim.aiinsurance.ai.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.GetCustomerByPolicyNumberReq;
import tech.yildirim.aiinsurance.model.ai.request.GetPoliciesByCustomerIdReq;

/**
 * Unit tests for {@link CustomerFunctions}.
 *
 * <p>Tests the AI-callable functions for customer management, including customer lookup by policy
 * number and policy retrieval by customer ID.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerFunctions Tests")
class CustomerFunctionsTest {

  @Mock private CustomersApiClient customersApiClient;

  private CustomerFunctions customerFunctions;

  @BeforeEach
  void setUp() {
    customerFunctions = new CustomerFunctions(customersApiClient);
  }

  @Test
  @DisplayName("Should return customer when valid policy number is provided")
  void getCustomerByPolicyNumber_WithValidPolicyNumber_ShouldReturnCustomer() {
    // Given
    String policyNumber = "POL-12345";
    CustomerDto expectedCustomer = createSampleCustomer();

    when(customersApiClient.getCustomerByPolicyNumber(policyNumber))
        .thenReturn(ResponseEntity.ok(expectedCustomer));

    // When
    Function<GetCustomerByPolicyNumberReq, ResponseWrapper<CustomerDto>> function =
        customerFunctions.getCustomerByPolicyNumber();
    ResponseWrapper<CustomerDto> result =
        function.apply(new GetCustomerByPolicyNumberReq(policyNumber));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getData().getId()).isEqualTo(expectedCustomer.getId());
    assertThat(result.getData().getFirstName()).isEqualTo(expectedCustomer.getFirstName());
    assertThat(result.getData().getLastName()).isEqualTo(expectedCustomer.getLastName());
  }

  @Test
  @DisplayName("Should throw exception when API returns null ResponseEntity")
  void getCustomerByPolicyNumber_WithNullResponseEntity_ShouldThrowException() {
    // Given
    String policyNumber = "POL-12345";
    when(customersApiClient.getCustomerByPolicyNumber(anyString())).thenReturn(null);

    // When & Then
    Function<GetCustomerByPolicyNumberReq, ResponseWrapper<CustomerDto>> function =
        customerFunctions.getCustomerByPolicyNumber();
    GetCustomerByPolicyNumberReq request = new GetCustomerByPolicyNumberReq(policyNumber);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Should return policies when valid customer ID is provided")
  void getPoliciesByCustomerId_WithValidCustomerId_ShouldReturnPolicies() {
    // Given
    Long customerId = 1L;
    List<PolicyDto> expectedPolicies = createSamplePolicies();

    when(customersApiClient.getPoliciesByCustomerId(customerId))
        .thenReturn(ResponseEntity.ok(expectedPolicies));

    // When
    Function<GetPoliciesByCustomerIdReq, ResponseWrapper<List<PolicyDto>>> function =
        customerFunctions.getPoliciesByCustomerId();
    ResponseWrapper<List<PolicyDto>> result =
        function.apply(new GetPoliciesByCustomerIdReq(customerId));

    // Then
    assertThat(result.getData()).isNotNull().hasSize(2);
    assertThat(result.getData().get(0).getPolicyNumber()).isEqualTo("POL-001");
    assertThat(result.getData().get(1).getPolicyNumber()).isEqualTo("POL-002");
  }

  @Test
  @DisplayName("Should return empty list when customer has no policies")
  void getPoliciesByCustomerId_WithNoPolices_ShouldReturnEmptyList() {
    // Given
    Long customerId = 1L;
    List<PolicyDto> emptyPolicies = List.of();

    when(customersApiClient.getPoliciesByCustomerId(customerId))
        .thenReturn(ResponseEntity.ok(emptyPolicies));

    // When
    Function<GetPoliciesByCustomerIdReq, ResponseWrapper<List<PolicyDto>>> function =
        customerFunctions.getPoliciesByCustomerId();
    ResponseWrapper<List<PolicyDto>> result =
        function.apply(new GetPoliciesByCustomerIdReq(customerId));

    // Then
    assertThat(result.getData()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should throw exception when policies API returns null ResponseEntity")
  void getPoliciesByCustomerId_WithNullResponseEntity_ShouldThrowException() {
    // Given
    Long customerId = 1L;
    when(customersApiClient.getPoliciesByCustomerId(anyLong())).thenReturn(null);

    // When & Then
    Function<GetPoliciesByCustomerIdReq, ResponseWrapper<List<PolicyDto>>> function =
        customerFunctions.getPoliciesByCustomerId();
    GetPoliciesByCustomerIdReq request = new GetPoliciesByCustomerIdReq(customerId);

    assertThatThrownBy(() -> function.apply(request)).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Request records should be properly constructed")
  void requestRecords_ShouldBeProperlyConstructed() {
    // Given & When
    GetCustomerByPolicyNumberReq customerRequest = new GetCustomerByPolicyNumberReq("POL-123");
    GetPoliciesByCustomerIdReq policiesRequest = new GetPoliciesByCustomerIdReq(1L);

    // Then
    assertThat(customerRequest.policyNumber()).isEqualTo("POL-123");
    assertThat(policiesRequest.customerId()).isEqualTo(1L);
  }

  /** Creates a sample customer for testing. */
  private CustomerDto createSampleCustomer() {
    CustomerDto customer = new CustomerDto();
    customer.setId(1L);
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john.doe@example.com");
    return customer;
  }

  /** Creates sample policies for testing. */
  private List<PolicyDto> createSamplePolicies() {
    PolicyDto policy1 = new PolicyDto();
    policy1.setId(1L);
    policy1.setPolicyNumber("POL-001");
    policy1.setCustomerId(1L);

    PolicyDto policy2 = new PolicyDto();
    policy2.setId(2L);
    policy2.setPolicyNumber("POL-002");
    policy2.setCustomerId(1L);

    return List.of(policy1, policy2);
  }
}
