package com.odradek.keycloak.adapter;

import com.odradek.keycloak.adapter.utils.RemotePublicKeyLocator;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.util.TokenUtil;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.security.PublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.keycloak.TokenVerifier.IS_ACTIVE;
import static org.keycloak.TokenVerifier.SUBJECT_EXISTS_CHECK;

@Slf4j
public class KeycloakJWTDecoder implements JwtDecoder {


    private final RemotePublicKeyLocator remotePublicKeyLocator;

    private final KeycloakProperties keycloakProperties;

    public KeycloakJWTDecoder(RemotePublicKeyLocator remotePublicKeyLocator, KeycloakProperties keycloakProperties) {
        this.remotePublicKeyLocator = remotePublicKeyLocator;
        this.keycloakProperties = keycloakProperties;
    }

    /**
     * Decodes the JWT from it's compact claims representation format and returns a
     * {@link Jwt}.
     *
     * @param token the JWT value
     * @return a {@link Jwt}
     * @throws JwtException if an error occurs while attempting to decode the JWT
     */
    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            TokenVerifier<JsonWebToken> tokenVerifier = TokenVerifier.create(token, JsonWebToken.class);
            PublicKey publicKey = remotePublicKeyLocator.getPublicKey(tokenVerifier.getHeader().getKeyId());
            tokenVerifier.withChecks(
                    SUBJECT_EXISTS_CHECK,
                    new TokenVerifier.TokenTypeCheck(TokenUtil.TOKEN_TYPE_BEARER),
                    IS_ACTIVE,
                    new TokenVerifier.RealmUrlCheck(this.keycloakProperties.getServerUrl() + "/realms/" + this.keycloakProperties.getRealm())
            );

            tokenVerifier.publicKey(publicKey);

            JsonWebToken jsonWebToken = tokenVerifier.verify().getToken();

            Map<String, Object> headers = new HashMap<>(Map.of(
                    "alg", tokenVerifier.getHeader().getAlgorithm().name(),
                    "typ", tokenVerifier.getHeader().getType(),
                    "kid", tokenVerifier.getHeader().getKeyId()
            ));

            if (tokenVerifier.getHeader().getContentType() != null) {
                headers.put("cty", tokenVerifier.getHeader().getContentType());
            }

            // TODO - add support for other claims
            Map<String, Object> claims = Map.of("sub", jsonWebToken.getSubject());

            Instant issuedAt = Instant.ofEpochSecond(jsonWebToken.getIat());
            Instant expiresAt = Instant.ofEpochSecond(jsonWebToken.getExp());

            return new Jwt(token, issuedAt, expiresAt, headers, claims);
        } catch (VerificationException e) {
            throw new JwtValidationException(e.getMessage(), List.of(createOAuth2Error(e.getMessage())));
        }
    }

    private OAuth2Error createOAuth2Error(String reason) {
        log.debug(reason);
        return new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, reason,
                "https://tools.ietf.org/html/rfc6750#section-3.1");
    }
}
