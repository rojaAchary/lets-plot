import org.jetbrains.gradle.ext.PackagePrefixContainer
import org.jetbrains.gradle.ext.ModuleSettings
import org.gradle.plugins.ide.idea.model.IdeaModule

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val ktor_version: String by project
val kotlinx_html_version: String by project

kotlin {
    jvm("jvmJfx")
    jvm("jvmRawJfx")
    jvm("jvmBrowser")
    jvm("jvmJfxPlot")
    js {
        browser {
            @Suppress("EXPERIMENTAL_API_USAGE")
            dceTask {
                keep.addAll(
                    arrayOf(
                        "ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.barsDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.emptyLiveMapDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.featuresDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.linesDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.pathsDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.piesDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.pointsDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.polygonsDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.rasterTilesDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.rectDemo",
                        "lets-plot-livemap-demo.jetbrains.livemap.demo.textDemo"
                    )
                )
            }
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":vis-canvas"))
                implementation(project(":vis-demo-common"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-base"))
                implementation(project(":plot-config-portable"))
                implementation(project(":plot-config"))
                implementation(project(":plot-common-portable"))
                implementation(project(":plot-demo-common"))
                implementation(project(":mapper-core"))
                implementation(project(":plot-livemap"))
                implementation(project(":vis-svg-mapper"))
            }
        }
        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                compileOnly("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinx_html_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")
            }
        }
        val jvmJfxMain by getting {
            dependsOn(allJvm)
        }
        val jvmJfxPlotMain by getting {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":vis-demo-common-jfx"))
                implementation(project(":vis-svg-mapper-jfx"))
                implementation(project(":livemap-jfx-package"))
            }
        }
        val jvmRawJfxMain by getting {
            dependsOn(allJvm)
        }
        val jvmBrowserMain by getting {
            dependsOn(allJvm)
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}

// Get from https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000579070-Issues-with-Gradle-and-packagePrefix

fun IdeaModule.settings(configure: ModuleSettings.() -> Unit) =
    (this as ExtensionAware).configure(configure)
val ModuleSettings.packagePrefix: PackagePrefixContainer
    get() = (this as ExtensionAware).the()

idea {
    module {
        settings {
            packagePrefix["src/jvmJfxMain/kotlin"] = ""
            packagePrefix["src/jvmBrowserMain/kotlin"] = "jetbrains"
        }
    }
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// Workaround for Idea/Gradle bug: https://youtrack.jetbrains.com/issue/KT-24463
// MPP: Run does not add resource directory to classpath [Cannot get resource when using common module]
//
// JavaFX Scene mapping requires stylesheet resource URI
tasks.register<Copy>("copyProcessedResources") {
    from("${project.buildDir}/processedResources")
    into("${project.buildDir}/classes/kotlin")
}

tasks.named("build") {
    dependsOn("copyProcessedResources")
}