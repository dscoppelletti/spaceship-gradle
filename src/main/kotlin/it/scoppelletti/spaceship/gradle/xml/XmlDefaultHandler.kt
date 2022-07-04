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

package it.scoppelletti.spaceship.gradle.xml

import mu.KotlinLogging
import org.gradle.tooling.BuildException
import org.xml.sax.EntityResolver
import org.xml.sax.InputSource
import org.xml.sax.Locator
import org.xml.sax.SAXParseException
import org.xml.sax.helpers.DefaultHandler

/**
 * Default handler of the events thrown by a SAX parser.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param entityResolver Entity resolver.
 *
 * @property locator Locator of the events thrown by the document parser.
 */
public abstract class XmlDefaultHandler(
    private val entityResolver: EntityResolver? = null
): DefaultHandler() {

    @Suppress("WeakerAccess", "RedundantGetter", "RedundantSetter")
    public var locator: Locator? = null
        get() = field
        private set(value) {
            field = value
        }

    private var contentBuffer: StringBuilder? = null

    /**
     * Sets the locator of the events thrown by the document parser.
     *
     * @param locator Object.
     */
    final override fun setDocumentLocator(locator: Locator?) {
        this.locator = locator
    }

    /**
     * Characters of the text content inner elements.
     *
     * This implementation collects the characters, provided the collection is
     * enabled.
     *
     * @param  ch     The text buffer.
     * @param  start  Index of the first character of the text in the array
     *                {@code ch}.
     * @param  length  Number of characters in the text.
     * @throws        org.xml.sax.SAXException
     */
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        contentBuffer?.append(ch, start, length)
    }

    /**
     * Blanks characters of the text content inner elements.
     *
     * Some parser may handle blank characters by this handler instead of the
     * handler `characters`. Anyway this implementation delegates the blank
     * characters handling to the method `characters`.</p>
     *
     * @param  ch     The text buffer.
     * @param  start  Index of the first character of the text in the array
     *                {@code ch}.
     * @param  length Number of characters in the text.
     * @throws        org.xml.sax.SAXException
     */
    override fun ignorableWhitespace(ch: CharArray?, start: Int, length: Int) {
        characters(ch, start, length)
    }

    /**
     * Activates the collection of the text content inner elements.
     *
     * A subclass usually activates the collection by calling this method in
     * the method `startElement` when it recognizes the start of an element of
     * which you want to collect the text content.
     *
     * @throws IllegalStateException The collection is already activated.
     */
    protected fun collectContent() {
        if (contentBuffer != null) {
            throw IllegalStateException("The collection is already activated.")
        }

        contentBuffer = StringBuilder()
    }

    /**
     * Returns the text collected from the contents inner elements and
     * deactivates the collection.
     *
     * A subclass usually fetches the collected content by calling this method
     * in the method `endElement` when it recognizes the end of an element of
     * which you want to collect the text content.</p>
     *
     * @return Text. If the collection has not been activated, returns
     *         {@code null}.
     */
    protected fun getCollectedContent(): String? {
        if (contentBuffer == null) {
            return null
        }

        val text = contentBuffer.toString()
        contentBuffer = null
        return text
    }

    /**
     * Resolves an entity.
     *
     * This implementation delegates to the entity resolver if provided,
     * otherwise returns `null`.
     *
     * @param  publicId Public identifier.
     * @param  systemId System identifier.
     * @return          Input stream for reading the entity. May be {@code null}
     *                  in order to look for the resource by system identifier
     *                  {@code systemId}.
     * @throws          java.io.IOException
     * @throws          org.xml.sax.SAXException
     */
    override fun resolveEntity(
        publicId: String?,
        systemId: String?
    ): InputSource? = entityResolver?.resolveEntity(publicId, systemId)

    /**
     * @throws org.xml.sax.SAXException
     */
    override fun fatalError(ex: SAXParseException?) {
        ex?.let {
            logger.error(it.toMessage(), it)
        }
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    override fun error(ex: SAXParseException?) {
        ex?.let {
            throw BuildException(it.toMessage(), it)
        }
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    override fun warning(ex: SAXParseException?) {
        ex?.let {
            logger.warn(it.toMessage(), it)
        }
    }

    private companion object {

        private val logger = KotlinLogging.logger { }
    }
}