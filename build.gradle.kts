plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}

group = "com.odradek"
version = "1.0.0"

java {
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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.keycloak:keycloak-core:21.1.1")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


val awsCodeArtifactDomainName = System.getenv("CODEARTIFACT_DOMAIN") ?: "odradek"
val awsCodeArtifactRepository = System.getenv("CODEARTIFACT_REPOSITORY") ?: "odradek"
val awsRegion = System.getenv("AWS_REGION") ?: "eu-central-1"
val awsAccountID = System.getenv("AWS_ACCOUNT_ID") ?: "662147410103"
val codeartifactToken = System.getenv("CODEARTIFACT_TOKEN") ?: ProcessBuilder(
        "aws",
        "codeartifact",
        "get-authorization-token",
        "--domain", awsCodeArtifactDomainName,
        "--domain-owner", awsAccountID,
        "--query", "authorizationToken",
        "--output", "text"
).start().inputStream.bufferedReader().readText().trim()

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.odradek"
            artifactId = "keycloak-adapter"
            version = rootProject.version.toString()

            from(components["java"])

            pom {
                name.set("Keycloak Adapter")
                description.set("Keycloak Adapter for Odradek Pay")
                url.set("https://github.com/odradek-pay/keycloak-adapter")

                scm {
                    connection.set("scm:git:git://github.com/odradek-pay/keycloak-adapter.git")
                    developerConnection.set("scm:git:ssh://github.com/odradek-pay/keycloak-adapter.git")
                    url.set("https://github.com/odradek-pay/keycloak-adapter")
                }

                developers {
                    developer {
                        id.set("hacks1ash")
                        name.set("Mikheil Maisuradze")
                        email.set("mikheil@odradek.dev")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url =
                    uri("https://${awsCodeArtifactDomainName}-${awsAccountID}.d.codeartifact.${awsRegion}.amazonaws.com/maven/${awsCodeArtifactRepository}/")
            credentials {
                username = "aws"
                password = codeartifactToken
            }
        }
    }
}