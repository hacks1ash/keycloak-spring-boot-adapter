package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import io.github.hacks1ash.keycloak.adapter.utils.RemotePublicKeyLocator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * Auto-configuration class for integrating Keycloak with Spring Boot applications. This
 * configuration is conditionally enabled based on the presence of Keycloak properties.
 */
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
@AutoConfigureBefore(UserDetailsServiceAutoConfiguration.class)
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
@Import({
  WebSecurityConfig.class,
  CustomAccessDeniedController.class,
  CustomAuthenticationEntryPoint.class
})
public class KeycloakAutoConfiguration {

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Creates a RemotePublicKeyLocator bean for locating public keys from a Keycloak server.
   *
   * @param keycloakProperties The Keycloak configuration properties.
   * @return A RemotePublicKeyLocator instance.
   */
  @Bean
  public RemotePublicKeyLocator remotePublicKeyLocator(KeycloakProperties keycloakProperties) {
    return new RemotePublicKeyLocator(keycloakProperties, restTemplate);
  }

  /**
   * Creates a JwtAuthConverter bean for converting JWT tokens into authentication tokens. The
   * converter is conditionally created if no existing bean of this type is present.
   *
   * @param keycloakProperties The Keycloak configuration properties.
   * @return A JwtAuthConverter instance for Keycloak users.
   */
  @Bean
  @ConditionalOnMissingBean
  public JwtAuthConverter<AbstractKeycloakUser> jwtAuthConverter(
      KeycloakProperties keycloakProperties) {
    return new JwtAuthConverter<>(keycloakProperties, AbstractKeycloakUser.class);
  }
}
