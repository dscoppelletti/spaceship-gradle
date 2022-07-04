/*
 * Copyright (C) 2009-2014 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.xml

import javax.xml.XMLConstants
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import org.gradle.tooling.BuildException
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

private const val JAXP_SCHEMA_LANGUAGE =
    "http://java.sun.com/xml/jaxp/properties/schemaLanguage"

/**
 * XML extensions.
 *
 * @since 1.0.0
 */
public object XmlExt {

    /**
     * Converts an XML string {@code boolean} to the corresponding Java value.
     *
     * @param  s String.
     * @return   Value.
     */
    public fun parseBoolean(s: String?): Boolean =
        s.orEmpty().trim().lowercase() in setOf("true", "1")
}

/**
 * Creates a SAX parser.
 *
 * The SAX parser has the following features:
 *
 * * Namespace support.
 * * XML schema validation (attributes `schemaLocation` or
 *   `noNamespaceSchemaLocation`).
 *
 * @return The new object.
 * @since  1.0.0
 */
public fun newSAXParser(): SAXParser {
    val saxFactory = SAXParserFactory.newInstance().apply {
        isNamespaceAware = true
        isValidating = true
    }

    return try {
        saxFactory.newSAXParser().apply {
            setProperty(JAXP_SCHEMA_LANGUAGE,
                XMLConstants.W3C_XML_SCHEMA_NS_URI)
        }
    } catch (ex: ParserConfigurationException) {
        throw BuildException(ex.message, ex)
    } catch (ex: SAXException) {
        throw BuildException(ex.message, ex)
    }
}

/**
 * Returns the message of a `SAXParseException` filled with the location info.
 *
 * @receiver Exception.
 * @return   Message.
 * @since    1.0.0
 */
public fun SAXParseException.toMessage(): String = """$message
    |[publicId=$publicId, systemId=$systemId, lineNumber=$lineNumber,
    |columnNumber=$columnNumber]""".trimMargin().replace('\n', ' ')