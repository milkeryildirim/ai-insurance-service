package tech.yildirim.aiinsurance.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.http.ResponseEntity;
import tech.yildirim.aiinsurance.ai.functions.CustomerFunctions;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.CreateCustomerReq;
import tech.yildirim.aiinsurance.model.ai.request.GetCustomerByIdRequestReq;
import tech.yildirim.aiinsurance.service.CustomerService;

@ExtendWith(MockitoExtension.class)
class SecurityAspectTest {

  @Mock private CustomersApiClient customersApiClient;

  @Mock private CustomerService customerService;

  @Mock private SecurityUtils securityUtils;

  /**
   * Creates a proxied CustomerFunctions instance with SecurityAspect applied. This method
   * centralizes the proxy setup to avoid code duplication across tests.
   */
  private CustomerFunctions createProxiedCustomerFunctions() {
    CustomerFunctions customerFunctions = new CustomerFunctions(customersApiClient);
    SecurityAspect securityAspect = new SecurityAspect(securityUtils, customerService);
    AspectJProxyFactory factory = new AspectJProxyFactory(customerFunctions);
    factory.addAspect(securityAspect);
    return factory.getProxy();
  }

  @Test
  @DisplayName("Should return error when trying to access AI-blocked function")
  void testBlockedForAIAccess() {
    CustomerFunctions proxiedCustomerFunctions = createProxiedCustomerFunctions();

    Function<CreateCustomerReq, ResponseWrapper<CustomerDto>> createCustomerFunction =
        proxiedCustomerFunctions.createCustomer();
    ResponseWrapper<CustomerDto> response =
        createCustomerFunction.apply(new CreateCustomerReq(null));

    assertFalse(response.isSuccess());
    assertEquals(
        "This operation is not available through the AI assistant for security reasons. Please contact customer service for assistance.",
        response.getErrorMessage());
  }

  @Test
  @DisplayName("Should return error when unauthenticated user tries to access function")
  void testUnauthenticatedAccess() {
    when(securityUtils.getCurrentUserCustomerId()).thenReturn(null);

    CustomerFunctions proxiedCustomerFunctions = createProxiedCustomerFunctions();

    Function<GetCustomerByIdRequestReq, ResponseWrapper<CustomerDto>> getCustomerByIdFunction =
        proxiedCustomerFunctions.getCustomerById();
    ResponseWrapper<CustomerDto> response =
        getCustomerByIdFunction.apply(new GetCustomerByIdRequestReq(1L));

    assertFalse(response.isSuccess());
    assertEquals("User is not authenticated", response.getErrorMessage());
  }

  @Test
  @DisplayName("Should return error when trying to access another customer's data")
  void testUnauthorizedAccess() {
    when(securityUtils.getCurrentUserCustomerId()).thenReturn(1L);

    CustomerFunctions proxiedCustomerFunctions = createProxiedCustomerFunctions();

    Function<GetCustomerByIdRequestReq, ResponseWrapper<CustomerDto>> getCustomerByIdFunction =
        proxiedCustomerFunctions.getCustomerById();
    // User 1 (authenticated) tries to get data for user 2
    ResponseWrapper<CustomerDto> response =
        getCustomerByIdFunction.apply(new GetCustomerByIdRequestReq(2L));

    assertFalse(response.isSuccess());
    assertTrue(
        response.getErrorMessage().contains("Access denied. You can only access your own data."));
  }

  @Test
  @DisplayName("Should succeed when authorized user accesses their own data")
  void testAuthorizedAccess() {
    when(securityUtils.getCurrentUserCustomerId()).thenReturn(1L);

    // Mock ResponseEntity - should return ResponseEntity, not null
    CustomerDto mockCustomer = new CustomerDto();
    // Generated class might not have setCustomerId method, empty CustomerDto is sufficient
    ResponseEntity<CustomerDto> responseEntity = ResponseEntity.ok(mockCustomer);
    when(customersApiClient.getCustomerById(1L)).thenReturn(responseEntity);

    CustomerFunctions proxiedCustomerFunctions = createProxiedCustomerFunctions();

    Function<GetCustomerByIdRequestReq, ResponseWrapper<CustomerDto>> getCustomerByIdFunction =
        proxiedCustomerFunctions.getCustomerById();
    GetCustomerByIdRequestReq request = new GetCustomerByIdRequestReq(1L);

    // This test verifies that the aspect passes security check and calls the original function
    ResponseWrapper<CustomerDto> response = getCustomerByIdFunction.apply(request);

    // If security check passes, original function is called and returns mockCustomer
    assertTrue(response.isSuccess());
    assertNull(response.getErrorMessage());
    assertNotNull(response.getData());
  }
}
