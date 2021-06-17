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
package org.jboss.hal.client.configuration.subsystem.elytron;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.autocomplete.SuggestCapabilitiesAutoComplete;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.form.Form.FinishRemove;
import org.jboss.hal.ballroom.form.Form.FinishReset;
import org.jboss.hal.ballroom.form.FormItem;
import org.jboss.hal.ballroom.form.TextBoxItem;
import org.jboss.hal.ballroom.form.ValidationResult;
import org.jboss.hal.core.ComplexAttributeOperations;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.core.configuration.PathsAutoComplete;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.mbui.MbuiPresenter;
import org.jboss.hal.core.mbui.MbuiView;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.dialog.NameItem;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mbui.form.RequireAtLeastOneAttributeValidation;
import org.jboss.hal.core.mvp.SupportsExpertMode;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.MetadataRegistry;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Requires;

import static java.util.Arrays.asList;
import static org.jboss.hal.client.configuration.subsystem.elytron.AddressTemplates.*;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.dmr.ModelNodeHelper.asNamedNodes;
import static org.jboss.hal.dmr.ModelNodeHelper.move;

public class OtherSettingsPresenter extends MbuiPresenter<OtherSettingsPresenter.MyView, OtherSettingsPresenter.MyProxy>
        implements SupportsExpertMode {

    private static final List<String> FILE_BASED_CS = asList("JCEKS", "JKS", "PKCS12");
    private final CrudOperations crud;
    private final ComplexAttributeOperations ca;
    private final FinderPathFactory finderPathFactory;
    private final StatementContext statementContext;
    private final MetadataRegistry metadataRegistry;
    private final Resources resources;
    private Dispatcher dispatcher;

    @Inject
    public OtherSettingsPresenter(EventBus eventBus,
            OtherSettingsPresenter.MyView view,
            OtherSettingsPresenter.MyProxy proxy,
            Finder finder,
            Dispatcher dispatcher,
            CrudOperations crud,
            ComplexAttributeOperations ca,
            FinderPathFactory finderPathFactory,
            StatementContext statementContext,
            MetadataRegistry metadataRegistry,
            Resources resources) {
        super(eventBus, view, proxy, finder);
        this.dispatcher = dispatcher;
        this.crud = crud;
        this.ca = ca;
        this.finderPathFactory = finderPathFactory;
        this.statementContext = statementContext;
        this.metadataRegistry = metadataRegistry;
        this.resources = resources;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public ResourceAddress resourceAddress() {
        return ELYTRON_SUBSYSTEM_TEMPLATE.resolve(statementContext);
    }

    @Override
    public FinderPath finderPath() {
        return finderPathFactory.configurationSubsystemPath(Ids.ELYTRON)
                .append(Ids.ELYTRON, Ids.asId(Names.OTHER_SETTINGS),
                        resources.constants().settings(), Names.OTHER_SETTINGS);
    }

    @Override
    public void reload() {

        ResourceAddress address = ELYTRON_SUBSYSTEM_TEMPLATE.resolve(statementContext);
        crud.readChildren(address, asList(
                ElytronResource.KEY_STORE.resource,
                ElytronResource.KEY_MANAGER.resource,
                ElytronResource.SERVER_SSL_CONTEXT.resource,
                ElytronResource.CLIENT_SSL_CONTEXT.resource,
                ElytronResource.TRUST_MANAGER.resource,
                ElytronResource.CREDENTIAL_STORE.resource,
                ElytronResource.FILTERING_KEY_STORE.resource,
                ElytronResource.LDAP_KEY_STORE.resource,
                ElytronResource.PROVIDER_LOADER.resource,
                ElytronResource.AGGREGATE_PROVIDERS.resource,
                ElytronResource.SECURITY_DOMAIN.resource,
                ElytronResource.DIR_CONTEXT.resource,
                ElytronResource.AUTHENTICATION_CONTEXT.resource,
                ElytronResource.AUTHENTICATION_CONFIGURATION.resource,
                ElytronResource.FILE_AUDIT_LOG.resource,
                ElytronResource.SIZE_ROTATING_FILE_AUDIT_LOG.resource,
                ElytronResource.PERIODIC_ROTATING_FILE_AUDIT_LOG.resource,
                ElytronResource.SYSLOG_AUDIT_LOG.resource,
                ElytronResource.AGGREGATE_SECURITY_EVENT_LISTENER.resource,
                ElytronResource.CUSTOM_SECURITY_EVENT_LISTENER.resource,
                ElytronResource.PERMISSION_SET.resource,
                ElytronResource.CERTIFICATE_AUTHORITY.resource,
                ElytronResource.CERTIFICATE_AUTHORITY_ACCOUNT.resource,
                ElytronResource.JASPI_CONFIGURATION.resource,
                ElytronResource.SERVER_SSL_SNI_CONTEXT.resource,
                ElytronResource.POLICY.resource), // policy must be the last item in the list!
                result -> {
                    int i = 0;
                    getView().updateResourceElement(ElytronResource.KEY_STORE.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.KEY_MANAGER.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.SERVER_SSL_CONTEXT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.CLIENT_SSL_CONTEXT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.TRUST_MANAGER.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.CREDENTIAL_STORE.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.FILTERING_KEY_STORE.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateLdapKeyStore(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.PROVIDER_LOADER.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.AGGREGATE_PROVIDERS.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.SECURITY_DOMAIN.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.DIR_CONTEXT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.AUTHENTICATION_CONTEXT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.AUTHENTICATION_CONFIGURATION.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.FILE_AUDIT_LOG.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.SIZE_ROTATING_FILE_AUDIT_LOG.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.PERIODIC_ROTATING_FILE_AUDIT_LOG.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.SYSLOG_AUDIT_LOG.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.AGGREGATE_SECURITY_EVENT_LISTENER.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.CUSTOM_SECURITY_EVENT_LISTENER.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.PERMISSION_SET.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.CERTIFICATE_AUTHORITY.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.CERTIFICATE_AUTHORITY_ACCOUNT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.JASPI_CONFIGURATION.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateResourceElement(ElytronResource.SERVER_SSL_SNI_CONTEXT.resource,
                            asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    // policy must be the last item in the list!
                    List<NamedNode> policies = asNamedNodes(result.step(i).get(RESULT).asPropertyList());
                    getView().updatePolicy(policies.isEmpty() ? null : policies.get(0));
                });
    }

    void reload(String resource, Consumer<List<NamedNode>> callback) {
        crud.readChildren(AddressTemplates.ELYTRON_SUBSYSTEM_TEMPLATE, resource,
                children -> callback.accept(asNamedNodes(children)));
    }

    // -------------------------------------------- Credential Store

    void addCredentialStore() {
        Metadata metadata = metadataRegistry.lookup(CREDENTIAL_STORE_TEMPLATE);
        SafeHtml typeHelp = SafeHtmlUtils.fromString(
                metadata.getDescription().get(ATTRIBUTES).get(TYPE).get(DESCRIPTION).asString());
        Metadata crMetadata = metadata.forComplexAttribute(CREDENTIAL_REFERENCE, true);
        crMetadata.copyComplexAttributeAttributes(asList(STORE, ALIAS, TYPE, CLEAR_TEXT), metadata);
        TextBoxItem typeItem = new TextBoxItem("type-", resources.constants().type());

        String id = Ids.build(Ids.ELYTRON_CREDENTIAL_STORE, Ids.ADD);
        NameItem nameItem = new NameItem();
        ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .addOnly()
                .unboundFormItem(nameItem, 0)
                .include(CREATE, PATH, RELATIVE_TO, STORE, ALIAS, TYPE, CLEAR_TEXT)
                .unboundFormItem(typeItem, 3, typeHelp)
                .unsorted()
                .build();
        form.getFormItem(RELATIVE_TO).registerSuggestHandler(new PathsAutoComplete());
        form.addFormValidation(new RequireAtLeastOneAttributeValidation<>(asList(STORE, CLEAR_TEXT), resources));
        form.addFormValidation(form1 -> {
            ValidationResult result = ValidationResult.OK;
            String typeValue = typeItem.getValue();
            FormItem<String> locationAttr = form1.getFormItem(PATH);
            boolean invalidLocation = locationAttr.isEmpty() &&
                    (typeItem.isEmpty() || Collections.binarySearch(FILE_BASED_CS, typeValue) > -1);
            if (invalidLocation) {
                form1.getFormItem(PATH).showError(resources.constants().requiredField());
                result = ValidationResult.invalid(resources.messages().pathRequired());
            }
            return result;
        });


        new AddResourceDialog(resources.messages().addResourceTitle(Names.CREDENTIAL_STORE), form, (name, model) -> {
            if (model != null) {
                move(model, STORE, CREDENTIAL_REFERENCE + "/" + STORE);
                move(model, ALIAS, CREDENTIAL_REFERENCE + "/" + ALIAS);
                move(model, TYPE, CREDENTIAL_REFERENCE + "/" + TYPE);
                move(model, CLEAR_TEXT, CREDENTIAL_REFERENCE + "/" + CLEAR_TEXT);
            }
            if (!typeItem.isEmpty()) {
                model.get(TYPE).set(typeItem.getValue());
            }
            ResourceAddress address = CREDENTIAL_STORE_TEMPLATE.resolve(statementContext, nameItem.getValue());
            crud.add(Names.CREDENTIAL_STORE, name, address, model, (n, a) ->
                    reload(CREDENTIAL_STORE, nodes ->
                            getView().updateResourceElement(CREDENTIAL_STORE, nodes)));
        }).show();
    }

    // -------------------------------------------- Security Domain

    void addSecurityDomain() {
        Metadata metadata = metadataRegistry.lookup(SECURITY_DOMAIN_TEMPLATE);
        // emulate capability-reference on default-realm
        String capabilityReference = metadata.getDescription()
                .findAttribute(ATTRIBUTES + "/" + REALMS + "/" + VALUE_TYPE, REALM)
                .getValue()
                .get(CAPABILITY_REFERENCE)
                .asString();

        String id = Ids.build(Ids.ELYTRON_SECURITY_DOMAIN, Ids.ADD);
        NameItem nameItem = new NameItem();
        ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .addOnly()
                .unboundFormItem(nameItem, 0)
                .include(DEFAULT_REALM)
                .unsorted()
                .build();
        form.getFormItem(DEFAULT_REALM).setRequired(true);
        form.getFormItem(DEFAULT_REALM)
                .registerSuggestHandler(
                        new SuggestCapabilitiesAutoComplete(dispatcher, statementContext, capabilityReference,
                                metadata.getTemplate()));

        new AddResourceDialog(resources.messages().addResourceTitle(Names.SECURITY_DOMAIN), form, (name, model) -> {
            if (model != null) {
                // add the default-realm in the list of realms
                ModelNode realm = new ModelNode();
                realm.get(REALM).set(model.get(DEFAULT_REALM).asString());
                model.get(REALMS).add(realm);
            }
            ResourceAddress address = SECURITY_DOMAIN_TEMPLATE.resolve(statementContext, nameItem.getValue());
            crud.add(Names.SECURITY_DOMAIN, name, address, model, (n, a) ->
                    reload(SECURITY_DOMAIN, nodes ->
                            getView().updateResourceElement(SECURITY_DOMAIN, nodes)));
        }).show();
    }

    // ------------------------------------------------------ key store

    void addKeyStore() {
        Metadata metadata = metadataRegistry.lookup(KEY_STORE_TEMPLATE);
        Metadata crMetadata = metadata.forComplexAttribute(CREDENTIAL_REFERENCE, true);
        crMetadata.copyComplexAttributeAttributes(asList(STORE, ALIAS, CLEAR_TEXT), metadata);

        String id = Ids.build(Ids.ELYTRON_KEY_STORE, Ids.ADD);
        NameItem nameItem = new NameItem();

        // there is a special handling for "type" attribute, as this attribute name exists in key-store and
        // credential-reference complex attribute. We must create an unbound form item for credential-reference-type
        String crType = "credential-reference-type";
        String crTypeLabel = new LabelBuilder().label(crType);
        TextBoxItem crTypeItem = new TextBoxItem(crType, crTypeLabel);
        SafeHtml crTypeItemHelp = SafeHtmlUtils.fromString(metadata.getDescription()
                .get(ATTRIBUTES)
                .get(CREDENTIAL_REFERENCE)
                .get(VALUE_TYPE)
                .get(TYPE)
                .get(DESCRIPTION)
                .asString());

        ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .addOnly()
                .unboundFormItem(nameItem, 0)
                .include(TYPE, PATH, RELATIVE_TO, STORE, ALIAS, CLEAR_TEXT)
                .unboundFormItem(crTypeItem, 7, crTypeItemHelp)
                .unsorted()
                .build();
        form.getFormItem(RELATIVE_TO).registerSuggestHandler(new PathsAutoComplete());
        form.addFormValidation(new RequireAtLeastOneAttributeValidation<>(asList(STORE, CLEAR_TEXT), resources));

        new AddResourceDialog(resources.messages().addResourceTitle(Names.KEY_STORE), form, (name, model) -> {
            if (model != null) {
                move(model, STORE, CREDENTIAL_REFERENCE + "/" + STORE);
                move(model, ALIAS, CREDENTIAL_REFERENCE + "/" + ALIAS);
                move(model, CLEAR_TEXT, CREDENTIAL_REFERENCE + "/" + CLEAR_TEXT);
                if (!crTypeItem.isEmpty()) {
                    model.get(CREDENTIAL_REFERENCE).get(TYPE).set(crTypeItem.getValue());
                }
            }
            ResourceAddress address = KEY_STORE_TEMPLATE.resolve(statementContext, nameItem.getValue());
            crud.add(Names.KEY_STORE, name, address, model, (n, a) ->
                    reload(KEY_STORE, nodes ->
                            getView().updateResourceElement(KEY_STORE, nodes)));
        }).show();
    }


    // ------------------------------------------------------ key manager

    void addKeyManager() {
        Metadata metadata = metadataRegistry.lookup(KEY_MANAGER_TEMPLATE);
        Metadata crMetadata = metadata.forComplexAttribute(CREDENTIAL_REFERENCE, true);
        crMetadata.copyComplexAttributeAttributes(asList(STORE, ALIAS, TYPE, CLEAR_TEXT), metadata);

        String id = Ids.build(Ids.ELYTRON_KEY_MANAGER, Ids.ADD);
        NameItem nameItem = new NameItem();
        ModelNodeForm<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .addOnly()
                .unboundFormItem(nameItem, 0)
                .include(STORE, ALIAS, TYPE, CLEAR_TEXT)
                .unsorted()
                .build();
        form.addFormValidation(new RequireAtLeastOneAttributeValidation<>(asList(STORE, CLEAR_TEXT), resources));

        new AddResourceDialog(resources.messages().addResourceTitle(Names.KEY_MANAGER), form, (name, model) -> {
            if (model != null) {
                move(model, STORE, CREDENTIAL_REFERENCE + "/" + STORE);
                move(model, ALIAS, CREDENTIAL_REFERENCE + "/" + ALIAS);
                move(model, TYPE, CREDENTIAL_REFERENCE + "/" + TYPE);
                move(model, CLEAR_TEXT, CREDENTIAL_REFERENCE + "/" + CLEAR_TEXT);
            }
            ResourceAddress address = KEY_MANAGER_TEMPLATE.resolve(statementContext, nameItem.getValue());
            crud.add(Names.KEY_MANAGER, name, address, model, (n, a) ->
                    reload(KEY_MANAGER, nodes ->
                            getView().updateResourceElement(KEY_MANAGER, nodes)));
        }).show();
    }

    // ------------------------------------------------------ LDAP key store

    void reloadLdapKeyStores() {
        crud.readChildren(AddressTemplates.ELYTRON_SUBSYSTEM_TEMPLATE, ModelDescriptionConstants.LDAP_KEY_STORE,
                children -> getView().updateLdapKeyStore(asNamedNodes(children)));
    }

    void saveLdapKeyStore(String name, Map<String, Object> changedValues) {
        crud.save(Names.LDAP_KEY_STORE, name, AddressTemplates.LDAP_KEY_STORE_TEMPLATE, changedValues,
                this::reloadLdapKeyStores);
    }

    void addNewItemTemplate(String ldapKeyStore) {
        Metadata metadata = metadataRegistry.lookup(AddressTemplates.LDAP_KEY_STORE_TEMPLATE)
                .forComplexAttribute(NEW_ITEM_TEMPLATE);
        String id = Ids.build(Ids.ELYTRON_LDAP_KEY_STORE, NEW_ITEM_TEMPLATE, Ids.ADD);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .include(NEW_ITEM_PATH, NEW_ITEM_RDN, NEW_ITEM_ATTRIBUTES)
                .customFormItem(NEW_ITEM_ATTRIBUTES,
                        (attributeDescription) -> new MultiValueListItem(NEW_ITEM_ATTRIBUTES))
                .unsorted()
                .addOnly()
                .build();
        String type = new LabelBuilder().label(NEW_ITEM_TEMPLATE);
        new AddResourceDialog(resources.messages().addResourceTitle(type), form, (name, model) ->
                ca.add(ldapKeyStore, NEW_ITEM_TEMPLATE, Names.NEW_ITEM_TEMPLATE,
                        AddressTemplates.LDAP_KEY_STORE_TEMPLATE, model, this::reloadLdapKeyStores)).show();
    }

    Operation pingNewItemTemplate(String ldapKeyStore) {
        ResourceAddress address = AddressTemplates.LDAP_KEY_STORE_TEMPLATE.resolve(statementContext, ldapKeyStore);
        return new Operation.Builder(address, READ_ATTRIBUTE_OPERATION)
                .param(NAME, NEW_ITEM_TEMPLATE)
                .build();
    }

    ResourceAddress resolveTemplate(ElytronResource er, String selectedItem) {
        return er.template.resolve(statementContext, selectedItem);
    }

    void saveNewItemTemplate(String ldapKeyStore, Map<String, Object> changedValues) {
        ca.save(ldapKeyStore, NEW_ITEM_TEMPLATE, Names.NEW_ITEM_TEMPLATE, AddressTemplates.LDAP_KEY_STORE_TEMPLATE,
                changedValues, this::reloadLdapKeyStores);
    }


    void removeNewItemTemplate(String ldapKeyStore, Form<ModelNode> form) {
        ca.remove(ldapKeyStore, NEW_ITEM_TEMPLATE, Names.NEW_ITEM_TEMPLATE, AddressTemplates.LDAP_KEY_STORE_TEMPLATE,
                new FinishRemove<ModelNode>(form) {
                    @Override
                    public void afterRemove(Form<ModelNode> form) {
                        reloadLdapKeyStores();
                    }
                });
    }

    // -------------------------------------------- JASPI Configuration

    void addJaspiConfiguration() {
        Metadata metadata = metadataRegistry.lookup(AddressTemplates.JASPI_CONFIGURATION_TEMPLATE);
        Metadata metaServerAuth = metadata.forComplexAttribute(SERVER_AUTH_MODULES, true);
        metaServerAuth.copyComplexAttributeAttributes(asList(CLASS_NAME, MODULE, FLAG), metadata);
        String id = Ids.build(Ids.ELYTRON_JASPI, Ids.ADD);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .unboundFormItem(new NameItem(), 0)
                .unsorted()
                .addOnly()
                .build();
        String type = new LabelBuilder().label(JASPI_CONFIGURATION);
        form.getFormItem(CLASS_NAME).setRequired(true);
        new AddResourceDialog(resources.messages().addResourceTitle(type), form,
                (name, model) -> {
                    ModelNode serverAuthModule = new ModelNode();
                    serverAuthModule.get(CLASS_NAME).set(model.remove(CLASS_NAME));
                    if (model.hasDefined(MODULE)) {
                        serverAuthModule.get(MODULE).set(model.remove(MODULE));
                    }
                    if (model.hasDefined(FLAG)) {
                        serverAuthModule.get(FLAG).set(model.remove(FLAG));
                    }
                    model.get(SERVER_AUTH_MODULES).add(serverAuthModule);
                    crud.add(type, name, AddressTemplates.JASPI_CONFIGURATION_TEMPLATE, model,  (name1, address) -> {
                                reload(JASPI_CONFIGURATION,
                                        nodes -> getView().updateResourceElement(JASPI_CONFIGURATION, nodes));
                            });
                })
                .show();
    }

    // -------------------------------------------- server ssl sni context

    void addServerSslSniContext() {
        Metadata metadata = metadataRegistry.lookup(AddressTemplates.SERVER_SSL_SNI_CONTEXT_TEMPLATE);
        String id = Ids.build(Ids.ELYTRON_SERVER_SSL_SNI_CONTEXT, Ids.ADD);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .unboundFormItem(new NameItem(), 0)
                .unsorted()
                .addOnly()
                .build();
        // "host-context-map" is not required in r-r-d for "add" operation
        // but the resource fails to add without this parameter, see https://issues.jboss.org/browse/WFCORE-4223
        form.getFormItem("host-context-map").setRequired(true);
        String type = new LabelBuilder().label(SERVER_SSL_SNI_CONTEXT);
        new AddResourceDialog(resources.messages().addResourceTitle(type), form,
                (name, model) -> {
                    crud.add(type, name, AddressTemplates.SERVER_SSL_SNI_CONTEXT_TEMPLATE, model,
                            (name1, address) -> reload(SERVER_SSL_SNI_CONTEXT,
                                    nodes -> getView().updateResourceElement(SERVER_SSL_SNI_CONTEXT, nodes)));
                })
                .show();
    }

    // -------------------------------------------- Policy

    void addPolicy(String complexAttribute, String type) {
        Metadata metadata = metadataRegistry.lookup(POLICY_TEMPLATE).forComplexAttribute(complexAttribute);
        String id = Ids.build(Ids.ELYTRON_POLICY, complexAttribute, Ids.ADD);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .unboundFormItem(new NameItem(), 0)
                .addOnly()
                .build();
        new AddResourceDialog(type, form, (name, model) -> {
            ResourceAddress address = POLICY_TEMPLATE.resolve(statementContext, name);
            ModelNode payload = new ModelNode();
            payload.get(complexAttribute)
                    .set(model != null && model.isDefined() ? model : new ModelNode().setEmptyObject());
            crud.add(type, address, payload, resources.messages().addSingleResourceSuccess(type),
                    (n, a) -> reloadPolicy());
        }).show();
    }

    void savePolicy(String policyName, String complexAttribute, String type, Map<String, Object> changedValues) {
        ResourceAddress address = POLICY_TEMPLATE.resolve(statementContext, policyName);
        Metadata metadata = metadataRegistry.lookup(POLICY_TEMPLATE).forComplexAttribute(complexAttribute);
        ca.save(complexAttribute, type, address, changedValues, metadata, this::reloadPolicy);
    }

    void resetPolicy(String policyName, String complexAttribute, String type, Form<ModelNode> form) {
        ResourceAddress address = POLICY_TEMPLATE.resolve(statementContext, policyName);
        Metadata metadata = metadataRegistry.lookup(POLICY_TEMPLATE).forComplexAttribute(complexAttribute);
        ca.reset(complexAttribute, type, address, metadata, form, new FinishReset<ModelNode>(form) {
            @Override
            public void afterReset(Form<ModelNode> form) {
                reloadPolicy();
            }
        });
    }

    void removePolicy(String policyName, String type) {
        ResourceAddress address = POLICY_TEMPLATE.resolve(statementContext, policyName);
        crud.removeSingleton(type, address, this::reloadPolicy);
    }

    private void reloadPolicy() {
        crud.readChildren(AddressTemplates.ELYTRON_SUBSYSTEM_TEMPLATE, ModelDescriptionConstants.POLICY,
                children -> {
                    if (children.isEmpty()) {
                        getView().updatePolicy(null);
                    } else {
                        getView().updatePolicy(asNamedNodes(children).get(0));
                    }
                });
    }


    @ProxyCodeSplit
    @Requires(value = {AGGREGATE_PROVIDERS_ADDRESS,
            AGGREGATE_SECURITY_EVENT_LISTENER_ADDRESS,
            CUSTOM_SECURITY_EVENT_LISTENER_ADDRESS,
            AUTHENTICATION_CONFIGURATION_ADDRESS,
            AUTHENTICATION_CONTEXT_ADDRESS,
            CERTIFICATE_AUTHORITY_ADDRESS,
            CERTIFICATE_AUTHORITY_ACCOUNT_ADDRESS,
            CLIENT_SSL_CONTEXT_ADDRESS,
            CREDENTIAL_STORE_ADDRESS,
            DIR_CONTEXT_ADDRESS,
            FILE_AUDIT_LOG_ADDRESS,
            FILTERING_KEY_STORE_ADDRESS,
            JASPI_CONFIGURATION_ADDRESS,
            KEY_MANAGER_ADDRESS,
            KEY_STORE_ADDRESS,
            LDAP_KEY_STORE_ADDRESS,
            PERIODIC_FILE_AUDIT_LOG_ADDRESS,
            POLICY_ADDRESS,
            PROVIDER_LOADER_ADDRESS,
            SECURITY_DOMAIN_ADDRESS,
            SERVER_SSL_CONTEXT_ADDRESS,
            SERVER_SSL_SNI_CONTEXT_ADDRESS,
            SIZE_ROTATING_FILE_AUDIT_LOG_ADDRESS,
            SYSLOG_AUDIT_LOG_ADDRESS,
            TRUST_MANAGER_ADDRESS,
            PERMISSION_SET_ADDRESS})
    @NameToken(NameTokens.ELYTRON_OTHER)
    public interface MyProxy extends ProxyPlace<OtherSettingsPresenter> {
    }


    // @formatter:off
    public interface MyView extends MbuiView<OtherSettingsPresenter> {
        void updateResourceElement(String resource, List<NamedNode> nodes);
        void updateLdapKeyStore(List<NamedNode> model);
        void updatePolicy(NamedNode policy);
    }
    // @formatter:on
}
