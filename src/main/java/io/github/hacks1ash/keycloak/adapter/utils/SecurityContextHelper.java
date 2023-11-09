package io.github.hacks1ash.keycloak.adapter.utils;

import io.github.hacks1ash.keycloak.adapter.KeycloakAuthentication;
import io.github.hacks1ash.keycloak.adapter.model.AbstractKeycloakUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

  private SecurityContextHelper() {
    throw new IllegalStateException("SecurityContextHelper class");
  }

  @SuppressWarnings("unchecked")
  public static <T extends AbstractKeycloakUser> T getCurrentUser() {
    return ((KeycloakAuthentication<T>) SecurityContextHolder.getContext().getAuthentication())
        .getAuthenticatedUser();
  }
}
