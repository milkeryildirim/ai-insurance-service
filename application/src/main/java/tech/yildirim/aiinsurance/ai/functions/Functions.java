package tech.yildirim.aiinsurance.ai.functions;

import java.util.Set;

public final class Functions {
  public static final String INFORM_HUMAN_OPERATOR = "informHumanOperator";
  public static final String GET_CUSTOMER_BY_POLICY_NUMBER = "getCustomerByPolicyNumber";
  public static final String GET_POLICIES_BY_CUSTOMER_ID = "getPoliciesByCustomerId";
  public static final String GET_POLICY_BY_ID = "getPolicyById";
  public static final String GET_POLICY_BY_POLICY_NUMBER = "getPolicyByPolicyNumber";
  public static final String CREATE_CUSTOMER = "createCustomer";
  public static final String DELETE_CUSTOMER = "deleteCustomer";
  public static final String GET_ALL_CUSTOMERS = "getAllCustomers";
  public static final String GET_CUSTOMER_BY_ID = "getCustomerById";
  public static final String UPDATE_CUSTOMER = "updateCustomer";
  public static final String CREATE_POLICY = "createPolicy";
  public static final String GET_ALL_POLICIES = "getAllPolicies";
  public static final String UPDATE_POLICY = "updatePolicy";

  public static final String GET_POLICY_CONDITIONS = "getPolicyConditions";
  public static final String UPDATE_POLICY_CONDITIONS = "updatePolicyConditions";

  // Auto Claim Functions
  public static final String CREATE_AUTO_CLAIM = "createAutoClaim";
  public static final String GET_AUTO_CLAIM_BY_ID = "getAutoClaimById";
  public static final String GET_AUTO_CLAIMS_BY_POLICY_ID = "getAutoClaimsByPolicyId";
  public static final String GET_ALL_AUTO_CLAIMS = "getAllAutoClaims";
  public static final String UPDATE_AUTO_CLAIM = "updateAutoClaim";
  public static final String DELETE_AUTO_CLAIM = "deleteAutoClaim";
  public static final String ASSIGN_ADJUSTER_TO_AUTO_CLAIM = "assignAdjusterToAutoClaim";

  // Home Claim Functions
  public static final String CREATE_HOME_CLAIM = "createHomeClaim";
  public static final String GET_HOME_CLAIM_BY_ID = "getHomeClaimById";
  public static final String GET_HOME_CLAIMS_BY_POLICY_ID = "getHomeClaimsByPolicyId";
  public static final String GET_ALL_HOME_CLAIMS = "getAllHomeClaims";
  public static final String UPDATE_HOME_CLAIM = "updateHomeClaim";
  public static final String DELETE_HOME_CLAIM = "deleteHomeClaim";
  public static final String ASSIGN_ADJUSTER_TO_HOME_CLAIM = "assignAdjusterToHomeClaim";

  // Health Claim Functions
  public static final String CREATE_HEALTH_CLAIM = "createHealthClaim";
  public static final String GET_HEALTH_CLAIM_BY_ID = "getHealthClaimById";
  public static final String GET_HEALTH_CLAIMS_BY_POLICY_ID = "getHealthClaimsByPolicyId";
  public static final String GET_ALL_HEALTH_CLAIMS = "getAllHealthClaims";
  public static final String UPDATE_HEALTH_CLAIM = "updateHealthClaim";
  public static final String DELETE_HEALTH_CLAIM = "deleteHealthClaim";
  public static final String ASSIGN_ADJUSTER_TO_HEALTH_CLAIM = "assignAdjusterToHealthClaim";

  public static final Set<String> ALL_FUNCTIONS =
      Set.of(
          INFORM_HUMAN_OPERATOR,
          GET_CUSTOMER_BY_POLICY_NUMBER,
          GET_POLICIES_BY_CUSTOMER_ID,
          GET_POLICY_BY_ID,
          GET_POLICY_BY_POLICY_NUMBER,
          CREATE_CUSTOMER,
          DELETE_CUSTOMER,
          GET_ALL_CUSTOMERS,
          GET_CUSTOMER_BY_ID,
          UPDATE_CUSTOMER,
          CREATE_POLICY,
          GET_ALL_POLICIES,
          UPDATE_POLICY,
          GET_POLICY_CONDITIONS,
          UPDATE_POLICY_CONDITIONS,
          CREATE_AUTO_CLAIM,
          GET_AUTO_CLAIM_BY_ID,
          GET_AUTO_CLAIMS_BY_POLICY_ID,
          GET_ALL_AUTO_CLAIMS,
          UPDATE_AUTO_CLAIM,
          DELETE_AUTO_CLAIM,
          ASSIGN_ADJUSTER_TO_AUTO_CLAIM,
          CREATE_HOME_CLAIM,
          GET_HOME_CLAIM_BY_ID,
          GET_HOME_CLAIMS_BY_POLICY_ID,
          GET_ALL_HOME_CLAIMS,
          UPDATE_HOME_CLAIM,
          DELETE_HOME_CLAIM,
          ASSIGN_ADJUSTER_TO_HOME_CLAIM,
          CREATE_HEALTH_CLAIM,
          GET_HEALTH_CLAIM_BY_ID,
          GET_HEALTH_CLAIMS_BY_POLICY_ID,
          GET_ALL_HEALTH_CLAIMS,
          UPDATE_HEALTH_CLAIM,
          DELETE_HEALTH_CLAIM,
          ASSIGN_ADJUSTER_TO_HEALTH_CLAIM);

  private Functions() {}
}
