plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val hamcrest_version: String by project
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
                implementation("io.github.microutils:kotlin-logging-common:$kotlinLogging_version")
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation(project(":vis-svg-portable"))
                implementation(project(":vis-canvas"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-base"))
                implementation(project(":plot-common-portable"))
                implementation(project(":plot-common"))
                implementation(project(":plot-builder-portable"))
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
