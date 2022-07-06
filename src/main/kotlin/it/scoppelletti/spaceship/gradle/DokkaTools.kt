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

@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.gradle

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import freemarker.template.TemplateException
import it.scoppelletti.spaceship.gradle.freemarker.FreemarkerExt
import it.scoppelletti.spaceship.gradle.model.DokkaConfigModel
import it.scoppelletti.spaceship.gradle.model.DokkaCopyrightModel
import it.scoppelletti.spaceship.gradle.model.LibraryExtension
import it.scoppelletti.spaceship.gradle.tasks.DokkaLogoStylesTask
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.util.Objects
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.tooling.BuildException
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder

private const val CONFIG_KEY = "org.jetbrains.dokka.base.DokkaBase"
private const val COPYRIGHT_TEMPLATE =
    "/it/scoppelletti/spaceship/gradle/copyright.ftl"

/**
 * Tools for documentation.
 *
 * @since 1.0.0
 */
public class DokkaTools private constructor(
    private val project: Project,
    private val configAdapter: JsonAdapter<DokkaConfigModel>
) {

    /**
     * Creates the documentation task.
     *
     * @param  config Configuration.
     * @return        The new task.
     */
    public fun createDokkaTask(config: DokkaConfigModel): DokkaTask? =
        (project.tasks.findByName(DokkaTools.TASK_NAME) as?
                DokkaTask)?.apply {
            configureTask(this, config)
        } ?: run {
            project.logger.info("Task ${DokkaTools.TASK_NAME} not found.")
            null
        }

    /**
     * Configures a task.
     *
     * @param task   Task.
     * @param config Configuration.
     */
    private fun configureTask(task: DokkaTask, config: DokkaConfigModel) {
        val workDir = project.buildDir.resolve(
            DokkaTools::class.java.canonicalName)

        task.outputDirectory.set(workDir.resolve("output"))
        task.moduleVersion.set(Objects.toString(project.version))
        task.suppressObviousFunctions.set(false)
        task.suppressInheritedMembers.set(true)
        task.offlineMode.set(true)
        task.dokkaSourceSets.configureEach(this::configureSourceSet)

        val cacheDir = workDir.resolve("cache")
        cacheDir.mkdirs()
        task.cacheRoot.set(cacheDir)

        // http://kotlin.github.io/dokka/1.6.20/user_guide/gradle/usage/#applying-plugins
        // http://stackoverflow.com/a/72020854 - April 26, 2022
        // http://kotlin.github.io/dokka/1.6.20/user_guide/base-specific/frontend
        val configJson = configAdapter.toJson(config)
        task.pluginsMapConfiguration.set(mapOf(CONFIG_KEY to configJson))
    }

    /**
     * Configures a source set.
     *
     * @param sourceSet Source set builder.
     */
    private fun configureSourceSet(sourceSet: GradleDokkaSourceSetBuilder) {
        sourceSet.documentedVisibilities.set(setOf(
            DokkaConfiguration.Visibility.PUBLIC,
            DokkaConfiguration.Visibility.PROTECTED))
        sourceSet.skipDeprecated.set(false)
        sourceSet.reportUndocumented.set(false)
        sourceSet.skipEmptyPackages.set(true)
        sourceSet.platform.set(Platform.jvm)
        sourceSet.noJdkLink.set(true)
        sourceSet.noAndroidSdkLink.set(true)
        sourceSet.suppressGeneratedFiles.set(true)
    }

    /**
     * Builds the copyright footer for documentation.
     *
     * @return String.
     */
    public fun buildCopyright(): String? {
        val spaceshipExt = project.extensions.findByType(
            LibraryExtension::class.java) ?: run {
            project.logger.warn(
                "Extension ${LibraryExtension::class.java} not found.")
            return null
        }

        val templ = try {
            FreemarkerExt.config.getTemplate(COPYRIGHT_TEMPLATE)
        } catch (ex: IOException) {
            project.logger.error("Fail to get $COPYRIGHT_TEMPLATE.", ex)
            throw BuildException("Fail to get $COPYRIGHT_TEMPLATE.", ex)
        }

        val model = spaceshipExt.let {
            DokkaCopyrightModel(
                developerName = it.developer.name.orNull,
                developerUrl = it.developer.url.orNull,
                inceptionYear = it.inceptionYear.orNull
            )
        }

        val writer = StringWriter()
        try {
            templ.process(model, writer)
        } catch (ex: IOException) {
            project.logger.error("Fail to process $COPYRIGHT_TEMPLATE.", ex)
            throw BuildException("Fail to process $COPYRIGHT_TEMPLATE.", ex)
        } catch (ex: TemplateException) {
            project.logger.error("Fail to process $COPYRIGHT_TEMPLATE.", ex)
            throw BuildException("Fail to process $COPYRIGHT_TEMPLATE.", ex)
        }

        writer.flush()
        return writer.toString()
    }

    /**
     * Creates the logo style sheet task.
     *
     * @param  taskName   Task name.
     * @param  outputFile Output file.
     * @return            The task provider.
     */
    public fun createLogoStylesTask(
        taskName: String,
        outputFile: File
    ): TaskProvider<DokkaLogoStylesTask>? {
        val spaceshipExt = project.extensions.findByType(
            LibraryExtension::class.java) ?: run {
            project.logger.warn(
                "Extension ${LibraryExtension::class.java} not found.")
            return null
        }

        val logoUrl = spaceshipExt.logoUrl.orNull ?: run {
            project.logger.warn("Logo URL not set.")
            return null
        }

        return project.tasks.register(taskName,
            DokkaLogoStylesTask::class.java) { task ->
            task.description = "Creates the logo styles sheet file"
            task.group = Javadoc.TASK_GROUP
            task.logoUrl.set(logoUrl)
            task.outputFile.set(outputFile)
            task.outputs.upToDateWhen { false }
        }
    }

    public companion object {

        /**
         * Name of the `README` file.
         */
        public const val README_NAME: String = "README.md"

        /**
         * Name of the `Dokka` task for HTML format.
         */
        public const val TASK_NAME: String = "dokkaHtml"

        /**
         * Creates a new instance.
         *
         * @param  project Project.
         * @return         The new object.
         */
        @JvmStatic
        public fun create(project: Project): DokkaTools {
            val moshi = Moshi.Builder().build()
            val configAdapter = moshi.adapter(DokkaConfigModel::class.java)

            return DokkaTools(project, configAdapter)
        }
    }
}
