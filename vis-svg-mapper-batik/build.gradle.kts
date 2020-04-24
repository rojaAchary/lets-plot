/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

rootProject.extra["batikGroupId"] = "org.apache.xmlgraphics"
rootProject.extra["batikArtifacts"] = arrayOf(
    "batik-codec"
)

val batikArtifacts = rootProject.extra["batikArtifacts"] as Array<String>
val batikGroupId = rootProject.extra["batikGroupId"] as String

val kotlin_version: String by project
val kotlinLogging_version: String by project
val batik_version: String by project

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":mapper-core"))
                implementation(project(":vis-svg-portable"))

                implementation(project(":test-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                compileOnly("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                implementation(project(":vis-svg-mapper"))

                batikArtifacts.forEach {
                    api("${batikGroupId}:$it:$batik_version")
                }
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation(project(":plot-config"))
            }
        }
    }
}
