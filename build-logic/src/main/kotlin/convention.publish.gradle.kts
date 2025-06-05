plugins {
    id("com.vanniktech.maven.publish")
}

publishing {
    repositories {
        maven {
            name = "LocalRoot"
            url = uri(rootProject.layout.projectDirectory.dir("libs"))
        }
    }
}