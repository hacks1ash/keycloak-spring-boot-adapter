package io.github.hacks1ash.keycloak.adapter.utils;

import io.github.hacks1ash.keycloak.adapter.KeycloakAuthentication;
import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper class for accessing the security context of the current user. Provides utility methods
 * related to the security context, particularly for Keycloak users.
 */
public class SecurityContextHelper {

  private SecurityContextHelper() {
    throw new IllegalStateException("SecurityContextHelper class");
  }

  /**
   * Retrieves the current authenticated user from the security context. This method casts the
   * current authentication object to a KeycloakAuthentication and returns the associated
   * authenticated user.
   *
   * @param <T> The type parameter extending AbstractKeycloakUser.
   * @return The current authenticated user of type T.
   * @throws ClassCastException if the current authentication object is not of type
   *     KeycloakAuthentication.
   */
  @SuppressWarnings("unchecked")
  public static <T extends AbstractKeycloakUser> T getCurrentUser() {
    return ((KeycloakAuthentication<T>) SecurityContextHolder.getContext().getAuthentication())
        .getAuthenticatedUser();
  }
}
