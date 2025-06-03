plugins {
    id("com.vanniktech.maven.publish")
}
mavenPublishing {
//    pomFromGradleProperties()
//    signAllPublications()
}

publishing {
    repositories {
        maven {
            name = "LocalRoot"
            url = uri(rootProject.layout.projectDirectory.dir("libs"))
        }
    }
}