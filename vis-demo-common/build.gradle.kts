plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinx_html_version: String by project

kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":vis-svg-portable"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinx_html_version")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}


/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// Workaround for Idea/Gradle bug: https://youtrack.jetbrains.com/issue/KT-24463
// MPP: Run does not add resource directory to classpath [Cannot get resource when using common module]
tasks.register<Copy>("copyProcessedResources") {
    from("${project.buildDir}/processedResources")
    into("${project.buildDir}/classes/kotlin")
}

tasks.named("build") {
    dependsOn("copyProcessedResources")
}