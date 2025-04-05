plugins {
    id("java")
    id("application")
}

group = "ru.programming"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.2.0")
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass = "ru/programming/Main"
}