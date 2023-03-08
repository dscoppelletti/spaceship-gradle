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

package it.scoppelletti.spaceship.gradle.freemarker

import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Freemarker extensions.
 *
 * @property config Configuration
 * @since           1.0.0
 */
public object FreemarkerExt {

    public val config: Configuration by lazy {
        Configuration(Configuration.VERSION_2_3_31)
            .apply {
                defaultEncoding = StandardCharsets.UTF_8.name()
                templateExceptionHandler =
                    TemplateExceptionHandler.RETHROW_HANDLER
                logTemplateExceptions = false
                wrapUncheckedExceptions = true
                fallbackOnNullLoopVariable = false

                templateLoader = MultiTemplateLoader(arrayOf(
                    FileTemplateLoader(File("/")),
                    ClassTemplateLoader(FreemarkerExt::class.java, "/"),
                    FreemarkerUrlTemplateLoader()))
            }
    }
}
