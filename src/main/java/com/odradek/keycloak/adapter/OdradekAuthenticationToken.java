package com.odradek.keycloak.adapter;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Transient
public class OdradekAuthenticationToken extends JwtAuthenticationToken {

  private final OdradekUser odradekUser;

  /**
   * Constructs a {@code JwtAuthenticationToken} using the provided parameters.
   *
   * @param jwt the JWT
   * @param authorities the authorities assigned to the JWT
   * @param name the principal name
   */
  public OdradekAuthenticationToken(
      Jwt jwt,
      Collection<? extends GrantedAuthority> authorities,
      String name,
      OdradekUser odradekUser) {
    super(jwt, authorities, name);
    this.odradekUser = odradekUser;
  }

  public OdradekUser getOdradekUser() {
    return odradekUser;
  }
}
