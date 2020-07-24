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
package org.jboss.hal.client.runtime.server;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.core.finder.ItemMonitor;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.server.Server;
import org.jboss.hal.core.runtime.server.ServerActionEvent;
import org.jboss.hal.core.runtime.server.ServerActionEvent.ServerActionHandler;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.core.runtime.server.ServerResultEvent;
import org.jboss.hal.core.runtime.server.ServerResultEvent.ServerResultHandler;
import org.jboss.hal.core.runtime.server.ServerSelectionEvent;
import org.jboss.hal.dmr.Composite;
import org.jboss.hal.dmr.CompositeResult;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.flow.Progress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.ManagementModel;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Column;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Requires;

import static java.util.Collections.singletonList;
import static org.jboss.hal.client.runtime.configurationchanges.ConfigurationChangesPresenter.HOST_CONFIGURATION_CHANGES_TEMPLATE;
import static org.jboss.hal.client.runtime.managementoperations.ManagementOperationsPresenter.MANAGEMENT_OPERATIONS_ADDRESS;
import static org.jboss.hal.client.runtime.server.StandaloneServerColumn.MANAGEMENT_ADDRESS;
import static org.jboss.hal.core.finder.FinderColumn.RefreshMode.RESTORE_SELECTION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

@Column(Ids.STANDALONE_SERVER_COLUMN)
@Requires(value = {"/", MANAGEMENT_ADDRESS, MANAGEMENT_OPERATIONS_ADDRESS},
        recursive = false)
public class StandaloneServerColumn extends FinderColumn<Server> implements ServerActionHandler, ServerResultHandler {

    static final String MANAGEMENT_ADDRESS = "/core-service=management";
    private static final AddressTemplate MANAGEMENT_TEMPLATE = AddressTemplate.of(MANAGEMENT_ADDRESS);

    private final Finder finder;
    private FinderPath refreshPath;

    @Inject
    public StandaloneServerColumn(Finder finder, EventBus eventBus, Dispatcher dispatcher,
            FinderPathFactory finderPathFactory, ItemActionFactory itemActionFactory,
            ServerActions serverActions, PlaceManager placeManager, Places places, @Footer Provider<Progress> progress,
            Resources resources, StatementContext statementContext) {
        super(new Builder<Server>(finder, Ids.STANDALONE_SERVER_COLUMN, Names.SERVER)

                .itemsProvider((context, callback) -> {
                    Operation attributes = new Operation.Builder(ResourceAddress.root(), READ_RESOURCE_OPERATION)
                            .param(INCLUDE_RUNTIME, true)
                            .param(ATTRIBUTES_ONLY, true)
                            .build();
                    Operation bootErrors = new Operation.Builder(ResourceAddress.root().add(CORE_SERVICE, MANAGEMENT),
                            READ_BOOT_ERRORS)
                            .build();
                    dispatcher.execute(new Composite(attributes, bootErrors), (CompositeResult result) -> {
                        Server.STANDALONE.addServerAttributes(result.step(0).get(RESULT));
                        Server.STANDALONE.setBootErrors(!result.step(1).get(RESULT).asList().isEmpty());
                        callback.onSuccess(singletonList(Server.STANDALONE));

                        // Restore pending servers visualization
                        if (serverActions.isPending(Server.STANDALONE)) {
                            ItemMonitor.startProgress(Server.STANDALONE.getId());
                        }
                    });
                })

                .onItemSelect(server -> eventBus.fireEvent(new ServerSelectionEvent(server.getName())))
                .onPreview(item -> new ServerPreview(serverActions, item, dispatcher, eventBus, progress,
                        statementContext, placeManager, places, finderPathFactory, resources))
        );

        this.finder = finder;
        eventBus.addHandler(ServerActionEvent.getType(), this);
        eventBus.addHandler(ServerResultEvent.getType(), this);

        setItemRenderer(item -> new ItemDisplay<Server>() {
            @Override
            public String getId() {
                return item.getId();
            }

            @Override
            public String getTitle() {
                return item.getName();
            }

            @Override
            public String getTooltip() {
                return new ServerTooltip(serverActions, resources).apply(item);
            }

            @Override
            public HTMLElement getIcon() {
                return new ServerIcon(serverActions).apply(item);
            }

            @Override
            public List<ItemAction<Server>> actions() {
                List<ItemAction<Server>> actions = new ArrayList<>();
                if (item.hasBootErrors()) {
                    PlaceRequest bootErrorsRequest = new PlaceRequest.Builder().nameToken(
                            NameTokens.SERVER_BOOT_ERRORS).build();
                    actions.add(itemActionFactory.placeRequest(Names.BOOT_ERRORS, bootErrorsRequest,
                            Constraint.executable(MANAGEMENT_TEMPLATE, READ_BOOT_ERRORS)));
                }
                PlaceRequest placeRequest = new PlaceRequest.Builder()
                        .nameToken(NameTokens.STANDALONE_SERVER).build();
                actions.add(itemActionFactory.viewAndMonitor(Ids.STANDALONE_SERVER_COLUMN, placeRequest));
                if (!serverActions.isPending(item)) {
                    // Order is: reload, restart, (resume | suspend)
                    actions.add(new ItemAction.Builder<Server>()
                            .title(resources.constants().reload())
                            .handler(serverActions::reload)
                            .constraint(Constraint.executable(AddressTemplate.ROOT, RELOAD))
                            .build());
                    actions.add(new ItemAction.Builder<Server>()
                            .title(resources.constants().restart())
                            .handler(serverActions::restart)
                            .constraint(Constraint.executable(AddressTemplate.ROOT, SHUTDOWN))
                            .build());
                    if (ManagementModel.supportsSuspend(item.getManagementVersion())) {
                        if (item.isSuspended()) {
                            actions.add(new ItemAction.Builder<Server>()
                                    .title(resources.constants().resume())
                                    .handler(serverActions::resume)
                                    .constraint(Constraint.executable(AddressTemplate.ROOT, RESUME))
                                    .build());
                        } else {
                            actions.add(new ItemAction.Builder<Server>()
                                    .title(resources.constants().suspend())
                                    .handler(serverActions::suspend)
                                    .constraint(Constraint.executable(AddressTemplate.ROOT, SUSPEND))
                                    .build());
                        }
                    }
                    actions.add(ItemAction.separator());
                    if (ManagementModel.supportsConfigurationChanges(item.getManagementVersion())) {
                        PlaceRequest ccPlaceRequest = new PlaceRequest.Builder()
                                .nameToken(NameTokens.CONFIGURATION_CHANGES).build();
                        actions.add(itemActionFactory.placeRequest(resources.constants().configurationChanges(),
                                ccPlaceRequest, Constraint.executable(HOST_CONFIGURATION_CHANGES_TEMPLATE, ADD)));
                    }
                    actions.add(new ItemAction.Builder<Server>()
                            .title(resources.constants().editURL())
                            .handler(itm -> serverActions.editUrl(itm, () -> refresh(RESTORE_SELECTION)))
                            .build());
                }
                return actions;
            }

            @Override
            public String nextColumn() {
                return Ids.RUNTIME_SUBSYSTEM;
            }
        });
    }

    @Override
    public void onServerAction(ServerActionEvent event) {
        if (isVisible()) {
            refreshPath = finder.getContext().getPath().copy();
            ItemMonitor.startProgress(event.getServer().getId());
            refresh(RESTORE_SELECTION);
        }
    }

    @Override
    public void onServerResult(ServerResultEvent event) {
        //noinspection Duplicates
        if (isVisible()) {
            ItemMonitor.stopProgress(event.getServer().getId());

            FinderPath path = refreshPath != null ? refreshPath : finder.getContext().getPath();
            refreshPath = null;
            finder.refresh(path);
        }
    }
}
