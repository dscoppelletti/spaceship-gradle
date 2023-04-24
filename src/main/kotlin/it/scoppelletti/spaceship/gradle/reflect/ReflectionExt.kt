/*
 * Copyright (C) 2008-2013 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.reflect

import java.io.InputStream
import java.security.PrivilegedAction
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Open the input stream for reading a resource.
 *
 * @param  name Resource name.
 * @return      Stream. If the resource is not found, returns `null`.
 * @since       1.0.0
 */
public fun getResourceAsStream(name: String): InputStream? =
    StreamFinder(name).findResource()

/**
 * Resource finder.
 *
 * @property resName Resource name.
 */
private class StreamFinder(
    val resName: String
): PrivilegedAction<InputStream?> {

    /**
     * Finds the resource.
     *
     * @return Resource. If the resource is not found, returns `null`.
     */
    fun findResource(): InputStream? =
        this.run() ?: run {
            logger.debug { "Resource $resName not found." }
            return null
        }

    override fun run(): InputStream? {
        Thread.currentThread().contextClassLoader
            ?.getResourceAsStream(resName)?.let {
                logger.debug { """
                    |Resource $resName found by class-loader
                    |Thread.currentThread().getContextClassLoader()."""
                        .trimMargin().replace('\n', ' ')
                }

                return it
            }

        StreamFinder::class.java.classLoader
            ?.getResourceAsStream(resName)?.let {
                logger.debug { """
                    |Resource $resName found by class-loader
                    |ReflectionExt.class.getClassLoader()."""
                        .trimMargin().replace('\n', ' ')
                }

                return it
            }

        ClassLoader.getSystemClassLoader()
            ?.getResourceAsStream(resName)?.let {
                logger.debug { """
                    |Resource $resName found by class-loader
                    |ClassLoader.getSystemClassLoader()."""
                    .trimMargin().replace('\n', ' ')
                }

                return it
            }

        return null
    }
}