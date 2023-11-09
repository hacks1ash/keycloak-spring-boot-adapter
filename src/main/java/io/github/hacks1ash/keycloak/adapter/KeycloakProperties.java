package io.github.hacks1ash.keycloak.adapter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

  private String serverUrl;

  private String realm;

  private String clientId;

  private String clientSecret;

  private boolean enabled = true;
}
