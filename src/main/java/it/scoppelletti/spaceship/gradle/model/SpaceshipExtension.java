/*
 * Copyright (C) 2019-2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * Extension object for the plug-in {@code SpaceshipPlugin}.
 *
 * @see   it.scoppelletti.spaceship.gradle.SpaceshipPlugin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SpaceshipExtension {

    /**
     * Name of this extension object.
     */
    public static final String NAME = "spaceship";

    private final Project myProject;

    /**
     * Developer.
     */
    @Getter
    @Nonnull
    private final DeveloperModel developer;

    /**
     * License.
     */
    @Getter
    @Nonnull
    private final LicenseModel license;

    /**
     * URL of the project.
     */
    @Getter
    @Setter
    @Nullable
    private String url;

    /**
     * URL of the source control management.
     */
    @Getter
    @Setter
    @Nullable
    private String scmUrl;

    /**
     * Inception year.
     */
    @Getter
    @Setter
    @Nullable
    private String inceptionYear;

    private final Provider<ITemplateEngine> myTemplateEngine;

    /**
     * Constructor.
     */
    @Inject
    public SpaceshipExtension(@Nonnull Project project,
            @Nonnull ObjectFactory objectFactory) {
        myProject = Objects.requireNonNull(project,
                "Argument projects is null.");
        developer = objectFactory.newInstance(DeveloperModel.class);
        license = objectFactory.newInstance(LicenseModel.class);
        myTemplateEngine = project.provider(this::initTemplateEngine);
    }

    @Nonnull
    private ITemplateEngine initTemplateEngine() {
        TemplateEngine engine;
        AbstractConfigurableTemplateResolver resolver;

        engine = new TemplateEngine();

        resolver = new FileTemplateResolver();
        resolver.setCheckExistence(true);
        resolver.setTemplateMode(TemplateMode.HTML);
        engine.addTemplateResolver(resolver);

        resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCheckExistence(true);
        engine.addTemplateResolver(resolver);

        resolver = new UrlTemplateResolver();
        resolver.setCheckExistence(true);
        resolver.setTemplateMode(TemplateMode.HTML);
        engine.addTemplateResolver(resolver);

        return engine;
    }

    /**
     * Gets the project description.
     *
     * @return Value.
     */
    public String getDescription() {
        if (StringUtils.isNotBlank(myProject.getDescription())) {
            return myProject.getDescription();
        }

        return myProject.getName();
    }

    /**
     * Configures the developer.
     *
     * @param action Configurator.
     */
    public void developer(@Nonnull Action<DeveloperModel> action) {
        action.execute(developer);
    }

    /**
     * Configures the license.
     *
     * @param action Configurator.
     */
    public void license(@Nonnull Action<LicenseModel> action) {
        action.execute(license);
    }

    @Nonnull
    public ITemplateEngine getTemplateEngine() {
        return myTemplateEngine.get();
    }
}
