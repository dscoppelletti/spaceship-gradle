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

package it.scoppelletti.spaceship.gradle.xml;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.gradle.tooling.BuildException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML extensions.
 *
 * @since 1.0.0
 */
public final class XmlExt {
    private static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private XmlExt() {
    }

    /**
     * Create a SAX parser.
     *
     * <p>The SAX parser has the following features:</p>
     *
     * <ol>
     *     <li>Namespace support.</li>
     *     <li>XML schema validation (attributes {@code schemaLocation} or
     *     {@code noNamespaceSchemaLocation}.</li>
     * </ol>
     * @return The new object.
     */
    @SuppressWarnings("unused")
    public static SAXParser newSAXParser() {
        SAXParserFactory saxFactory;
        SAXParser saxParser;

        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        saxFactory.setValidating(true);

        try {
            saxParser = saxFactory.newSAXParser();
            saxParser.setProperty(XmlExt.JAXP_SCHEMA_LANGUAGE,
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
        } catch (ParserConfigurationException|SAXException ex) {
            throw new BuildException(ex.getMessage(), ex);
        }

        return saxParser;
    }

    /**
     * Returns the message of a {@code SAXParseException} filled with the
     * location info.
     *
     * @param  ex Exception.
     * @return    Message.
     */
    @Nonnull
    @SuppressWarnings("StringBufferReplaceableByString")
    public static String toString(SAXParseException ex) {
        return new StringBuilder(ex.getMessage())
                .append(" [publicId=")
                .append(ex.getPublicId())
                .append(", systemId=")
                .append(ex.getSystemId())
                .append(", lineNumber=")
                .append(ex.getLineNumber())
                .append(", columnNumber=")
                .append(ex.getColumnNumber())
                .append(']').toString();
    }
}
