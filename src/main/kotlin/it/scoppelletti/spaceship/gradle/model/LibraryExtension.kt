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
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Extension object for the library plugins.
 *
 * @since 1.0.0
 *
 * @property url           URL of the project.
 * @property scmUrl        URL of the source control management.
 * @property inceptionYear Inception year.
 * @property logoUrl       URL of the logo.
 * @property developer     License.
 * @property license       Develper.
 */
public abstract class LibraryExtension @Inject constructor(
    objects: ObjectFactory
) {

    public abstract val url: Property<String>
    public abstract val scmUrl: Property<String>
    public abstract val inceptionYear: Property<String>

    public val logoUrl: Property<String> =
        objects.property(String::class.java).apply {
            convention("http://www.scoppelletti.it/spaceship/images/spaceship-logo/ic_launcher-web.png")
        }

    @get:Nested
    public abstract val developer: DeveloperModel

    @Suppress("WeakerAccess")
    public fun developer(action: Action<DeveloperModel>) {
        action.execute(developer)
    }

    @get:Nested
    public abstract val license: LicenseModel

    @Suppress("WeakerAccess")
    public fun license(action: Action<LicenseModel>) {
        action.execute(license)
    }

    override fun toString(): String =
        ToStringBuilder(this)
            .append("url", url.orNull)
            .append("scmUrl", scmUrl.orNull)
            .append("inceptionYear", inceptionYear.orNull)
            .append("logoUrl", logoUrl.orNull)
            .append("developer", developer)
            .append("license", license)
            .build()

    public companion object {

        /**
         * Name of this extension object.
         */
        public const val NAME: String = "spaceship"
    }
}