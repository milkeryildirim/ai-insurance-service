package tech.yildirim.aiinsurance.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting security information from the current authentication context.
 * Provides methods to get the current user's customer ID from JWT token claims.
 */
@Component
@Slf4j
public class SecurityUtils {

  /**
   * Extracts the insurance_user_id claim from the current user's JWT token. This represents the
   * customer ID that the authenticated user is associated with.
   *
   * @return the customer ID from the insurance_user_id claim
   * @throws SecurityException if user is not authenticated or claim is missing
   */
  public Long getCurrentUserCustomerId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.warn("User is not authenticated");
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof OidcUser oidcUser)) {
      log.warn("Authentication principal is not a JWT token");
      return null;
    }

    Object insuranceUserIdClaim = oidcUser.getClaim("insurance_user_id");
    if (insuranceUserIdClaim == null) {
      log.warn("insurance_user_id claim not found in JWT token");
      return null;
    }

    try {
      return switch (insuranceUserIdClaim) {
        case Number insuranceUserId -> insuranceUserId.longValue();
        case String insuranceUserId -> Long.parseLong(insuranceUserId);
        default -> {
          log.warn(
              "insurance_user_id claim has unexpected type: {}", insuranceUserIdClaim.getClass());
          yield null;
        }
      };
    } catch (NumberFormatException e) {
      log.warn("insurance_user_id claim is not a valid number: {}", insuranceUserIdClaim);
      return null;
    }
  }
}
