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

@Configuration
@ConditionalOnMissingBean(AccessDeniedHandler.class)
public class CustomAccessDeniedController implements AccessDeniedHandler {

  /**
   * Handles an access denied failure.
   *
   * @param request that resulted in an <code>AccessDeniedException</code>
   * @param response so that the user agent can be advised of the failure
   * @param accessDeniedException that caused the invocation
   * @throws IOException in the event of an IOException
   * @throws ServletException in the event of a ServletException
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
  }
}
