/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3.1/userguide/building_java_projects.html
 */

val log4jVersion by extra("2.24.1")
val jmhVersion by extra("1.37")

val springBootVersion by extra("3.3.5")
val junitVersion by extra("5.11.3")

plugins {
    // Apply the java-library plugin for API and implementation separation.
    java
    //me.champeau.jmh
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
//    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
//    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
//    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${log4jVersion}")

    implementation("org.springframework.boot:spring-boot-starter-logging:${springBootVersion}")

    // https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core
    implementation("org.openjdk.jmh:jmh-core:${jmhVersion}")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}")

    // this is the line that solves the missing /META-INF/BenchmarkList error
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}")


    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}")
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
