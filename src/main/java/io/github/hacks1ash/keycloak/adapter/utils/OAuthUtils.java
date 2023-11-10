package io.github.hacks1ash.keycloak.adapter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

/**
 * Utility class providing methods for handling OAuth2 related operations. This class mainly focuses
 * on creating standardized OAuth2 errors for consistent error handling.
 */
public class OAuthUtils {

  private static final Logger log = LoggerFactory.getLogger(OAuthUtils.class);

  /**
   * Creates a new {@link OAuth2Error} instance based on the provided reason. This method
   * standardizes the creation of OAuth2 errors for use across the application.
   *
   * @param reason A string describing the reason for the error.
   * @return An {@link OAuth2Error} instance encapsulating the provided reason.
   */
  public static OAuth2Error createOAuth2Error(String reason) {
    log.debug(reason);
    return new OAuth2Error(
        OAuth2ErrorCodes.INVALID_TOKEN, reason, "https://tools.ietf.org/html/rfc6750#section-3.1");
  }

  private OAuthUtils() {
    throw new IllegalStateException("OAuthUtils class");
  }
}
