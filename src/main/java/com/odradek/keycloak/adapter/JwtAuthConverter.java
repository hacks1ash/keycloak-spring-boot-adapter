package com.odradek.keycloak.adapter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private final KeycloakProperties keycloakProperties;

  public JwtAuthConverter(KeycloakProperties keycloakProperties) {
    this.keycloakProperties = keycloakProperties;
  }

  /**
   * Convert the source object of type {@code S} to target type {@code T}.
   *
   * @param source the source object to convert, which must be an instance of {@code S} (never
   *     {@code null})
   * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
   * @throws IllegalArgumentException if the source cannot be converted to the desired target type
   */
  @Override
  public AbstractAuthenticationToken convert(Jwt source) {
    try {
      TokenVerifier<OdradekUser> tokenVerifier =
          TokenVerifier.create(source.getTokenValue(), OdradekUser.class);

      OdradekUser token = tokenVerifier.getToken();
      AccessToken.Access access =
          token.getResourceAccess().get(this.keycloakProperties.getClientId());

      Set<String> roles = access == null ? new HashSet<>() : access.getRoles();
      Set<SimpleGrantedAuthority> grantedAuthorities =
          roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
              .collect(Collectors.toSet());
      return new OdradekAuthenticationToken(source, grantedAuthorities, token.getSubject(), token);
    } catch (VerificationException e) {
      throw new RuntimeException(e);
    }
  }
}
