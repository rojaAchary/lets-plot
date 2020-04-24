/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

kotlin {
    jvm()
    js {
        browser()
    }

    if ((rootProject.extra["buildSettings"] as Map<String, Any?>)["build_python_extension"] as Boolean) {
        val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()

        when {
            currentOs.isMacOsX() -> macosX64("native")
            currentOs.isLinux() -> linuxX64("native")
            currentOs.isWindows() -> mingwX64("native")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("test-common"))

                api(project(":base-portable"))
                api(project(":plot-base-portable"))
                api(project(":plot-config-portable"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                compileOnly(kotlin("test-js"))
            }
        }
    }
}
