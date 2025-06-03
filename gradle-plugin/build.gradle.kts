plugins {
    `kotlin-dsl`
    id("convention.publish")
}

version = "0.1.0"

gradlePlugin {
    plugins {
        create("ktools"){
            id = "com.teegarcs.ktools"
            implementationClass = "com.teegarcs.ktools.KToolsPlugin"
        }
    }
}

dependencies {
    implementation(libs.gradle.kotlin.api)
}