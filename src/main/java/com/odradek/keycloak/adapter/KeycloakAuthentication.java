package com.odradek.keycloak.adapter;

import com.odradek.keycloak.adapter.model.AbstractKeycloakUser;
import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Getter
@Transient
@EqualsAndHashCode(callSuper = true)
public class KeycloakAuthentication<T extends AbstractKeycloakUser> extends JwtAuthenticationToken {

  private final T authenticatedUser;

  /**
   * Constructs a {@code JwtAuthenticationToken} using the provided parameters.
   *
   * @param jwt the JWT
   * @param authorities the authorities assigned to the JWT
   * @param name the principal name
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
