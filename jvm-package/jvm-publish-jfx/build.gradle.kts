import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    id("com.jfrog.bintray")
}

val kotlin_version: String by project
val slf4j_version: String by project

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(kotlin("stdlib-common"))
                compileOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

                compileOnly(project(":jvm-package:jvm-publish-common"))
                api(project(":mapper-core"))
                api(project(":vis-svg-mapper"))
                api(project(":vis-svg-mapper-jfx"))
                api(project(":vis-demo-common"))
                api(project(":vis-demo-common-jfx"))
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly(kotlin("stdlib-jdk8"))
                compileOnly("org.slf4j:slf4j-simple:$slf4j_version")
            }
        }
    }
}

val artifactBaseName = "lets-plot-jfx"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String

val task_group = "lets plot"

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// https://discuss.kotlinlang.org/t/how-to-publish-fat-jar-from-kotlin-multi-project-mpp/14520
// https://youtrack.jetbrains.com/issue/KT-25709
// https://discuss.kotlinlang.org/t/kotlin-1-3-m2-new-multiplatform-projects-model/9264/107
tasks.register<ShadowJar>("jarLetsPlotJvmJfxClasses") {
    group = task_group
    archiveBaseName
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    configurations = listOf(project.configurations["jvmRuntimeClasspath"])

    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.kotlin_builtins")

    // exclude(classes that duplicates `lets-plot-common.jar`)
    exclude("com/google/**/*")
    exclude("kotlin/**/*")
//    exclude("kotlinx/**/*")
    exclude("mu/**/*")
    exclude("org/intellij/**/*")
    exclude("org/jetbrains/annotations/**/*")
    exclude("org/slf4j/**/*")

//    relocate "com", "shade.com"
//    relocate "org", "shade.org"
//    relocate "kotlin", "shade.kotlin"
//    relocate "mu", "shade.mu"
}

tasks.register<ShadowJar>("jarLetsPlotJvmJfxSources") {
    group = task_group
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("sources")
    configurations = listOf(rootProject.configurations["letsPlotJvmJfxSources"])
}

publishing {
    publications {
        register("letsPlotJvmJfx", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(tasks["jarLetsPlotJvmJfxClasses"])
            artifact(tasks["jarLetsPlotJvmJfxSources"])

            pom.withXml {
                asNode().apply {
                    appendNode("name", "Lets-Plot for JavaFX")
                    appendNode("description", "Lets-Plot JVM package with JavaFX rendering")
                    appendNode("url", "https://github.com/JetBrains/lets-plot")
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", "MIT")
                        appendNode("url", "https://opensource.org/licenses/MIT")
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", "jetbrains")
                        appendNode("name", "JetBrains")
                        appendNode("email", "lets-plot@jetbrains.com")
                    }
                    appendNode("scm").apply {
                        appendNode("url", "https://github.com/JetBrains/lets-plot")
                    }
                }
            }
        }
    }
    repositories {
        maven {
//            url = uri("$rootDir/.maven-publish-dev-repo")
            url = uri("https://jetbrains.bintray.com/lets-plot-maven")
        }
    }
}

// Provide Bintray-specific Information
val bintrayBuildSettings = (rootProject.extra["buildSettings"] as Map<String, Map<String, Any?>?>)["bintray"]
val bintraySettings = rootProject.extra["bintraySettings"] as Map<String, Any>

val bintray_user = bintrayBuildSettings?.get("user") as String?
val bintray_key = bintrayBuildSettings?.get("key") as String?
val artifact_version = project.extra["js_artifact_version"] as String

if (bintray_user != null && bintray_key != null) {

    bintray {
        setPublications("letsPlotJvmJfx")
        override = true

        user = bintray_user
        key = bintray_key
        publish = bintraySettings["publish"] as Boolean

        pkg.apply {
            repo = bintraySettings["mvn_repo"] as String
            name = bintraySettings["mvn_pkg_name"] as String
            userOrg = bintraySettings["userOrg"] as String
            vcsUrl = bintraySettings["vcsUrl"] as String
            version.name = artifactVersion
        }

        pkg.setLicenses(*(bintraySettings["licenses"] as Array<String>))
    }
}
