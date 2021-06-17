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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Iterables;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.hal.ballroom.form.SingleSelectBoxItem;
import org.jboss.hal.ballroom.form.TextBoxItem;
import org.jboss.hal.client.runtime.BrowseByColumn;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.core.configuration.ProfileSelectionEvent;
import org.jboss.hal.core.finder.ColumnAction;
import org.jboss.hal.core.finder.ColumnActionFactory;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.FinderSegment;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.core.finder.ItemMonitor;
import org.jboss.hal.core.finder.ItemsProvider;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.dialog.NameItem;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.TopologyTasks;
import org.jboss.hal.core.runtime.group.ServerGroupSelectionEvent;
import org.jboss.hal.core.runtime.host.HostSelectionEvent;
import org.jboss.hal.core.runtime.server.Server;
import org.jboss.hal.core.runtime.server.ServerActionEvent;
import org.jboss.hal.core.runtime.server.ServerActionEvent.ServerActionHandler;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.core.runtime.server.ServerResultEvent;
import org.jboss.hal.core.runtime.server.ServerResultEvent.ServerResultHandler;
import org.jboss.hal.core.runtime.server.ServerSelectionEvent;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.ModelNodeHelper;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.flow.FlowContext;
import org.jboss.hal.flow.Outcome;
import org.jboss.hal.flow.Progress;
import org.jboss.hal.flow.Task;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.ManagementModel;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.MetadataRegistry;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.processing.MetadataProcessor;
import org.jboss.hal.meta.processing.SuccessfulMetadataCallback;
import org.jboss.hal.meta.security.AuthorisationDecision;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.security.ElementGuard;
import org.jboss.hal.meta.security.SecurityContextRegistry;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Column;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Requires;

import static elemental2.dom.DomGlobal.document;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.jboss.hal.client.runtime.configurationchanges.ConfigurationChangesPresenter.CONFIGURATION_CHANGES_ADDRESS;
import static org.jboss.hal.core.finder.FinderColumn.RefreshMode.RESTORE_SELECTION;
import static org.jboss.hal.core.runtime.TopologyTasks.serversOfHost;
import static org.jboss.hal.core.runtime.TopologyTasks.serversOfServerGroup;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.flow.Flow.series;
import static org.jboss.hal.resources.Ids.FORM;

@Column(Ids.SERVER)
@Requires(value = {"/host=*/server-config=*", "/host=*/server=*",
        "opt:///host=*/server=*" + CONFIGURATION_CHANGES_ADDRESS}, recursive = false)
public class ServerColumn extends FinderColumn<Server> implements ServerActionHandler, ServerResultHandler {

    static final String HOST_KEY = "/host=";

    static AddressTemplate serverConfigTemplate(Server server) {
        return server.isStandalone() ? AddressTemplate.ROOT : serverConfigTemplate(server.getHost());
    }

    static AddressTemplate serverConfigTemplate(String host) {
        return AddressTemplate.of(HOST_KEY + host + "/server-config=*");
    }

    private final Finder finder;
    private final Environment environment;
    private final SecurityContextRegistry securityContextRegistry;
    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private final Provider<Progress> progress;
    private final StatementContext statementContext;
    private final MetadataProcessor metadataProcessor;
    private final ServerActions serverActions;
    private final CrudOperations crud;
    private final Resources resources;
    private FinderPath refreshPath;

    @Inject
    public ServerColumn(Finder finder,
            Dispatcher dispatcher,
            Environment environment,
            EventBus eventBus,
            @Footer Provider<Progress> progress,
            SecurityContextRegistry securityContextRegistry,
            StatementContext statementContext,
            PlaceManager placeManager,
            Places places,
            MetadataProcessor metadataProcessor,
            FinderPathFactory finderPathFactory,
            ColumnActionFactory columnActionFactory,
            ItemActionFactory itemActionFactory,
            ServerActions serverActions,
            CrudOperations crud,
            Resources resources,
            MetadataRegistry metadataRegistry) {

        super(new Builder<Server>(finder, Ids.SERVER, Names.SERVER)

                .onItemSelect(server -> {
                    if (BrowseByColumn.browseByServerGroups(finder.getContext())) {
                        // if we browse by server groups we still need to have a valid {selected.host}
                        eventBus.fireEvent(new HostSelectionEvent(server.getHost()));
                    } else if (BrowseByColumn.browseByHosts(finder.getContext())) {
                        // if we browse by hosts we still need to have a valid {selected.group}
                        eventBus.fireEvent(new ServerGroupSelectionEvent(server.getServerGroup()));
                    }
                    eventBus.fireEvent(new ServerSelectionEvent(server.getName()));
                    if (server.isStarted()) {
                        // some runtime screens use "{selected.profile}"
                        eventBus.fireEvent(new ProfileSelectionEvent(server.get(PROFILE_NAME).asString()));
                    }
                })

                .onBreadcrumbItem((item, context) -> {
                    PlaceRequest.Builder builder;
                    PlaceRequest current = placeManager.getCurrentPlaceRequest();

                    if (NameTokens.GENERIC_SUBSYSTEM.equals(current.getNameToken())) {
                        // switch server in address parameter of generic presenter
                        builder = new PlaceRequest.Builder().nameToken(current.getNameToken());
                        String addressParam = current.getParameter(Places.ADDRESS_PARAM, null);
                        if (addressParam != null) {
                            ResourceAddress currentAddress = AddressTemplate.of(addressParam).resolve(statementContext);
                            ResourceAddress newAddress = currentAddress
                                    .replaceValue(HOST, item.getHost())
                                    .replaceValue(SERVER, item.getName())
                                    .replaceValue(SERVER_CONFIG, item.getName());
                            builder.with(Places.ADDRESS_PARAM, newAddress.toString());
                        }

                    } else {
                        // try to replace 'host', 'server' and 'server-config' request parameter
                        PlaceRequest place = places.replaceParameter(current, HOST, item.getHost()).build();
                        place = places.replaceParameter(place, SERVER, item.getName()).build();
                        builder = places.replaceParameter(place, SERVER_CONFIG, item.getName());
                    }
                    placeManager.revealPlace(builder.build());
                })

                .pinnable()
                .showCount()
                .withFilter()
                .filterDescription(resources.messages().serverFilterDescription())
                .onPreview(item -> new ServerPreview(serverActions, item, dispatcher, eventBus, progress,
                        statementContext, placeManager, places, finderPathFactory, resources))
        );
        this.finder = finder;
        this.dispatcher = dispatcher;
        this.environment = environment;
        this.eventBus = eventBus;
        this.progress = progress;
        this.securityContextRegistry = securityContextRegistry;
        this.statementContext = statementContext;
        this.metadataProcessor = metadataProcessor;
        this.serverActions = serverActions;
        this.crud = crud;
        this.resources = resources;

        ItemsProvider<Server> itemsProvider = (context, callback) -> {
            List<Task<FlowContext>> tasks;
            if (BrowseByColumn.browseByHosts(context)) {
                processAddColumnAction(statementContext.selectedHost());
                tasks = serversOfHost(environment, dispatcher, statementContext.selectedHost());

            } else {
                tasks = serversOfServerGroup(environment, dispatcher, statementContext.selectedServerGroup());
            }

            series(new FlowContext(progress.get()), tasks)
                    .subscribe(new Outcome<FlowContext>() {
                        @Override
                        public void onError(FlowContext context, Throwable error) {
                            callback.onFailure(error);
                        }

                        @Override
                        public void onSuccess(FlowContext context) {
                            List<Server> servers = context.get(TopologyTasks.SERVERS);
                            if (servers == null) {
                                servers = emptyList();
                            }
                            callback.onSuccess(servers.stream().sorted(comparing(Server::getName))
                                    .collect(toList()));

                            // Restore pending servers visualization
                            servers.stream()
                                    .filter(serverActions::isPending)
                                    .forEach(server -> ItemMonitor.startProgress(server.getId()));
                        }
                    });
        };
        setItemsProvider(itemsProvider);

        // reuse the items provider to filter breadcrumb items
        setBreadcrumbItemsProvider((context, callback) ->
                itemsProvider.get(context, new AsyncCallback<List<Server>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(List<Server> servers) {
                        if (!serverIsLastSegment()) {
                            // When the server is not the last segment in the finder path, we assume that
                            // the current path is related to something which requires a running server.
                            // In that case return only started servers.
                            callback.onSuccess(servers.stream().filter(Server::isStarted).collect(toList()));
                        } else {
                            callback.onSuccess(servers);
                        }
                    }
                }));

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
            public HTMLElement element() {
                return ItemDisplay.withSubtitle(item.getName(),
                        BrowseByColumn.browseByHosts(finder.getContext())
                                ? item.getServerGroup()
                                : item.getHost());
            }

            @Override
            public String getFilterData() {
                List<String> data = new ArrayList<>();
                data.add(item.getName());
                data.add(item.getHost());
                data.add(item.getServerGroup());
                data.add(ModelNodeHelper.asAttributeValue(item.getServerConfigStatus()));
                if (item.isStarted()) {
                    data.add(ModelNodeHelper.asAttributeValue(item.getServerState()));
                    data.add(ModelNodeHelper.asAttributeValue(item.getSuspendState()));
                } else {
                    data.add("stopped");
                }
                return String.join(" ", data);
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
                PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.SERVER_CONFIGURATION)
                        .with(HOST, item.getHost())
                        .with(SERVER_CONFIG, item.getName())
                        .build();
                List<ItemAction<Server>> actions = new ArrayList<>();

                actions.add(itemActionFactory.viewAndMonitor(item.getId(), placeRequest));
                if (item.hasBootErrors()) {
                    PlaceRequest bootErrorsRequest = new PlaceRequest.Builder().nameToken(NameTokens.SERVER_BOOT_ERRORS)
                            .with(HOST, item.getHost())
                            .with(SERVER, item.getName())
                            .build();
                    actions.add(itemActionFactory.placeRequest(Names.BOOT_ERRORS, bootErrorsRequest));
                }
                if (!serverActions.isPending(item)) {
                    actions.add(new ItemAction.Builder<Server>()
                            .title(resources.constants().copy())
                            .handler(itm -> copyServer(itm))
                            .constraint(Constraint.executable(serverConfigTemplate(item), ADD))
                            .build());
                    if (!item.isStarted()) {
                        AddressTemplate template = AddressTemplate
                                .of(HOST_KEY + item.getHost() + "/server-config=" + item.getName());
                        actions.add(itemActionFactory.remove(Names.SERVER, item.getName(),
                                template, serverConfigTemplate(item), ServerColumn.this));
                    }
                    if (item.isStarted()) {
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().editURL())
                                .handler(itm -> serverActions.editUrl(itm, () -> refresh(RESTORE_SELECTION)))
                                .build());

                        if (ManagementModel.supportsConfigurationChanges(item.getManagementVersion())
                            && metadataRegistry.contains(AddressTemplate
                                .of(HOST_KEY + item.getHost() + "/server=" + item.getName())
                                .append(CONFIGURATION_CHANGES_ADDRESS))) {
                            Map<String, String> params = new HashMap<>();
                            params.put(HOST, item.getHost());
                            params.put(SERVER, item.getName());
                            params.put(PROFILE, item.get(PROFILE_NAME).asString());
                            PlaceRequest ccPlaceRequest = new PlaceRequest.Builder()
                                    .nameToken(NameTokens.CONFIGURATION_CHANGES)
                                    .with(params)
                                    .build();
                            AddressTemplate template = AddressTemplate
                                    .of(HOST_KEY + item.getHost() + "/server=" + item.getName())
                                    .append(CONFIGURATION_CHANGES_ADDRESS);
                            actions.add(itemActionFactory.placeRequest(resources.constants().configurationChanges(),
                                    ccPlaceRequest, Constraint.executable(template, LIST_CHANGES_OPERATION)));
                        }

                        actions.add(ItemAction.separator());
                        // Order is: reload, restart, (resume | suspend), stop
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().reload())
                                .handler(serverActions::reload)
                                .constraint(Constraint.executable(serverConfigTemplate(item), RELOAD))
                                .build());
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().restart())
                                .handler(serverActions::restart)
                                .constraint(Constraint.executable(serverConfigTemplate(item), RESTART))
                                .build());
                        if (ManagementModel.supportsSuspend(item.getManagementVersion())) {
                            if (item.isSuspended()) {
                                actions.add(new ItemAction.Builder<Server>()
                                        .title(resources.constants().resume())
                                        .handler(serverActions::resume)
                                        .constraint(Constraint.executable(serverConfigTemplate(item), RESUME))
                                        .build());
                            } else {
                                actions.add(new ItemAction.Builder<Server>()
                                        .title(resources.constants().suspend())
                                        .handler(serverActions::suspend)
                                        .constraint(Constraint.executable(serverConfigTemplate(item), SUSPEND))
                                        .build());
                            }
                        }
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().stop())
                                .handler(serverActions::stop)
                                .constraint(Constraint.executable(serverConfigTemplate(item), STOP))
                                .build());
                    } else {
                        actions.add(ItemAction.separator());
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().start())
                                .handler(serverActions::start)
                                .constraint(Constraint.executable(serverConfigTemplate(item), START))
                                .build());
                        actions.add(new ItemAction.Builder<Server>()
                                .title(resources.constants().startInSuspendedMode())
                                .handler(serverActions::startInSuspendedMode)
                                .constraint(Constraint.executable(serverConfigTemplate(item), SUSPEND))
                                .build());
                    }
                }
                // add destroy and kill action regardless of state
                // to destroy and kill servers which might show a wrong state
                actions.add(new ItemAction.Builder<Server>()
                        .title(resources.constants().destroy())
                        .handler(serverActions::destroy)
                        .constraint(Constraint.executable(serverConfigTemplate(item), DESTROY))
                        .build());
                actions.add(new ItemAction.Builder<Server>()
                        .title(resources.constants().kill())
                        .handler(serverActions::kill)
                        .constraint(Constraint.executable(serverConfigTemplate(item), KILL))
                        .build());
                return actions;
            }

            @Override
            public String nextColumn() {
                return item.isStarted() ? Ids.RUNTIME_SUBSYSTEM : null;
            }
        });

        // Don't use columnActionFactory.add() here. This would add a default constraint,
        // but we want to manage the visibility by ourselves.
        ColumnAction<Server> addAction = new ColumnAction.Builder<Server>(Ids.SERVER_ADD)
                .element(columnActionFactory.addButton(Names.SERVER))
                .handler(column -> addServer(BrowseByColumn.browseByHosts(finder.getContext())))
                .build();
        addColumnAction(addAction);
        addColumnAction(columnActionFactory.refresh(Ids.SERVER_REFRESH));

        eventBus.addHandler(ServerActionEvent.getType(), this);
        eventBus.addHandler(ServerResultEvent.getType(), this);
    }

    private void addServer(boolean browseByHost) {
        if (browseByHost) {
            AddressTemplate template = serverConfigTemplate(statementContext.selectedHost());
            String id = Ids.build(HOST, statementContext.selectedHost(), SERVER, Ids.ADD);
            List<String> attributes = asList(AUTO_START, GROUP, SOCKET_BINDING_DEFAULT_INTERFACE,
                    SOCKET_BINDING_GROUP, SOCKET_BINDING_PORT_OFFSET, UPDATE_AUTO_START_WITH_SERVER_STATUS);
            crud.add(id, Names.SERVER, template, attributes,
                    createUniqueValidation(), (name, address) -> refresh(RESTORE_SELECTION));
        } else {

            // load all available hosts to show in the copy dialog
            Operation operation = new Operation.Builder(ResourceAddress.root(), READ_CHILDREN_NAMES_OPERATION)
                    .param(CHILD_TYPE, HOST)
                    .build();

            dispatcher.execute(operation, result -> {

                List<String> hosts = new ArrayList<>();
                result.asList().forEach(m -> hosts.add(m.asString()));

                // get the first host, only to retrieve the r-r-d for server-config
                // as /host=*/server-config=*:read-operation-description(name=add) does not work
                AddressTemplate template = serverConfigTemplate(hosts.get(0));
                metadataProcessor
                        .lookup(template, progress.get(), new SuccessfulMetadataCallback(eventBus, resources) {
                            @Override
                            public void onMetadata(Metadata metadata) {

                                String id = Ids.build(SERVER_GROUP, statementContext.selectedServerGroup(), SERVER,
                                        FORM);
                                SingleSelectBoxItem hostFormItem = new SingleSelectBoxItem(HOST, Names.HOST, hosts,
                                        false);
                                hostFormItem.setRequired(true);
                                NameItem nameItem = new NameItem();

                                ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                                        .unboundFormItem(nameItem, 0)
                                        .unboundFormItem(hostFormItem, 1, resources.messages().addServerHostHelp())
                                        // add group as custom form item, to set it as read-only and pre-set with
                                        // the selected server-group column
                                        .customFormItem(GROUP, attributeDescription -> {
                                            TextBoxItem groupItem = new TextBoxItem(GROUP,
                                                    resources.constants().group());
                                            groupItem.setEnabled(false);
                                            return groupItem;
                                        })
                                        .fromRequestProperties()
                                        .build();

                                AddResourceDialog dialog = new AddResourceDialog(resources.messages().addServerTitle(),
                                        form, (resource, payload) -> {

                                    payload.get(GROUP).set(statementContext.selectedServerGroup());
                                    String serverName = nameItem.getValue();
                                    ResourceAddress address = serverConfigTemplate(hostFormItem.getValue())
                                            .resolve(statementContext, serverName);

                                    crud.add(serverName, address, payload,
                                            resources.messages().addResourceSuccess(Names.SERVER,
                                                    serverName), (name, address1) -> refresh(RESTORE_SELECTION));
                                });
                                dialog.getForm().<String>getFormItem(NAME).addValidationHandler(
                                        createUniqueValidation());
                                dialog.show();
                                form.<String>getFormItem(GROUP).setValue(statementContext.selectedServerGroup());
                            }
                        });
            });
        }
    }

    private void copyServer(Server server) {
        serverActions.copyServer(server, () -> refresh(RESTORE_SELECTION));
    }

    private boolean serverIsLastSegment() {
        FinderSegment segment = Iterables.getLast(finder.getContext().getPath(), null);
        return segment != null && Ids.SERVER.equals(segment.getColumnId());
    }

    private void processAddColumnAction(String host) {
        AuthorisationDecision ad = AuthorisationDecision.from(environment, c -> {
            if (securityContextRegistry.contains(c.getTemplate())) {
                return Optional.of(securityContextRegistry.lookup(c.getTemplate()));
            }
            return Optional.empty();
        });
        HTMLElement addButton = (HTMLElement) document.getElementById(Ids.SERVER_ADD);
        ElementGuard.toggle(addButton, !ad.isAllowed(Constraint.executable(serverConfigTemplate(host), ADD)));
    }

    @Override
    public void onServerAction(ServerActionEvent event) {
        if (isVisible()) {
            // remember current selection for onServerResult()
            refreshPath = finder.getContext().getPath().copy();
            ItemMonitor.startProgress(event.getServer().getId());
            refresh(RESTORE_SELECTION);
        }
    }

    @Override
    public void onServerResult(ServerResultEvent event) {
        if (isVisible()) {
            ItemMonitor.stopProgress(event.getServer().getId());

            FinderPath path = refreshPath != null ? refreshPath : finder.getContext().getPath();
            if (event.getServer().isStopped()) {
                FinderPath woRuntime = new FinderPath();
                for (FinderSegment<?> segment : path) {
                    if (Ids.RUNTIME_SUBSYSTEM.equals(segment.getColumnId())) {
                        break;
                    }
                    woRuntime.append(segment.getColumnId(), segment.getItemId(),
                            segment.getColumnTitle(), segment.getItemTitle());
                }
                path = woRuntime;
            }
            finder.refresh(path);
            refreshPath = null;
        }
    }
}
