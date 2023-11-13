package io.github.hacks1ash.keycloak.adapter.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;

/**
 * Customizer for configuring Cross-Origin Resource Sharing (CORS) in Spring Security.
 *
 * <p>This class implements {@link org.springframework.security.config.Customizer} and is
 * specifically designed to work with the {@link CorsConfigurer} of Spring Security's HttpSecurity
 * configuration. It is used to apply custom rules for handling CORS requests in the application.
 *
 * <p>The customize method in this class disables CORS handling in the Spring Security
 * configuration.
 */
public class CorsCustomizer implements Customizer<CorsConfigurer<HttpSecurity>> {

  /**
   * Customizes the {@link CorsConfigurer} of HttpSecurity to define custom CORS policies.
   *
   * <p>This implementation disables CORS handling, meaning the application will not handle CORS
   * requests specifically. It is important to understand the security implications of this
   * configuration in the context of your application.
   *
   * @param configurer The CORS configurer to be customized.
   */
  @Override
  public void customize(CorsConfigurer<HttpSecurity> configurer) {
    configurer.disable();
  }
}
