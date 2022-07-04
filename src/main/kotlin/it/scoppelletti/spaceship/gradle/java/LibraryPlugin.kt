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

@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.gradle.java

import it.scoppelletti.spaceship.gradle.DokkaTools
import it.scoppelletti.spaceship.gradle.JarTools
import it.scoppelletti.spaceship.gradle.LicenseTools
import it.scoppelletti.spaceship.gradle.PublishTools
import it.scoppelletti.spaceship.gradle.model.DokkaConfigModel
import it.scoppelletti.spaceship.gradle.model.LibraryExtension
import it.scoppelletti.spaceship.gradle.tasks.DokkaLogoStylesTask
import it.scoppelletti.spaceship.gradle.tasks.NoticeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

private const val COMPONENT_JAVA = "java"

/**
 * Plugin for Java libraries.
 *
 * @since 1.0.0
 */
public abstract class LibraryPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(LibraryExtension.NAME,
            LibraryExtension::class.java)

        project.afterEvaluate(this::onProjectAfterEvaluate)
    }

    /**
     * Completes the project.
     *
     * @param project Project.
     */
    private fun onProjectAfterEvaluate(project: Project) {
        val metainfDeps = mutableListOf<TaskProvider<out Task>>()
        val metainfSources = project.files()

        val licenseTools = LicenseTools.create(project)
        val licenseDir = project.buildDir
            .resolve(LicenseTools.LICENSE_TASK)
            .resolve("META-INF")
        metainfDeps.add(licenseTools.createLicenseTask("copyLicense",
            licenseDir))
        metainfSources.from(licenseDir)

        val noticeDir = project.buildDir
            .resolve(NoticeTask::class.java.canonicalName)
            .resolve("META-INF")
        metainfDeps.add(licenseTools.createNoticeTask("generateNotice",
            noticeDir.resolve(NoticeTask.NOTICE_NAME)))
        metainfSources.from(noticeDir)

        val jarTools = JarTools.create(project)
        val metainfDir = project.buildDir
            .resolve(JarTools.METAINF_TASK)
            .resolve("META-INF")
        val metainfTask = jarTools.createMetainfTask("copyMetainf",
            metainfSources, metainfDir).apply {
            configure { task ->
                task.setDependsOn(metainfDeps)
            }
        }

        (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as? Jar)?.apply {
            dependsOn(metainfTask)
            metaInf { spec -> spec.from(metainfDir) }
        } ?: run {
            project.logger.error(
                "Task ${JavaPlugin.JAR_TASK_NAME} not found.")
        }

        val publTools = PublishTools.create(project) ?: return

        val publ = publTools.createPublication(LibraryPlugin.PUBL_NAME,
            COMPONENT_JAVA).apply {
                pom.packaging = Jar.DEFAULT_EXTENSION
        }

        val javaTools = JavaTools.create(project)
        javaTools.buildSourceDirs()?.let { sources ->
            publ.artifact(
                javaTools.createSourcePackageTask(sources,
                    metainfDir).apply {
                    configure { task ->
                        task.dependsOn(metainfTask)
                    }
                }
            )
        }

        val dokkaTools = DokkaTools.create(project)

        val logoStylesFile = project.buildDir
            .resolve(DokkaLogoStylesTask::class.java.canonicalName)
            .resolve(DokkaLogoStylesTask.STYLES_NAME)
        val logoStylesTask = dokkaTools.createLogoStylesTask(
            "generateDokkaLogoStyles", logoStylesFile)

        val dokkaConfigModel = DokkaConfigModel(
            footerMessage = dokkaTools.buildCopyright(),
            customStyleSheets = if (logoStylesTask != null) {
                listOf(logoStylesFile.path)
            } else {
                null
            }
        )

        dokkaTools.createDokkaTask(dokkaConfigModel)?.apply {
            moduleName.set(project.name)
            dokkaSourceSets.configureEach { sourceSet ->
                sourceSet.includes.from(DokkaTools.README_NAME)
            }
        }?.let { dokkaTask ->
            logoStylesTask?.let {
                dokkaTask.dependsOn(it)
            }

            publ.artifact(
                publTools.createJavadocPackageTask(dokkaTask.outputDirectory,
                    metainfDir).apply {
                        configure { task ->
                            task.dependsOn(metainfTask)
                        }
                    }
            )
        }

        publTools.createPublishingRepo()
    }

    public companion object {

        /**
         * Name of the Maven publication.
         */
        public const val PUBL_NAME: String = "library"
    }
}