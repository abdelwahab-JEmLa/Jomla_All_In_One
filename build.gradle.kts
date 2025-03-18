plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    dependencies {
        classpath(libs.gradle.v800)
        classpath(libs.kotlin.gradle.plugin)
    }
}
