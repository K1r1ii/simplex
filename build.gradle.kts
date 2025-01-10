plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id ("edu.sc.seis.launch4j") version "3.0.6"
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

launch4j {
    mainClassName = "FXMLAppLauncher"
    outfile.set("simplex.exe")
    bundledJrePath.set("/usr/lib/jvm/java-21-openjdk-amd64")
}

application {
    mainClass = "FXMLAppLauncher"
}
