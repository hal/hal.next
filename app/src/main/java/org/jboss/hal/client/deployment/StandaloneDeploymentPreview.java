/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.client.deployment;

import org.jboss.hal.ballroom.Alert;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.deployment.Deployment;
import org.jboss.hal.core.deployment.Deployment.Status;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.finder.PreviewAttributes.PreviewAttribute;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Resources;

import static java.util.Arrays.asList;
import static org.jboss.hal.client.deployment.StandaloneDeploymentColumn.DEPLOYMENT_TEMPLATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEPLOY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RUNTIME_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.UNDEPLOY;

class StandaloneDeploymentPreview extends DeploymentPreview<Deployment> {

    static final String LAST_ENABLED_AT = "Last enabled at";
    static final String LAST_DISABLED_AT = "Last disabled at";

    StandaloneDeploymentPreview(final StandaloneDeploymentColumn column, final Deployment deployment,
            final Resources resources, final ServerActions serverActions, Environment environment) {
        super(deployment.getName(), serverActions, environment, deployment);

        if (deployment.getStatus() == Status.FAILED) {
            previewBuilder().add(new Alert(Icons.ERROR, resources.messages().deploymentFailed(deployment.getName())));
        } else if (deployment.getStatus() == Status.STOPPED) {
            previewBuilder().add(new Alert(Icons.STOPPED, resources.messages().deploymentStopped(deployment.getName()),
                    resources.constants().enable(), event -> column.enable(deployment),
                    Constraint.executable(DEPLOYMENT_TEMPLATE, DEPLOY)));
        } else if (deployment.getStatus() == Status.OK) {
            previewBuilder().add(new Alert(Icons.OK, resources.messages().deploymentActive(deployment.getName()),
                    resources.constants().disable(), event -> column.disable(deployment),
                    Constraint.executable(DEPLOYMENT_TEMPLATE, UNDEPLOY)));
        } else {
            if (deployment.isEnabled()) {
                previewBuilder().add(new Alert(Icons.OK, resources.messages().deploymentEnabled(deployment.getName()),
                        resources.constants().disable(), event -> column.disable(deployment),
                        Constraint.executable(DEPLOYMENT_TEMPLATE, UNDEPLOY)));
            } else {
                previewBuilder()
                        .add(new Alert(Icons.DISABLED, resources.messages().deploymentDisabled(deployment.getName()),
                                resources.constants().enable(), event -> column.enable(deployment),
                                Constraint.executable(DEPLOYMENT_TEMPLATE, DEPLOY)));
            }
        }

        PreviewAttributes<Deployment> attributes = new PreviewAttributes<>(deployment, asList(NAME, RUNTIME_NAME));
        contextRoot(attributes, deployment);
        hash(attributes, deployment);
        eme(attributes);
        status(attributes, deployment);
        attributes.append(model -> new PreviewAttribute(LAST_ENABLED_AT, deployment.getEnabledTime()));
        attributes.append(model -> new PreviewAttribute(LAST_DISABLED_AT, deployment.getDisabledTime()));
        previewBuilder().addAll(attributes);

        // sub-deployments
        if (deployment.hasSubdeployments()) {
            subDeployments(deployment);
        }
    }
}
