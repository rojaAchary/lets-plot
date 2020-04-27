/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.RecordingCopyTask

val bintrayBuildSettings = (rootProject.extra["buildSettings"] as Map<String, Map<String, Any?>?>)["bintray"]
val bintraySettings = rootProject.extra["bintraySettings"] as Map<String, Any>

val bintray_user = bintrayBuildSettings?.get("user") as String?
val bintray_key = bintrayBuildSettings?.get("key") as String?
val artifact_version = project.extra["js_artifact_version"] as String

if (bintray_user != null && bintray_key != null && !artifact_version.contains("dev")) {

    apply(plugin = "com.jfrog.bintray")

    configure<BintrayExtension> {
        filesSpec = RecordingCopyTask()
        filesSpec.rootSpec.apply {
            from("../build/distributions") {
                include("*latest*")
            }
            into(".")
        }

        override = true

        user = bintray_user
        key = bintray_key
        publish = bintraySettings["publish"] as Boolean

        pkg.apply {
            repo = bintraySettings["js_repo"] as String
            name = bintraySettings["js_pkg_name"] as String
            userOrg = bintraySettings["userOrg"] as String
            vcsUrl = bintraySettings["vcsUrl"] as String
            version.name = "latest"
        }

        pkg.setLicenses(*(bintraySettings["licenses"] as Array<String>))
    }
}
