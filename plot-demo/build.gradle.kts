plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val slf4j_version: String by project
val hamcrest_version: String by project
val kotlinx_html_version: String by project
val gson_version: String by project
val math3_version: String by project
val mockito_version: String by project
val assertj_version: String by project

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

kotlin {
    jvm()
    jvm("jvmBatik")
    jvm("jvmJfx")
    jvm("jvmBrowser")
    js {
        browser {
            @Suppress("EXPERIMENTAL_API_USAGE")
            dceTask {
                keep.addAll(
                    arrayOf(
                        "lets-plot-plot-demo.jetbrains.datalore.plotDemo.component.axisComponentDemo",
                        "lets-plot-plot-demo.jetbrains.datalore.plotDemo.plotAssembler.linearRegressionDemo",
                        "lets-plot-plot-demo.jetbrains.datalore.plotDemo.plotAssembler.loessRegressionDemo"
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
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-base"))
                implementation(project(":plot-common-portable"))
                implementation(project(":plot-common"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-config-portable"))
                implementation(project(":plot-config"))
                implementation(project(":plot-demo-common"))
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
        val jvmBatikMain by getting {
            dependsOn(allJvm)
            dependsOn(jvmMain)
            dependencies {
                implementation("org.apache.commons:commons-math3:$math3_version")
                implementation(project(":vis-svg-mapper-batik"))
                implementation(project(":vis-demo-common-batik"))
            }
        }
        val jvmJfxMain by getting {
            dependsOn(allJvm)
            dependsOn(jvmMain)
            dependencies {
                implementation("org.apache.commons:commons-math3:$math3_version")
                implementation(project(":vis-canvas"))
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


// Workaround for Idea/Gradle bug: https://youtrack.jetbrains.com/issue/KT-24463
// MPP: Run does not add resource directory to classpath [Cannot get resource when using common module]
tasks.register<Copy>("copyProcessedResources") {
    from("${project.buildDir}/processedResources")
    into("${project.buildDir}/classes/kotlin")
}

tasks.named("build") {
    dependsOn("copyProcessedResources")
}