package io.github.hacks1ash.keycloak.adapter.model;

import org.keycloak.representations.AccessToken;

/**
 * Abstract base class for Keycloak user representation.
 * This class extends the AccessToken representation from Keycloak,
 * enabling additional functionality and fields specific to the user's context in a Spring Boot application.
 */
public abstract class AbstractKeycloakUser extends AccessToken {

  /**
   * Checks if the current user is a service account.
   *
   * @return true if the user is a service account, false otherwise.
   */
  public boolean isServiceAccount() {
    return this.otherClaims.containsKey("clientId");
  }

  /**
   * Retrieves the service account ID if the user is a service account.
   * This method reads the 'clientId' from the otherClaims of the AccessToken.
   *
   * @return The service account ID as a string if available, otherwise null.
   */
  public String getServiceAccountId() {
    Object serviceAccountId = this.getOtherClaims().get("clientId");
    return serviceAccountId != null ? serviceAccountId.toString() : null;
  }
}
