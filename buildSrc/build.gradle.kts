import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.7.2")
}