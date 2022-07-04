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

package it.scoppelletti.spaceship.gradle.tasks

import freemarker.template.TemplateException
import it.scoppelletti.spaceship.gradle.freemarker.FreemarkerExt
import it.scoppelletti.spaceship.gradle.model.NoticeModel
import it.scoppelletti.spaceship.gradle.model.LibraryExtension
import java.io.IOException
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException

private const val TEMPLATE_NAME =
    "/it/scoppelletti/spaceship/gradle/tasks/notice.ftl"

/**
 * Creates the license files.
 *
 * @since 1.0.0
 *
 * @property outputFile Output file.
 */
public abstract class NoticeTask: DefaultTask() {

    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    public fun action() {
        val outFile = outputFile.get().asFile
        outFile.parentFile?.mkdirs()

        val templ = try {
            FreemarkerExt.config.getTemplate(TEMPLATE_NAME)
        } catch (ex: IOException) {
            throw BuildException("Fail to get $TEMPLATE_NAME.", ex)
        }

        val model = project.extensions.findByType(
            LibraryExtension::class.java)?.let {
            NoticeModel(
                projectTitle =  project.description,
                developerName = it.developer.name.orNull,
                developerUrl = it.developer.url.orNull,
                inceptionYear = it.inceptionYear.orNull
            )
        } ?: run {
            logger.warn(
                "Extension ${LibraryExtension::class.java} not found.")
            NoticeModel(projectTitle = project.description)
        }

        outFile.writer().use { writer ->
            try {
                templ.process(model, writer)
            } catch (ex: IOException) {
                logger.error(ex.message, ex)
                throw BuildException("Fail to process $TEMPLATE_NAME.", ex)
            } catch (ex: TemplateException) {
                throw BuildException("Fail to process $TEMPLATE_NAME.", ex)
            }
        }
    }

    public companion object {

        /**
         * Name of the notice file.
         */
        public const val NOTICE_NAME: String = "NOTICE.txt"
    }
}
