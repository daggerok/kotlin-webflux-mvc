pluginManagement {
  val kotlinVersion: String by extra
  val springBootVersion: String by extra
  val versionsPluginVersion: String by extra
  val dependencyManagementVersion: String by extra
  plugins {
    repositories {
      mavenCentral()
      gradlePluginPortal()
    }
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version springBootVersion
    id("com.github.ben-manes.versions") version versionsPluginVersion
    id("io.spring.dependency-management") version dependencyManagementVersion
  }
}

val rootProjectName: String by extra
rootProject.name = rootProjectName
