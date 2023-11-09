package io.github.hacks1ash.keycloak.adapter.model;

import org.keycloak.representations.AccessToken;

public class AbstractKeycloakUser extends AccessToken {

  public boolean isServiceAccount() {
    return this.otherClaims.containsKey("clientId");
  }

  public String getServiceAccountId() {
    Object serviceAccountId = this.getOtherClaims().get("clientId");
    if (serviceAccountId == null) return null;
    return serviceAccountId.toString();
  }
}
