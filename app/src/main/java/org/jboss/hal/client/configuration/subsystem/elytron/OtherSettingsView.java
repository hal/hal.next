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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.VerticalNavigation;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.form.SingleSelectBoxItem;
import org.jboss.hal.ballroom.table.Table;
import org.jboss.hal.core.configuration.PathsAutoComplete;
import org.jboss.hal.core.elytron.CredentialReference;
import org.jboss.hal.core.mbui.MbuiContext;
import org.jboss.hal.core.mbui.ResourceElement;
import org.jboss.hal.core.mvp.HalViewImpl;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jboss.hal.ballroom.LayoutBuilder.column;
import static org.jboss.hal.ballroom.LayoutBuilder.row;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.AGGREGATE_PROVIDERS;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.AGGREGATE_SECURITY_EVENT_LISTENER;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.AUTHENTICATION_CONFIGURATION;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.AUTHENTICATION_CONTEXT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.CERTIFICATE_AUTHORITY;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.CERTIFICATE_AUTHORITY_ACCOUNT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.CLIENT_SSL_CONTEXT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.CREDENTIAL_STORE;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.CUSTOM_SECURITY_EVENT_LISTENER;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.DIR_CONTEXT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.FILE_AUDIT_LOG;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.FILTERING_KEY_STORE;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.KEY_MANAGER;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.KEY_STORE;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.PERIODIC_ROTATING_FILE_AUDIT_LOG;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.PROVIDER_LOADER;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.SECURITY_DOMAIN;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.SERVER_SSL_CONTEXT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.SERVER_SSL_SNI_CONTEXT;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.SIZE_ROTATING_FILE_AUDIT_LOG;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.SYSLOG_AUDIT_LOG;
import static org.jboss.hal.client.configuration.subsystem.elytron.ElytronResource.TRUST_MANAGER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

public class OtherSettingsView extends HalViewImpl implements OtherSettingsPresenter.MyView {

    private final Map<String, ResourceElement> elements;
    private LdapKeyStoreElement ldapKeyStoreElement;
    private ResourceElement securityDomainElement;
    private PolicyElement policyElement;
    private VerticalNavigation navigation;
    private OtherSettingsPresenter presenter;

    @Inject
    OtherSettingsView(MbuiContext mbuiContext, CredentialReference cr) {

        elements = new HashMap<>();
        navigation = new VerticalNavigation();
        registerAttachable(navigation);

        navigation.addPrimary(Ids.ELYTRON_STORE_ITEM, "Stores", "fa fa-exchange");
        navigation.addPrimary(Ids.ELYTRON_SSL_ITEM, "SSL", "fa fa-file-o");
        navigation.addPrimary(Ids.ELYTRON_AUTHENTICATION_ITEM, "Authentication", "fa fa-terminal");
        navigation.addPrimary(Ids.ELYTRON_LOGS_ITEM, "Logs", "fa fa-folder-o");
        navigation.addPrimary(Ids.ELYTRON_OTHER_ITEM, "Other Settings", "fa fa-address-card-o");

        LabelBuilder labelBuilder = new LabelBuilder();

        // ===== store

        ResourceElement credentialStoreElement = CREDENTIAL_STORE.resourceElementBuilder(mbuiContext,
                () -> presenter.reload(CREDENTIAL_STORE.resource,
                        nodes -> updateResourceElement(CREDENTIAL_STORE.resource, nodes)))
                .onAdd(() -> presenter.addCredentialStore())
                .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, CREDENTIAL_STORE))
                .build();
        credentialStoreElement.getForm().getFormItem(RELATIVE_TO).registerSuggestHandler(new PathsAutoComplete());
        addResourceElement(CREDENTIAL_STORE, credentialStoreElement, Ids.ELYTRON_STORE_ITEM,
                Ids.build(CREDENTIAL_STORE.baseId, Ids.ITEM),
                labelBuilder.label(CREDENTIAL_STORE.resource));

        addResourceElement(FILTERING_KEY_STORE,
                FILTERING_KEY_STORE.resourceElement(mbuiContext,
                        () -> presenter.reload(FILTERING_KEY_STORE.resource,
                                nodes -> updateResourceElement(FILTERING_KEY_STORE.resource, nodes))),
                Ids.ELYTRON_STORE_ITEM,
                Ids.build(FILTERING_KEY_STORE.baseId, Ids.ITEM),
                labelBuilder.label(FILTERING_KEY_STORE.resource));

        ResourceElement keyStoreElement = KEY_STORE.resourceElementBuilder(mbuiContext,
                () -> presenter.reload(KEY_STORE.resource,
                        nodes -> updateResourceElement(KEY_STORE.resource, nodes)))
                .onAdd(() -> presenter.addKeyStore())
                .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, KEY_STORE))
                .build();
        keyStoreElement.getForm().getFormItem(RELATIVE_TO).registerSuggestHandler(new PathsAutoComplete());
        addResourceElement(KEY_STORE,
                keyStoreElement,
                Ids.ELYTRON_STORE_ITEM,
                Ids.build(KEY_STORE.baseId, Ids.ITEM),
                labelBuilder.label(KEY_STORE.resource));

        Metadata metadata = mbuiContext.metadataRegistry().lookup(AddressTemplates.LDAP_KEY_STORE_TEMPLATE);
        ldapKeyStoreElement = new LdapKeyStoreElement(metadata, mbuiContext.tableButtonFactory(),
                mbuiContext.resources());
        registerAttachable(ldapKeyStoreElement);
        navigation.addSecondary(Ids.ELYTRON_STORE_ITEM, Ids.ELYTRON_LDAP_KEY_STORE, Names.LDAP_KEY_STORE,
                ldapKeyStoreElement.element());

        // ==== SSL elements

        addResourceElement(AGGREGATE_PROVIDERS,
                AGGREGATE_PROVIDERS.resourceElement(mbuiContext,
                        () -> presenter.reload(AGGREGATE_PROVIDERS.resource,
                                nodes -> updateResourceElement(AGGREGATE_PROVIDERS.resource, nodes))),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(AGGREGATE_PROVIDERS.baseId, Ids.ITEM),
                labelBuilder.label(AGGREGATE_PROVIDERS.resource));

        addResourceElement(CLIENT_SSL_CONTEXT,
                CLIENT_SSL_CONTEXT.resourceElement(mbuiContext,
                        () -> presenter.reload(CLIENT_SSL_CONTEXT.resource,
                                nodes -> updateResourceElement(CLIENT_SSL_CONTEXT.resource, nodes))),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(CLIENT_SSL_CONTEXT.baseId, Ids.ITEM),
                labelBuilder.label(CLIENT_SSL_CONTEXT.resource));

        addResourceElement(KEY_MANAGER,
                KEY_MANAGER.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(KEY_MANAGER.resource,
                                nodes -> updateResourceElement(KEY_MANAGER.resource, nodes)))
                        .onAdd(() -> presenter.addKeyManager())
                        .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, KEY_MANAGER))
                        .build(),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(KEY_MANAGER.baseId, Ids.ITEM),
                labelBuilder.label(KEY_MANAGER.resource));

        addResourceElement(PROVIDER_LOADER,
                PROVIDER_LOADER.resourceElement(mbuiContext,
                        () -> presenter.reload(PROVIDER_LOADER.resource,
                                nodes -> updateResourceElement(PROVIDER_LOADER.resource, nodes))),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(PROVIDER_LOADER.baseId, Ids.ITEM),
                labelBuilder.label(PROVIDER_LOADER.resource));

        securityDomainElement = SECURITY_DOMAIN.resourceElementBuilder(mbuiContext,
                () -> presenter.reload(SECURITY_DOMAIN.resource,
                        nodes -> updateResourceElement(SECURITY_DOMAIN.resource, nodes)))
                .customFormItem(DEFAULT_REALM,
                        ad -> new SingleSelectBoxItem(DEFAULT_REALM, labelBuilder.label(DEFAULT_REALM),
                                Collections.emptyList(), false))
                .onAdd(() -> presenter.addSecurityDomain())
                .setComplexListAttribute(REALMS, REALM)
                .build();
        // user cannot modify realm name of the inner complext object list if it is referenced in default-realm attribute
        securityDomainElement.getFormComplexList().getFormItem(REALM).setEnabled(false);
        securityDomainElement.getFormComplexList().getFormItem(REALM).registerSuggestHandler(null);
        addResourceElement(SECURITY_DOMAIN, securityDomainElement, Ids.ELYTRON_SSL_ITEM,
                Ids.build(SECURITY_DOMAIN.baseId, Ids.ITEM), labelBuilder.label(SECURITY_DOMAIN.resource));

        addResourceElement(SERVER_SSL_CONTEXT,
                SERVER_SSL_CONTEXT.resourceElement(mbuiContext,
                        () -> presenter.reload(SERVER_SSL_CONTEXT.resource,
                                nodes -> updateResourceElement(SERVER_SSL_CONTEXT.resource, nodes))),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(SERVER_SSL_CONTEXT.baseId, Ids.ITEM),
                labelBuilder.label(SERVER_SSL_CONTEXT.resource));

        addResourceElement(SERVER_SSL_SNI_CONTEXT,
                SERVER_SSL_SNI_CONTEXT.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(SERVER_SSL_SNI_CONTEXT.resource,
                                nodes -> updateResourceElement(SERVER_SSL_SNI_CONTEXT.resource, nodes)))
                        .onAdd(() -> presenter.addServerSslSniContext())
                        .build(),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(SERVER_SSL_SNI_CONTEXT.baseId, Ids.ITEM),
                labelBuilder.label(SERVER_SSL_SNI_CONTEXT.resource));

        addResourceElement(TRUST_MANAGER,
                TRUST_MANAGER.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(TRUST_MANAGER.resource,
                                nodes -> updateResourceElement(TRUST_MANAGER.resource, nodes)))
                        .addComplexObjectAttribute("certificate-revocation-list")
                        .build(),
                Ids.ELYTRON_SSL_ITEM,
                Ids.build(TRUST_MANAGER.baseId, Ids.ITEM),
                labelBuilder.label(TRUST_MANAGER.resource));

        // ===== Authentication

        addResourceElement(AUTHENTICATION_CONFIGURATION,
                AUTHENTICATION_CONFIGURATION.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(AUTHENTICATION_CONFIGURATION.resource,
                                nodes -> updateResourceElement(AUTHENTICATION_CONFIGURATION.resource, nodes)))
                        .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, AUTHENTICATION_CONFIGURATION))
                        .build(),
                Ids.ELYTRON_AUTHENTICATION_ITEM,
                Ids.build(AUTHENTICATION_CONFIGURATION.baseId, Ids.ITEM),
                labelBuilder.label(AUTHENTICATION_CONFIGURATION.resource));

        addResourceElement(AUTHENTICATION_CONTEXT,
                AUTHENTICATION_CONTEXT.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(AUTHENTICATION_CONTEXT.resource,
                                nodes -> updateResourceElement(AUTHENTICATION_CONTEXT.resource, nodes)))
                        // display all attributes as none of them are required=true
                        .setComplexListAttribute("match-rules", asList(
                                "match-abstract-type",
                                "match-abstract-type-authority",
                                "match-host",
                                "match-local-security-domain",
                                "match-no-user",
                                "match-path",
                                "match-port",
                                "match-protocol",
                                "match-urn",
                                "match-user",
                                ModelDescriptionConstants.AUTHENTICATION_CONFIGURATION,
                                SSL_CONTEXT))
                        .build(),
                Ids.ELYTRON_AUTHENTICATION_ITEM,
                Ids.build(AUTHENTICATION_CONTEXT.baseId, Ids.ITEM),
                labelBuilder.label(AUTHENTICATION_CONTEXT.resource));

        // ======= Logs

        addResourceElement(AGGREGATE_SECURITY_EVENT_LISTENER,
                AGGREGATE_SECURITY_EVENT_LISTENER.resourceElement(mbuiContext,
                        () -> presenter.reload(AGGREGATE_SECURITY_EVENT_LISTENER.resource,
                                nodes -> updateResourceElement(AGGREGATE_SECURITY_EVENT_LISTENER.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(AGGREGATE_SECURITY_EVENT_LISTENER.baseId, Ids.ITEM),
                labelBuilder.label(AGGREGATE_SECURITY_EVENT_LISTENER.resource));

        addResourceElement(CUSTOM_SECURITY_EVENT_LISTENER,
                CUSTOM_SECURITY_EVENT_LISTENER.resourceElement(mbuiContext,
                        () -> presenter.reload(CUSTOM_SECURITY_EVENT_LISTENER.resource,
                                nodes -> updateResourceElement(CUSTOM_SECURITY_EVENT_LISTENER.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(CUSTOM_SECURITY_EVENT_LISTENER.baseId, Ids.ITEM),
                labelBuilder.label(CUSTOM_SECURITY_EVENT_LISTENER.resource));

        addResourceElement(FILE_AUDIT_LOG,
                FILE_AUDIT_LOG.resourceElement(mbuiContext,
                        () -> presenter.reload(FILE_AUDIT_LOG.resource,
                                nodes -> updateResourceElement(FILE_AUDIT_LOG.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(FILE_AUDIT_LOG.baseId, Ids.ITEM),
                labelBuilder.label(FILE_AUDIT_LOG.resource));

        addResourceElement(PERIODIC_ROTATING_FILE_AUDIT_LOG,
                PERIODIC_ROTATING_FILE_AUDIT_LOG.resourceElement(mbuiContext,
                        () -> presenter.reload(PERIODIC_ROTATING_FILE_AUDIT_LOG.resource,
                                nodes -> updateResourceElement(PERIODIC_ROTATING_FILE_AUDIT_LOG.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(PERIODIC_ROTATING_FILE_AUDIT_LOG.baseId, Ids.ITEM),
                labelBuilder.label(PERIODIC_ROTATING_FILE_AUDIT_LOG.resource));

        addResourceElement(SIZE_ROTATING_FILE_AUDIT_LOG,
                SIZE_ROTATING_FILE_AUDIT_LOG.resourceElement(mbuiContext,
                        () -> presenter.reload(SIZE_ROTATING_FILE_AUDIT_LOG.resource,
                                nodes -> updateResourceElement(SIZE_ROTATING_FILE_AUDIT_LOG.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(SIZE_ROTATING_FILE_AUDIT_LOG.baseId, Ids.ITEM),
                labelBuilder.label(SIZE_ROTATING_FILE_AUDIT_LOG.resource));

        addResourceElement(SYSLOG_AUDIT_LOG,
                SYSLOG_AUDIT_LOG.resourceElement(mbuiContext,
                        () -> presenter.reload(SYSLOG_AUDIT_LOG.resource,
                                nodes -> updateResourceElement(SYSLOG_AUDIT_LOG.resource, nodes))),
                Ids.ELYTRON_LOGS_ITEM,
                Ids.build(SYSLOG_AUDIT_LOG.baseId, Ids.ITEM),
                labelBuilder.label(SYSLOG_AUDIT_LOG.resource));

        // ====== Other settings

        addResourceElement(CERTIFICATE_AUTHORITY,
                CERTIFICATE_AUTHORITY.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(CERTIFICATE_AUTHORITY.resource,
                                nodes -> updateResourceElement(CERTIFICATE_AUTHORITY.resource, nodes)))
                        .build(),
                Ids.ELYTRON_OTHER_ITEM,
                Ids.build(CERTIFICATE_AUTHORITY.baseId, Ids.ITEM),
                labelBuilder.label(CERTIFICATE_AUTHORITY.resource));

        addResourceElement(CERTIFICATE_AUTHORITY_ACCOUNT,
                CERTIFICATE_AUTHORITY_ACCOUNT.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(CERTIFICATE_AUTHORITY_ACCOUNT.resource,
                                nodes -> updateResourceElement(CERTIFICATE_AUTHORITY_ACCOUNT.resource, nodes)))
                        .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, CERTIFICATE_AUTHORITY_ACCOUNT))
                        .build(),
                Ids.ELYTRON_OTHER_ITEM,
                Ids.build(CERTIFICATE_AUTHORITY_ACCOUNT.baseId, Ids.ITEM),
                labelBuilder.label(CERTIFICATE_AUTHORITY_ACCOUNT.resource));

        addResourceElement(DIR_CONTEXT,
                DIR_CONTEXT.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(DIR_CONTEXT.resource,
                                nodes -> updateResourceElement(DIR_CONTEXT.resource, nodes)))
                        .addComplexObjectAttributeForm(CREDENTIAL_REFERENCE, createCrForm(cr, mbuiContext, DIR_CONTEXT))
                        .build(),
                Ids.ELYTRON_OTHER_ITEM,
                Ids.build(DIR_CONTEXT.baseId, Ids.ITEM),
                labelBuilder.label(DIR_CONTEXT.resource));

        addResourceElement(ElytronResource.JASPI_CONFIGURATION,
                ElytronResource.JASPI_CONFIGURATION.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(ElytronResource.JASPI_CONFIGURATION.resource,
                                nodes -> updateResourceElement(ElytronResource.JASPI_CONFIGURATION.resource, nodes)))
                        .setComplexListAttribute(SERVER_AUTH_MODULES, asList(
                                CLASS_NAME,
                                MODULE,
                                FLAG,
                                "options"))
                        .onAdd(() -> presenter.addJaspiConfiguration())
                        .build(),
                Ids.ELYTRON_OTHER_ITEM,
                Ids.build(ElytronResource.JASPI_CONFIGURATION.baseId, Ids.ITEM),
                labelBuilder.label(ElytronResource.JASPI_CONFIGURATION.resource));

        addResourceElement(ElytronResource.PERMISSION_SET,
                ElytronResource.PERMISSION_SET.resourceElementBuilder(mbuiContext,
                        () -> presenter.reload(ElytronResource.PERMISSION_SET.resource,
                                nodes -> updateResourceElement(ElytronResource.PERMISSION_SET.resource, nodes)))
                        .setComplexListAttribute(PERMISSIONS, asList(
                                CLASS_NAME,
                                MODULE,
                                "target-name",
                                ACTION))
                        .build(),
                Ids.ELYTRON_OTHER_ITEM,
                Ids.build(ElytronResource.PERMISSION_SET.baseId, Ids.ITEM),
                labelBuilder.label(ElytronResource.PERMISSION_SET.resource));

        Metadata policyMetadata = mbuiContext.metadataRegistry().lookup(AddressTemplates.POLICY_TEMPLATE);
        policyElement = new PolicyElement(policyMetadata, mbuiContext.resources());
        registerAttachable(policyElement);
        navigation.addSecondary(Ids.ELYTRON_OTHER_ITEM, Ids.ELYTRON_POLICY, Names.POLICY, policyElement.element());


        initElement(row()
                .add(column()
                        .addAll(navigation.panes())));

    }

    private void addResourceElement(ElytronResource resource, ResourceElement element,
            String primaryId, String secondaryId, String text) {
        elements.put(resource.resource, element);
        registerAttachable(element);
        navigation.addSecondary(primaryId, secondaryId, text, element.element());
    }

    private Form<ModelNode> createCrForm(CredentialReference cr, MbuiContext mbuiContext, ElytronResource er) {
        return cr.form(er.baseId, mbuiContext.metadataRegistry().lookup(er.template), null, null,
                () -> {
                    Table<NamedNode> table = elements.get(er.resource).getTable();
                    return table.hasSelection() ? presenter.resolveTemplate(er, table.selectedRow().getName()) : null;
                },
                () -> presenter.reload()
        );
    }

    @Override
    public void updateResourceElement(String resource, List<NamedNode> nodes) {
        ResourceElement resourceElement = elements.get(resource);
        if (resourceElement != null) {
            resourceElement.update(nodes);
        }
    }

    @Override
    public void attach() {
        super.attach();

        securityDomainElement.getTable().onSelectionChange(table -> {
            // update the list of realms for default-realm attribute
            if (table.hasSelection()) {
                List<String> realmList = table.selectedRow().get(REALMS).asList().stream()
                        .map(modelNode -> modelNode.get(REALM).asString())
                        .collect(toList());
                SingleSelectBoxItem singleSelectBoxItem = (SingleSelectBoxItem) securityDomainElement.getForm().
                        <String>getFormItem(DEFAULT_REALM);
                singleSelectBoxItem.updateAllowedValues(realmList);
            }
        });

        ldapKeyStoreElement.attach();
        policyElement.attach();
    }

    @Override
    public void updateLdapKeyStore(List<NamedNode> model) {
        ldapKeyStoreElement.update(model);
    }

    @Override
    public void updatePolicy(NamedNode policy) {
        policyElement.update(policy);
    }

    @Override
    public void setPresenter(OtherSettingsPresenter presenter) {
        this.presenter = presenter;

        ldapKeyStoreElement.setPresenter(presenter);
        policyElement.setPresenter(presenter);
    }
}
