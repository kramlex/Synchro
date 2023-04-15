plugins {
    id("kmp-library-convention")
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonTestImplementation(libs.kotlinJunit)
}
