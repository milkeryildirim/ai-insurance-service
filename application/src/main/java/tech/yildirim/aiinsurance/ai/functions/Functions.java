package tech.yildirim.aiinsurance.ai.functions;

import java.util.Set;

public final class Functions {
  public static final String INFORM_HUMAN_OPERATOR = "informHumanOperator";
  public static final String GET_CUSTOMER_BY_POLICY_NUMBER = "getCustomerByPolicyNumber";
  public static final String GET_POLICIES_BY_CUSTOMER_ID = "getPoliciesByCustomerId";
  public static final String GET_POLICY_BY_ID = "getPolicyById";
  public static final String GET_POLICY_BY_POLICY_NUMBER = "getPolicyByPolicyNumber";

  public static final Set<String> ALL_FUNCTIONS =
      Set.of(
          INFORM_HUMAN_OPERATOR,
          GET_CUSTOMER_BY_POLICY_NUMBER,
          GET_POLICIES_BY_CUSTOMER_ID,
          GET_POLICY_BY_ID,
          GET_POLICY_BY_POLICY_NUMBER);

  private Functions() {}
}
