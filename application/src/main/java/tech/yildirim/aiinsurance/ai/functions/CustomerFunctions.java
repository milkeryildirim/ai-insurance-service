package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Scope;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.CreateCustomerReq;
import tech.yildirim.aiinsurance.model.ai.request.DeleteCustomerReq;
import tech.yildirim.aiinsurance.model.ai.request.GetAllCustomersReq;
import tech.yildirim.aiinsurance.model.ai.request.GetCustomerByIdRequestReq;
import tech.yildirim.aiinsurance.model.ai.request.GetCustomerByPolicyNumberReq;
import tech.yildirim.aiinsurance.model.ai.request.GetPoliciesByCustomerIdReq;
import tech.yildirim.aiinsurance.model.ai.request.UpdateCustomerRequestReq;
import tech.yildirim.aiinsurance.security.SecuredAI;

/** Defines all AI-callable functions related to customer management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for retrieving customer and policy information")
public class CustomerFunctions {

  private final CustomersApiClient customersApiClient;

  @Bean(Functions.GET_CUSTOMER_BY_POLICY_NUMBER)
  @Scope("prototype")
  @SecuredAI
  @Description(
      "Retrieves customer information by searching with a policy number. Use this function when: "
          + "1) Customer provides their policy number (format: POL-XXXXX or similar), "
          + "2) You need to identify who owns a specific policy, "
          + "3) Customer says 'my policy number is...' or similar phrases. "
          + "This is the primary way to identify customers in insurance conversations. "
          + "Returns complete customer details including name, contact information, and customer ID.")
  public Function<GetCustomerByPolicyNumberReq, ResponseWrapper<CustomerDto>>
      getCustomerByPolicyNumber() {
    return request ->
        ResponseWrapper.<CustomerDto>builder()
            .success(true)
            .data(customersApiClient.getCustomerByPolicyNumber(request.policyNumber()).getBody())
            .build();
  }

  @Bean(Functions.GET_POLICIES_BY_CUSTOMER_ID)
  @SecuredAI
  @Description(
      "Retrieves all insurance policies belonging to a specific customer using their internal customer ID. "
          + "Use this function when: "
          + "1) You already have the customer's ID from a previous lookup, "
          + "2) Customer asks 'what policies do I have?', 'show me all my insurance', or 'list my coverage', "
          + "3) You need to display all policies for a known customer. "
          + "Returns a list of all policies with details like policy numbers, types, coverage amounts, and status.")
  public Function<GetPoliciesByCustomerIdReq, ResponseWrapper<List<PolicyDto>>>
      getPoliciesByCustomerId() {
    return request ->
        ResponseWrapper.<List<PolicyDto>>builder()
            .success(true)
            .data(customersApiClient.getPoliciesByCustomerId(request.customerId()).getBody())
            .build();
  }

  @Bean(Functions.CREATE_CUSTOMER)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Creates a new customer record in the insurance system. Use this function when: "
          + "1) A new person wants to become a customer and needs registration, "
          + "2) Customer says 'I want to sign up', 'register me', or 'create my account', "
          + "3) You need to add a completely new customer to the system. "
          + "Requires complete customer information including personal details, contact information. "
          + "This is typically used during new customer onboarding process. "
          + "Returns the created customer object with assigned customer ID.")
  public Function<CreateCustomerReq, ResponseWrapper<CustomerDto>> createCustomer() {
    return request ->
        ResponseWrapper.<CustomerDto>builder()
            .success(true)
            .data(customersApiClient.createCustomer(request.customerDto()).getBody())
            .build();
  }

  @Bean(Functions.DELETE_CUSTOMER)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Permanently removes a customer record from the system by their customer ID. "
          + "Use this function ONLY when: "
          + "1) Customer explicitly requests account deletion or cancellation, "
          + "2) Customer says 'delete my account', 'remove me from system', or 'cancel my registration', "
          + "3) Legal or compliance requirement to remove customer data. "
          + "WARNING: This is irreversible and will remove all customer data. "
          + "Always confirm with customer before executing. Consider data retention policies.")
  public Function<DeleteCustomerReq, ResponseWrapper<String>> deleteCustomer() {
    return request -> {
      customersApiClient.deleteCustomer(request.customerId());
      return ResponseWrapper.<String>builder()
          .success(true)
          .data("Customer deleted successfully.")
          .build();
    };
  }

  @Bean(Functions.GET_ALL_CUSTOMERS)
  @SecuredAI(blockedForAI = true)
  @Description(
      "Retrieves all customers in the system or searches customers by name. Use this function when: "
          + "1) Customer service representative needs to find a customer by name, "
          + "2) Customer says 'find customer named John Smith' or provides partial name, "
          + "3) You need to search when policy number is not available, "
          + "4) Administrative task requires customer list or search. "
          + "If 'name' parameter is provided, searches in both first name and last name fields. "
          + "If 'name' is null/empty, returns all customers (use carefully - may return large dataset).")
  public Function<GetAllCustomersReq, ResponseWrapper<List<CustomerDto>>> getAllCustomers() {
    return request ->
        ResponseWrapper.<List<CustomerDto>>builder()
            .success(true)
            .data(customersApiClient.getAllCustomers(request.name()).getBody())
            .build();
  }

  @Bean(Functions.GET_CUSTOMER_BY_ID)
  @Scope("prototype")
  @SecuredAI
  @Description(
      "Retrieves detailed customer information using their unique internal customer ID. "
          + "Use this function when: "
          + "1) You have the customer's numeric ID from previous operations, "
          + "2) You need to refresh or verify customer details during conversation, "
          + "3) Following up on a customer interaction where ID is already known, "
          + "4) System references or logs contain customer ID that needs to be resolved. "
          + "This is faster than searching by name when ID is available. "
          + "Returns complete customer profile including all contact and personal information.")
  public Function<GetCustomerByIdRequestReq, ResponseWrapper<CustomerDto>> getCustomerById() {
    return request ->
        ResponseWrapper.<CustomerDto>builder()
            .success(true)
            .data(customersApiClient.getCustomerById(request.customerId()).getBody())
            .build();
  }

  @Bean(Functions.UPDATE_CUSTOMER)
  @SecuredAI
  @Description(
      "Updates existing customer information in the system. Use this function when: "
          + "1) Customer wants to change their personal details (address, phone, email), "
          + "2) Customer says 'update my information', 'change my address', or 'modify my details', "
          + "3) Correction of incorrect customer data is needed, "
          + "4) Customer profile needs to be maintained with current information. "
          + "Requires customer ID and updated customer object. "
          + "Always verify the changes with customer before applying. "
          + "Returns the updated customer object with all modifications applied.")
  public Function<UpdateCustomerRequestReq, ResponseWrapper<CustomerDto>> updateCustomer() {
    return request ->
        ResponseWrapper.<CustomerDto>builder()
            .success(true)
            .data(
                customersApiClient
                    .updateCustomer(request.customerId(), request.customerDto())
                    .getBody())
            .build();
  }
}
