package io.github.hacks1ash.keycloak.adapter.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

/**
 * Customizer for configuring Cross-Site Request Forgery (CSRF) protection in Spring Security.
 *
 * <p>This class implements {@link org.springframework.security.config.Customizer} and is
 * specifically designed to work with the {@link CsrfConfigurer} of Spring Security's HttpSecurity
 * configuration. It is used to apply custom rules for handling CSRF protection in the application.
 *
 * <p>The customize method in this class disables CSRF protection in the Spring Security
 * configuration.
 */
public class CsrfCustomizer implements Customizer<CsrfConfigurer<HttpSecurity>> {

  /**
   * Customizes the {@link CsrfConfigurer} of HttpSecurity to define custom CSRF policies.
   *
   * <p>This implementation disables CSRF protection, meaning the application will not actively
   * check for CSRF tokens in requests. This configuration may be appropriate for APIs or services
   * where CSRF protection is not needed or is managed through other means.
   *
   * <p>However, it's important to be aware of the security implications of disabling CSRF
   * protection, particularly for traditional web applications.
   *
   * @param configurer The CSRF configurer to be customized.
   */
  @Override
  public void customize(CsrfConfigurer<HttpSecurity> configurer) {
    configurer.disable();
  }
}
