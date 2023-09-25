package com.odradek.keycloak.adapter.utils;

import com.odradek.keycloak.adapter.KeycloakAuthentication;
import com.odradek.keycloak.adapter.model.AbstractKeycloakUser;
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
