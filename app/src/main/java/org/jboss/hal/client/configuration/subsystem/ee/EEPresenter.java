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
package org.jboss.hal.client.configuration.subsystem.ee;

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import org.jboss.hal.ballroom.dialog.DialogFactory;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.form.Form.FinishReset;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.core.configuration.PathsAutoComplete;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.dialog.NameItem;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mvp.ApplicationFinderPresenter;
import org.jboss.hal.core.mvp.HalView;
import org.jboss.hal.core.mvp.HasPresenter;
import org.jboss.hal.core.mvp.SupportsExpertMode;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.MetadataRegistry;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.jboss.hal.spi.Requires;

import static org.jboss.hal.client.configuration.subsystem.ee.AddressTemplates.EE_SUBSYSTEM_TEMPLATE;
import static org.jboss.hal.client.configuration.subsystem.ee.AddressTemplates.GLOBAL_DIRECTORY_TEMPLATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

public class EEPresenter
        extends ApplicationFinderPresenter<EEPresenter.MyView, EEPresenter.MyProxy>
        implements SupportsExpertMode {

    static Metadata globalModulesMetadata(MetadataRegistry metadataRegistry) {
        Metadata metadata = metadataRegistry.lookup(EE_SUBSYSTEM_TEMPLATE);
        return metadata.forComplexAttribute(GLOBAL_MODULES);
    }

    private final CrudOperations crud;
    private final FinderPathFactory finderPathFactory;
    private final Resources resources;
    private final Dispatcher dispatcher;
    private final StatementContext statementContext;
    private final EventBus eventBus;
    private final Metadata globalModulesMetadata;
    private final Metadata globalDirectoryMetadata;

    @Inject
    public EEPresenter(EventBus eventBus,
            MyView view,
            MyProxy proxy,
            Finder finder,
            CrudOperations crud,
            FinderPathFactory finderPathFactory,
            Resources resources,
            Dispatcher dispatcher,
            StatementContext statementContext,
            MetadataRegistry metadataRegistry) {
        super(eventBus, view, proxy, finder);
        this.crud = crud;
        this.finderPathFactory = finderPathFactory;
        this.resources = resources;
        this.dispatcher = dispatcher;
        this.statementContext = statementContext;
        this.eventBus = eventBus;
        this.globalModulesMetadata = globalModulesMetadata(metadataRegistry);
        this.globalDirectoryMetadata = metadataRegistry.lookup(GLOBAL_DIRECTORY_TEMPLATE);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public ResourceAddress resourceAddress() {
        return EE_SUBSYSTEM_TEMPLATE.resolve(statementContext);
    }

    @Override
    public FinderPath finderPath() {
        return finderPathFactory.configurationSubsystemPath(EE);
    }

    @Override
    protected void reload() {
        crud.readRecursive(EE_SUBSYSTEM_TEMPLATE, result -> getView().update(result));
    }

    void save(AddressTemplate addressTemplate, Map<String, Object> changedValues, Metadata metadata,
            SafeHtml successMessage) {
        crud.save(addressTemplate.resolve(statementContext), changedValues, metadata, successMessage, this::reload);
    }

    void reset(String type, String name, AddressTemplate template, Form<NamedNode> form,
            Metadata metadata, SafeHtml successMessage) {
        crud.reset(type, name, template.resolve(statementContext), form, metadata, successMessage,
                new FinishReset<NamedNode>(form) {
                    @Override
                    public void afterReset(Form<NamedNode> form) {
                        reload();
                    }
                });
    }

    void resetSingleton(String type, AddressTemplate template, Form<ModelNode> form, Metadata metadata) {
        crud.resetSingleton(type, template.resolve(statementContext), form, metadata, new FinishReset<ModelNode>(form) {
            @Override
            public void afterReset(Form<ModelNode> form) {
                reload();
            }
        });
    }

    void launchAddDialogGlobalModule() {
        Form<ModelNode> form = new ModelNodeForm.Builder<>(Ids.EE_GLOBAL_MODULES_FORM, globalModulesMetadata)
                .addOnly()
                .include(NAME, "slot", "annotations", "services", "meta-inf")
                .unsorted()
                .build();

        AddResourceDialog dialog = new AddResourceDialog(resources.messages().addResourceTitle(Names.GLOBAL_MODULES),
                form, (name, globalModule) -> {
            ResourceAddress address = EE_SUBSYSTEM_TEMPLATE.resolve(statementContext);
            Operation operation = new Operation.Builder(address, LIST_ADD_OPERATION)
                    .param(NAME, GLOBAL_MODULES)
                    .param(VALUE, globalModule)
                    .build();
            dispatcher.execute(operation, result -> {
                MessageEvent.fire(eventBus, Message.success(
                        resources.messages().addResourceSuccess(Names.GLOBAL_MODULES, name)));
                reload();
            });
        });

        dialog.show();
    }

    void removeGlobalModule(ModelNode globalModule) {
        String name = globalModule.get(NAME).asString();
        DialogFactory.showConfirmation(
                resources.messages().removeConfirmationTitle(Names.GLOBAL_MODULES),
                resources.messages().removeConfirmationQuestion(name),
                () -> {
                    ResourceAddress address = EE_SUBSYSTEM_TEMPLATE.resolve(statementContext);
                    Operation operation = new Operation.Builder(address, LIST_REMOVE_OPERATION)
                            .param(NAME, GLOBAL_MODULES)
                            .param(VALUE, globalModule)
                            .build();
                    dispatcher.execute(operation, result -> {
                        MessageEvent.fire(eventBus, Message.success(
                                resources.messages().removeResourceSuccess(Names.GLOBAL_MODULES, name)));
                        reload();
                    });
                });
    }

    void removeGlobalDirectory(AddressTemplate template) {
        Operation operation = new Operation.Builder(template.resolve(statementContext), REMOVE).build();
        dispatcher.execute(operation, result -> {
            MessageEvent.fire(eventBus, Message.success(
                    resources.messages().removeResourceSuccess(Names.EE, Names.GLOBAL_DIRECTORY)));
            reload();
        });
    }

    void launchAddDialogGlobalDirectory() {
        Form<ModelNode> form = new ModelNodeForm.Builder<>(Ids.build(Ids.EE, GLOBAL_DIRECTORY, ADD), globalDirectoryMetadata)
                .addOnly()
                .unboundFormItem(new NameItem(), 0)
                .include(PATH, RELATIVE_TO)
                .unsorted()
                .build();

        form.getFormItem(RELATIVE_TO).registerSuggestHandler(new PathsAutoComplete());

        AddResourceDialog dialog = new AddResourceDialog(resources.messages().addResourceTitle(Names.GLOBAL_DIRECTORY),
                form, (name, globalDirectory) -> {
            ResourceAddress address = GLOBAL_DIRECTORY_TEMPLATE.resolve(statementContext, name);
            Operation operation = new Operation.Builder(address, ADD)
                    .payload(globalDirectory)
                    .build();
            dispatcher.execute(operation, result -> {
                MessageEvent.fire(eventBus, Message.success(
                        resources.messages().addResourceSuccess(Names.GLOBAL_DIRECTORY, name)));
                reload();
            });
        });

        dialog.show();
    }

    // @formatter:off
    @ProxyCodeSplit
    @NameToken(NameTokens.EE)
    @Requires(AddressTemplates.EE_ADDRESS)
    public interface MyProxy extends ProxyPlace<EEPresenter> {
    }

    public interface MyView extends HalView, HasPresenter<EEPresenter> {
        void update(ModelNode eeData);
    }
    // @formatter:on
}
