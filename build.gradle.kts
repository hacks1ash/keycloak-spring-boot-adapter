plugins {
  `java-library`
  `maven-publish`
  signing
  id("org.springframework.boot") version "3.1.5"
  id("com.gradleup.nmcp").version("0.0.4")
}

tasks.named("bootJar") {
  enabled = false
}

tasks.named("jar") {
  enabled = true
}

group = "io.github.hacks1ash"
version = System.getenv("PACKAGE_VERSION") ?: "DEV"

java {
  withSourcesJar()
  withJavadocJar()
  sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

extra["keycloakVersion"] = "23.0.0"
extra["lombokVersion"] = "1.18.30"
extra["springBootVersion"] = "3.1.5"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")

  api("org.springframework.boot:spring-boot-starter-oauth2-resource-server:${property("springBootVersion")}")
  api("org.springframework.boot:spring-boot-starter-security:${property("springBootVersion")}")
  api("org.keycloak:keycloak-core:${property("keycloakVersion")}")

  compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
  annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")

  testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
//  testImplementation("org.springframework.security:spring-security-test:${property("springBootVersion")}")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

// ------------------------------------
// PUBLISHING TO SONATYPE CONFIGURATION
// ------------------------------------
object Meta {
  const val COMPONENT_TYPE = "java" // "java" or "versionCatalog"
  const val GROUP = "io.github.hacks1ash"
  const val ARTIFACT_ID = "keycloak-spring-boot-adapter"
  const val PUBLISHING_TYPE = "AUTOMATIC" // USER_MANAGED or AUTOMATIC
  val SHA_ALGORITHMS = listOf(
    "SHA-256",
    "SHA-512"
  ) // sha256 and sha512 are supported but not mandatory. Only sha1 is mandatory but it is supported by default.
  const val DESC = "Keycloak Adapter for Spring Boot >= 3"
  const val LICENSE = "The Apache License, Version 2.0"
  const val LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"
  const val GITHUB_REPO = "hacks1ash/keycloak-spring-boot-adapter.git"
  const val DEVELOPER_ID = "hacks1ash"
  const val DEVELOPER_NAME = "Mikheil Maisuradze"
  const val DEVELOPER_EMAIL = "28116494+hacks1ash@users.noreply.github.com"
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "io.github.hacks1ash"
      artifactId = "keycloak-spring-boot-adapter"
      version = project.version.toString()

      from(components["java"])

      pom {
        name.set("Keycloak Spring Boot Adapter")
        description.set("Keycloak Adapter for Spring Boot >= 3")
        url.set("https://github.com/hacks1ash/keycloak-spring-boot-adapter")

        licenses {
          license {
            name = "The Apache License, Version 2.0"
            url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        scm {
          connection.set("scm:git:git://github.com/hacks1ash/keycloak-spring-boot-adapter.git")
          developerConnection.set("scm:git:ssh://github.com/hacks1ash/keycloak-spring-boot-adapter.git")
          url.set("https://github.com/hacks1ash/keycloak-spring-boot-adapter.git")
        }

        developers {
          developer {
            id.set("hacks1ash")
            name.set("Mikheil Maisuradze")
            email.set("28116494+hacks1ash@users.noreply.github.com")
          }
        }
      }
    }
  }
  repositories {
    maven {
      val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
      val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
      name = "OSSRH"
      url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
      credentials {
        username = System.getenv("MAVEN_USERNAME")
        password = System.getenv("MAVEN_PASSWORD")
      }
    }
  }
}

signing {
  val signingKey: String? = System.getenv("SIGNING_KEY")
  val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
  useInMemoryPgpKeys(signingKey, signingPassword)

  sign(publishing.publications["mavenJava"])
}

nmcp {
  // nameOfYourPublication must point to an existing publication
  publish("mavenJava") {
    username = System.getenv("SONARTYPE_USERNAME")
    password = System.getenv("SONARTYPE_PASSWORD")
    // publish manually from the portal
    publicationType = "AUTOMATIC"
  }
}

tasks.javadoc {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}
