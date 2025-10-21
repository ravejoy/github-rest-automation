plugins {
    java
    jacoco
    id("com.diffplug.spotless") version "6.25.0"
    id("io.qameta.allure") version "2.12.0"
}

group = "com.ravejoy.github"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("io.rest-assured:rest-assured:5.5.0")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.8")

    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("ch.qos.logback:logback-classic:1.5.8")
    testImplementation("io.qameta.allure:allure-junit5:2.27.0")
}

tasks.test {
    useJUnitPlatform {
        val include =
            System.getProperty("includeTags")?.takeIf { it.isNotBlank() }
                ?.split(",")?.map { it.trim() }?.toTypedArray()
        if (include != null) includeTags(*include)

        val exclude =
            System.getProperty("excludeTags")?.takeIf { it.isNotBlank() }
                ?.split(",")?.map { it.trim() }?.toTypedArray()
        if (exclude != null) excludeTags(*exclude)
    }
    systemProperty("allure.results.directory", "$buildDir/allure-results")
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.22.0")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.2.1")
    }
}

allure {
    version.set("2.29.0")
    adapter {
        frameworks {
            junit5 {
                enabled.set(true)
            }
        }
        aspectjWeaver.set(true)
    }
}
