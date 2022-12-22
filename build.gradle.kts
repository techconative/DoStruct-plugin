plugins {
  id("java")
  id("groovy")
  id("org.jetbrains.intellij") version "1.9.0"
}

group = "com.tech"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies{
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-common
  implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.22")
  // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
  testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.0")
}
configurations.all {
  resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2022.1.4")
  type.set("IC") // Target IDE Platform
  plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }

  patchPluginXml {
    sinceBuild.set("221")
    untilBuild.set("231.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
