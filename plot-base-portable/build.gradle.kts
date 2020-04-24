/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val gson_version: String by project
val math3_version: String by project
val hamcrest_version: String by project
val mockito_version: String by project
val assertj_version: String by project

kotlin {
    jvm()
    js {
        browser()
    }

    if ((rootProject.extra["buildSettings"] as Map<String, Any?>)["build_python_extension"] as Boolean) {
        val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()

        when {
            currentOs.isMacOsX() -> macosX64()
            currentOs.isLinux() -> linuxX64()
            currentOs.isWindows() -> mingwX64()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.github.microutils:kotlin-logging-common:$kotlinLogging_version")
                implementation(project(":base-portable"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":plot-common-portable"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":test-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.apache.commons:commons-math3:$math3_version")
                implementation("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.hamcrest:hamcrest-core:$hamcrest_version")
                implementation("org.hamcrest:hamcrest-library:$hamcrest_version")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation("org.mockito:mockito-core:$mockito_version")
                implementation("org.assertj:assertj-core:$assertj_version")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("io.github.microutils:kotlin-logging-js:$kotlinLogging_version")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
