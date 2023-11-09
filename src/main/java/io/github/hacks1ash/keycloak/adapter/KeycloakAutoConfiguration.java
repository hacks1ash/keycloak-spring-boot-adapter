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

  @Bean
  public RemotePublicKeyLocator remotePublicKeyLocator(KeycloakProperties keycloakProperties) {
    return new RemotePublicKeyLocator(keycloakProperties, restTemplate);
  }


  @Bean
  @ConditionalOnMissingBean
  public JwtAuthConverter<AbstractKeycloakUser> jwtAuthConverter(KeycloakProperties keycloakProperties) {
    return new JwtAuthConverter<>(keycloakProperties, AbstractKeycloakUser.class);
  }

}
