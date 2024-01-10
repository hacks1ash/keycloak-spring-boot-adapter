package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.customizer.OAuth2ResourceServerCustomizer;
import io.github.hacks1ash.keycloak.adapter.model.DefaultKeycloakUser;
import io.github.hacks1ash.keycloak.adapter.utils.RemotePublicKeyLocator;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Configuration class for setting up web security using Keycloak in a Spring Boot application. This
 * class defines the necessary beans and configuration to integrate Keycloak with Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {

  private KeycloakProperties keycloakProperties;

  private AccessDeniedHandler accessDeniedHandler;

  private AuthenticationEntryPoint authenticationEntryPoint;

  private JwtAuthConverter<? extends DefaultKeycloakUser> jwtAuthConverter;

  private RemotePublicKeyLocator remotePublicKeyLocator;

  private Customizer<
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
      authorizeHttpRequestsCustomizer;

  private Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer;

  private Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer;

  /**
   * Registers the HttpSessionEventPublisher as a servlet listener. This is necessary for proper
   * session management, especially in a clustered environment.
   *
   * @return {@link ServletListenerRegistrationBean} for the HttpSessionEventPublisher.
   */
  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
  }

  /**
   * Configures the HttpSecurity for the application. This method defines how security is managed,
   * including CORS, CSRF, session management, and the setup of the resource server for OAuth2.
   *
   * @param http HttpSecurity object to be configured.
   * @return Configured {@link SecurityFilterChain}.
   * @throws Exception if an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
    return http.cors(this.corsCustomizer)
        .csrf(this.csrfCustomizer)
        .authorizeHttpRequests(this.authorizeHttpRequestsCustomizer)
        .oauth2ResourceServer(oAuth2ResourceServerCustomizer())
        .sessionManagement(
            sessionManagement -> {
              sessionManagement
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                  .sessionAuthenticationStrategy(
                      new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl()));
            })
        .build();
  }

  /**
   * Creates a JwtDecoder bean using the KeycloakJWTDecoder. This decoder is responsible for
   * decoding and validating JWT tokens in the context of Keycloak.
   *
   * @return An instance of {@link JwtDecoder}.
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    return new KeycloakJWTDecoder(this.remotePublicKeyLocator, keycloakProperties);
  }

  /**
   * Sets up the OAuth2 resource server configuration for the application, incorporating the
   * necessary components such as JwtDecoder, JwtAuthConverter, AuthenticationEntryPoint, and
   * AccessDeniedHandler.
   *
   * @return An OAuth2ResourceServerCustomizer instance for OAuth2 resource server configuration.
   */
  @Bean
  public Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oAuth2ResourceServerCustomizer() {
    return new OAuth2ResourceServerCustomizer(
        jwtDecoder(),
        this.jwtAuthConverter,
        this.authenticationEntryPoint,
        this.accessDeniedHandler);
  }
}
