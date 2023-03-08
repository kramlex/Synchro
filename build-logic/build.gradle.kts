plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    api(libs.mobileMultiplatformGradlePlugin)
    api(libs.kotlinGradlePlugin)
}
