package com.odradek.keycloak.adapter.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.crypto.PublicKeysWrapper;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKParser;

@Slf4j
public class JWKSUtils {

  private JWKSUtils() {
    throw new IllegalStateException("JWKSUtils class");
  }

  public static PublicKeysWrapper getKeyWrappersForUse(JSONWebKeySet keySet, JWK.Use requestedUse) {
    List<KeyWrapper> result = new ArrayList<>();
    for (JWK jwk : keySet.getKeys()) {
      JWKParser parser = JWKParser.create(jwk);
      if (jwk.getPublicKeyUse() == null) {
        log.debug("Ignoring JWK key '%s'. Missing required field 'use'.", jwk.getKeyId());
      } else if (requestedUse.asString().equals(jwk.getPublicKeyUse())
          && parser.isKeyTypeSupported(jwk.getKeyType())) {
        KeyWrapper keyWrapper = new KeyWrapper();
        keyWrapper.setKid(jwk.getKeyId());
        if (jwk.getAlgorithm() != null) {
          keyWrapper.setAlgorithm(jwk.getAlgorithm());
        }
        keyWrapper.setType(jwk.getKeyType());
        keyWrapper.setUse(getKeyUse(jwk.getPublicKeyUse()));
        keyWrapper.setPublicKey(parser.toPublicKey());
        result.add(keyWrapper);
      }
    }
    return new PublicKeysWrapper(result);
  }

  private static KeyUse getKeyUse(String keyUse) {
    return switch (keyUse) {
      case "sig" -> KeyUse.SIG;
      case "enc" -> KeyUse.ENC;
      default -> null;
    };
  }

  public static JWK getKeyForUse(JSONWebKeySet keySet, JWK.Use requestedUse) {
    for (JWK jwk : keySet.getKeys()) {
      JWKParser parser = JWKParser.create(jwk);
      if (jwk.getPublicKeyUse() == null) {
        log.debug("Ignoring JWK key '%s'. Missing required field 'use'.", jwk.getKeyId());
      } else if (requestedUse.asString().equals(parser.getJwk().getPublicKeyUse())
          && parser.isKeyTypeSupported(jwk.getKeyType())) {
        return jwk;
      }
    }

    return null;
  }
}
