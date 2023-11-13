package io.github.hacks1ash.keycloak.adapter.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Customizer for configuring HTTP request authorization in Spring Security.
 *
 * <p>This class implements {@link Customizer} and is specifically designed to work with the {@link
 * AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry} of Spring Security's
 * HttpSecurity configuration. It is used to apply custom rules for HTTP request authorization.
 *
 * <p>The customize method in this class configures the application to authenticate any HTTP
 * request.
 */
public class AuthorizeHttpRequestsCustomizer
    implements Customizer<
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

  /**
   * Customizes the {@link
   * AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry} to define custom
   * authorization rules for HTTP requests.
   *
   * <p>This implementation requires authentication for any HTTP request to the application.
   *
   * @param authorizationManagerRequestMatcherRegistry The registry to configure.
   */
  @Override
  public void customize(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorizationManagerRequestMatcherRegistry) {
    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
  }
}
