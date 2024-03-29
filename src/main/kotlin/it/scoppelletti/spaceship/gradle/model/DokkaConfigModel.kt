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

import com.squareup.moshi.JsonClass

/**
 * Dokka configuration model.
 *
 * @since 1.0.0
 *
 * @property footerMessage     Footer.
 * @property customStyleSheets List of custum style sheet files.
 */
@JsonClass(generateAdapter = true)
public data class DokkaConfigModel(
    public val footerMessage: String? = null,
    public val customStyleSheets: List<String>? = null
)