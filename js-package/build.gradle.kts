
plugins {
    kotlin("js")
}

val kotlin_version: String by project
val kotlinLogging_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlin_version")

    implementation(project(":base-portable"))
    implementation(project(":base"))
    implementation(project(":mapper-core"))
    implementation(project(":vis-svg-portable"))
    implementation(project(":vis-canvas"))
    implementation(project(":vis-svg-mapper"))
    implementation(project(":plot-base-portable"))
    implementation(project(":plot-base"))
    implementation(project(":plot-common-portable"))
    implementation(project(":plot-common"))
    implementation(project(":plot-builder-portable"))
    implementation(project(":plot-builder"))
    implementation(project(":plot-config-portable"))
    implementation(project(":plot-config"))

    if ((rootProject.extra["buildSettings"] as Map<String, Any?>)["enable_livemap"] as Boolean) {
        implementation(project(":gis"))
        implementation(project(":plot-livemap"))
    } else {
        implementation(project(":plot-livemap-stub"))
    }

    implementation("io.github.microutils:kotlin-logging-js:$kotlinLogging_version")
}

kotlin {
    target {
        browser {
            @Suppress("EXPERIMENTAL_API_USAGE")
            dceTask {
                keep.addAll(
                    arrayOf(
                        "lets-plot-js-package.buildPlotFromRawSpecs",
                        "lets-plot-js-package.buildPlotFromProcessedSpecs"
                    )
                )

                if ((rootProject.extra["buildSettings"] as Map<String, Any?>)["enable_livemap"] as Boolean) {
                    keep.add("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
                }
            }
        }
    }
}

val artifact_version = project.extra["js_artifact_version"] as String
val dist_dir = "${project.buildDir}/distributions"

tasks.register("copyProduction") {
    group = "lets-plot"

    doLast {
        val prodTargets = arrayOf("lets-plot-latest.min.js", "lets-plot-${artifact_version}.min.js")

        prodTargets.forEach {
            copy {
                from("${dist_dir}/js-package.js")
                rename("js-package.js", it)
                into(dist_dir)
            }
        }
    }
}

tasks.register("copyDevelopment") {
    group = "lets-plot"

    doLast {
        val devTargets = arrayOf("lets-plot-latest.js", "lets-plot-${artifact_version}.js")

        devTargets.forEach {
            copy {
                from("${dist_dir}/js-package.js")
                rename("js-package.js", it)
                into(dist_dir)
            }
        }
    }
}

tasks.named("browserProductionWebpack") {
    finalizedBy("copyProduction")
}

tasks.named("browserDevelopmentWebpack") {
    finalizedBy("copyDevelopment")
}

tasks.named("build") {
    dependsOn("browserDevelopmentWebpack")
}

