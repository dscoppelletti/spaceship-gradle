/*
 * Copyright (C) 2019-2022 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.model

import javax.inject.Inject
import org.apache.commons.lang3.builder.ToStringBuilder
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * License.
 *
 * @since 1.0.0
 *
 * @property name Name.
 * @property url  URL.
 */
public abstract class LicenseModel @Inject constructor(objects: ObjectFactory) {

    public val name: Property<String> =
        objects.property(String::class.java).apply {
            convention("The Apache License, Version 2.0")
        }

    public val url: Property<String> =
        objects.property(String::class.java).apply {
            convention("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }

    override fun toString(): String =
        ToStringBuilder(this)
            .append("name", name.orNull)
            .append("url", url.orNull)
            .build()
}