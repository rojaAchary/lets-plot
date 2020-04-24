/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":mapper-core"))
                implementation(project(":vis-svg-portable"))
                api(project(":vis-demo-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation(project(":vis-canvas"))
                implementation(project(":vis-svg-mapper-jfx"))
            }
        }
    }
}
