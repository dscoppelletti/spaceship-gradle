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

import freemarker.cache.URLTemplateLoader
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import mu.KotlinLogging

/**
 * Simple implementation of `URLTemplateLoader` interface.
 */
internal class FreemarkerUrlTemplateLoader: URLTemplateLoader() {

    override fun getURL(name: String?): URL? {
        if (name == null) {
            return null
        }

        return try {
            val url = URL(name)
            val cn = url.openConnection()
            cn.getInputStream().use {
                // NOP
            }

            url
        } catch (ex: IOException) {
            logger.debug(ex) { "Connection to URL $name failed."}
            null
        } catch (ex: MalformedURLException) {
            logger.debug(ex) { "Malformed URL $name." }
            null
        }
    }

    private companion object {
        private val logger = KotlinLogging.logger { }
    }
}