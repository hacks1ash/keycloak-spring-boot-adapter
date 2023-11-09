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

@Configuration
@ConditionalOnMissingBean(AuthenticationEntryPoint.class)
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * Commences an authentication scheme.
   *
   * <p><code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code> attribute
   * named <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
   * with the requested target URL before calling this method.
   *
   * <p>Implementations should modify the headers on the <code>ServletResponse</code> as necessary
   * to commence the authentication process.
   *
   * @param request that resulted in an <code>AuthenticationException</code>
   * @param response so that the user agent can begin authentication
   * @param authException that caused the invocation
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
