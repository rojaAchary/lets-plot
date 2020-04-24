import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}


/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()

kotlin {
    if ((rootProject.extra["buildSettings"] as Map<String, Any?>)["build_python_extension"] as Boolean) {
        val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()

        val target = when {
            currentOs.isMacOsX() -> macosX64("native")
            currentOs.isLinux() -> linuxX64("native")
            currentOs.isWindows() -> mingwX64("native")
            else -> throw IllegalStateException("Unsupported platform " + currentOs.displayName)
        }

        target.binaries {
            staticLib {
                baseName = "lets-plot-${project.name}"
            }
        }

        target.compilations["main"].cinterops {
            val python by creating {
                compilerOpts.add("-I${(rootProject.extra["buildSettings"] as Map<String, Map<String, String>>)["python"]!!["include_path"]}")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                // (!) only `portable` sources
                implementation(project(":base-portable"))
                implementation(project(":plot-config-portable"))
            }
        }
    }
}