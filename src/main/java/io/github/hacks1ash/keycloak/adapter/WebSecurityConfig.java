package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import io.github.hacks1ash.keycloak.adapter.utils.RemotePublicKeyLocator;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class WebSecurityConfig {

  private KeycloakProperties keycloakProperties;

  private AccessDeniedHandler accessDeniedHandler;

  private AuthenticationEntryPoint authenticationEntryPoint;

  private JwtAuthConverter<? extends AbstractKeycloakUser> jwtAuthConverter;

  private RemotePublicKeyLocator remotePublicKeyLocator;

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
  }

  @Bean
  public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
    return http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(
                        jwt ->
                            jwt.decoder(jwtDecoder(this.remotePublicKeyLocator))
                                .jwtAuthenticationConverter(this.jwtAuthConverter))
                    .authenticationEntryPoint(this.authenticationEntryPoint)
                    .accessDeniedHandler(this.accessDeniedHandler))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionAuthenticationStrategy(
                    new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl())))
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder(RemotePublicKeyLocator remotePublicKeyLocator) {
    return new KeycloakJWTDecoder(remotePublicKeyLocator, keycloakProperties);
  }
}
