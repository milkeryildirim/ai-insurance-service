package tech.yildirim.aiinsurance.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tech.yildirim.aiinsurance.api.generated.model.CustomerDto;
import tech.yildirim.aiinsurance.api.generated.model.PolicyDto;

@FeignClient(name = "insurance-service", url = "${insurance.service.base-url}")
public interface CustomerServiceClient {

  @GetMapping("/customers/{id}")
  CustomerDto getCustomerById(@PathVariable("id") Long customerId);

  @GetMapping("/customers/{id}/policies")
  List<PolicyDto> getPoliciesByCustomerId(@PathVariable("id") Long customerId);

  /**
   * Retrieves all customers, optionally filtering by name. Corresponds to the 'getAllCustomers'
   * operationId in the OpenAPI spec.
   *
   * @param name The name to search for (optional).
   * @return A list of matching customers.
   */
  @GetMapping("/customers")
  List<CustomerDto> findCustomersByName(
      @RequestParam(value = "name", required = false) String name);

}
