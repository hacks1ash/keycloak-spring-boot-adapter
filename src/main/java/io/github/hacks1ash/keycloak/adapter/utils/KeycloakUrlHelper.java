package io.github.hacks1ash.keycloak.adapter.utils;

/**
 * Utility class for constructing URLs for various Keycloak endpoints. This class provides static
 * methods to generate URLs based on server and realm configurations.
 */
public class KeycloakUrlHelper {

  /**
   * Constructs the URL for a given realm on a Keycloak server.
   *
   * @param serverUrl Base URL of the Keycloak server.
   * @param realm The name of the realm.
   * @return A String representing the URL to the specified realm on the Keycloak server.
   */
  public static String getRealmUrl(String serverUrl, String realm) {
    if (serverUrl.strip().endsWith("/")) return serverUrl + "realms/" + realm;
    return serverUrl + "/realms/" + realm;
  }

  /**
   * Constructs the URL for the certificate endpoint of a given realm on a Keycloak server.
   *
   * @param serverUrl Base URL of the Keycloak server.
   * @param realm The name of the realm.
   * @return A String representing the URL to the certificate endpoint of the specified realm.
   */
  public static String getCertificateUrl(String serverUrl, String realm) {
    return getRealmUrl(serverUrl, realm) + "/protocol/openid-connect/certs";
  }

  private KeycloakUrlHelper() {
    throw new IllegalStateException("KeycloakUrlHelper class");
  }
}
