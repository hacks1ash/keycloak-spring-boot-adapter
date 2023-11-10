plugins {
  `java-library`
  `maven-publish`
  signing
  id("org.springframework.boot") version "3.1.5"
  id("io.spring.dependency-management") version "1.1.3"
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

val keycloakVersion = "22.0.5"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")

  api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  api("org.springframework.boot:spring-boot-starter-security")
  api("org.keycloak:keycloak-core:$keycloakVersion")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
  useJUnitPlatform()
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

tasks.javadoc {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}
