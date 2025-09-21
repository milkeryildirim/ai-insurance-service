package tech.yildirim.aiinsurance.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark AI functions that require customer authorization. Functions annotated
 * with @SecuredAI will validate that the authenticated user can only access their own data based on
 * the insurance_user_id claim in JWT token.
 *
 * <p>The annotation supports blocking certain operations completely from AI access using the
 * blockedForAI parameter.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredAI {

  /**
   * When set to true, completely blocks AI access to this function. Used for sensitive operations
   * that should never be accessible through AI, such as delete operations or administrative
   * functions.
   *
   * @return true if the function should be completely blocked for AI access
   */
  boolean blockedForAI() default false;
}
