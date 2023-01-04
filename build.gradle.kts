plugins {
  id("java")
  id("groovy")
  id("org.jetbrains.intellij") version "1.11.0"
  id("org.springframework.boot") version "2.6.0"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.0"
  kotlin("plugin.spring") version "1.6.0"
  application
}

application {
  mainClass.set("com.techconative.actions.DozerTOMapperStructPlugin")
}

group = "com.techconative"
version = "0.0.2"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies{
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-common
  implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.22")
  // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
  testCompileOnly("org.junit.jupiter:junit-jupiter-api:5.9.1")
// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
  testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
  // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.1")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.2")
  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.9")
  // https://mvnrepository.com/artifact/com.squareup/javapoet
  implementation("com.squareup:javapoet:1.9.0")
  // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
  implementation("org.mapstruct:mapstruct:1.5.3.Final")
// https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor
  implementation("org.mapstruct:mapstruct-processor:1.5.3.Final")
// https://mvnrepository.com/artifact/org.springframework/spring-core
  implementation("org.springframework:spring-core:6.0.3")
}

tasks.test {
  useJUnitPlatform()
}
tasks {
  val fatJar = register<Jar>("fatJar") {
    dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
    archiveClassifier.set("standalone") // Naming the jar
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
    from(contents)
  }
  build {
    dependsOn(fatJar) // Trigger fat jar creation during build
  }
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
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
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
