/*
 * Copyright (C) 2010-2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.reflect;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Resource finder.
 */
final class StreamFinder implements PrivilegedAction<InputStream> {
    private static final Logger myLogger = Logging.getLogger(
            ReflectionExt.class);
    private final String myResName;

    /**
     * Constructor.
     *
     * @param resName Resource name.
     */
    StreamFinder(@Nonnull String resName) {
        if (StringUtils.isBlank(resName)) {
            throw new NullPointerException("Argument resName is null.");
        }

        myResName = resName;
    }

    /**
     * Finds a resource.
     *
     * @return Resource. If the resource is not found, returns {@code null}.
     */
    @Nullable
    InputStream findResource() {
        InputStream res;

        res = AccessController.doPrivileged(this);
        if (res == null) {
            myLogger.debug("Resource {} not found.", myResName);
            return null;
        }

        return res;
    }

    @Override
    public InputStream run() {
        InputStream res;
        ClassLoader loader;

        // Class-loader from current thread context
        loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            res = loader.getResourceAsStream(myResName);
            if (res != null) {
                myLogger.debug("Resource {} found by class-loader {}.",
                        myResName,
                        "Thread.currentThread().getContextClassLoader()");
                return res;
            }
        }

        // Class-loader that loaded ReflectionExt class.
        loader = ReflectionExt.class.getClassLoader();
        if (loader != null) {
            res = loader.getResourceAsStream(myResName);
            if (res != null) {
                myLogger.debug("Resource {} found by class-loader {}.",
                        myResName, "ReflectionExt.class.getClassLoader()");
                return res;
            }
        }

        // System class-loader
        loader = ClassLoader.getSystemClassLoader();
        res = loader.getResourceAsStream(myResName);
        if (res != null) {
            myLogger.debug("Resource {} found by class-loader {}.", myResName,
                    "ClassLoader.getSystemClassLoader()");
            return res;
        }

        return null;
    }
}
