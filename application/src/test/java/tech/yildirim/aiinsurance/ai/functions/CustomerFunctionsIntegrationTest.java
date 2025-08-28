package tech.yildirim.aiinsurance.ai.functions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tech.yildirim.aiinsurance.ai.functions.CustomerFunctions.GetCustomerByPolicyNumberRequest;
import tech.yildirim.aiinsurance.ai.functions.CustomerFunctions.GetPoliciesByCustomerIdRequest;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/**
 * Integration tests for {@link CustomerFunctions}.
 *
 * <p>These tests verify that the AI function beans are properly registered in the Spring
 * application context and can be retrieved by their names.
 */
@SpringBootTest(classes = {CustomerFunctions.class})
@ActiveProfiles("test")
@DisplayName("CustomerFunctions Integration Tests")
class CustomerFunctionsIntegrationTest {

  @Autowired private ApplicationContext applicationContext;

  @MockitoBean private CustomersApiClient customersApiClient;

  @Test
  @DisplayName("Should register getCustomerByPolicyNumber function bean with correct name")
  void getCustomerByPolicyNumber_ShouldBeRegisteredAsBean() {
    // When
    boolean beanExists = applicationContext.containsBean(Functions.GET_CUSTOMER_BY_POLICY_NUMBER);
    Object bean = applicationContext.getBean(Functions.GET_CUSTOMER_BY_POLICY_NUMBER);

    // Then
    assertThat(beanExists).isTrue();
    assertThat(bean).isInstanceOf(Function.class);

    @SuppressWarnings("unchecked")
    Function<GetCustomerByPolicyNumberRequest, CustomerDto> function =
        (Function<GetCustomerByPolicyNumberRequest, CustomerDto>) bean;

    assertThat(function).isNotNull();
  }

  @Test
  @DisplayName("Should register getPoliciesByCustomerId function bean with correct name")
  void getPoliciesByCustomerId_ShouldBeRegisteredAsBean() {
    // When
    boolean beanExists = applicationContext.containsBean(Functions.GET_POLICIES_BY_CUSTOMER_ID);
    Object bean = applicationContext.getBean(Functions.GET_POLICIES_BY_CUSTOMER_ID);

    // Then
    assertThat(beanExists).isTrue();
    assertThat(bean).isInstanceOf(Function.class);

    @SuppressWarnings("unchecked")
    Function<GetPoliciesByCustomerIdRequest, List<PolicyDto>> function =
        (Function<GetPoliciesByCustomerIdRequest, List<PolicyDto>>) bean;

    assertThat(function).isNotNull();
  }

  @Test
  @DisplayName("Should inject CustomerFunctions configuration bean")
  void customerFunctions_ShouldBeInjectedAsBean() {
    // When
    CustomerFunctions customerFunctions = applicationContext.getBean(CustomerFunctions.class);

    // Then
    assertThat(customerFunctions).isNotNull();
  }

  @Test
  @DisplayName("All function names should be available in Functions constants")
  void allFunctionNames_ShouldBeDefinedInConstants() {
    // When & Then
    assertThat(Functions.GET_CUSTOMER_BY_POLICY_NUMBER).isEqualTo("getCustomerByPolicyNumber");
    assertThat(Functions.GET_POLICIES_BY_CUSTOMER_ID).isEqualTo("getPoliciesByCustomerId");

    // Verify the functions are included in ALL_FUNCTIONS set
    assertThat(Functions.ALL_FUNCTIONS)
        .contains(Functions.GET_CUSTOMER_BY_POLICY_NUMBER, Functions.GET_POLICIES_BY_CUSTOMER_ID);
  }
}
