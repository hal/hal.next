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
package org.jboss.hal.client.runtime.subsystem.messaging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.hal.ballroom.dialog.DialogFactory;
import org.jboss.hal.client.runtime.subsystem.messaging.Destination.Type;
import org.jboss.hal.core.finder.ColumnActionFactory;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.FinderSegment;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.core.finder.ItemsProvider;
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
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.resources.Strings;
import org.jboss.hal.spi.AsyncColumn;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.jboss.hal.spi.Requires;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.jboss.hal.client.runtime.subsystem.messaging.AddressTemplates.*;
import static org.jboss.hal.client.runtime.subsystem.messaging.Destination.Type.DEPLOYMENT_RESOURCES;
import static org.jboss.hal.client.runtime.subsystem.messaging.Destination.Type.SUBSYSTEM_RESOURCES;
import static org.jboss.hal.core.finder.FinderColumn.RefreshMode.RESTORE_SELECTION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.resources.CSS.fontAwesome;

@AsyncColumn(Ids.MESSAGING_SERVER_DESTINATION_RUNTIME)
@Requires({MESSAGING_CORE_QUEUE_ADDRESS,
        MESSAGING_JMS_QUEUE_ADDRESS,
        MESSAGING_JMS_TOPIC_ADDRESS,
        MESSAGING_DEPLOYMENT_JMS_QUEUE_ADDRESS,
        MESSAGING_DEPLOYMENT_JMS_TOPIC_ADDRESS})
public class DestinationColumn extends FinderColumn<Destination> {

    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private final Resources resources;

    @Inject
    public DestinationColumn(Finder finder,
            ColumnActionFactory columnActionFactory,
            ItemActionFactory itemActionFactory,
            FinderPathFactory finderPathFactory,
            Places places,
            Dispatcher dispatcher,
            EventBus eventBus,
            StatementContext statementContext,
            Resources resources) {

        super(new Builder<Destination>(finder, Ids.MESSAGING_SERVER_DESTINATION_RUNTIME, Names.DESTINATION)
                .columnAction(columnActionFactory.refresh(Ids.MESSAGING_SERVER_DESTINATION_REFRESH))
                .onPreview(item -> new DestinationPreview(item, finderPathFactory, places, dispatcher, resources))
                .useFirstActionAsBreadcrumbHandler()
                .pinnable()
                .showCount()
                .filterDescription(resources.messages().destinationFilterDescription())
                .withFilter());

        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.resources = resources;

        ItemsProvider<Destination> itemsProvider = (context, callback) -> {
            // extract server name from the finder path
            FinderSegment segment = context.getPath().findColumn(Ids.MESSAGING_SERVER_RUNTIME);
            if (segment != null) {
                String server = segment.getItemTitle();
                List<Operation> operations = new ArrayList<>();
                for (Type type : SUBSYSTEM_RESOURCES) {
                    ResourceAddress address = MESSAGING_SERVER_TEMPLATE.append(type.resource + "=*")
                            .resolve(statementContext, server);
                    operations.add(new Operation.Builder(address, READ_RESOURCE_OPERATION)
                            .param(INCLUDE_RUNTIME, true)
                            .build());
                }
                for (Type type : DEPLOYMENT_RESOURCES) {
                    ResourceAddress address = MESSAGING_DEPLOYMENT_TEMPLATE.append(type.resource + "=*")
                            .resolve(statementContext);
                    operations.add(new Operation.Builder(address, READ_RESOURCE_OPERATION)
                            .param(INCLUDE_RUNTIME, true)
                            .build());
                }
                dispatcher.execute(new Composite(operations), (CompositeResult result) -> {
                    List<Destination> destinations = new ArrayList<>();
                    for (ModelNode step : result) {
                        if (!step.isFailure()) {
                            for (ModelNode node : step.get(RESULT).asList()) {
                                AddressTemplate template = AddressTemplate.of(new ResourceAddress(node.get(ADDRESS)));
                                if (!template.firstName().equals(HOST)) {
                                    //Add correct host and server before the messaging address if it is missing
                                    template = SELECTED_HOST_SELECTED_SERVER_TEMPLATE.append(template);
                                }
                                destinations.add(new Destination(template.resolve(statementContext), node.get(RESULT)));
                            }
                        }
                    }
                    destinations.sort(Comparator.comparing(NamedNode::getName));
                    callback.onSuccess(destinations);
                });
            } else {
                callback.onSuccess(emptyList());
            }
        };
        setItemsProvider(itemsProvider);
        setBreadcrumbItemsProvider(
                (context, callback) -> itemsProvider.get(context, new AsyncCallback<List<Destination>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(List<Destination> result) {
                        callback.onSuccess(result.stream()
                                .filter(d -> d.type == Type.QUEUE || d.type == Type.JMS_QUEUE)
                                .collect(toList()));
                    }
                }));

        setItemRenderer(item -> new ItemDisplay<Destination>() {
            @Override
            public String getId() {
                return Ids.destination(item.getDeployment(), item.getSubdeployment(), messageServer(), item.type.name(),
                        item.getName());
            }

            @Override
            public String getTitle() {
                return item.getName();
            }

            @Override
            public HTMLElement element() {
                if (item.fromDeployment()) {
                    return ItemDisplay.withSubtitle(item.getName(), item.getPath());
                }
                return null;
            }

            @Override
            public String getTooltip() {
                return item.type.type;
            }

            @Override
            public HTMLElement getIcon() {
                if (item.isPaused()) {
                    return Icons.paused();
                } else {
                    switch (item.type) {
                        case JMS_QUEUE:
                            return Icons.custom(fontAwesome("long-arrow-right"));
                        case JMS_TOPIC:
                            return Icons.custom(fontAwesome("arrows"));
                        case QUEUE:
                            return Icons.custom(fontAwesome("cog"));
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
            public List<ItemAction<Destination>> actions() {
                List<ItemAction<Destination>> actions = new ArrayList<>();

                if (item.type == Type.JMS_QUEUE || item.type == Type.QUEUE) {
                    PlaceRequest.Builder builder = places.selectedServer(item.type.token);
                    if (item.fromDeployment()) {
                        builder.with(DEPLOYMENT, item.getDeployment());
                        if (item.getSubdeployment() != null) {
                            builder.with(SUBDEPLOYMENT, item.getSubdeployment());
                        }
                    }
                    builder.with(Ids.MESSAGING_SERVER, messageServer()).with(NAME, item.getName());
                    actions.add(itemActionFactory.view(builder.build()));
                } else if (item.type == Type.JMS_TOPIC) {
                    actions.add(new ItemAction.Builder<Destination>()
                            .title(Strings.abbreviateMiddle(resources.constants().dropSubscriptions(), 16))
                            .constraint(Constraint.executable(item.template(), DROP_ALL_SUBSCRIPTIONS))
                            .handler(DestinationColumn.this::dropSubscriptions)
                            .build());
                }

                if (item.isPaused()) {
                    actions.add(new ItemAction.Builder<Destination>()
                            .title(resources.constants().resume())
                            .constraint(Constraint.executable(item.template(), RESUME))
                            .handler(DestinationColumn.this::resume)
                            .build());
                } else {
                    actions.add(new ItemAction.Builder<Destination>()
                            .title(resources.constants().pause())
                            .constraint(Constraint.executable(item.template(), PAUSE))
                            .handler(DestinationColumn.this::pause)
                            .build());
                }

                return actions;
            }
        });
    }

    private String messageServer() {
        String server = UNDEFINED;
        FinderSegment<?> segment = getFinder().getContext().getPath().findColumn(Ids.MESSAGING_SERVER_RUNTIME);
        if (segment != null) {
            server = Ids.extractMessagingServer(segment.getItemId());
        }
        return server;
    }

    private void resume(Destination destination) {
        Operation operation = new Operation.Builder(destination.getAddress(), RESUME).build();
        dispatcher.execute(operation, result -> {
            refresh(RESTORE_SELECTION);
            MessageEvent.fire(eventBus,
                    Message.success(resources.messages().resumeQueueSuccess(destination.getName())));
        });
    }

    private void pause(Destination destination) {
        Operation operation = new Operation.Builder(destination.getAddress(), PAUSE).build();
        dispatcher.execute(operation, result -> {
            refresh(RESTORE_SELECTION);
            MessageEvent.fire(eventBus, Message.success(resources.messages().pauseQueueSuccess(destination.getName())));
        });
    }

    private void dropSubscriptions(Destination destination) {
        DialogFactory.showConfirmation(resources.constants().dropSubscriptions(),
                resources.messages().dropSubscriptionsQuestion(destination.getName()), () -> {
                    Operation operation = new Operation.Builder(destination.getAddress(), DROP_ALL_SUBSCRIPTIONS)
                            .build();
                    dispatcher.execute(operation, result -> {
                        refresh(RESTORE_SELECTION);
                        MessageEvent.fire(eventBus,
                                Message.success(resources.messages().dropSubscriptionsSuccess(destination.getName())));
                    });
                });
    }
}
