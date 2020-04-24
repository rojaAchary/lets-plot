/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project
val gson_version: String by project
val hamcrest_version: String by project
val mockito_version: String by project
val mockk_version: String by project
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
                implementation(project(":base-portable"))
                implementation(project(":base"))
                implementation("io.github.microutils:kotlin-logging-common:$kotlinLogging_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk-common:$mockk_version")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
                implementation("io.github.microutils:kotlin-logging:$kotlinLogging_version")
                compileOnly("org.hamcrest:hamcrest-core:$hamcrest_version")
                compileOnly("org.hamcrest:hamcrest-library:$hamcrest_version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.hamcrest:hamcrest-core:$hamcrest_version")
                implementation("org.hamcrest:hamcrest-library:$hamcrest_version")
                implementation("org.mockito:mockito-core:$mockito_version")
                implementation("org.assertj:assertj-core:$assertj_version")
                implementation("io.mockk:mockk:$mockk_version")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLogging_version")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
