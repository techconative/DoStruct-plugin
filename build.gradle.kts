plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.11.0"
  id("com.diffplug.spotless") version "6.17.0"
  application
}

application {
  mainClass.set("com.techconative.actions.DozerTOMapperStructPlugin")
}

group = "com.techconative"
version = "0.0.5"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies{
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-common
  implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.22")
  // https://mvnrepository.com/artifact/com.squareup/javapoet
  implementation("com.squareup:javapoet:1.9.0")
  // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
  implementation("org.mapstruct:mapstruct:1.5.3.Final")
// https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor
  implementation("org.mapstruct:mapstruct-processor:1.5.3.Final")

}


tasks {
  val fatJar = register<Jar>("fatJar") {
    dependsOn.addAll(listOf("compileJava")) // , "compileKotlin", "processResources" We need this for Gradle optimization to work
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
    version.set("2022.3.1")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"))
  }

  tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
      sourceCompatibility = "17"
      targetCompatibility = "17"
    }

    signPlugin {
      certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
      privateKey.set(System.getenv("PRIVATE_KEY"))
      password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
      token.set(System.getenv("PUBLISH_TOKEN"))
    }

    spotless {
      java {
        googleJavaFormat()
        removeUnusedImports()
        palantirJavaFormat()
        trimTrailingWhitespace()
        endWithNewline()
      }
    }
  }

