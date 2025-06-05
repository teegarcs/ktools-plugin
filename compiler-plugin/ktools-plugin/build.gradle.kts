import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("convention.publish")
}

mavenPublishing {
    configure(KotlinJvm(
        javadocJar = JavadocJar.None(),
        sourcesJar = true,
    ))
}
group "com.teegarcs.ktools"
version = "0.1.0"

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}