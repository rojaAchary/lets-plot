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
                api(kotlin("stdlib-common"))
                api("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

                api(project(":base-portable"))
                api(project(":base"))
                api(project(":vis-canvas"))
                api(project(":vis-svg-portable"))
                api(project(":plot-base-portable"))
                api(project(":plot-base"))
                api(project(":plot-common-portable"))
                api(project(":plot-common"))
                api(project(":plot-builder-portable"))
                api(project(":plot-builder"))
                api(project(":plot-config-portable"))
                api(project(":plot-config"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                // Enable logging to console (make sure it is jared)
                implementation("org.slf4j:slf4j-simple:$slf4j_version")
            }
        }
    }
}

val artifactBaseName = "lets-plot-common"
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
tasks.register<ShadowJar>("jarLetsPlotJvmCommonClasses") {
    group = task_group
    archiveBaseName
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    configurations = listOf(project.configurations["jvmRuntimeClasspath"])

    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.kotlin_builtins")

//    relocate "com", "shade.com"
//    relocate "org", "shade.org"
//    relocate "kotlin", "shade.kotlin"
//    relocate "mu", "shade.mu"
}

tasks.register<ShadowJar>("jarLetsPlotJvmCommonSources") {
    group = task_group
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("sources")
    configurations = listOf(rootProject.configurations["letsPlotJvmCommonSources"])
}

publishing {
    publications {
        register("letsPlotJvmCommon", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(tasks["jarLetsPlotJvmCommonClasses"])
            artifact(tasks["jarLetsPlotJvmCommonSources"])

            pom.withXml {
                asNode().apply {
                    appendNode("name", "Lets-Plot common classes")
                    appendNode("description", "Lets-Plot JVM package without rendering part")
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
val bintraySettings = (rootProject.extra["buildSettings"] as Map<String, Map<String, Any?>?>)["bintray"]

val bintray_user = bintraySettings?.get("user") as String?
val bintray_key = bintraySettings?.get("key") as String?
val artifact_version = project.extra["js_artifact_version"] as String

if (bintray_user != null && bintray_key != null) {

    bintraySettings!!

    bintray {
        setPublications("letsPlotJvmCommon")
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
