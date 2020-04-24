/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val mockito_version: String by project
val assertj_version: String by project

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
                implementation(project(":plot-common-portable"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":base"))
                implementation(project(":base-portable"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":vis-canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation(project(":plot-config"))
                implementation(project(":plot-config-portable"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.mockito:mockito-core:$mockito_version")
                implementation("org.assertj:assertj-core:$assertj_version")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}