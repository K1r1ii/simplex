plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:20")
    implementation("org.openjfx:javafx-fxml:20")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.0")
}

javafx {
    version = "14"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "FXMLAppLauncher"
//    mainClass = "Hello"
}
