plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val slf4j_version: String by project

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-config"))
                implementation(project(":vis-demo-common"))
            }
        }

        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                compileOnly("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                implementation("org.slf4j:slf4j-simple:$slf4j_version")  // Enable logging to console
            }
        }
        val jvmMain by getting {
            dependsOn(allJvm)
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}


// Workaround for Idea/Gradle bug: https://youtrack.jetbrains.com/issue/KT-24463
// MPP: Run does not add resource directory to classpath [Cannot get resource when using common module]
tasks.register<Copy>("copyProcessedResources") {
    from("${project.buildDir}/processedResources")
    into("${project.buildDir}/classes/kotlin")
}

tasks.named("build") {
    dependsOn("copyProcessedResources")
}