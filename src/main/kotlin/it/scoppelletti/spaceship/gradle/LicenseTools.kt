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

import it.scoppelletti.spaceship.gradle.tasks.NoticeTask
import java.io.File
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider

/**
 * Tools for licensing.
 *
 * @since 1.0.0
 */
public class LicenseTools private constructor(
    private val project: Project
) {

    /**
     * Copies the license file.
     *
     * @param  taskName  Task name.
     * @param  outputDir Output directory.
     * @return           The task provider.
     */
    public fun createLicenseTask(
        taskName: String,
        outputDir: File
    ): TaskProvider<Copy> =
        project.tasks.register(taskName, Copy::class.java) { task ->
            task.description = "Copies the license file"
            task.group = BasePlugin.BUILD_GROUP
            task.into(outputDir)
                .from(project.rootDir)
                .include(LICENSE_SOURCE)
                .rename(LICENSE_SOURCE, LICENSE_NAME)

            // http://docs.gradle.org/7.2/userguide/validation_problems.html
            //  #implementation_unknown
            // Cannot replace Action by lambda
            task.doFirst(object : Action<Task> {
                override fun execute(task: Task) {
                    outputDir.mkdirs()
                }
            })
        }

    /**
     * Creates the notice file.
     *
     * @param  taskName   Task name.
     * @param  outputFile Output file.
     * @return            The task provider.
     */
    public fun createNoticeTask(
        taskName: String,
        outputFile: File
    ): TaskProvider<NoticeTask> =
        project.tasks.register(taskName, NoticeTask::class.java) { task ->
            task.description = "Creates the notice file"
            task.group = BasePlugin.BUILD_GROUP
            task.outputFile.set(outputFile)
            task.outputs.upToDateWhen { false }
        }

    public companion object {

        /**
         * Name of the license file.
         */
        public const val LICENSE_NAME: String = "LICENSE.txt"

        /**
         * Name of the license source file.
         */
        public const val LICENSE_SOURCE: String = "LICENSE"

        /**
         * Name of the fictious class that implements the task for copying the
         * license file.
         */
        public const val LICENSE_TASK: String =
            "it.scoppelletti.spaceship.gradle.tasks.copyLicense"

        /**
         * Creates a new instance.
         *
         * @param  project Project.
         * @return         The new object.
         */
        @JvmStatic
        public fun create(project: Project): LicenseTools =
            LicenseTools(project)
    }
}