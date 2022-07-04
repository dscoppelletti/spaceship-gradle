/*
 * Copyright (C) 2022 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle

import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider

/**
 * Tools for Jar package.
 *
 * @since 1.0.0
 */
public class JarTools private constructor(
    private val project: Project
) {

    /**
     * Creates the task generating the META-INF resource files.
     *
     * @param  taskName   Task name.
     * @param  sourceDirs Collection of the META-INF resource folders.
     * @param  outputDir  Output directory.
     * @return            The task provider.
     */
    public fun createMetainfTask(
        taskName: String,
        sourceDirs: FileCollection,
        outputDir: File
    ): TaskProvider<Copy> =
        project.tasks.register(taskName, Copy::class.java) { task ->
            task.description = "Copies the META-INF files"
            task.group = BasePlugin.BUILD_GROUP
            task.from(sourceDirs)
            task.destinationDir = outputDir
        }

    public companion object {

        /**
         * Javadoc classification
         */
        public const val CLASSIFIER_JAVADOC: String = "javadoc"

        /**
         * Source code classification.
         */
        public const val CLASSIFIER_SOURCE: String = "sources"

        /**
         * Name of the fictious class that implements the task for copying the
         * META-INF resource files.
         */
        public const val METAINF_TASK: String =
            "it.scoppelletti.spaceship.gradle.tasks.copyMetainf"

        /**
         * Creates a new instance.
         *
         * @param  project Project.
         * @return         The new object.
         */
        @JvmStatic
        public fun create(project: Project): JarTools = JarTools(project)
    }
}