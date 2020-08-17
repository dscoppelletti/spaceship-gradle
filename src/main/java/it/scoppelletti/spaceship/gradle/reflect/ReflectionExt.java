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

package it.scoppelletti.spaceship.gradle.reflect;

import java.io.InputStream;

/**
 * Reflection extensions.
 *
 * @since 1.0.0
 */
public final class ReflectionExt {

    private ReflectionExt() {
    }

    /**
     * Open the input stream for reading a resource.
     *
     * @param  name Resource name.
     * @return      Stream. If the resource is not found, returns {@code null}.
     */
    public static InputStream getResourceAsStream(String name) {
        StreamFinder finder;

        finder = new StreamFinder(name);
        return finder.findResource();
    }
}
