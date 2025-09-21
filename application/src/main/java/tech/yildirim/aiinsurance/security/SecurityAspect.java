package tech.yildirim.aiinsurance.security;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tech.yildirim.aiinsurance.model.ResponseWrapper;
import tech.yildirim.aiinsurance.model.ai.request.IClaimReq;
import tech.yildirim.aiinsurance.model.ai.request.ICustomerIdentifiableReq;
import tech.yildirim.aiinsurance.model.ai.request.IPolicyIDIdentifiableReq;
import tech.yildirim.aiinsurance.model.ai.request.IPolicyNumberIdentifiableReq;
import tech.yildirim.aiinsurance.service.CustomerService;

/**
 * AOP Aspect that enforces comprehensive customer-level authorization for AI functions in the
 * insurance system.
 *
 * <p>This aspect implements a robust security model that ensures customers can only access their
 * own data through AI assistant interactions. It intercepts all AI function calls annotated with
 * {@code @SecuredAI} and performs multi-layered security validations.
 *
 * <h3>Security Model</h3>
 *
 * <ul>
 *   <li><strong>Authentication Validation:</strong> Verifies user is authenticated and has a valid
 *       customer ID
 *   <li><strong>Authorization Check:</strong> Ensures authenticated customer can only access their
 *       own resources
 *   <li><strong>AI Access Control:</strong> Respects {@code @SecuredAI(blockedForAI=true)}
 *       annotations to prevent AI access to sensitive operations
 *   <li><strong>Ownership Chain Validation:</strong> Follows resource ownership chains (Customer →
 *       Policy → Claim) for complex authorization using {@link CustomerService}
 * </ul>
 *
 * <h3>Supported Request Types</h3>
 *
 * <ul>
 *   <li><strong>Customer Operations:</strong> Direct customer data access (GetCustomerById,
 *       UpdateCustomer, GetCustomerByPolicyNumber)
 *   <li><strong>Policy Operations:</strong> Policy creation, retrieval, and updates (CreatePolicy,
 *       GetPolicyById, GetPolicyByPolicyNumber, UpdatePolicy)
 *   <li><strong>Claims by Policy:</strong> Retrieving claims associated with specific policies
 *       (GetAutoClaimsByPolicyId, GetHomeClaimsByPolicyId, GetHealthClaimsByPolicyId)
 *   <li><strong>Claim Operations:</strong> Claim creation, retrieval, and updates for all claim
 *       types (Auto, Home, Health)
 *   <li><strong>Customer Policy Listings:</strong> Retrieving policies owned by specific customers
 *       (GetPoliciesByCustomerId)
 * </ul>
 *
 * <h3>Error Handling</h3>
 *
 * <p>The aspect provides user-friendly error responses instead of throwing exceptions, ensuring the
 * AI receives proper error messages that can be communicated to users. All errors are wrapped in
 * {@link ResponseWrapper} objects with appropriate error messages.
 *
 * <h3>Architecture</h3>
 *
 * <p>This aspect delegates ownership validation logic to {@link CustomerService}, promoting
 * separation of concerns and centralizing customer-related business logic. The service layer
 * handles the complex ownership chain validations while this aspect focuses purely on security
 * enforcement.
 *
 * <h3>Performance Considerations</h3>
 *
 * <p>Uses {@code @Lazy} annotations to prevent circular dependencies during Spring context
 * initialization and to optimize bean creation timing.
 *
 * @author M.Ilker Yildirim
 * @since 1.0
 * @see SecuredAI
 * @see SecurityUtils
 * @see CustomerService
 * @see ResponseWrapper
 */
@Aspect
@Component
@Lazy
@RequiredArgsConstructor
@Slf4j
public class SecurityAspect {

  private final SecurityUtils securityUtils;
  @Lazy private final CustomerService customerService;

  /**
   * Main AOP interceptor method that wraps AI functions with comprehensive security validations.
   *
   * <p>This method is triggered for all methods annotated with {@code @SecuredAI} and performs the
   * following security operations:
   *
   * <ol>
   *   <li>Intercepts the original function execution
   *   <li>Wraps Function instances with security logic
   *   <li>Returns non-Function objects unchanged
   * </ol>
   *
   * @param joinPoint the AOP join point containing method execution context
   * @param securedAI the security annotation containing configuration (e.g., blockedForAI flag)
   * @return the wrapped function with security validations or the original object if not a Function
   * @throws Throwable if the original method execution fails
   * @see SecuredAI
   */
  @Around("@annotation(securedAI)")
  public Object wrapSecuredAIFunction(ProceedingJoinPoint joinPoint, SecuredAI securedAI)
      throws Throwable {
    Object originalBean = joinPoint.proceed();

    if (originalBean instanceof Function) {
      return wrapFunction((Function<?, ?>) originalBean, securedAI, joinPoint);
    }

    return originalBean;
  }

  /**
   * Wraps a Function with comprehensive security validations for AI operations.
   *
   * <p>This method creates a security wrapper around the original function that performs:
   *
   * <ol>
   *   <li><strong>AI Access Control:</strong> Blocks functions marked with {@code
   *       blockedForAI=true}
   *   <li><strong>Authentication Check:</strong> Validates user authentication and customer ID
   *       presence
   *   <li><strong>Authorization Validation:</strong> Ensures customer can only access their own
   *       resources
   *   <li><strong>Error Response Generation:</strong> Provides user-friendly error messages instead
   *       of exceptions
   * </ol>
   *
   * @param <T> the input type of the function (request object)
   * @param <R> the return type of the function (response object)
   * @param originalFunction the original function to be wrapped with security
   * @param securedAI the security annotation containing configuration flags
   * @param joinPoint the AOP join point for logging and method identification
   * @return a new function that applies security validations before calling the original function
   */
  private <T, R> Function<T, R> wrapFunction(
      Function<T, R> originalFunction, SecuredAI securedAI, ProceedingJoinPoint joinPoint) {
    return request -> {
      log.info("Intercepted function: {}", originalFunction.getClass().getSimpleName());

      if (securedAI != null && securedAI.blockedForAI()) {
        String methodName = joinPoint.getSignature().getName();
        log.warn("AI access blocked for method: {}", methodName);

        return (R)
            createErrorResponse(
                "This operation is not available through the AI assistant for security reasons. "
                    + "Please contact customer service for assistance.");
      }

      Long authenticatedCustomerId = securityUtils.getCurrentUserCustomerId();
      if (authenticatedCustomerId == null) {
        log.warn("User is not authenticated");
        return (R) createErrorResponse("User is not authenticated");
      }

      Long requestedCustomerId = extractCustomerIdFromRequest(request);
      if (requestedCustomerId == null) {
        log.warn("Customer ID could not be extracted from request");
        return (R)
            createErrorResponse(
                "Customer ID could not be extracted from request. Request could not be processed.");
      }

      if (!authenticatedCustomerId.equals(requestedCustomerId)) {
        log.warn(
            "Unauthorized access attempt. Authenticated customer: {}, Requested customer: {}",
            authenticatedCustomerId,
            requestedCustomerId);
        return (R)
            createErrorResponse(
                "Access denied. You can only access your own data. "
                    + "If you believe this is an error, please contact customer service.");
      }
      log.info("Access granted for customer: {}", authenticatedCustomerId);
      return originalFunction.apply(request);
    };
  }

  /**
   * Creates appropriate error response based on the function's return type. This ensures AI gets a
   * proper response instead of an exception.
   */
  private ResponseWrapper<?> createErrorResponse(String errorMessage) {
    return ResponseWrapper.builder().success(false).errorMessage(errorMessage).build();
  }

  /**
   * Extracts customer ID from various request types based on the method being called.
   *
   * <p>This method handles different request patterns:
   *
   * <ul>
   *   <li>Direct customer operations (getCustomerById, etc.)
   *   <li>Policy-related operations (getPoliciesByCustomerId)
   *   <li>Claim creation operations (validates policy ownership)
   *   <li>Claim access operations (validates claim ownership through policy)
   * </ul>
   *
   * @param request the first argument of the intercepted method call
   * @return the customer ID that owns the requested resource
   * @throws UnauthorizedAccessException if the request type is unknown or unsupported
   * @throws IllegalArgumentException if required data (policy, claim) is not found
   */
  private Long extractCustomerIdFromRequest(Object request) {
    log.info("Extracting customer ID from request: {}", request.getClass().getSimpleName());

    return switch (request) {
      case ICustomerIdentifiableReq req -> req.customerId();
      case IPolicyIDIdentifiableReq req -> getCustomerIdByPolicyId(req.policyId());
      case IPolicyNumberIdentifiableReq req -> getCustomerIdByPolicyNumber(req.policyNumber());
      case IClaimReq req -> getCustomerIdByClaimReqType(req);
      default -> {
        log.warn("Unknown request type: {}", request.getClass().getSimpleName());
        yield null;
      }
    };
  }

  /**
   * Retrieves the customer ID who owns the specified policy.
   *
   * <p>This method is used to validate that claim creation operations are performed only on
   * policies owned by the authenticated customer.
   *
   * @param policyId the unique identifier of the policy
   * @return the customer ID who owns the policy
   * @throws IllegalArgumentException if the policy is not found
   */
  private Long getCustomerIdByPolicyId(Long policyId) {
    return customerService.getCustomerIdByPolicyId(policyId);
  }

  /**
   * Retrieves the customer ID who owns the specified policy by policy number.
   *
   * <p>This overloaded method provides policy lookup by policy number instead of ID, supporting
   * operations that work with human-readable policy identifiers.
   *
   * @param policyNumber the unique policy number string identifier
   * @return the customer ID who owns the policy
   * @throws IllegalArgumentException if the policy is not found with the given policy number
   */
  private Long getCustomerIdByPolicyNumber(String policyNumber) {
    return customerService.getCustomerIdByPolicyNumber(policyNumber);
  }

  /**
   * Retrieves the customer ID who owns the policy associated with the specified claim.
   *
   * <p>This method handles different claim types (Auto, Home, Health) and follows the ownership
   * chain: Claim → Policy → Customer to determine access rights.
   *
   * @param request the claim request object containing claim ID
   * @return the customer ID who owns the policy associated with the claim
   * @throws IllegalArgumentException if the claim or associated policy is not found
   */
  private Long getCustomerIdByClaimReqType(Object request) {
    return customerService.getCustomerIdByClaimRequestType(request);
  }
}
