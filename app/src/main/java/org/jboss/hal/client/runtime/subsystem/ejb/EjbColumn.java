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
package org.jboss.hal.client.runtime.subsystem.ejb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.dmr.Composite;
import org.jboss.hal.dmr.CompositeResult;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.AsyncColumn;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.jboss.hal.spi.Requires;

import static org.jboss.hal.client.runtime.subsystem.ejb.AddressTemplates.EJB3_DEPLOYMENT_ADDRESS;
import static org.jboss.hal.client.runtime.subsystem.ejb.AddressTemplates.EJB3_DEPLOYMENT_TEMPLATE;
import static org.jboss.hal.client.runtime.subsystem.ejb.AddressTemplates.EJB3_SUBDEPLOYMENT_TEMPLATE;
import static org.jboss.hal.client.runtime.subsystem.ejb.AddressTemplates.ejbDeploymentTemplate;
import static org.jboss.hal.client.runtime.subsystem.ejb.EjbNode.Type.MDB;
import static org.jboss.hal.core.finder.FinderColumn.RefreshMode.RESTORE_SELECTION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.dmr.ModelNodeHelper.failSafeList;
import static org.jboss.hal.resources.CSS.fontAwesome;
import static org.jboss.hal.resources.CSS.pfIcon;

@AsyncColumn(Ids.EJB3)
@Requires(EJB3_DEPLOYMENT_ADDRESS)
public class EjbColumn extends FinderColumn<EjbNode> {

    private final EventBus eventBus;
    private final Dispatcher dispatcher;
    private final Resources resources;

    @Inject
    public EjbColumn(Finder finder,
            FinderPathFactory finderPathFactory,
            ItemActionFactory itemActionFactory,
            EventBus eventBus,
            Places places,
            Dispatcher dispatcher,
            StatementContext statementContext,
            Resources resources) {
        super(new Builder<EjbNode>(finder, Ids.EJB3, Names.EJB3)

                .itemsProvider((context, callback) -> {
                    ResourceAddress baseAddress = AddressTemplate.of("{selected.host}/{selected.server}").resolve(statementContext);
                    Composite composite = new Composite(baseAddress);

                    AddressTemplate[] templates = new AddressTemplate[]{
                            EJB3_DEPLOYMENT_TEMPLATE,
                            EJB3_SUBDEPLOYMENT_TEMPLATE
                    };
                    for (EjbNode.Type type : EjbNode.Type.values()) {
                        for (AddressTemplate template : templates) {
                            ResourceAddress deploymentAddress = template.subTemplate(2,template.size())
                                    .append(type.resource + "=*")
                                    .resolve(statementContext);
                            composite.add(new Operation.Builder(deploymentAddress, READ_RESOURCE_OPERATION)
                                    .param(INCLUDE_RUNTIME, true)
                                    .param(RECURSIVE, true)
                                    .build());
                        }
                    }

                    dispatcher.execute(composite, (CompositeResult result) -> {
                        List<EjbNode> ejbs = new ArrayList<>();
                        for (ModelNode step : result) {
                            if (!step.isFailure()) {
                                for (ModelNode node : step.get(RESULT).asList()) {
                                    final ResourceAddress ejbAddress = new ResourceAddress(node.get(ADDRESS));
                                    ResourceAddress address = baseAddress.isDefined() ? new ResourceAddress().add(baseAddress).add(ejbAddress) : ejbAddress;

                                    ejbs.add(new EjbNode(address, node.get(RESULT)));
                                }
                            }
                        }
                        ejbs.sort(Comparator.comparing(NamedNode::getName));
                        callback.onSuccess(ejbs);
                    });
                })
                .onPreview(item -> new EjbPreview(item, finderPathFactory, places, dispatcher, resources))
                .useFirstActionAsBreadcrumbHandler()
                .withFilter()
                .filterDescription(resources.messages().ejbFilterDescription())
                .showCount()
                .pinnable());

        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        this.resources = resources;

        setItemRenderer(item -> new ItemDisplay<EjbNode>() {
            @Override
            public String getId() {
                return Ids.ejb3(item.getDeployment(), item.getSubdeployment(), item.type.name(), item.getName());
            }

            @Override
            public String getTitle() {
                return item.getName();
            }

            @Override
            public HTMLElement element() {
                return ItemDisplay.withSubtitle(item.getName(), item.getPath());
            }

            @Override
            public String getTooltip() {
                String tt = item.type.type;
                if (item.type == MDB && !item.isDeliveryActive()) {
                    tt += " (" + resources.constants().inactive() + ")";
                }
                return tt;
            }

            @Override
            public HTMLElement getIcon() {
                if (hasTimer(item)) {
                    return Icons.custom(pfIcon("history"));
                } else {
                    switch (item.type) {
                        case MDB:
                            return item.isDeliveryActive()
                                    ? Icons.custom(fontAwesome("exchange"))
                                    : Icons.paused();
                        case SINGLETON:
                            return Icons.custom(fontAwesome("cube"));
                        case STATEFUL:
                            return Icons.custom(fontAwesome("file-text-o"));
                        case STATELESS:
                            return Icons.custom(fontAwesome("file-o"));
                        default:
                            return Icons.unknown();
                    }
                }
            }

            @Override
            public String getFilterData() {
                return item.getName() + " " + item.type.type +
                        (item.fromDeployment() ? " " + Names.DEPLOYMENT : "");
            }

            @Override
            public List<ItemAction<EjbNode>> actions() {
                List<ItemAction<EjbNode>> actions = new ArrayList<>();
                PlaceRequest.Builder builder = places.selectedServer(NameTokens.EJB3_RUNTIME)
                        .with(DEPLOYMENT, item.getDeployment());
                if (item.getSubdeployment() != null) {
                    builder.with(SUBDEPLOYMENT, item.getSubdeployment());
                }
                PlaceRequest placeRequest = builder
                        .with(TYPE, item.type.name().toLowerCase())
                        .with(NAME, item.getName())
                        .build();
                actions.add(itemActionFactory.view(placeRequest));
                if (item.type == MDB) {
                    if (item.get(DELIVERY_ACTIVE).asBoolean()) {
                        actions.add(new ItemAction.Builder<EjbNode>()
                                .title(resources.constants().stopDelivery())
                                .constraint(Constraint.executable(ejbDeploymentTemplate(MDB), STOP_DELIVERY))
                                .handler(EjbColumn.this::stopDelivery)
                                .build());
                    } else {
                        actions.add(new ItemAction.Builder<EjbNode>()
                                .title(resources.constants().startDelivery())
                                .constraint(Constraint.executable(ejbDeploymentTemplate(MDB), START_DELIVERY))
                                .handler(EjbColumn.this::startDelivery)
                                .build());
                    }
                }
                return actions;
            }
        });
    }

    private void startDelivery(EjbNode ejb) {
        Operation operation = new Operation.Builder(ejb.getAddress(), START_DELIVERY).build();
        dispatcher.execute(operation, result -> {
            refresh(RESTORE_SELECTION);
            MessageEvent.fire(eventBus, Message.success(resources.messages().startDeliverySuccess(ejb.getName())));
        });
    }

    private void stopDelivery(EjbNode ejb) {
        Operation operation = new Operation.Builder(ejb.getAddress(), STOP_DELIVERY).build();
        dispatcher.execute(operation, result -> {
            refresh(RESTORE_SELECTION);
            MessageEvent.fire(eventBus, Message.success(resources.messages().stopDeliverySuccess(ejb.getName())));
        });
    }

    private boolean hasTimer(EjbNode ejb) {
        return !failSafeList(ejb, TIMERS).isEmpty();
    }
}
