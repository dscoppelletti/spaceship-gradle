/*
 * Copyright (C) 2019-2022 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.gradle.java

import it.scoppelletti.spaceship.gradle.JarTools
import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

private const val SOURCESET_MAIN = "main"

/**
 * Tools for Java compilation.
 */
internal class JavaTools private constructor(
    private val project: Project
) {

    /**
     * Builds the collection of the source code folders.
     *
     * @return Collection.
     */
    fun buildSourceDirs(): FileCollection? {
        val convention = project.extensions.findByType(
            JavaPluginExtension::class.java)
        if (convention == null) {
            project.logger.info(
                "Extension ${PublishingExtension::class.java} not found.")
            return null
        }

        val mainSourceSet = convention.sourceSets.findByName(SOURCESET_MAIN)
        if (mainSourceSet == null) {
            project.logger.error("SourceSet $SOURCESET_MAIN not found.")
            return null
        }

        val dirs = mutableListOf<File>()
        mainSourceSet.allJava.sourceDirectories.toCollection(dirs)
        return project.files(*dirs.toTypedArray())
    }

    /**
     * Creates the source package task.
     *
     * @param  sources Collection of the source code folders.
     * @param  metaInf META-INF resource folder.
     * @return         The task provider.
     */
    fun createSourcePackageTask(
        sources: FileCollection,
        metaInf: File,
    ): TaskProvider<Jar> =
        project.tasks.register(
            "generateSourcePackage",
            Jar::class.java
        ) { task ->
            task.description = "Generates the source package"
            task.group = PublishingPlugin.PUBLISH_TASK_GROUP
            task.from(sources)
            task.metaInf { spec -> spec.from(metaInf) }
            task.destinationDirectory.set(project.buildDir.resolve("libs"))
            task.archiveBaseName.set("src")
            task.archiveVersion.set(project.version.toString())
            task.archiveClassifier.set(JarTools.CLASSIFIER_SOURCE)
            task.archiveExtension.set(Jar.DEFAULT_EXTENSION)
        }

    companion object {

        /**
         * Creates a new instance.
         *
         * @param  project Project.
         * @return         The new object.
         */
        @JvmStatic
        fun create(project: Project) = JavaTools(project)
    }
}