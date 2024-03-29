package io.github.hacks1ash.keycloak.adapter;

import static org.keycloak.TokenVerifier.IS_ACTIVE;
import static org.keycloak.TokenVerifier.SUBJECT_EXISTS_CHECK;

import io.github.hacks1ash.keycloak.adapter.utils.KeycloakUrlHelper;
import io.github.hacks1ash.keycloak.adapter.utils.OAuthUtils;
import io.github.hacks1ash.keycloak.adapter.utils.RemotePublicKeyLocator;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.util.TokenUtil;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;

/**
 * A custom implementation of {@link JwtDecoder} for decoding and verifying JWT tokens issued by
 * Keycloak.
 */
public class KeycloakJWTDecoder implements JwtDecoder {

  private final RemotePublicKeyLocator remotePublicKeyLocator;

  private final KeycloakProperties keycloakProperties;

  /**
   * Constructs a KeycloakJWTDecoder with specified remote public key locator and Keycloak
   * properties.
   *
   * @param remotePublicKeyLocator Locator for public keys.
   * @param keycloakProperties Properties configuration for Keycloak.
   */
  public KeycloakJWTDecoder(
      RemotePublicKeyLocator remotePublicKeyLocator, KeycloakProperties keycloakProperties) {
    this.remotePublicKeyLocator = remotePublicKeyLocator;
    this.keycloakProperties = keycloakProperties;
  }

  /**
   * Decodes a JWT token to a {@link Jwt} object.
   *
   * @param token the JWT token string.
   * @return a decoded {@link Jwt} object.
   * @throws JwtException if the token cannot be decoded or if it's invalid.
   */
  @Override
  public Jwt decode(String token) throws JwtException {

    try {
      TokenVerifier<JsonWebToken> tokenVerifier = TokenVerifier.create(token, JsonWebToken.class);
      PublicKey publicKey =
          remotePublicKeyLocator.getPublicKey(tokenVerifier.getHeader().getKeyId());
      tokenVerifier.withChecks(
          SUBJECT_EXISTS_CHECK,
          new TokenVerifier.TokenTypeCheck(Arrays.asList(TokenUtil.TOKEN_TYPE_BEARER)),
          IS_ACTIVE,
          new TokenVerifier.RealmUrlCheck(
              KeycloakUrlHelper.getRealmUrl(
                  this.keycloakProperties.getServerUrl(), this.keycloakProperties.getRealm())));

      tokenVerifier.publicKey(publicKey);

      JsonWebToken jsonWebToken = tokenVerifier.verify().getToken();

      Map<String, Object> headers =
          new HashMap<>(
              Map.of(
                  "alg", tokenVerifier.getHeader().getAlgorithm().name(),
                  "typ", tokenVerifier.getHeader().getType(),
                  "kid", tokenVerifier.getHeader().getKeyId()));

      if (tokenVerifier.getHeader().getContentType() != null) {
        headers.put("cty", tokenVerifier.getHeader().getContentType());
      }

      // TODO - add support for other claims
      Map<String, Object> claims = Map.of("sub", jsonWebToken.getSubject());

      Instant issuedAt = Instant.ofEpochSecond(jsonWebToken.getIat());
      Instant expiresAt = Instant.ofEpochSecond(jsonWebToken.getExp());

      return new Jwt(token, issuedAt, expiresAt, headers, claims);
    } catch (VerificationException e) {
      throw new JwtValidationException(
          e.getMessage(), List.of(OAuthUtils.createOAuth2Error(e.getMessage())));
    }
  }
}
