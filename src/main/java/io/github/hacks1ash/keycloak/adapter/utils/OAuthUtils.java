package io.github.hacks1ash.keycloak.adapter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

@Slf4j
public class OAuthUtils {
  public static OAuth2Error createOAuth2Error(String reason) {
    log.debug(reason);
    return new OAuth2Error(
        OAuth2ErrorCodes.INVALID_TOKEN, reason, "https://tools.ietf.org/html/rfc6750#section-3.1");
  }

  private OAuthUtils() {
    throw new IllegalStateException("OAuthUtils class");
  }
}
