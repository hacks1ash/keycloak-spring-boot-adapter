package com.odradek.keycloak.adapter.utils;

import com.odradek.keycloak.adapter.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.Time;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RemotePublicKeyLocator {

    private static final int PUBLIC_KEY_CACHE_TTL = 86400; // 1 Day
    private static final int MIN_TIME_BETWEEN_REQUESTS = 10; // 10 Seconds

    private final KeycloakProperties keycloakProperties;

    private final RestTemplate restTemplate;

    private final Map<String, PublicKey> currentKeys = new ConcurrentHashMap<>();

    private volatile int lastRequestTime = 0;

    public RemotePublicKeyLocator(KeycloakProperties keycloakProperties, RestTemplate restTemplate) {
        this.keycloakProperties = keycloakProperties;
        this.restTemplate = restTemplate;
    }

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
                log.debug("Won't send request to realm jwks url. Last request time was %d. Current time is %d.", lastRequestTime, currentTime);
            }

            return lookupCachedKey(PUBLIC_KEY_CACHE_TTL, currentTime, kid);
        }
    }


    public void reset() {
        synchronized (this) {
            sendRequest();
            lastRequestTime = Time.currentTime();
            log.debug("Reset time offset to %d.", lastRequestTime);
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
            log.trace("Going to send request to retrieve new set of realm public keys for client " + keycloakProperties.getClientId());
        }

        try {
            ResponseEntity<JSONWebKeySet> responseEntity = restTemplate.getForEntity(keycloakProperties.getServerUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/certs", JSONWebKeySet.class);
            JSONWebKeySet jwks = responseEntity.getBody();
            Map<String, PublicKey> publicKeys = JWKSUtils.getKeysForUse(jwks, JWK.Use.SIG);

            if (log.isDebugEnabled()) {
                log.debug("Realm public keys successfully retrieved for client " + keycloakProperties.getClientId() + ". New kids: " + publicKeys.keySet().toString());
            }

            // Update current keys
            currentKeys.clear();
            currentKeys.putAll(publicKeys);

        } catch (RestClientException e) {
            log.error("Error when sending request to retrieve realm keys", e);
        }
    }

}
