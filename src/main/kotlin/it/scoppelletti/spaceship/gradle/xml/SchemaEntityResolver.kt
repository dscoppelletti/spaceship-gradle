/*
 * Copyright (C) 2009-2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.xml

import it.scoppelletti.spaceship.gradle.reflect.getResourceAsStream
import org.xml.sax.EntityResolver
import org.xml.sax.InputSource
import org.xml.sax.SAXException

/**
 * XML schema resolver.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       path       Schema path.
 * @param       versionMin Minimum version number.
 * @param       versionMax Maximum version number.
 */
public class SchemaEntityResolver(
    private val path: String,
    private val versionMin: Int,
    private val versionMax: Int
): EntityResolver {

    override fun resolveEntity(
        publicId: String?,
        systemId: String?
    ): InputSource {
        val name = toResourceName(systemId) ?: throw SAXException(
            "Bad schema (publicId=$publicId, systemId=$systemId).")

        val stream = getResourceAsStream(name) ?: throw SAXException(
            "Resource $name not found.")

        return InputSource(stream)
    }

    /**
     * Converts a system identifier to the corresponding resource name.
     *
     * @param  systemId Sistem identifier.
     * @return          Resource name. If the system identifier is not
     *                  compliant, returns `null`.
     */
    private fun toResourceName(systemId: String?): String? {
        if (systemId == null) {
            return null
        }

        val uriPrefix = SchemaEntityResolver.URI_PREFIX + path
        if (!systemId.startsWith(uriPrefix) ||
            !systemId.endsWith(SchemaEntityResolver.SCHEMA_EXT)) {
            return null
        }

        val value = systemId.substring(uriPrefix.length, systemId.length -
                SchemaEntityResolver.SCHEMA_EXT.length)

        val version = try {
            Integer.parseInt(value)
        } catch (ex: NumberFormatException) {
            return null
        }
        if (version < versionMin || version > versionMax) {
            return null
        }

        return SchemaEntityResolver.RESOURCE_PREFIX + path +
                version.toString() + SchemaEntityResolver.SCHEMA_EXT
    }

    public companion object {

        /**
         * XML schema file extension.
         */
        public const val SCHEMA_EXT: String = ".xsd"

        /**
         * Prefix to recognize the URIs of the XML schemas resolvable by this
         * class.
         */
        public const val URI_PREFIX: String = "http://www.scoppelletti.it/res/"

        /**
         * Prefix to find the resources corresponding to XML schemas.
         */
        public const val RESOURCE_PREFIX: String = "it/scoppelletti/"
    }
}