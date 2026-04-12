import org.gradle.kotlin.dsl.internal.sharedruntime.codegen.kotlinDslPackageName

plugins {
    id("java")
    id("war")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    implementation("org.springframework:spring-webmvc:6.2.1")
    implementation("org.springframework.data:spring-data-jdbc:3.4.1")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")  // обновлено
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
    implementation("com.h2database:h2:2.2.224")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.springframework:spring-test:6.2.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")  // обновлено
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")

}

tasks.test {
    useJUnitPlatform()
}