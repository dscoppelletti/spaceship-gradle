/*
 * Copyright (C) 2022 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

/**
 * Notice file model.
 *
 * @since 1.0.0
 *
 * @property projectTitle  Title of the project.
 * @property developerName Name of the developer.
 * @property developerUrl  URL of the developer website.
 * @property inceptionYear Inception year.
 */
public data class NoticeModel(
    public val projectTitle: String?,
    public val developerName: String? = null,
    public val developerUrl: String? = null,
    public val inceptionYear: String? = null
)
