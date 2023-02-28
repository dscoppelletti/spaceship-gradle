/*
 * Copyright (C) 2020-2022 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle

import groovy.lang.MissingPropertyException
import it.scoppelletti.spaceship.gradle.model.DeveloperModel
import it.scoppelletti.spaceship.gradle.model.LicenseModel
import it.scoppelletti.spaceship.gradle.model.LibraryExtension
import java.io.File
import java.util.Objects
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

private const val PROTOCOL_SCM = "scm:git:"

/**
 * Tools for publishing libraries.
 *
 * @since 1.0.0
 */
public class PublishTools private constructor(
    private val project: Project,
    private val publishExt: PublishingExtension
) {

    /**
     * Creates the Maven publication.
     *
     * @param  publName      Name of the publication.
     * @param  componentName Name of the Java component.
     * @return               The new object.
     */
    public fun createPublication(
        publName: String,
        componentName: String
    ): MavenPublication {
        val publ = publishExt.publications.create(publName,
            MavenPublication::class.java)

        publ.groupId = Objects.toString(project.group)
        publ.version = Objects.toString(project.version)

        project.extensions.findByType(BasePluginExtension::class.java)?.let {
            publ.artifactId = it.archivesName.get()
        } ?: run {
            project.logger.warn(
                "Extension ${BasePluginExtension::class.java} not found.")
        }

        project.components.findByName(componentName)?.let {
            publ.from(it)
        } ?: run {
            project.logger.warn("Software component $componentName not found.")
        }

        publ.pom(this::configurePom)
        return publ
    }

    /**
     * Configures the POM.
     */
    private fun configurePom(pom: MavenPom) {
        val spaceshipExt = project.extensions.findByType(
            LibraryExtension::class.java) ?: run {
            project.logger.warn(
                "Extension ${LibraryExtension::class.java} not found.")
            return
        }

        pom.name.set(project.rootProject.name)
        pom.description.set(project.description)
        pom.url.set(spaceshipExt.url)
        pom.inceptionYear.set(spaceshipExt.inceptionYear)

        pom.developers { developers ->
            developers.developer { dev ->
                configureDeveloper(dev, spaceshipExt.developer)
            }
        }

        pom.licenses { licenses ->
            licenses.license { lic ->
                configureLicense(lic, spaceshipExt.license)
            }
        }

        pom.scm { scm ->
            configureScm(scm, spaceshipExt)
        }
    }

    /**
     * Configures the POM element `developer`.
     *
     * @param pom       POM.
     * @param developer Developer.
     */
    private fun configureDeveloper(
        pom: MavenPomDeveloper,
        developer: DeveloperModel
    ) {
        pom.name.set(developer.name)
        pom.email.set(developer.email)
        pom.url.set(developer.url)
    }

    /**
     * Configures the POM element `license`.
     *
     * @param pom     POM.
     * @param license License.
     */
    private fun configureLicense(
        pom: MavenPomLicense,
        license: LicenseModel
    ) {
        pom.name.set(license.name)
        pom.url.set(license.url)
    }

    /**
     * Configures the POM element `scm`.
     *
     * @param pom POM.
     */
    private fun configureScm(
        pom: MavenPomScm,
        spaceshipExt: LibraryExtension
    ) {
        val scm = spaceshipExt.scmUrl.map {
            PROTOCOL_SCM.plus(it)
        }

        pom.connection.set(scm)
        pom.developerConnection.set(scm)
        pom.url.set(spaceshipExt.scmUrl)
    }

    /**
     * Creates the Maven repository where to publish the artifacts.
     *
     * If the property `it.scoppelletti.spaceship.repository.url` is not set,
     * the Maven repository is not created.
     */
    public fun createPublishingRepo() {
        val url = try {
            project.property(PROP_REPOURL) as String?
        } catch (ex: MissingPropertyException) {
            project.logger.info(
                "Property $PROP_REPOURL not set.")
            return
        }

        project.logger.info("Property $PROP_REPOURL=$url")
        if (url.isNullOrBlank()) {
            return
        }

        publishExt.repositories.maven { repo ->
            repo.name = REPO_NAME
            repo.url = project.uri(url)
        }
    }

    /**
     * Creates the Javadoc package task.
     *
     * @param  sourceDir Documentation folder.
     * @param  metaInf   META-INF resource folder.
     * @return           The task provider.
     */
    public fun createJavadocPackageTask(
        sourceDir: Property<File>,
        metaInf: File
    ): TaskProvider<Jar> =
        project.tasks.register(
            "generateJavadocPackage",
            Jar::class.java
        ) { task ->
            task.description = "Generates the Javadoc package"
            task.group = PublishingPlugin.PUBLISH_TASK_GROUP
            task.from(sourceDir)
            task.metaInf { spec -> spec.from(metaInf) }
            task.destinationDirectory.set(project.buildDir.resolve("libs"))
            task.archiveBaseName.set("javadoc")
            task.archiveVersion.set(project.version.toString())
            task.archiveClassifier.set(JarTools.CLASSIFIER_JAVADOC)
            task.archiveExtension.set(Jar.DEFAULT_EXTENSION)
        }

    public companion object {

        /**
         * Property containing the URL of the development Maven repository.
         */
        public const val PROP_REPOURL: String =
            "it.scoppelletti.spaceship.repository.url"

        /**
         * Name of the development Maven repository.
         */
        public const val REPO_NAME: String = "dev"

        /**
         * Creates a new instance.
         *
         * @param  project Project.
         * @return         The new object.
         */
        @JvmStatic
        public fun create(project: Project): PublishTools? {
            val publExt = project.extensions.findByType(
                PublishingExtension::class.java) ?: run {
                project.logger.info(
                    "Extension ${PublishingExtension::class.java} not found.")
                return null
            }

            return PublishTools(project, publExt)
        }
    }
}