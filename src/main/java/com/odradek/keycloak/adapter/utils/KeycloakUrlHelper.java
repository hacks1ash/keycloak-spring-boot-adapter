package com.odradek.keycloak.adapter.utils;

public class KeycloakUrlHelper {

  public static String getRealmUrl(String serverUrl, String realm) {
    if (serverUrl.strip().endsWith("/")) return serverUrl + "realms/" + realm;
    return serverUrl + "/realms/" + realm;
  }

  public static String getCertificateUrl(String serverUrl, String realm) {
    return getRealmUrl(serverUrl, realm) + "/protocol/openid-connect/certs";
  }

  private KeycloakUrlHelper() {
    throw new IllegalStateException("KeycloakUrlHelper class");
  }
}
