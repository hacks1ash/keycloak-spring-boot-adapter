package io.github.hacks1ash.keycloak.adapter.customizer;

import io.github.hacks1ash.keycloak.adapter.JwtAuthConverter;
import io.github.hacks1ash.keycloak.adapter.KeycloakJWTDecoder;
import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Customizes the OAuth2 Resource Server configuration in Spring Security.
 *
 * <p>This class implements {@link org.springframework.security.config.Customizer} and is
 * specifically designed to work with the {@link OAuth2ResourceServerConfigurer} of Spring
 * Security's HttpSecurity configuration. It allows for fine-tuning the OAuth2 Resource Server
 * settings, including JWT decoding and authentication conversion, setting up an authentication
 * entry point, and handling access denied scenarios.
 */
@AllArgsConstructor
public class OAuth2ResourceServerCustomizer
    implements Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> {

  private JwtDecoder jwtDecoder;

  private JwtAuthConverter<? extends AbstractKeycloakUser> jwtAuthConverter;

  private AuthenticationEntryPoint authenticationEntryPoint;

  private AccessDeniedHandler accessDeniedHandler;

  /**
   * Customizes the {@link OAuth2ResourceServerConfigurer} of HttpSecurity to define custom OAuth2
   * Resource Server policies.
   *
   * <p>This method configures various aspects of OAuth2 Resource Server, such as JWT decoding, JWT
   * authentication conversion, authentication entry point, and access denied handler. It ensures
   * that the resource server is aligned with the application's security requirements and the
   * specificities of the Keycloak authentication.
   *
   * @param configurer The OAuth2 Resource Server configurer to be customized.
   */
  @Override
  public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
    configurer
        .jwt(jwt -> jwt.decoder(this.jwtDecoder).jwtAuthenticationConverter(this.jwtAuthConverter))
        .authenticationEntryPoint(this.authenticationEntryPoint)
        .accessDeniedHandler(this.accessDeniedHandler);
  }
}
