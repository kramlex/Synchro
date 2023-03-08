plugins {
    id("kmp-library-convention")
}

dependencies {

    commonTestImplementation(libs.kotlinJunit)
}

cocoaPods {
    pod("SwiftUtils")
}
