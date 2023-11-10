package io.github.hacks1ash.keycloak.adapter;

import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
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

/**
 * A converter that transforms a JWT token into an AbstractAuthenticationToken, specifically for
 * Keycloak authentication in a Spring Security context.
 *
 * @param <T> The user type that extends AbstractKeycloakUser.
 */
public class JwtAuthConverter<T extends AbstractKeycloakUser>
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final KeycloakProperties keycloakProperties;
  private final Class<T> tClass;

  /**
   * Constructor for JwtAuthConverter.
   *
   * @param keycloakProperties Configuration properties for Keycloak.
   * @param tClass The class type of the user.
   */
  public JwtAuthConverter(KeycloakProperties keycloakProperties, Class<T> tClass) {
    this.keycloakProperties = keycloakProperties;
    this.tClass = tClass;
  }

  /**
   * Converts a JWT token to an AbstractAuthenticationToken.
   *
   * @param source The source JWT token to convert.
   * @return The converted AbstractAuthenticationToken.
   * @throws IllegalArgumentException if the source JWT token cannot be converted.
   */
  @Override
  public AbstractAuthenticationToken convert(Jwt source) {
    try {

      TokenVerifier<T> tokenVerifier = TokenVerifier.create(source.getTokenValue(), tClass);

      T token = tokenVerifier.getToken();

      AccessToken.Access access =
          token.getResourceAccess().get(this.keycloakProperties.getClientId());

      Set<String> roles = access == null ? new HashSet<>() : access.getRoles();
      Set<SimpleGrantedAuthority> grantedAuthorities =
          roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
              .collect(Collectors.toSet());
      return new KeycloakAuthentication<>(source, grantedAuthorities, token.getSubject(), token);
    } catch (VerificationException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}
