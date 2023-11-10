package io.github.hacks1ash.keycloak.adapter.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.crypto.PublicKeysWrapper;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling JSON Web Key Set (JWKS) operations. This class provides methods to
 * extract and manage public keys from a JWKS.
 */
public class JWKSUtils {

  private static final Logger log = LoggerFactory.getLogger(JWKSUtils.class);

  private JWKSUtils() {
    throw new IllegalStateException("JWKSUtils class");
  }

  /**
   * Extracts key wrappers for a specific use (e.g., signature) from a JSONWebKeySet.
   *
   * @param keySet The JSON Web Key Set to extract keys from.
   * @param requestedUse The key use for which keys are requested (e.g., 'sig' for signature).
   * @return A wrapper containing all the keys from the provided JWKS that match the requested use.
   */
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

  /**
   * Retrieves a single JWK for a specific use from a JSONWebKeySet.
   *
   * @param keySet The JSON Web Key Set to extract the key from.
   * @param requestedUse The key use for which the key is requested (e.g., 'sig' for signature).
   * @return The JWK matching the requested use, or null if no matching key is found.
   */
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

  private static KeyUse getKeyUse(String keyUse) {
    return switch (keyUse) {
      case "sig" -> KeyUse.SIG;
      case "enc" -> KeyUse.ENC;
      default -> null;
    };
  }
}
