package com.odradek.keycloak.adapter;

import com.odradek.keycloak.adapter.model.AbstractKeycloakUser;
import com.odradek.keycloak.adapter.utils.RemotePublicKeyLocator;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KeycloakProperties.class)
@AutoConfigureBefore(UserDetailsServiceAutoConfiguration.class)
public class WebSecurityConfig {

  private KeycloakProperties keycloakProperties;

  private AccessDeniedHandler accessDeniedHandler;

  private AuthenticationEntryPoint authenticationEntryPoint;

  private JwtAuthConverter<? extends AbstractKeycloakUser> jwtAuthConverter;

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
  }

  @Bean
  public SecurityFilterChain httpSecurity(
      HttpSecurity http, RemotePublicKeyLocator remotePublicKeyLocator) throws Exception {
    return http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(
                        jwt ->
                            jwt.decoder(jwtDecoder(remotePublicKeyLocator))
                                .jwtAuthenticationConverter(jwtAuthConverter))
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
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

  @Bean
  @ConditionalOnMissingBean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  public JwtAuthConverter<AbstractKeycloakUser> jwtAuthConverter() {
    return new JwtAuthConverter<>(keycloakProperties, AbstractKeycloakUser.class);
  }

  @Bean
  public RemotePublicKeyLocator remotePublicKeyLocator(RestTemplate restTemplate) {
    return new RemotePublicKeyLocator(keycloakProperties, restTemplate);
  }
}
