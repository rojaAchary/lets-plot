plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val math3_version: String by project
val kotlinx_html_version: String by project

kotlin {
    jvm("jvmBatik")
    jvm("jvmJfx")
    jvm("jvmBrowser")   // generates index.html and opens it in browser
    js {
        browser {
            @Suppress("EXPERIMENTAL_API_USAGE")
            dceTask {
                keep.addAll(
                    listOf(
                        "lets-plot-vis-demo-svg-mapper.jetbrains.datalore.vis.svgMapperDemo.svgElementsDemoA",
                        "lets-plot-vis-demo-svg-mapper.jetbrains.datalore.vis.svgMapperDemo.svgElementsDemo"
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
                implementation(project(":mapper-core"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":vis-svg-mapper"))
                implementation(project(":vis-demo-common"))
            }
        }

        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                compileOnly("io.github.microutils:kotlin-logging:$kotlinLogging_version")
            }
        }
        val jvmBatikMain by getting {
            dependsOn(allJvm)
            dependencies {
                implementation("org.apache.commons:commons-math3:$math3_version")
                implementation(project(":vis-svg-mapper-batik"))
                implementation(project(":vis-demo-common-batik"))
            }
        }
        val jvmJfxMain by getting {
            dependsOn(allJvm)
            dependencies {
                implementation("org.apache.commons:commons-math3:$math3_version")
                implementation(project(":vis-canvas")) // needed for `svg transform` parsing
                implementation(project(":vis-svg-mapper-jfx"))
                implementation(project(":vis-demo-common-jfx"))
            }
        }
        val jvmBrowserMain by getting {
            dependsOn(allJvm)
            dependencies {
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