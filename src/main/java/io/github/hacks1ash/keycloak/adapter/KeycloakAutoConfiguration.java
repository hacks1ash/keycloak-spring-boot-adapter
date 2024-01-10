package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.customizer.AuthorizeHttpRequestsCustomizer;
import io.github.hacks1ash.keycloak.adapter.customizer.CorsCustomizer;
import io.github.hacks1ash.keycloak.adapter.customizer.CsrfCustomizer;
import io.github.hacks1ash.keycloak.adapter.model.DefaultKeycloakUser;
import io.github.hacks1ash.keycloak.adapter.utils.RemotePublicKeyLocator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.web.client.RestTemplate;

/**
 * Auto-configuration class for integrating Keycloak with Spring Boot applications. This
 * configuration is conditionally enabled based on the presence of Keycloak properties.
 *
 * <p>This class is responsible for setting up the necessary beans and configurations to integrate
 * Keycloak into a Spring Boot application. It includes the setup of beans for JWT authentication
 * conversion, remote public key locator, and customizers for HTTP security configuration.
 */
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
@AutoConfigureBefore(UserDetailsServiceAutoConfiguration.class)
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
@Import({
  WebSecurityConfig.class,
  CustomAccessDeniedController.class,
  CustomAuthenticationEntryPoint.class,
})
public class KeycloakAutoConfiguration {

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Creates a RemotePublicKeyLocator bean for locating public keys from a Keycloak server. This
   * bean is essential for validating JWT tokens against the public keys from the Keycloak server.
   *
   * @param keycloakProperties The Keycloak configuration properties.
   * @return A RemotePublicKeyLocator instance.
   */
  @Bean
  public RemotePublicKeyLocator remotePublicKeyLocator(KeycloakProperties keycloakProperties) {
    return new RemotePublicKeyLocator(keycloakProperties, restTemplate);
  }

  /**
   * Creates a JwtAuthConverter bean for converting JWT tokens into authentication tokens. This
   * converter is essential for integrating JWT token-based authentication in Spring Security. The
   * converter is conditionally created if no existing bean of this type is present.
   *
   * @param keycloakProperties The Keycloak configuration properties.
   * @return A JwtAuthConverter instance for Keycloak users.
   */
  @Bean
  @ConditionalOnMissingBean(JwtAuthConverter.class)
  public JwtAuthConverter<DefaultKeycloakUser> jwtAuthConverter(
      KeycloakProperties keycloakProperties) {
    return new JwtAuthConverter<>(keycloakProperties, DefaultKeycloakUser.class);
  }

  /**
   * Registers customizers for the authorization of HTTP requests. These customizers define the
   * security constraints on the HTTP requests handled by the application.
   *
   * @return A customizer for the authorization of HTTP requests.
   */
  @Bean
  @ConditionalOnMissingBean
  public Customizer<
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
      authorizeHttpRequestsCustomizer() {
    return new AuthorizeHttpRequestsCustomizer();
  }

  /**
   * Registers a customizer for CORS configuration in HTTP security. This customizer allows for the
   * configuration of Cross-Origin Resource Sharing policies.
   *
   * @return A customizer for CORS configuration.
   */
  @Bean
  @ConditionalOnMissingBean
  public Customizer<CorsConfigurer<HttpSecurity>> corsConfigurerCustomizer() {
    return new CorsCustomizer();
  }

  /**
   * Registers a customizer for CSRF configuration in HTTP security. This customizer allows for the
   * configuration of Cross-Site Request Forgery protection policies.
   *
   * @return A customizer for CSRF configuration.
   */
  @Bean
  @ConditionalOnMissingBean
  public Customizer<CsrfConfigurer<HttpSecurity>> csrfConfigurerCustomizer() {
    return new CsrfCustomizer();
  }
}
