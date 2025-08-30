package tech.yildirim.aiinsurance.ai.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import tech.yildirim.aiinsurance.api.generated.clients.CustomersApiClient;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

/** Defines all AI-callable functions related to customer management. */
@Configuration
@RequiredArgsConstructor
@Description("Functions for retrieving customer and policy information")
public class CustomerFunctions {

  private final CustomersApiClient customersApiClient;

  // --- Request Records for Type Safety ---
  public record GetCustomerByPolicyNumberRequest(String policyNumber) {}

  public record GetPoliciesByCustomerIdRequest(Long customerId) {}

  public record CreateCustomerRequest(CustomerDto customerDto) {}

  public record DeleteCustomerRequest(Long customerId) {}

  public record GetAllCustomersRequest(String name) {} // name is optional

  public record GetCustomerByIdRequest(Long customerId) {}

  public record UpdateCustomerRequest(Long customerId, CustomerDto customerDto) {}

  @Bean(Functions.GET_CUSTOMER_BY_POLICY_NUMBER)
  @Description(
      "Retrieves customer information by searching with a policy number. Use this function when: "
          + "1) Customer provides their policy number (format: POL-XXXXX or similar), "
          + "2) You need to identify who owns a specific policy, "
          + "3) Customer says 'my policy number is...' or similar phrases. "
          + "This is the primary way to identify customers in insurance conversations. "
          + "Returns complete customer details including name, contact information, and customer ID.")
  public Function<GetCustomerByPolicyNumberRequest, CustomerDto> getCustomerByPolicyNumber() {
    return request ->
        Objects.requireNonNull(
            customersApiClient.getCustomerByPolicyNumber(request.policyNumber()).getBody());
  }

  @Bean(Functions.GET_POLICIES_BY_CUSTOMER_ID)
  @Description(
      "Retrieves all insurance policies belonging to a specific customer using their internal customer ID. "
          + "Use this function when: "
          + "1) You already have the customer's ID from a previous lookup, "
          + "2) Customer asks 'what policies do I have?', 'show me all my insurance', or 'list my coverage', "
          + "3) You need to display all policies for a known customer. "
          + "Returns a list of all policies with details like policy numbers, types, coverage amounts, and status.")
  public Function<GetPoliciesByCustomerIdRequest, List<PolicyDto>> getPoliciesByCustomerId() {
    return request ->
        Objects.requireNonNull(
            customersApiClient.getPoliciesByCustomerId(request.customerId()).getBody());
  }

  @Bean(Functions.CREATE_CUSTOMER)
  @Description(
      "Creates a new customer record in the insurance system. Use this function when: "
          + "1) A new person wants to become a customer and needs registration, "
          + "2) Customer says 'I want to sign up', 'register me', or 'create my account', "
          + "3) You need to add a completely new customer to the system. "
          + "Requires complete customer information including personal details, contact information. "
          + "This is typically used during new customer onboarding process. "
          + "Returns the created customer object with assigned customer ID.")
  public Function<CreateCustomerRequest, CustomerDto> createCustomer() {
    return request ->
        Objects.requireNonNull(customersApiClient.createCustomer(request.customerDto()).getBody());
  }

  @Bean(Functions.DELETE_CUSTOMER)
  @Description(
      "Permanently removes a customer record from the system by their customer ID. "
          + "Use this function ONLY when: "
          + "1) Customer explicitly requests account deletion or cancellation, "
          + "2) Customer says 'delete my account', 'remove me from system', or 'cancel my registration', "
          + "3) Legal or compliance requirement to remove customer data. "
          + "WARNING: This is irreversible and will remove all customer data. "
          + "Always confirm with customer before executing. Consider data retention policies.")
  public Function<DeleteCustomerRequest, String> deleteCustomer() {
    return request -> {
      customersApiClient.deleteCustomer(request.customerId());
      return "{\"status\": \"SUCCESS\", \"message\": \"Customer deleted successfully.\"}";
    };
  }

  @Bean(Functions.GET_ALL_CUSTOMERS)
  @Description(
      "Retrieves all customers in the system or searches customers by name. Use this function when: "
          + "1) Customer service representative needs to find a customer by name, "
          + "2) Customer says 'find customer named John Smith' or provides partial name, "
          + "3) You need to search when policy number is not available, "
          + "4) Administrative task requires customer list or search. "
          + "If 'name' parameter is provided, searches in both first name and last name fields. "
          + "If 'name' is null/empty, returns all customers (use carefully - may return large dataset).")
  public Function<GetAllCustomersRequest, List<CustomerDto>> getAllCustomers() {
    return request ->
        Objects.requireNonNull(customersApiClient.getAllCustomers(request.name()).getBody());
  }

  @Bean(Functions.GET_CUSTOMER_BY_ID)
  @Description(
      "Retrieves detailed customer information using their unique internal customer ID. "
          + "Use this function when: "
          + "1) You have the customer's numeric ID from previous operations, "
          + "2) You need to refresh or verify customer details during conversation, "
          + "3) Following up on a customer interaction where ID is already known, "
          + "4) System references or logs contain customer ID that needs to be resolved. "
          + "This is faster than searching by name when ID is available. "
          + "Returns complete customer profile including all contact and personal information.")
  public Function<GetCustomerByIdRequest, CustomerDto> getCustomerById() {
    return request ->
        Objects.requireNonNull(customersApiClient.getCustomerById(request.customerId()).getBody());
  }

  @Bean(Functions.UPDATE_CUSTOMER)
  @Description(
      "Updates existing customer information in the system. Use this function when: "
          + "1) Customer wants to change their personal details (address, phone, email), "
          + "2) Customer says 'update my information', 'change my address', or 'modify my details', "
          + "3) Correction of incorrect customer data is needed, "
          + "4) Customer profile needs to be maintained with current information. "
          + "Requires customer ID and updated customer object. "
          + "Always verify the changes with customer before applying. "
          + "Returns the updated customer object with all modifications applied.")
  public Function<UpdateCustomerRequest, CustomerDto> updateCustomer() {
    return request ->
        Objects.requireNonNull(
            customersApiClient
                .updateCustomer(request.customerId(), request.customerDto())
                .getBody());
  }
}
