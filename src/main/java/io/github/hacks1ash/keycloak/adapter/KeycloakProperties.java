package io.github.hacks1ash.keycloak.adapter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Keycloak integration. This class holds the settings used for
 * configuring a Keycloak server within a Spring Boot application.
 */
@Data
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

  private String serverUrl;

  private String realm;

  private String clientId;

  private String clientSecret;

  private boolean enabled = true;
}
