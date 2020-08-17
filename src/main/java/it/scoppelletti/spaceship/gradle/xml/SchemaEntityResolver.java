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
import java.io.InputStream;
import it.scoppelletti.spaceship.gradle.reflect.ReflectionExt;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML schema resolver.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class SchemaEntityResolver implements EntityResolver {

    /**
     * XML schema file extension.
     */
    public static final String SCHEMA_EXT = ".xsd";

    /**
     * Prefix to recognize the URIs of the XML schemas resolvable by this class.
     */
    public static final String URI_PREFIX = "http://www.scoppelletti.it/res/";

    /**
     * Prefix to find the resources corresponding to XML schemas.
     */
    public static final String RESOURCE_PREFIX = "it/scoppelletti/";

    private final String myPath;
    private final int myVerMin;
    private final int myVerMax;

    /**
     * Constructor.
     *
     * @param path       Schema path.
     * @param versionMin Minimum version number.
     * @param versionMax Maximum version number.
     */
    public SchemaEntityResolver(String path, int versionMin, int versionMax) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("Argument path is null");
        }
        if (versionMax < 1) {
            throw new IllegalArgumentException(
                    "Argument varsionMax must be >= 1.");
        }
        if (versionMin < 1 || versionMin > versionMax) {
            throw new IllegalArgumentException(String.format(
                    "Argument versionMin must be in [1, %1$d]", versionMax));
        }

        myPath = path;
        myVerMin = versionMin;
        myVerMax = versionMax;
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public InputSource resolveEntity(String publicId, String systemId) throws
            SAXException, IOException {
        String name;
        InputStream in;

        name = toResourceName(systemId);
        if (name == null) {
            throw new SAXException(String.format(
                    "Bad schema (publicId=%1$s, systemId=%2$s).", publicId,
                    systemId));
        }

        in = ReflectionExt.getResourceAsStream(name);
        if (in == null) {
            throw new SAXException(String.format(
                    "Resource name %1$s not found.", name));
        }

        return new InputSource(in);
    }

    /**
     * Converts a system identifier to the corresponding resource name.
     *
     * @param  systemId Sistem identifier.
     * @return          Resource name. If the system identifier is not
     *                  compliant, returns {@code null}.
     */
    private String toResourceName(String systemId) {
        int ver;
        String name, uriPrefix, value;

        if (systemId == null) {
            return null;
        }

        uriPrefix = SchemaEntityResolver.URI_PREFIX.concat(myPath);
        if (!systemId.startsWith(uriPrefix) ||
                !systemId.endsWith(SchemaEntityResolver.SCHEMA_EXT)) {
            return null;
        }

        value = systemId.substring(uriPrefix.length(), systemId.length() -
                SchemaEntityResolver.SCHEMA_EXT.length());
        try {
            ver = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
        if (ver < myVerMin || ver > myVerMax) {
            return null;
        }

        name = SchemaEntityResolver.RESOURCE_PREFIX.concat(myPath)
                .concat(Integer.toString(ver))
                .concat(SchemaEntityResolver.SCHEMA_EXT);

        return name;
    }
}
