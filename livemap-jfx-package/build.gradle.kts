plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlinLogging_version: String by project

kotlin {
    jvm("jvmJfx")

    sourceSets {
        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-config-portable"))
                implementation(project(":plot-config"))
                implementation(project(":plot-livemap"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":vis-canvas"))
                implementation(project(":vis-demo-common-jfx"))
            }
        }
        val jvmJfxMain by getting {
            dependsOn(allJvm)
        }
    }
}