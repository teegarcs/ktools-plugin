import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

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

mavenPublishing {
    configure(GradlePlugin(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true,
    ))
}

dependencies {
    implementation(libs.gradle.kotlin.api)
}