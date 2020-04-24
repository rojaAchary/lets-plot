import org.yaml.snakeyaml.Yaml
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.gradle.ext.PackagePrefixContainer
import org.jetbrains.gradle.ext.ModuleSettings
import org.gradle.plugins.ide.idea.model.IdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel

buildscript {
    dependencies {
        classpath("org.yaml:snakeyaml:1.25")
    }
}

val kotlin_version: String by project
val idea_ext_version: String by project

plugins {
    id("org.jetbrains.kotlin.multiplatform") apply false
    id("org.jetbrains.gradle.plugin.idea-ext") apply false
    id("com.jfrog.bintray") apply false
    id("com.github.johnrengelman.shadow") apply false
}

val include_sources_letsPlotJvmCommon = arrayOf(
        "base-portable",
        "base",
        "vis-canvas",
        "vis-svg-portable",
        "plot-base-portable",
        "plot-base",
        "plot-common-portable",
        "plot-common",
        "plot-builder-portable",
        "plot-builder",
        "plot-config-portable",
        "plot-config"
)

val include_sources_letsPlotJvmJfx = arrayOf(
        "mapper-core",
        "vis-svg-mapper",
        "vis-svg-mapper-jfx",
        "vis-demo-common",
        "vis-demo-common-jfx"
)

val include_sources_letsPlotJvmBatik = arrayOf(
        "mapper-core",
        "vis-svg-mapper",
        "vis-svg-mapper-batik",
        "vis-demo-common",
        "vis-demo-common-batik"
)

val notDataloreProjects = arrayOf(
    "gis",
    "livemap",
    "livemap-demo"
)


configurations {
    create("letsPlotJvmCommonSources")
    create("letsPlotJvmJfxSources")
    create("letsPlotJvmBatikSources")
}


allprojects {
    group = "org.jetbrains.lets-plot"
    version = "1.3.2-SNAPSHOT"
    extra["js_artifact_version"] = "1.3.dev2"

    repositories {
        mavenCentral()
        jcenter()
    }

    // jar jvm sources of this project
    if (name in include_sources_letsPlotJvmCommon || name in include_sources_letsPlotJvmJfx || name in include_sources_letsPlotJvmBatik) {
        apply(plugin = "kotlin-multiplatform")
        configure<KotlinMultiplatformExtension> {
            jvm()
        }

        tasks.named("build") {
            dependsOn(tasks.named("jvmSourcesJar"))
        }
    }

    // make build configuration depend on sources jars
    val sources_jar_path = "${buildDir}/libs/${name}-jvm-${version}-sources.jar"
    if (name in include_sources_letsPlotJvmCommon) {
        rootProject.dependencies {
            rootProject.configurations.getByName("letsPlotJvmCommonSources").files.addAll(files(sources_jar_path))
        }
    }
    if (name in include_sources_letsPlotJvmJfx) {
        rootProject.dependencies {
            rootProject.configurations.getByName("letsPlotJvmJfxSources").files.addAll(files(sources_jar_path))
        }
    }
    if (name in include_sources_letsPlotJvmBatik) {
        rootProject.dependencies {
            rootProject.configurations.getByName("letsPlotJvmBatikSources").files.addAll(files(sources_jar_path))
        }
    }

    if (name !in notDataloreProjects) {
        apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

        configure<IdeaModel> {
            module {
                settings {
                    packagePrefix["src/jvmMain/kotlin"] = "jetbrains.datalore"
                    packagePrefix["src/jvmBatikMain/kotlin"] = "jetbrains.datalore"
                    packagePrefix["src/jvmJfxMain/kotlin"] = "jetbrains.datalore"
                    packagePrefix["src/jvmBrowserMain/kotlin"] = "jetbrains.datalore"
                    packagePrefix["src/jvmTest/kotlin"] = "jetbrains.datalore"
                }
            }
        }
    }
}

val build_settings_file = File(rootDir, "build_settings.yml")
if (!build_settings_file.canRead()) {
    throw GradleException("Couldn't read build_settings.yml")
}
val settings = Yaml().load<Map<String, Any?>>(build_settings_file.inputStream())
if (settings["build_python_extension"] as Boolean) {
    assert(settings["python.include_path"] != null)
}
if (settings["enable_python_package"] as Boolean) {
    assert(settings["build_python_extension"] as Boolean)
    assert((settings["python"] as Map<String, Any?>)["bin_path"] != null)
}

data class BuildSettings(
    val build_python_extension: Boolean,
    val enable_python_package: Boolean,
    val enable_livemap: Boolean = false,
    val python: PythonSettings,
    val pypi: PyPiSettings,
    val bintray: BintraySettings
) {
    data class PythonSettings(
        val bin_path: String?,
        val include_path: String?
    )

    data class PyPiSettings(
        val test: Map<String, String>,
        val prod: Map<String, String>
    )

    data class BintraySettings(
        val user: String,
        val key: String
    )
}

extra["buildSettings"] = settings


// Bintray settings
extra["bintraySettings"] = mapOf(
        "userOrg"             to  "jetbrains",
        "licenses"            to  arrayOf("MIT"),
        "vcsUrl"              to  "https://github.com/JetBrains/lets-plot",
        "publish"             to  true,

        "js_repo"             to  "lets-plot",
        "js_pkg_name"         to  "lets-plot-js",
        "js_artifact_version" to  extra["js_artifact_version"],

        "mvn_repo"            to  "lets-plot-maven",
        "mvn_pkg_name"        to  "lets-plot-jars"
)

// Get from https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000579070-Issues-with-Gradle-and-packagePrefix

fun IdeaModule.settings(configure: ModuleSettings.() -> Unit) =
    (this as ExtensionAware).configure(configure)
val ModuleSettings.packagePrefix: PackagePrefixContainer
    get() = (this as ExtensionAware).the()