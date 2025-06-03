plugins {
    alias(libs.plugins.kotlin.jvm)
    id("convention.publish")
}

version = "0.1.0"

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}