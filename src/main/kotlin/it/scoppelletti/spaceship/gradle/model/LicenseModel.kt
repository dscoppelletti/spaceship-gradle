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
import org.gradle.api.provider.ProviderFactory

/**
 * License.
 *
 * @since 1.0.0
 *
 * @property name Name.
 * @property url  URL.
 */
public abstract class LicenseModel @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory
) {

    public val name: Property<String> =
        objects.property(String::class.java).apply {
            convention(providers.gradleProperty(PROP_NAME))
        }

    public val url: Property<String> =
        objects.property(String::class.java).apply {
            convention(providers.gradleProperty(PROP_URL))
        }

    override fun toString(): String =
        ToStringBuilder(this)
            .append("name", name.orNull)
            .append("url", url.orNull)
            .build()

    public companion object {

        /**
         * Property containing the name of the license.
         */
        public const val PROP_NAME: String =
            "it.scoppelletti.spaceship.license.name"

        /**
         * Property containing the URL of the license text.
         */
        public const val PROP_URL: String =
            "it.scoppelletti.spaceship.license.url"
    }
}