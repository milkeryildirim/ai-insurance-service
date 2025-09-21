package tech.yildirim.aiinsurance.security;

/**
 * Exception thrown when a user attempts to access data or perform operations that they are not
 * authorized for based on their customer ID. This exception is specifically designed for AI
 * function security violations.
 */
public class UnauthorizedAccessException extends RuntimeException {

  public UnauthorizedAccessException(String message) {
    super(message);
  }
}
