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

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.Alert;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.deployment.Deployment;
import org.jboss.hal.core.deployment.Deployment.Status;
import org.jboss.hal.core.deployment.ServerGroupDeployment;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.finder.PreviewAttributes.PreviewAttribute;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.*;

import static java.util.Arrays.asList;
import static org.jboss.hal.client.deployment.ServerGroupDeploymentColumn.SERVER_GROUP_DEPLOYMENT_TEMPLATE;
import static org.jboss.hal.client.deployment.StandaloneDeploymentPreview.LAST_DISABLED_AT;
import static org.jboss.hal.client.deployment.StandaloneDeploymentPreview.LAST_ENABLED_AT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

class ServerGroupDeploymentPreview extends DeploymentPreview<ServerGroupDeployment> {

    ServerGroupDeploymentPreview(ServerGroupDeploymentColumn column, ServerGroupDeployment sgd, Places places,
            Resources resources, ServerActions serverActions, Environment environment) {
        super(sgd.getName(), serverActions, environment, sgd.getDeployment());

        Deployment deployment = sgd.getDeployment();
        if (deployment != null) {
            String referenceServerMsg = resources.messages()
                    .referenceServer(sgd.getDeployment().getReferenceServer().getName());
            if (deployment.getStatus() == Status.FAILED) {
                previewBuilder().add(
                        new Alert(Icons.ERROR, resources.messages().deploymentFailed(sgd.getName()),
                                referenceServerMsg));
            } else if (deployment.getStatus() == Status.STOPPED) {
                previewBuilder().add(new Alert(Icons.STOPPED, resources.messages().deploymentStopped(sgd.getName()),
                        referenceServerMsg,
                        resources.constants().enable(), event -> column.enable(sgd),
                        Constraint.executable(SERVER_GROUP_DEPLOYMENT_TEMPLATE, DEPLOY)));
            } else if (deployment.getStatus() == Status.OK) {
                previewBuilder().add(
                        new Alert(Icons.OK, resources.messages().deploymentActive(sgd.getName()), referenceServerMsg,
                                resources.constants().disable(), event -> column.disable(sgd),
                                Constraint.executable(SERVER_GROUP_DEPLOYMENT_TEMPLATE, UNDEPLOY)));
            } else {
                previewBuilder()
                        .add(new Alert(Icons.UNKNOWN, resources.messages().deploymentUnknownState(sgd.getName()),
                                referenceServerMsg, resources.constants().disable(), event -> column.disable(sgd),
                                Constraint.executable(SERVER_GROUP_DEPLOYMENT_TEMPLATE, UNDEPLOY)));
            }
        } else {
            if (sgd.isEnabled()) {
                previewBuilder().add(new Alert(Icons.OK, resources.messages().deploymentEnabled(sgd.getName()),
                        resources.constants().disable(), event -> column.disable(sgd),
                        Constraint.executable(SERVER_GROUP_DEPLOYMENT_TEMPLATE, UNDEPLOY)));
            } else {
                previewBuilder()
                        .add(new Alert(Icons.DISABLED, resources.messages().deploymentDisabled(sgd.getName()),
                                resources.constants().enable(), event -> column.enable(sgd),
                                Constraint.executable(SERVER_GROUP_DEPLOYMENT_TEMPLATE, DEPLOY)));
            }
        }

        // main attributes
        PreviewAttributes<ServerGroupDeployment> attributes = new PreviewAttributes<>(sgd,
                asList(NAME, RUNTIME_NAME));
        if (deployment != null) {
            contextRoot(attributes, deployment);
        }
        attributes.append(model -> {
            PlaceRequest placeRequest = places.finderPlace(NameTokens.DEPLOYMENTS, new FinderPath()
                    .append(Ids.DEPLOYMENT_BROWSE_BY, Ids.asId(resources.constants().contentRepository()))
                    .append(Ids.CONTENT, Strings.sanitize(Ids.content(model.getName()))))
                    .build();
            return new PreviewAttribute(resources.constants().providedBy(), model.getName(),
                    places.historyToken(placeRequest));
        });
        if (deployment != null) {
            hash(attributes, deployment);
        }
        eme(attributes);
        if (deployment != null) {
            status(attributes, deployment);
            attributes.append(model -> new PreviewAttribute(LAST_ENABLED_AT, deployment.getEnabledTime()));
            attributes.append(model -> new PreviewAttribute(LAST_DISABLED_AT, deployment.getDisabledTime()));
        }
        previewBuilder().addAll(attributes);

        // sub-deployments
        if (deployment != null && deployment.hasSubdeployments()) {
            subDeployments(deployment);
        }

        // reference server
        if (deployment == null) {
            previewBuilder().add(Elements.h(2).textContent(resources.constants().noReferenceServer()));
            String serverGroup = sgd.getServerGroup();
            PlaceRequest serverGroupPlaceRequest = places.finderPlace(NameTokens.RUNTIME, new FinderPath()
                    .append(Ids.DOMAIN_BROWSE_BY, Ids.asId(Names.SERVER_GROUPS))
                    .append(Ids.SERVER_GROUP, Ids.serverGroup(serverGroup)))
                    .build();
            String serverGroupHistoryToken = places.historyToken(serverGroupPlaceRequest);
            LabelBuilder labelBuilder = new LabelBuilder();
            previewBuilder().add(Elements.p().innerHtml(resources.messages().noReferenceServerPreview(sgd.getName(),
                    labelBuilder.label(STATUS), labelBuilder.label(LAST_ENABLED_AT),
                    serverGroup, serverGroupHistoryToken)));
        }
    }
}
