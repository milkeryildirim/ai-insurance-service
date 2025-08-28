package tech.yildirim.aiinsurance.ai.functions;

import java.util.Set;

public final class Functions {
  public static final String GET_CUSTOMER_BY_POLICY_NUMBER = "getCustomerByPolicyNumber";
  public static final String GET_POLICIES_BY_CUSTOMER_ID = "getPoliciesByCustomerId";
  public static final Set<String> ALL_FUNCTIONS =
      Set.of(GET_CUSTOMER_BY_POLICY_NUMBER, GET_POLICIES_BY_CUSTOMER_ID);

  private Functions() {}
}
