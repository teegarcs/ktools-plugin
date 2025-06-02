plugins {
    `kotlin-dsl`
    id("convention.publish")
}

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