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

package it.scoppelletti.spaceship.gradle.xml;

import java.io.IOException;
import javax.annotation.Nullable;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.tooling.BuildException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unused")
public abstract class XmlDefaultHandler extends DefaultHandler {
    private static final Logger myLogger = Logging.getLogger(
            XmlDefaultHandler.class);
    private Locator myLocator = null;
    private EntityResolver myEntityResolver = null;
    private StringBuilder myContentBuffer = null;

    /**
     * Sole constructor.
     */
    protected XmlDefaultHandler() {
    }

    /**
     * Gets the locator of the events thrown by the document parser.
     *
     * @return Object. May be {@code null}.
     */
    @Nullable
    protected final Locator getDocumentLocator() {
        return myLocator;
    }

    /**
     * Sets the locator of the events thrown by the document parser.
     *
     * @param locator Object.
     */
    @Override
    public final void setDocumentLocator(Locator locator) {
        myLocator = locator;
    }

    /**
     * Gets the entity resolver.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    public final EntityResolver getEntityResolver() {
        return myEntityResolver;
    }

    /**
     * Sets the entity resolver.
     *
     * @param obj Object.
     *            
     * @see #resolveEntity
     */
    public final void setEntityResolver(EntityResolver obj) {
        myEntityResolver = obj;
    }

    /**
     * Characters of the text content inner elements.
     *
     * <p>This implementation collects the characters, provided the collection
     * is enabled.</p>
     *
     * @param ch     The text buffer.
     * @param start  Index of the first character of the text in the array
     *               {@code ch}.
     * @param length Number of characters in the text.
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public void characters(char[] ch, int start, int length) throws
            SAXException {
        if (myContentBuffer != null) {
            myContentBuffer.append(ch, start, length);
        }
    }

    /**
     * Blanks characters of the text content inner elements.
     *
     * <p>Some parser may handle blank characters by this handler instead of
     * the handler {@code characters}.<br />
     * Anyway this implementation delegates the blank characters handling to the
     * method {@code characters}.</p>
     *
     * @param ch     The text buffer.
     * @param start  Index of the first character of the text in the array
     *               {@code ch}.
     * @param length Number of characters in the text.
     *               
     * @see #collectContent
     * @see #ignorableWhitespace
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws
            SAXException {
        characters(ch, start, length);
    }

    /**
     * Activates the collection of the text content inner elements.
     *
     * <p>A subclass usually activates the collection by calling this method in
     * the method {@code startElement} when it recognizes the start of an
     * element of which you want to collect the text content.</p>
     *
     * @throws IllegalStateException The collection is already activated.
     * @see    #getCollectedContent
     */
    protected final void collectContent() {
        if (myContentBuffer != null) {
            throw new IllegalStateException(
                    "The collection is already activated.");
        }

        myContentBuffer = new StringBuilder();
    }

    /**
     * Returns the text collected from the contents inner elements and
     * deactivates the collection.
     *
     * <p>A subclass usually fetches the collected content by calling this
     * method in the method {@code endElement} when it recognizes the end of an
     * element of which you want to collect the text content.</p>
     *
     * @return Text. If the collection has not been activated, returns
     *         {@code null}.
     * @see    #collectContent
     */
    @Nullable
    protected final String getCollectedContent() {
        String text;

        if (myContentBuffer == null) {
            return null;
        }

        text = myContentBuffer.toString();
        myContentBuffer = null;
        return text;
    }

    /**
     * Resolves an entity.
     *
     * <p>This implementation delegates to the entity resolver if provided,
     * otherwise returns {@code null}.</p>
     *
     * @param  publicId Public identifier.
     * @param  systemId System identifier.
     * @return          Input stream for reading the entity. May be {@code null}
     *                  in order to look for the resource by system identifier
     *                  {@code systemId}.
     * @see #setEntityResolver
     */
    @Override
    @Nullable
    public InputSource resolveEntity(String publicId, String systemId) throws
            SAXException, IOException {
        if (myEntityResolver != null) {
            return myEntityResolver.resolveEntity(publicId, systemId);
        }

        return null;
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public void fatalError(SAXParseException ex) throws SAXException {
        myLogger.error(XmlExt.toString(ex), ex);
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public void error(SAXParseException ex) throws SAXException {
        throw new BuildException(XmlExt.toString(ex), ex);
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public void warning(SAXParseException ex) throws SAXException {
        myLogger.warn(XmlExt.toString(ex), ex);
    }
}
