@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

kotlin {
    jvm()
    ios()
    iosSimulatorArm64()

    sourceSets {

        getByName("iosSimulatorArm64Main").dependsOn(getByName("iosMain"))
        getByName("iosSimulatorArm64Test").dependsOn(getByName("iosTest"))

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        all {
            languageSettings.enableLanguageFeature("DataObjects")
        }
    }
}
