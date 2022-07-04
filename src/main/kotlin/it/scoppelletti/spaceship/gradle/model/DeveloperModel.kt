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
 * Developer.
 *
 * @since 1.0.0
 *
 * @property name  Name.
 * @property email Email.
 * @property url   Website.
 */
public abstract class DeveloperModel @Inject constructor(
    objects: ObjectFactory
) {

    public val name: Property<String> =
        objects.property(String::class.java).apply {
            convention("Dario Scoppelletti")
        }

    public val email: Property<String> =
        objects.property(String::class.java).apply {
            convention("dario@scoppelletti.it")
        }

    public val url: Property<String> =
        objects.property(String::class.java).apply {
            convention("http://www.scoppelletti.it")
        }

    override fun toString(): String =
        ToStringBuilder(this)
            .append("name", name.orNull)
            .append("email", email.orNull)
            .append("url", url.orNull)
            .build()
}