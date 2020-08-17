/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle;

import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven;
import org.jetbrains.dokka.gradle.DokkaPlugin;

/**
 * Tools for implementing plug-ins.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class ProjectTools {
    private static final Logger myLogger = Logging.getLogger(
            ProjectTools.class);
    private final Project myProject;

    /**
     * Constructor.
     *
     * @param project Project.
     */
    public ProjectTools(@Nonnull Project project) {
        myProject = Objects.requireNonNull(project,
                "Argument projects is null.");
    }

    /**
     * Indicates whether the generation of the KDoc documentation is enabled or
     * disabled.
     *
     * @return Returns {@code true} if the plug-in {@code DokkaPlugin} has been
     *         applied, {@code false} otherwise.
     */
    public boolean isKDocEnabled() {
        if (myProject.getPlugins().hasPlugin(DokkaPlugin.class)) {
            myLogger.info("Plugin {} applied.", DokkaPlugin.class);
            return true;
        }

        myLogger.info("Plugin {} not applied.", DokkaPlugin.class);
        return false;
    }

    /**
     * Applies the plug-in {@code MavenPublishPlugin}.
     *
     * @return URL of the development Maven repository. If the property
     *         {@code it.scoppelletti.tools.devRepoUrl} is not set, the plug-in
     *         will not be applied and the method returns {@code null}.
     */
    @Nullable
    public URI applyMavenPublish() {
        String url;

        if (!myProject.hasProperty(SpaceshipPlugin.PROP_DEVREPOURL)) {
            myLogger.info("Property {} not set.",
                    SpaceshipPlugin.PROP_DEVREPOURL);
            return null;
        }

        url = (String) myProject.property(
                SpaceshipPlugin.PROP_DEVREPOURL);
        myLogger.info("Property {}={}", SpaceshipPlugin.PROP_DEVREPOURL, url);
        if (StringUtils.isBlank(url)) {
            return null;
        }

        if (myProject.getPlugins().hasPlugin(MavenPublishPlugin.class)) {
            myLogger.info("Plugin {} already applied.",
                    MavenPublishPlugin.class);
        } else {
            myLogger.info("Applying plugin {}.", MavenPublishPlugin.class);
            myProject.getPluginManager().apply(MavenPublishPlugin.class);
        }

        return myProject.uri(url);
    }

    /**
     * Defines the Maven repository where to publish the artifacts.
     *
     * @param url      URL of the development Maven repository.
     * @param taskName Name of the build task on which all publishing tasks will
     *                 depend.
     */
    public void definePublishingRepo(@Nonnull URI url,
            @Nonnull String taskName) {
        PublishingExtension publishExt;

        Objects.requireNonNull(url, "Argument Url is null.");
        if (StringUtils.isBlank(taskName)) {
            throw new NullPointerException("Argument taskName is null.");
        }

        publishExt = Objects.requireNonNull(
                myProject.getExtensions().findByType(PublishingExtension.class),
                () -> String.format("Extension %1$s not found.",
                        PublishingExtension.class));

        publishExt.getRepositories().maven(repo -> {
            repo.setName(SpaceshipPlugin.REPO_DEV);
            repo.setUrl(url);
        });

        myProject.getTasks().all(task -> {
            if (task instanceof AbstractPublishToMaven) {
                task.dependsOn(taskName);
            }
        });
    }

    /**
     * Gets the Credit database file.
     *
     * @return File. If the property
     *         {@code it.scoppelletti.tools.credits.databaseUrl} is not set,
     *         returns {@code null}.
     */
    @Nullable
    public URI getCreditDatabaseUrl() {
        String url;

        if (!myProject.hasProperty(SpaceshipPlugin.PROP_CREDITDATABASEURL)) {
            myLogger.info("Property {} not set.",
                    SpaceshipPlugin.PROP_CREDITDATABASEURL);
            return null;
        }

        url = (String) myProject.property(
                SpaceshipPlugin.PROP_CREDITDATABASEURL);
        myLogger.info("Property {}={}", SpaceshipPlugin.PROP_CREDITDATABASEURL,
                url);
        if (StringUtils.isBlank(url)) {
            return null;
        }

        return myProject.uri(url);
    }

    /**
     * Gets the Credit database file.
     *
     * @return File. If the property
     *         {@code it.scoppelletti.tools.credits.databaseUrl} is not set,
     *         returns {@code null}.
     */
    @Nullable
    public String getCreditTemplateName() {
        String name;

        if (!myProject.hasProperty(SpaceshipPlugin.PROP_CREDITTEMPLATENAME)) {
            myLogger.info("Property {} not set.",
                    SpaceshipPlugin.PROP_CREDITTEMPLATENAME);
            return null;
        }

        name = (String) myProject.property(
                SpaceshipPlugin.PROP_CREDITTEMPLATENAME);
        myLogger.info("Property {}={}", SpaceshipPlugin.PROP_CREDITTEMPLATENAME,
                name);
        if (StringUtils.isBlank(name)) {
            return null;
        }

        return name;
    }
}
