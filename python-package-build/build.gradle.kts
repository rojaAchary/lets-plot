import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// ----------------------------------------
// Building and publishing Python package
// ----------------------------------------

val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()
val task_group = "lets plot"
val tools_dir = "${rootDir}/tools"
val python_package_dir = "${rootDir}/python-package"
val python_package_build_dir = "${python_package_dir}/build"
val python_package_dist_dir = "${python_package_dir}/dist"

tasks.named("clean", Delete::class) {
    delete(python_package_build_dir)
    delete(python_package_dist_dir)
    delete("${python_package_dir}/lets_plot/package_data")
    delete("${python_package_dir}/lets_plot.egg-info")
}

val buildSettings = rootProject.extra["buildSettings"] as Map<String, Any?>

if (buildSettings["enable_python_package"] as Boolean) {

    val pythonSettings = buildSettings["python"] as Map<String, String>

    val python_bin_path = pythonSettings["bin_path"]

    tasks.register<Exec>("buildPythonPackage") {
        group = task_group
        description = "Builds lets-plot wheel distribution (python)"

        workingDir = File(python_package_dir)
        if (currentOs.isWindows) {
            commandLine = listOf(
                "${python_bin_path}/python",
                "setup.py",
                "update_js",
                "bdist_wheel",
                "--dist-dir=${python_package_dist_dir}",
                "build",
                "-c",
                "mingw32"
            )
        } else {
            commandLine = listOf(
                "${python_bin_path}/python3",
                "setup.py",
                "update_js",
                "bdist_wheel",
                "--dist-dir=${python_package_dist_dir}"
            )
        }
    }

    tasks.register<Exec>("buildManylinuxWheels") {
        group = task_group
        description = "Builds lets-plot wheel distribution with Manylinux platform for publication(python)"

        workingDir = File(tools_dir)
        commandLine = listOf(
            "./run_manylinux_docker.sh", "${rootDir}"
        )
    }

    val pypiSettings = buildSettings["pypi"] as Map<String, String>
    val pypi_prod = pypiSettings["prod"] as Map<String, String>
    if (pypi_prod["username"] != null && pypi_prod["password"] != null) {
        tasks.register<Exec>("publishProdPythonPackage") {
            group = task_group
            description = "Publishes lets-plot python package to pypi.org"

            workingDir = File(python_package_dist_dir)
            commandLine = listOf(
                "${python_bin_path}/twine",
                "upload",
                "-u",
                pypi_prod["username"],
                "-p",
                pypi_prod["password"],
                "./*"
            )
        }
    }

    val pipy_test = pypiSettings["test"] as Map<String, String>
    if (pipy_test["username"] != null && pipy_test["password"] != null) {
        tasks.register<Exec>("publishTestPythonPackage") {
            group = task_group
            description = "Publishes lets-plot python package to test.pypi.org"

            workingDir = File(python_package_dist_dir)
            commandLine = listOf(
                "${python_bin_path}/twine",
                "upload",
                "--repository-url",
                "https://test.pypi.org/legacy/",
                "-u",
                pipy_test["username"],
                "-p",
                pipy_test["password"],
                "./*"
            )
        }
    }

    tasks.named("buildPythonPackage") {
        dependsOn(":js-package:build", ":python-extension:build")
    }

    tasks.named("build") {
        dependsOn("buildPythonPackage")
    }

    //publishPythonPackage.dependsOn(buildPythonPackage)
}