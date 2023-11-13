# Keycloak Spring Boot Adapter

### Table of Content

- [Description](#description)
- [Features](#features)
- [Installation](#installation)
    - [Maven](#maven)
    - [Gradle](#gradle)
- [Usage](#usage)
    - [Configuration](#configuration)
    - [Extending the User Model](#extending-the-user-model)
    - [Accessing User Information](#accessing-user-information)
    - [Custom AuthenticationEntryPoint and AccessDeniedHandler](#custom-authenticationentrypoint-and-accessdeniedhandler)
        - [AuthenticationEntryPoint](#authenticationentrypoint)
        - [AccessDeniedHandler](#accessdeniedhandler)
    - [Custom Customizers](#custom-customizers)
        - [Custom CSRF Customizer](#custom-csrf-customizer)
        - [Custom Cors Customizer](#custom-cors-customizer)
        - [Custom HttpRequest Authorization Customizer](#custom-httprequest-authorization-customizer)
- [Important Notes](#important-notes)
- [Contributing](#contributing)
- [License](#license)

## Description

This Keycloak Spring Boot Adapter enables the integration of Keycloak for authentication and authorization in Spring
Boot applications. It focuses on providing a seamless and flexible approach to configuring Spring Security with
Keycloak.

## Features

- Simplified configuration for Keycloak with Spring Boot.
- JWT token decoding and validation.
- Customizable user model extension.
- Spring Security integration.

## Installation

### Maven

```xml

<dependency>
    <groupId>io.github.hacks1ash</groupId>
    <artifactId>keycloak-spring-boot-adapter</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'io.github.hacks1ash:keycloak-spring-boot-adapter:1.0.3'
}
```

## Usage

### Configuration

In your `application.yaml`, add:

```yaml
keycloak:
  server-url: http://localhost:8080/auth # Replace with your Keycloak server URL
  realm: myrealm                         # Replace with your Keycloak realm
  client-id: myclientid                  # Replace with your Keycloak client ID
  client-secret: myclientsecret          # Replace with your Keycloak client secret
  enabled: true                          # Enable or disable Keycloak integration
```

### Extending the User Model

To extend the user model, inherit from `AbstractKeycloakUser`. Use `@JsonProperty` to annotate custom fields:

```java
public class CustomKeycloakUser extends AbstractKeycloakUser {
  @JsonProperty("customField")
  private String customField;
  // additional fields and methods
}
```

Define a bean for your custom user type:

```java
@Bean
public JwtAuthConverter<CustomKeycloakUser> jwtAuthConverter(KeycloakProperties keycloakProperties){
    return new JwtAuthConverter<>(keycloakProperties,CustomKeycloakUser.class);
    }
```

### Accessing User Information

Retrieve the authenticated user using `SecurityContextHelper`:

```java
CustomKeycloakUser currentUser=SecurityContextHelper.getCurrentUser();
```

### Custom AuthenticationEntryPoint and AccessDeniedHandler

#### AuthenticationEntryPoint

Customize the response when authentication fails:

```java

@Configuration
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Custom Unauthorized Message");
  }
}

```

#### AccessDeniedHandler

Customize the response when access is denied:

```java

@Configuration
public class CustomAccessDeniedController implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {
    response.sendError(HttpStatus.FORBIDDEN.value(), "Custom Access Denied Message");
  }
}

```

## Custom Customizers

### Custom CSRF Customizer

To customize CSRF, implement `Customizer<CsrfConfigurer<HttpSecurity>>`:

```java
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

@Configuration
class KeycloakConfig {

  @Bean
  public Customizer<CsrfConfigurer<HttpSecurity>> csrf() {
    return configurer -> {
      configurer.disable();
    };
  }
}

```

### Custom Cors Customizer

To customize CORS, implement `Customizer<CorsConfigurer<HttpSecurity>>`:

```java
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;

@Configuration
class KeycloakConfig {

  @Bean
  public Customizer<CorsConfigurer<HttpSecurity>> cors() {
    return configurer -> {
      configurer.disable();
    };
  }
}

```

### Custom HttpRequest Authorization Customizer

To customize HttpRequest Authorization,
implement `AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry`:

```java
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;

@Configuration
class KeycloakConfig {

  @Bean
  public Customizer<
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
  customizer() {
    return configurer -> {
      configurer.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated();
    };
  }
}

```

## Important Notes

- Custom user properties are only loaded from the decoded access token.
- The `@JsonProperty` annotation is required for all custom user fields.
- This adapter is compatible only with Spring Boot version 3.0 and above.
- By default, all http request should be authenticated. To customize this, implement
  `AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry` and define a bean for it.
  See [Custom HttpRequest Authorization Customizer](#custom-httprequest-authorization-customizer) for an example.
- By default, all cors requests are disabled. To customize this, implement `Customizer<CorsConfigurer<HttpSecurity>>`
  and define a bean for it.
  See [Custom Cors Customizer](#custom-cors-customizer) for an example.
- By default, all csrf requests are disabled. To customize this, implement `Customizer<CsrfConfigurer<HttpSecurity>>`
  and define a bean for it.
  See [Custom CSRF Customizer](#custom-csrf-customizer) for an example.

## Contributing

We welcome contributions. Please submit pull requests for enhancements.

## License

This project is licensed under the **The Apache License, Version 2.0** - see
the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0.txt) file for details.