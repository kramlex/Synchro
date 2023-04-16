import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
    antlr
}

dependencies {
    antlr("org.antlr:antlr4:4.12.0")
    implementation(libs.coroutines)
    implementation(projects.utils)
    api(projects.multi)

    testImplementation(kotlin("test"))
}

tasks.generateGrammarSource {
    maxHeapSize = "128m"
    arguments = arguments + listOf(
        "-visitor", "-listener"
    )
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.generateGrammarSource)
    kotlinOptions.jvmTarget = "1.8"
    incremental = false
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    dependsOn(tasks.generateGrammarSource)
}
