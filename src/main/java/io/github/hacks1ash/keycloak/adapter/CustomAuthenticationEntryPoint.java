package io.github.hacks1ash.keycloak.adapter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Custom implementation of the AuthenticationEntryPoint interface for handling initial
 * authentication entries in a Spring Security context. This class responds with an HTTP 401
 * Unauthorized status when unauthenticated users attempt to access protected resources.
 */
@Configuration
@ConditionalOnMissingBean(AuthenticationEntryPoint.class)
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * Commences the authentication process. This method is called when an unauthenticated user tries
   * to access a resource that requires authentication.
   *
   * @param request The HTTP request that resulted in an AuthenticationException.
   * @param response The HTTP response to be sent to the client.
   * @param authException The exception that triggered this entry point.
   * @throws IOException In the event of an IOException.
   * @throws ServletException In the event of a ServletException.
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
  }
}
