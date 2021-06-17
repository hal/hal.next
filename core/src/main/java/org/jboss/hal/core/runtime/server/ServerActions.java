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
package org.jboss.hal.core.runtime.server;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Strings;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import elemental2.dom.HTMLElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.Alert;
import org.jboss.hal.ballroom.dialog.BlockingDialog;
import org.jboss.hal.ballroom.dialog.Dialog;
import org.jboss.hal.ballroom.dialog.DialogFactory;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.form.SingleSelectBoxItem;
import org.jboss.hal.ballroom.form.TextBoxItem;
import org.jboss.hal.core.Core;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.dialog.NameItem;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mbui.form.OperationFormBuilder;
import org.jboss.hal.core.runtime.Action;
import org.jboss.hal.core.runtime.Result;
import org.jboss.hal.core.runtime.RunningState;
import org.jboss.hal.core.runtime.SuspendState;
import org.jboss.hal.core.runtime.Timeouts;
import org.jboss.hal.core.runtime.server.ServerUrlTasks.ReadSocketBinding;
import org.jboss.hal.core.runtime.server.ServerUrlTasks.ReadSocketBindingGroup;
import org.jboss.hal.dmr.Composite;
import org.jboss.hal.dmr.CompositeResult;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.Property;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.dispatch.Dispatcher.OnError;
import org.jboss.hal.dmr.dispatch.Dispatcher.OnFail;
import org.jboss.hal.flow.FlowContext;
import org.jboss.hal.flow.Outcome;
import org.jboss.hal.flow.Progress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.ManagementModel;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.processing.MetadataProcessor;
import org.jboss.hal.meta.processing.SuccessfulMetadataCallback;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Callback;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.CompletableSubscriber;
import rx.Subscription;

import static elemental2.dom.DomGlobal.setTimeout;
import static org.jboss.gwt.elemento.core.Elements.a;
import static org.jboss.gwt.elemento.core.Elements.p;
import static org.jboss.gwt.elemento.core.Elements.span;
import static org.jboss.hal.core.runtime.RunningState.RUNNING;
import static org.jboss.hal.core.runtime.SuspendState.SUSPENDED;
import static org.jboss.hal.core.runtime.server.ServerConfigStatus.DISABLED;
import static org.jboss.hal.core.runtime.server.ServerConfigStatus.STARTED;
import static org.jboss.hal.core.runtime.server.ServerConfigStatus.STOPPED;
import static org.jboss.hal.core.runtime.server.ServerUrlTasks.URL_KEY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.dmr.ModelNodeHelper.asEnumValue;
import static org.jboss.hal.dmr.ModelNodeHelper.getOrDefault;
import static org.jboss.hal.dmr.dispatch.TimeoutHandler.repeatOperationUntil;
import static org.jboss.hal.dmr.dispatch.TimeoutHandler.repeatUntilTimeout;
import static org.jboss.hal.flow.Flow.series;
import static org.jboss.hal.resources.CSS.fontAwesome;
import static org.jboss.hal.resources.CSS.marginLeft5;
import static org.jboss.hal.resources.CSS.pfIcon;
import static org.jboss.hal.resources.Ids.FORM;
import static org.jboss.hal.resources.UIConstants.SHORT_TIMEOUT;

public class ServerActions implements Timeouts {

    private static final Logger logger = LoggerFactory.getLogger(ServerActions.class);

    private static AddressTemplate serverConfigTemplate(Server server) {
        return server.isStandalone()
                ? AddressTemplate.ROOT
                : AddressTemplate.of("/host=" + server.getHost() + "/server-config=*" + server.getName());
    }

    private final EventBus eventBus;
    private final Dispatcher dispatcher;
    private final MetadataProcessor metadataProcessor;
    private final Provider<Progress> progress;
    private final Resources resources;
    private final Map<String, Server> pendingServers;
    private final ServerUrlStorage serverUrlStorage;
    private final StatementContext statementContext;

    @Inject
    public ServerActions(EventBus eventBus,
            Dispatcher dispatcher,
            ServerUrlStorage serverUrlStorage,
            StatementContext statementContext,
            MetadataProcessor metadataProcessor,
            @Footer Provider<Progress> progress,
            Resources resources) {
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        this.serverUrlStorage = serverUrlStorage;
        this.statementContext = statementContext;
        this.metadataProcessor = metadataProcessor;
        this.progress = progress;
        this.resources = resources;
        this.pendingServers = new HashMap<>();
    }


    // ------------------------------------------------------ server operations

    public void copyServer(Server server, Callback callback) {
        Operation operation = new Operation.Builder(ResourceAddress.root(), READ_CHILDREN_NAMES_OPERATION)
                .param(CHILD_TYPE, HOST)
                .build();

        dispatcher.execute(operation, result -> {

            List<String> hosts = new ArrayList<>();
            result.asList().forEach(m -> hosts.add(m.asString()));
            // get the first host only to retrieve the r-r-d for server-config
            // as /host=*/server-config=*:read-operation-description(name=add) does not work
            AddressTemplate template = AddressTemplate.of("/host=" + hosts.get(0) + "/server-config=*");
            metadataProcessor.lookup(template, progress.get(), new SuccessfulMetadataCallback(eventBus, resources) {
                @Override
                public void onMetadata(Metadata metadata) {

                    String id = Ids.build(SERVER_GROUP, statementContext.selectedServerGroup(), SERVER,
                            FORM);
                    SingleSelectBoxItem hostFormItem = new SingleSelectBoxItem(HOST, Names.HOST, hosts,
                            false);
                    hostFormItem.setRequired(true);
                    NameItem nameItem = new NameItem();

                    ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                            .fromRequestProperties()
                            .unboundFormItem(nameItem, 0)
                            .unboundFormItem(hostFormItem, 1, resources.messages().addServerHostHelp())
                            .exclude(AUTO_START, SOCKET_BINDING_DEFAULT_INTERFACE,
                                    SOCKET_BINDING_GROUP, UPDATE_AUTO_START_WITH_SERVER_STATUS)
                            .build();

                    AddResourceDialog dialog = new AddResourceDialog(resources.messages().copyServerTitle(),
                            form, (resource, payload) -> {

                        // read server-config recursively to retrieve nested resources
                        ModelNode serverConfigModel = new ModelNode();
                        serverConfigModel.get(HOST).set(server.getHost());
                        serverConfigModel.get(SERVER_CONFIG).set(server.getName());

                        ResourceAddress serverAddress = new ResourceAddress(serverConfigModel);
                        Operation opReadServer = new Operation.Builder(serverAddress, READ_RESOURCE_OPERATION)
                                .param(RECURSIVE, true)
                                .build();

                        dispatcher.execute(opReadServer, new Consumer<ModelNode>() {
                            @Override
                            public void accept(ModelNode newServerModel) {

                                String newServerName = nameItem.getValue();
                                // set the chosen group in the model
                                newServerModel.get(GROUP).set(payload.get(GROUP).asString());
                                if (payload.hasDefined(SOCKET_BINDING_PORT_OFFSET)) {
                                    newServerModel.get(SOCKET_BINDING_PORT_OFFSET)
                                            .set(payload.get(SOCKET_BINDING_PORT_OFFSET).asLong());
                                }
                                newServerModel.get(NAME).set(newServerName);

                                ModelNode newServerModelAddress = new ModelNode();
                                newServerModelAddress.get(HOST).set(hostFormItem.getValue());
                                newServerModelAddress.get(SERVER_CONFIG).set(newServerName);

                                Operation opAddServer = new Operation.Builder(
                                        new ResourceAddress(newServerModelAddress), ADD)
                                        .payload(newServerModel)
                                        .build();
                                Composite comp = new Composite();
                                comp.add(opAddServer);

                                // create operation for each nested resource of the source server
                                createOperation(comp, JVM, newServerModel, newServerModelAddress);
                                createOperation(comp, INTERFACE, newServerModel, newServerModelAddress);
                                createOperation(comp, PATH, newServerModel, newServerModelAddress);
                                createOperation(comp, SYSTEM_PROPERTY, newServerModel, newServerModelAddress);
                                createOperation(comp, SSL, newServerModel, newServerModelAddress);

                                dispatcher.execute(comp, (CompositeResult result) -> {
                                    MessageEvent.fire(eventBus, Message.success(
                                            resources.messages()
                                                    .addResourceSuccess(Names.SERVER, newServerName)));
                                    callback.execute();
                                }, (operation1, failure) -> {
                                    MessageEvent.fire(eventBus, Message.error(
                                            resources.messages().addResourceError(newServerName, failure)));
                                    callback.execute();
                                }, (operation1, exception) -> {
                                    MessageEvent.fire(eventBus, Message.error(resources.messages()
                                            .addResourceError(newServerName, exception.getMessage())));
                                    callback.execute();
                                });
                            }

                            private void createOperation(Composite composite, String resource, ModelNode model,
                                    ModelNode baseAddress) {
                                if (model.hasDefined(resource)) {
                                    List<Property> props = model.get(resource).asPropertyList();
                                    props.forEach(p -> {
                                        String propname = p.getName();
                                        ModelNode _address = baseAddress.clone();
                                        _address.get(resource).set(propname);
                                        Operation operation = new Operation.Builder(
                                                new ResourceAddress(_address), ADD)
                                                .payload(p.getValue())
                                                .build();
                                        composite.add(operation);
                                    });
                                }
                            }
                        });
                    });
                    dialog.show();
                }
            });
        });
    }


    // ------------------------------------------------------ lifecycle operations

    public void reload(Server server) {
        Operation operation = new Operation.Builder(server.getServerConfigAddress(), RELOAD)
                .param(BLOCKING, false)
                .build();
        reloadRestart(server, operation, Action.RELOAD, SERVER_RELOAD_TIMEOUT,
                resources.messages().reload(server.getName()),
                resources.messages().reloadServerQuestion(server.getName()),
                resources.messages().reloadServerSuccess(server.getName()),
                resources.messages().reloadServerError(server.getName()));
    }

    public void restart(Server server) {
        if (server.isStandalone()) {
            restartStandalone(server);
        } else {
            Operation operation = new Operation.Builder(server.getServerConfigAddress(), RESTART)
                    .param(BLOCKING, false)
                    .build();
            reloadRestart(server, operation, Action.RESTART, SERVER_RESTART_TIMEOUT,
                    resources.messages().restart(server.getName()),
                    resources.messages().restartServerQuestion(server.getName()),
                    resources.messages().restartServerSuccess(server.getName()),
                    resources.messages().restartServerError(server.getName()));
        }
    }

    private void restartStandalone(Server server) {
        restartStandalone(server, resources.messages().restartStandaloneQuestion(server.getName()));
    }

    public void restartStandalone(Server server, SafeHtml question) {
        String title = resources.messages().restart(server.getName());
        DialogFactory.showConfirmation(title, question, () -> {
            // execute the restart with a little delay to ensure the confirmation dialog is closed
            // before the next dialog is opened (only one modal can be open at a time!)
            setTimeout((o) -> {

                prepare(server, Action.RESTART);
                BlockingDialog pendingDialog = DialogFactory
                        .buildLongRunning(title,
                                resources.messages().restartStandalonePending(server.getName()));
                pendingDialog.show();
                Operation operation = new Operation.Builder(ResourceAddress.root(), SHUTDOWN)
                        .param(RESTART, true)
                        .build();
                Operation ping = new Operation.Builder(ResourceAddress.root(), READ_RESOURCE_OPERATION).build();
                dispatcher.execute(operation, result -> repeatUntilTimeout(dispatcher, SERVER_RESTART_TIMEOUT, ping)
                                .subscribe(new CompletableSubscriber() {
                                    @Override
                                    public void onSubscribe(Subscription d) {
                                    }

                                    @Override
                                    public void onCompleted() {
                                        // wait a little bit before event handlers try to use the restarted server
                                        setTimeout((o1) -> {
                                            pendingDialog.close();
                                            finish(Server.STANDALONE, Result.SUCCESS, Message.success(
                                                    resources.messages()
                                                            .restartServerSuccess(server.getName())));
                                        }, 666);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        pendingDialog.close();
                                        DialogFactory.buildBlocking(title,
                                                resources.messages().restartStandaloneTimeout(server.getName()))
                                                .show();
                                        finish(Server.STANDALONE, Result.TIMEOUT, null);
                                    }
                                }),
                        (o1, failure) -> finish(Server.STANDALONE, Result.ERROR,
                                Message.error(resources.messages().restartServerError(server.getName()))),
                        (o2, exception) -> finish(Server.STANDALONE, Result.ERROR,
                                Message.error(resources.messages().restartServerError(server.getName()))));

            }, SHORT_TIMEOUT);
        });
    }

    private void reloadRestart(Server server, Operation operation, Action action, int timeout,
            String title, SafeHtml question, SafeHtml successMessage, SafeHtml errorMessage) {
        DialogFactory.showConfirmation(title, question, () -> {

            prepare(server, action);
            dispatcher.execute(operation,
                    result -> repeatOperationUntil(dispatcher, timeout,
                            server.isStandalone() ? readServerState(server) : readServerConfigStatus(server),
                            server.isStandalone() ? checkServerState(RUNNING) : checkServerConfigStatus(STARTED))
                            .subscribe(new ServerTimeoutCallback(server, action, successMessage)),
                    new ServerFailedCallback(server, errorMessage),
                    new ServerExceptionCallback(server, errorMessage));
        });
    }

    public void suspend(Server server) {
        if (!ManagementModel.supportsSuspend(server.getManagementVersion())) {
            logger.error("Server {} using version {} does not support suspend operation", server.getName(),
                    server.getManagementVersion());
            return;
        }

        metadataProcessor.lookup(serverConfigTemplate(server), progress.get(),
                new MetadataProcessor.MetadataCallback() {
                    @Override
                    public void onMetadata(Metadata metadata) {
                        String id = Ids.build(SUSPEND, server.getName(), Ids.FORM);
                        Form<ModelNode> form = new OperationFormBuilder<>(id, metadata, SUSPEND).build();

                        Dialog dialog = DialogFactory.buildConfirmation(
                                resources.messages().suspend(server.getName()),
                                resources.messages().suspendServerQuestion(server.getName()),
                                form.element(),
                                Dialog.Size.MEDIUM,
                                () -> {

                                    form.save();
                                    int timeout = getOrDefault(form.getModel(), TIMEOUT,
                                            () -> form.getModel().get(TIMEOUT).asInt(), 0);
                                    int uiTimeout = timeout + SERVER_SUSPEND_TIMEOUT;

                                    prepare(server, Action.SUSPEND);
                                    Operation operation = new Operation.Builder(server.getServerConfigAddress(),
                                            SUSPEND)
                                            .param(TIMEOUT, timeout)
                                            .build();
                                    dispatcher.execute(operation,
                                            result -> repeatOperationUntil(dispatcher, uiTimeout,
                                                    readSuspendState(server), checkSuspendState(SUSPENDED))
                                                    .subscribe(new ServerTimeoutCallback(server, Action.SUSPEND,
                                                            resources.messages()
                                                                    .suspendServerSuccess(server.getName()))),
                                            new ServerFailedCallback(server,
                                                    resources.messages().suspendServerError(server.getName())),
                                            new ServerExceptionCallback(server,
                                                    resources.messages().suspendServerError(server.getName())));
                                });

                        dialog.registerAttachable(form);
                        dialog.show();

                        ModelNode model = new ModelNode();
                        model.get(TIMEOUT).set(0);
                        form.edit(model);
                    }

                    @Override
                    public void onError(Throwable error) {
                        MessageEvent.fire(eventBus,
                                Message.error(resources.messages().metadataError(), error.getMessage()));
                    }
                });
    }

    public void resume(Server server) {
        if (!ManagementModel.supportsSuspend(server.getManagementVersion())) {
            logger.error("Server {} using version {} does not support resume operation", server.getName(),
                    server.getManagementVersion());
            return;
        }

        prepare(server, Action.RESUME);
        ResourceAddress address = server.isStandalone() ? server.getServerAddress() : server.getServerConfigAddress();
        Operation operation = new Operation.Builder(address, RESUME).build();
        dispatcher.execute(operation, result -> repeatOperationUntil(dispatcher, SERVER_START_TIMEOUT,
                server.isStandalone() ? readServerState(server) : readServerConfigStatus(server),
                server.isStandalone() ? checkServerState(RUNNING) : checkServerConfigStatus(STARTED))
                        .subscribe(new ServerTimeoutCallback(server, Action.RESUME,
                                resources.messages().resumeServerSuccess(server.getName()))),
                new ServerFailedCallback(server, resources.messages().resumeServerError(server.getName())),
                new ServerExceptionCallback(server, resources.messages().resumeServerError(server.getName())));
    }


    public void stop(Server server) {
        metadataProcessor.lookup(serverConfigTemplate(server), progress.get(),
                new MetadataProcessor.MetadataCallback() {
                    @Override
                    public void onMetadata(Metadata metadata) {
                        String id = Ids.build(STOP, server.getName(), Ids.FORM);
                        Form<ModelNode> form = new OperationFormBuilder<>(id, metadata, STOP)
                                .include(TIMEOUT).build();

                        Dialog dialog = DialogFactory.buildConfirmation(
                                resources.messages().stop(server.getName()),
                                resources.messages().stopServerQuestion(server.getName()),
                                form.element(),
                                Dialog.Size.MEDIUM,
                                () -> {

                                    form.save();
                                    int timeout = getOrDefault(form.getModel(), TIMEOUT,
                                            () -> form.getModel().get(TIMEOUT).asInt(), 0);
                                    int uiTimeout = timeout + SERVER_STOP_TIMEOUT;

                                    prepare(server, Action.STOP);
                                    Operation operation = new Operation.Builder(server.getServerConfigAddress(), STOP)
                                            .param(TIMEOUT, timeout)
                                            .param(BLOCKING, false)
                                            .build();
                                    dispatcher.execute(operation,
                                            result -> repeatOperationUntil(dispatcher, uiTimeout,
                                                    readServerConfigStatus(server),
                                                    checkServerConfigStatus(STOPPED, DISABLED))
                                                    .subscribe(new ServerTimeoutCallback(server, Action.STOP,
                                                            resources.messages().stopServerSuccess(server.getName()))),
                                            new ServerFailedCallback(server,
                                                    resources.messages().stopServerError(server.getName())),
                                            new ServerExceptionCallback(server,
                                                    resources.messages().stopServerError(server.getName())));
                                });

                        dialog.registerAttachable(form);
                        dialog.show();

                        ModelNode model = new ModelNode();
                        model.get(TIMEOUT).set(0);
                        form.edit(model);
                    }

                    @Override
                    public void onError(Throwable error) {
                        MessageEvent
                                .fire(eventBus,
                                        Message.error(resources.messages().metadataError(), error.getMessage()));
                    }
                });
    }

    /**
     * Call <code>/host={host}/server-config={sever}:stop(blocking=false)</code> the intended action is to immediately
     * stop the server.
     *
     * @param server
     */
    public void stopNow(Server server) {
        prepare(server, Action.STOP);
        Operation operation = new Operation.Builder(server.getServerConfigAddress(), STOP)
                .param(BLOCKING, false)
                .build();
        dispatcher.execute(operation, result -> repeatOperationUntil(dispatcher, SERVER_STOP_TIMEOUT,
                readServerConfigStatus(server), checkServerConfigStatus(STOPPED, DISABLED))
                        .subscribe(new ServerTimeoutCallback(server, Action.STOP,
                                resources.messages().stopServerSuccess(server.getName()))),
                new ServerFailedCallback(server, resources.messages().stopServerError(server.getName())),
                new ServerExceptionCallback(server, resources.messages().stopServerError(server.getName())));
    }

    public void destroy(Server server) {
        DialogFactory.showConfirmation(resources.messages().destroy(server.getName()),
                resources.messages().destroyServerQuestion(server.getName()),
                () -> {
                    prepare(server, Action.DESTROY);
                    Operation operation = new Operation.Builder(server.getServerConfigAddress(), DESTROY).build();
                    dispatcher.execute(operation,
                            result -> repeatOperationUntil(dispatcher, SERVER_DESTROY_TIMEOUT,
                                    readServerConfigStatus(server), checkServerConfigStatus(STOPPED, DISABLED))
                                    .subscribe(new ServerTimeoutCallback(server, Action.DESTROY,
                                            resources.messages().destroyServerSuccess(server.getName()))),
                            new ServerFailedCallback(server,
                                    resources.messages().destroyServerError(server.getName())),
                            new ServerExceptionCallback(server,
                                    resources.messages().destroyServerError(server.getName())));
                });
    }

    public void kill(Server server) {
        DialogFactory.showConfirmation(resources.messages().kill(server.getName()),
                resources.messages().killServerQuestion(server.getName()),
                () -> {
                    prepare(server, Action.KILL);
                    Operation operation = new Operation.Builder(server.getServerConfigAddress(), KILL).build();
                    dispatcher.execute(operation,
                            result -> repeatOperationUntil(dispatcher, SERVER_KILL_TIMEOUT,
                                    readServerConfigStatus(server), checkServerConfigStatus(STOPPED, DISABLED))
                                    .subscribe(new ServerTimeoutCallback(server, Action.KILL,
                                            resources.messages().killServerSuccess(server.getName()))),
                            new ServerFailedCallback(server,
                                    resources.messages().killServerError(server.getName())),
                            new ServerExceptionCallback(server,
                                    resources.messages().killServerError(server.getName())));
                });
    }

    public void start(Server server) {
        prepare(server, Action.START);
        Operation operation = new Operation.Builder(server.getServerConfigAddress(), START)
                .param(BLOCKING, false)
                .build();
        dispatcher.execute(operation,
                result -> repeatOperationUntil(dispatcher, SERVER_START_TIMEOUT,
                        readServerConfigStatus(server), checkServerConfigStatus(STARTED))
                        .subscribe(new ServerTimeoutCallback(server, Action.START,
                                resources.messages().startServerSuccess(server.getName()))),
                new ServerFailedCallback(server, resources.messages().startServerError(server.getName())),
                new ServerExceptionCallback(server, resources.messages().startServerError(server.getName())));
    }

    public void startInSuspendedMode(Server server) {
        prepare(server, Action.START);
        Operation operation = new Operation.Builder(server.getServerConfigAddress(), START)
                .param(START_MODE, SUSPEND)
                .param(BLOCKING, false)
                .build();
        dispatcher.execute(operation,
                result -> repeatOperationUntil(dispatcher, SERVER_START_TIMEOUT,
                        readServerConfigStatus(server), checkServerConfigStatus(STARTED))
                        .subscribe(new ServerTimeoutCallback(server, Action.START,
                                resources.messages().startServerInSuspendedModeSuccess(server.getName()))),
                new ServerFailedCallback(server, resources.messages().startServerError(server.getName())),
                new ServerExceptionCallback(server, resources.messages().startServerError(server.getName())));
    }


    // ------------------------------------------------------ server url methods

    /** Reads the URL and updates the specified HTML element */
    public void readUrl(Server server, HTMLElement element) {
        readUrl(server, new AsyncCallback<ServerUrl>() {
            @Override
            public void onFailure(Throwable caught) {
                Elements.removeChildrenFrom(element);
                element.textContent = Names.NOT_AVAILABLE;
            }

            @Override
            public void onSuccess(ServerUrl url) {
                Elements.removeChildrenFrom(element);
                element.appendChild(a(url.getUrl())
                        .apply(a -> a.target = server.getId())
                        .textContent(url.getUrl()).element());
                String icon;
                String tooltip;
                if (url.isCustom()) {
                    icon = fontAwesome("external-link");
                    tooltip = resources.constants().serverUrlCustom();
                } else {
                    icon = pfIcon("server");
                    tooltip = resources.constants().serverUrlManagementModel();
                }
                element.appendChild(span().css(icon, marginLeft5).style("cursor:help").title(tooltip).element()); //NON-NLS
            }
        });
    }

    /** Reads the URL using the provided parameters */
    public void readUrl(boolean standalone, String host, String serverGroup, String server,
            AsyncCallback<ServerUrl> callback) {
        if (serverUrlStorage.hasUrl(host, server)) {
            ServerUrl serverUrl = new ServerUrl(serverUrlStorage.load(host, server), true);
            callback.onSuccess(serverUrl);

        } else {
            series(new FlowContext(),
                    new ReadSocketBindingGroup(standalone, serverGroup, dispatcher),
                    new ReadSocketBinding(standalone, host, server, dispatcher))
                    .subscribe(new Outcome<FlowContext>() {
                        @Override
                        public void onError(FlowContext context, Throwable error) {
                            logger.error(error.getMessage());
                            callback.onFailure(error);
                        }

                        @Override
                        public void onSuccess(FlowContext context) {
                            callback.onSuccess(context.get(URL_KEY));
                        }
                    });
        }
    }

    /** Reads the URL using the information from the specified server instance */
    private void readUrl(Server server, AsyncCallback<ServerUrl> callback) {
        readUrl(server.isStandalone(), server.getHost(), server.getServerGroup(), server.getName(), callback);
    }

    public void editUrl(Server server, Callback callback) {
        Alert alert = new Alert(Icons.ERROR, resources.messages().serverUrlError());
        HTMLElement info = p().element();
        TextBoxItem urlItem = new TextBoxItem(URL, Names.URL);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(Ids.SERVER_URL_FORM, Metadata.empty())
                .unboundFormItem(urlItem)
                .addOnly()
                .onSave((f, changedValues) -> {
                    String url = urlItem.getValue();
                    if (Strings.isNullOrEmpty(url)) {
                        serverUrlStorage.remove(server.getHost(), server.getName());
                    } else {
                        serverUrlStorage.save(server.getHost(), server.getName(), url);
                    }
                    callback.execute();
                })
                .build();
        Dialog dialog = new Dialog.Builder(resources.constants().editURL())
                .add(alert.element())
                .add(info)
                .add(form.element())
                .primary(form::save)
                .cancel()
                .closeIcon(true)
                .closeOnEsc(true)
                .build();
        dialog.registerAttachable(form);
        Elements.setVisible(alert.element(), false);
        Elements.setVisible(info, false);

        readUrl(server, new AsyncCallback<ServerUrl>() {
            @Override
            public void onFailure(Throwable caught) {
                Elements.setVisible(alert.element(), true);
                show(null);
            }

            @Override
            public void onSuccess(ServerUrl serverUrl) {
                if (serverUrl.isCustom()) {
                    info.innerHTML = resources.messages().serverUrlCustom().asString();
                } else {
                    info.innerHTML = resources.messages().serverUrlManagementModel().asString();
                }
                Elements.setVisible(info, true);
                show(serverUrl);
            }

            private void show(ServerUrl serverUrl) {
                dialog.show();
                form.edit(new ModelNode());
                if (serverUrl != null) {
                    urlItem.setValue(serverUrl.getUrl());
                }
            }
        });
    }


    // ------------------------------------------------------ helper methods

    private void prepare(Server server, Action action) {
        markAsPending(server); // mark as pending *before* firing the event!
        eventBus.fireEvent(new ServerActionEvent(server, action));
    }

    private void finish(Server server, Result result, Message message) {
        clearPending(server); // clear pending state *before* firing the event!
        eventBus.fireEvent(new ServerResultEvent(server, result));
        MessageEvent.fire(eventBus, message);
    }

    public void markAsPending(Server server) {
        Core.setPendingLifecycleAction(true);
        pendingServers.put(Ids.hostServer(server.getHost(), server.getName()), server);
        logger.debug("Mark server {} as pending", server.getName());
    }

    public void clearPending(Server server) {
        Core.setPendingLifecycleAction(false);
        pendingServers.remove(Ids.hostServer(server.getHost(), server.getName()));
        logger.debug("Clear pending state for server {}", server.getName());
    }

    public boolean isPending(Server server) {
        return pendingServers.containsKey(Ids.hostServer(server.getHost(), server.getName()));
    }

    private Operation readServerConfigStatus(Server server) {
        return new Operation.Builder(server.getServerConfigAddress(), READ_ATTRIBUTE_OPERATION)
                .param(NAME, STATUS)
                .build();
    }

    private Operation readServerState(Server server) {
        return new Operation.Builder(server.getServerAddress(), READ_ATTRIBUTE_OPERATION)
                .param(NAME, SERVER_STATE)
                .build();
    }

    private Predicate<ModelNode> checkServerConfigStatus(ServerConfigStatus first, ServerConfigStatus... rest) {
        return result -> {
            ServerConfigStatus status = asEnumValue(result, name -> ServerConfigStatus.valueOf(name),
                    ServerConfigStatus.UNDEFINED);
            return EnumSet.of(first, rest).contains(status);
        };
    }

    private Predicate<ModelNode> checkServerState(RunningState first, RunningState... rest) {
        return result -> {
            RunningState state = asEnumValue(result, (name) -> RunningState.valueOf(name), RunningState.UNDEFINED);
            return EnumSet.of(first, rest).contains(state);
        };
    }

    private Operation readSuspendState(Server server) {
        return new Operation.Builder(server.getServerAddress(), READ_ATTRIBUTE_OPERATION)
                .param(NAME, SUSPEND_STATE)
                .build();
    }

    private Predicate<ModelNode> checkSuspendState(SuspendState statusToReach) {
        return result -> statusToReach == asEnumValue(result, name -> SuspendState.valueOf(name),
                SuspendState.UNDEFINED);
    }


    private class ServerTimeoutCallback implements CompletableSubscriber {

        private final Server server;
        private final Action action;
        private final SafeHtml successMessage;

        ServerTimeoutCallback(Server server, Action action, SafeHtml successMessage) {
            this.server = server;
            this.action = action;
            this.successMessage = successMessage;
        }

        @Override
        public void onCompleted() {
            if (Action.isStart(action)) {
                // read boot errors
                ResourceAddress address = server.getServerAddress().add(CORE_SERVICE, MANAGEMENT);
                Operation operation = new Operation.Builder(address, READ_BOOT_ERRORS).build();
                dispatcher.execute(operation, result -> {
                    if (!result.asList().isEmpty()) {
                        finish(server, Result.ERROR,
                                Message.error(resources.messages().serverBootErrors(server.getName())));
                    } else {
                        finish(server, Result.SUCCESS, Message.success(successMessage));
                    }
                });
            } else if (Action.isStop(action)) {
                // update server for event
                ResourceAddress address = server.getServerConfigAddress();
                Operation operation = new Operation.Builder(address, READ_RESOURCE_OPERATION)
                        .param(ATTRIBUTES_ONLY, true)
                        .param(INCLUDE_RUNTIME, true)
                        .build();
                dispatcher.execute(operation, result -> {
                    Server stoppedServer = new Server(this.server.getHost(), result);
                    finish(stoppedServer, Result.SUCCESS, Message.success(successMessage));
                });
            } else {
                finish(server, Result.SUCCESS, Message.success(successMessage));
            }
        }

        @Override
        public void onError(Throwable e) {
            finish(server, Result.TIMEOUT, Message.error(resources.messages().serverTimeout(server.getName())));
        }

        @Override
        public void onSubscribe(Subscription d) {
        }
    }


    private class ServerFailedCallback implements OnFail {

        private final Server server;
        private final SafeHtml errorMessage;

        ServerFailedCallback(Server server, SafeHtml errorMessage) {
            this.server = server;
            this.errorMessage = errorMessage;
        }

        @Override
        public void onFailed(Operation operation, String failure) {
            finish(server, Result.ERROR, Message.error(errorMessage, failure));
        }
    }


    private class ServerExceptionCallback implements OnError {

        private final Server server;
        private final SafeHtml errorMessage;

        ServerExceptionCallback(Server server, SafeHtml errorMessage) {
            this.server = server;
            this.errorMessage = errorMessage;
        }

        @Override
        public void onException(Operation operation, Throwable exception) {
            finish(server, Result.ERROR, Message.error(errorMessage, exception.getMessage()));
        }
    }
}
