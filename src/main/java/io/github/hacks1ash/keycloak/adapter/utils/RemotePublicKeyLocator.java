package io.github.hacks1ash.keycloak.adapter.utils;

import io.github.hacks1ash.keycloak.adapter.KeycloakProperties;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.keycloak.common.util.Time;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Locator class for retrieving remote public keys from a Keycloak server. This class is responsible
 * for fetching and caching the public keys used to verify JWT tokens.
 */
public class RemotePublicKeyLocator {

  private static final Logger log = LoggerFactory.getLogger(RemotePublicKeyLocator.class);

  private static final int PUBLIC_KEY_CACHE_TTL = 86400; // 1 Day
  private static final int MIN_TIME_BETWEEN_REQUESTS = 10; // 10 Seconds

  private final KeycloakProperties keycloakProperties;

  private final RestTemplate restTemplate;

  private final Map<String, PublicKey> currentKeys = new ConcurrentHashMap<>();

  private volatile int lastRequestTime = 0;

  /**
   * Constructs a new instance of RemotePublicKeyLocator.
   *
   * @param keycloakProperties Configuration properties for Keycloak.
   * @param restTemplate RestTemplate for HTTP requests.
   */
  public RemotePublicKeyLocator(KeycloakProperties keycloakProperties, RestTemplate restTemplate) {
    this.keycloakProperties = keycloakProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the public key for a given key ID (KID). If the key is not available in the cache, it
   * triggers a request to the Keycloak server to fetch the latest public keys and updates the
   * cache.
   *
   * @param kid Key ID for which the public key is required.
   * @return PublicKey associated with the given KID, or null if not found.
   */
  public PublicKey getPublicKey(String kid) {
    int currentTime = Time.currentTime();

    // Check if key is in cache.
    PublicKey publicKey = lookupCachedKey(PUBLIC_KEY_CACHE_TTL, currentTime, kid);
    if (publicKey != null) {
      return publicKey;
    }

    // Check if we are allowed to send request
    synchronized (this) {
      currentTime = Time.currentTime();
      if (currentTime > lastRequestTime + MIN_TIME_BETWEEN_REQUESTS) {
        sendRequest();
        lastRequestTime = currentTime;
      } else {
        log.debug(
            String.format(
                "Won't send request to realm jwks url. Last request time was %d. Current time is %d.",
                lastRequestTime, currentTime));
      }

      return lookupCachedKey(PUBLIC_KEY_CACHE_TTL, currentTime, kid);
    }
  }

  /**
   * Resets the cached keys by fetching the latest set from the Keycloak server. This method is
   * useful when there's a need to manually refresh the public keys cache.
   */
  public void reset() {
    synchronized (this) {
      sendRequest();
      lastRequestTime = Time.currentTime();
      log.debug(String.format("Reset time offset to %d.", lastRequestTime));
    }
  }

  private PublicKey lookupCachedKey(int publicKeyCacheTtl, int currentTime, String kid) {
    if (lastRequestTime + publicKeyCacheTtl > currentTime && kid != null) {
      return currentKeys.get(kid);
    } else {
      return null;
    }
  }

  private void sendRequest() {
    if (log.isTraceEnabled()) {
      log.trace(
          String.format(
              "Sending request to retrieve realm public keys for client %s",
              keycloakProperties.getClientId()));
    }

    try {
      ResponseEntity<JSONWebKeySet> responseEntity =
          restTemplate.getForEntity(
              KeycloakUrlHelper.getCertificateUrl(
                  keycloakProperties.getServerUrl(), keycloakProperties.getRealm()),
              JSONWebKeySet.class);
      JSONWebKeySet jwks = responseEntity.getBody();

      if (jwks == null) {
        log.debug(String.format("Realm public keys not found  %s", keycloakProperties.getRealm()));
        return;
      }

      Map<String, PublicKey> publicKeys =
          JWKSUtils.getKeyWrappersForUse(jwks, JWK.Use.SIG).getKeys().stream()
              .collect(
                  Collectors.toMap(
                      KeyWrapper::getKid, keyWrapper -> (PublicKey) keyWrapper.getPublicKey()));

      if (log.isDebugEnabled()) {
        log.debug(
            String.format(
                "Realm public keys successfully retrieved for client %s. New kids: %s",
                keycloakProperties.getClientId(), publicKeys.keySet()));
      }

      // Update current keys
      currentKeys.clear();
      currentKeys.putAll(publicKeys);

    } catch (RestClientException e) {
      log.error("Error when sending request to retrieve realm keys", e);
    }
  }
}
