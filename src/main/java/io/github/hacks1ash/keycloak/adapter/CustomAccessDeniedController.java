package io.github.hacks1ash.keycloak.adapter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Custom implementation of the AccessDeniedHandler interface for handling access denied failures in
 * a Spring Security context. This class responds with an HTTP 403 Forbidden status when an
 * authenticated user attempts to access resources they are not authorized for.
 */
@Configuration
@ConditionalOnMissingBean(AccessDeniedHandler.class)
public class CustomAccessDeniedController implements AccessDeniedHandler {

  /**
   * Handles an AccessDeniedException by sending a 403 Forbidden response. This method is called
   * when an authenticated user tries to access a resource without the necessary permissions.
   *
   * @param request The HTTP request that resulted in an AccessDeniedException.
   * @param response The HTTP response to be sent to the client.
   * @param accessDeniedException The exception that triggered this handler.
   * @throws IOException In the event of an IOException.
   * @throws ServletException In the event of a ServletException.
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
  }
}
