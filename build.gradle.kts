plugins {
  kotlin("jvm")
  kotlin("plugin.spring")
  id("org.springframework.boot")
  id("com.github.ben-manes.versions")
  id("io.spring.dependency-management")
}

val projectGroup: String by project
val projectVersion: String by project
val freeCompilerArg: String by project
val jvmTargetVersion: String by project
val gradleWrapperVersion: String by project
val kotlinCoroutinesReactor: String by project

group = projectGroup
version = projectVersion

java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
  runtimeClasspath {
    extendsFrom(developmentOnly)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesReactor"))
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions") // toMono
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("io.projectreactor:reactor-test")
}

tasks {
  withType<Test> {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events(
          org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
      )
    }
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf(freeCompilerArg)
      jvmTarget = jvmTargetVersion
    }
  }
  withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    launchScript()
  }
  withType<Wrapper> {
    gradleVersion = gradleWrapperVersion
  }
}

defaultTasks("build")
