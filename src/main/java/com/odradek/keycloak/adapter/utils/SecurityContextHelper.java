package com.odradek.keycloak.adapter.utils;

import com.odradek.keycloak.adapter.OdradekAuthenticationToken;
import com.odradek.keycloak.adapter.OdradekUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

  private SecurityContextHelper() {
    throw new IllegalStateException("SecurityContextHelper class");
  }

  public static OdradekUser getCurrentUser() {
    return ((OdradekAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
        .getOdradekUser();
  }
}
