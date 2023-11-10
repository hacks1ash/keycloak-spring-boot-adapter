package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Custom authentication class that extends JwtAuthenticationToken to include Keycloak-specific user
 * details.
 *
 * @param <T> The type of the user details, which extends AbstractKeycloakUser.
 */
@Getter
@Transient
@EqualsAndHashCode(callSuper = true)
public class KeycloakAuthentication<T extends AbstractKeycloakUser> extends JwtAuthenticationToken {

  private final T authenticatedUser;

  /**
   * Constructs a KeycloakAuthentication object with the specified parameters.
   *
   * @param jwt The JWT token.
   * @param authorities The collection of granted authorities.
   * @param name The principal name.
   * @param authenticatedUser The authenticated user details.
   */
  public KeycloakAuthentication(
      Jwt jwt,
      Collection<? extends GrantedAuthority> authorities,
      String name,
      T authenticatedUser) {
    super(jwt, authorities, name);
    this.authenticatedUser = authenticatedUser;
  }
}
