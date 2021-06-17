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

import org.jboss.hal.core.mbui.MbuiContext;
import org.jboss.hal.core.mbui.ResourceElement;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.spi.Callback;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;

enum ElytronResource {

    ADD_PREFIX_ROLE_MAPPER(Ids.ELYTRON_ADD_PREFIX_ROLE_MAPPER,
            ModelDescriptionConstants.ADD_PREFIX_ROLE_MAPPER,
            AddressTemplates.ADD_PREFIX_ROLE_MAPPER_TEMPLATE, false),

    ADD_SUFFIX_ROLE_MAPPER(Ids.ELYTRON_ADD_SUFFIX_ROLE_MAPPER,
            ModelDescriptionConstants.ADD_SUFFIX_ROLE_MAPPER,
            AddressTemplates.ADD_SUFFIX_ROLE_MAPPER_TEMPLATE, false),

    AGGREGATE_HTTP_SERVER_MECHANISM_FACTORY(Ids.ELYTRON_AGGREGATE_HTTP_SERVER_MECHANISM_FACTORY,
            ModelDescriptionConstants.AGGREGATE_HTTP_SERVER_MECHANISM_FACTORY,
            AddressTemplates.AGGREGATE_HTTP_SERVER_MECHANISM_FACTORY_TEMPLATE, false),

    AGGREGATE_PRINCIPAL_DECODER(Ids.ELYTRON_AGGREGATE_PRINCIPAL_DECODER,
            ModelDescriptionConstants.AGGREGATE_PRINCIPAL_DECODER,
            AddressTemplates.AGGREGATE_PRINCIPAL_DECODER_TEMPLATE, false),

    AGGREGATE_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_AGGREGATE_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.AGGREGATE_PRINCIPAL_TRANSFORMER,
            AddressTemplates.AGGREGATE_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    AGGREGATE_PROVIDERS(Ids.ELYTRON_AGGREGATE_PROVIDERS,
            ModelDescriptionConstants.AGGREGATE_PROVIDERS,
            AddressTemplates.AGGREGATE_PROVIDERS_TEMPLATE, false),

    AGGREGATE_REALM(Ids.ELYTRON_AGGREGATE_REALM,
            ModelDescriptionConstants.AGGREGATE_REALM,
            AddressTemplates.AGGREGATE_REALM_TEMPLATE, false),

    AGGREGATE_ROLE_MAPPER(Ids.ELYTRON_AGGREGATE_ROLE_MAPPER,
            ModelDescriptionConstants.AGGREGATE_ROLE_MAPPER,
            AddressTemplates.AGGREGATE_ROLE_MAPPER_TEMPLATE, false),

    AGGREGATE_SASL_SERVER_FACTORY(Ids.ELYTRON_AGGREGATE_SASL_SERVER_FACTORY,
            ModelDescriptionConstants.AGGREGATE_SASL_SERVER_FACTORY,
            AddressTemplates.AGGREGATE_SASL_SERVER_FACTORY_TEMPLATE, false),

    AGGREGATE_SECURITY_EVENT_LISTENER(Ids.ELYTRON_AGGREGATE_SECURITY_EVENT_LISTENER,
            ModelDescriptionConstants.AGGREGATE_SECURITY_EVENT_LISTENER,
            AddressTemplates.AGGREGATE_SECURITY_EVENT_LISTENER_TEMPLATE, false),

    AUTHENTICATION_CONFIGURATION(Ids.ELYTRON_AUTHENTICATION_CONFIGURATION,
            ModelDescriptionConstants.AUTHENTICATION_CONFIGURATION,
            AddressTemplates.AUTHENTICATION_CONFIGURATION_TEMPLATE, false),

    AUTHENTICATION_CONTEXT(Ids.ELYTRON_AUTHENTICATION_CONTEXT,
            ModelDescriptionConstants.AUTHENTICATION_CONTEXT,
            AddressTemplates.AUTHENTICATION_CONTEXT_TEMPLATE, false),

    CACHING_REALM(Ids.ELYTRON_CACHING_REALM,
            ModelDescriptionConstants.CACHING_REALM,
            AddressTemplates.CACHING_REALM_TEMPLATE, false),

    CERTIFICATE_AUTHORITY(Ids.ELYTRON_CERTIFICATE_AUTHORITY,
            ModelDescriptionConstants.CERTIFICATE_AUTHORITY,
            AddressTemplates.CERTIFICATE_AUTHORITY_TEMPLATE, false),

    CERTIFICATE_AUTHORITY_ACCOUNT(Ids.ELYTRON_CERTIFICATE_AUTHORITY_ACCOUNT,
            ModelDescriptionConstants.CERTIFICATE_AUTHORITY_ACCOUNT,
            AddressTemplates.CERTIFICATE_AUTHORITY_ACCOUNT_TEMPLATE, false),

    CHAINED_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_CHAINED_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.CHAINED_PRINCIPAL_TRANSFORMER,
            AddressTemplates.CHAINED_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    CLIENT_SSL_CONTEXT(Ids.ELYTRON_CLIENT_SSL_CONTEXT,
            ModelDescriptionConstants.CLIENT_SSL_CONTEXT,
            AddressTemplates.CLIENT_SSL_CONTEXT_TEMPLATE, false),

    CONCATENATING_PRINCIPAL_DECODER(Ids.ELYTRON_CONCATENATING_PRINCIPAL_DECODER,
            ModelDescriptionConstants.CONCATENATING_PRINCIPAL_DECODER,
            AddressTemplates.CONCATENATING_PRINCIPAL_DECODER_TEMPLATE, false),

    CONFIGURABLE_HTTP_SERVER_MECHANISM_FACTORY(Ids.ELYTRON_CONFIGURABLE_HTTP_SERVER_MECHANISM_FACTORY,
            ModelDescriptionConstants.CONFIGURABLE_HTTP_SERVER_MECHANISM_FACTORY,
            AddressTemplates.CONFIGURABLE_HTTP_SERVER_MECHANISM_FACTORY_TEMPLATE, false),

    CONFIGURABLE_SASL_SERVER_FACTORY(Ids.ELYTRON_CONFIGURABLE_SASL_SERVER_FACTORY,
            ModelDescriptionConstants.CONFIGURABLE_SASL_SERVER_FACTORY,
            AddressTemplates.CONFIGURABLE_SASL_SERVER_FACTORY_TEMPLATE, false),

    CONSTANT_PERMISSION_MAPPER(Ids.ELYTRON_CONSTANT_PERMISSION_MAPPER,
            ModelDescriptionConstants.CONSTANT_PERMISSION_MAPPER,
            AddressTemplates.CONSTANT_PERMISSION_MAPPER_TEMPLATE, false),

    CONSTANT_PRINCIPAL_DECODER(Ids.ELYTRON_CONSTANT_PRINCIPAL_DECODER,
            ModelDescriptionConstants.CONSTANT_PRINCIPAL_DECODER,
            AddressTemplates.CONSTANT_PRINCIPAL_DECODER_TEMPLATE, false),

    CONSTANT_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_CONSTANT_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.CONSTANT_PRINCIPAL_TRANSFORMER,
            AddressTemplates.CONSTANT_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    CONSTANT_REALM_MAPPER(Ids.ELYTRON_CONSTANT_REALM_MAPPER,
            ModelDescriptionConstants.CONSTANT_REALM_MAPPER,
            AddressTemplates.CONSTANT_REALM_MAPPER_TEMPLATE, false),

    CONSTANT_ROLE_MAPPER(Ids.ELYTRON_CONSTANT_ROLE_MAPPER,
            ModelDescriptionConstants.CONSTANT_ROLE_MAPPER,
            AddressTemplates.CONSTANT_ROLE_MAPPER_TEMPLATE, false),

    CREDENTIAL_STORE(Ids.ELYTRON_CREDENTIAL_STORE,
            ModelDescriptionConstants.CREDENTIAL_STORE,
            AddressTemplates.CREDENTIAL_STORE_TEMPLATE, false),

    CUSTOM_CREDENTIAL_SECURITY_FACTORY(Ids.ELYTRON_CUSTOM_CREDENTIAL_SECURITY_FACTORY,
            ModelDescriptionConstants.CUSTOM_CREDENTIAL_SECURITY_FACTORY,
            AddressTemplates.CUSTOM_CREDENTIAL_SECURITY_FACTORY_TEMPLATE, false),

    CUSTOM_MODIFIABLE_REALM(Ids.ELYTRON_CUSTOM_MODIFIABLE_REALM,
            ModelDescriptionConstants.CUSTOM_MODIFIABLE_REALM,
            AddressTemplates.CUSTOM_MODIFIABLE_REALM_TEMPLATE, false),

    CUSTOM_PERMISSION_MAPPER(Ids.ELYTRON_CUSTOM_PERMISSION_MAPPER,
            ModelDescriptionConstants.CUSTOM_PERMISSION_MAPPER,
            AddressTemplates.CUSTOM_PERMISSION_MAPPER_TEMPLATE, false),

    CUSTOM_PRINCIPAL_DECODER(Ids.ELYTRON_CUSTOM_PRINCIPAL_DECODER,
            ModelDescriptionConstants.CUSTOM_PRINCIPAL_DECODER,
            AddressTemplates.CUSTOM_PRINCIPAL_DECODER_TEMPLATE, false),

    CUSTOM_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_CUSTOM_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.CUSTOM_PRINCIPAL_TRANSFORMER,
            AddressTemplates.CUSTOM_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    CUSTOM_REALM(Ids.ELYTRON_CUSTOM_REALM,
            ModelDescriptionConstants.CUSTOM_REALM,
            AddressTemplates.CUSTOM_REALM_TEMPLATE, false),

    CUSTOM_REALM_MAPPER(Ids.ELYTRON_CUSTOM_REALM_MAPPER,
            ModelDescriptionConstants.CUSTOM_REALM_MAPPER,
            AddressTemplates.CUSTOM_REALM_MAPPER_TEMPLATE, false),

    CUSTOM_ROLE_DECODER(Ids.ELYTRON_CUSTOM_ROLE_DECODER,
            ModelDescriptionConstants.CUSTOM_ROLE_DECODER,
            AddressTemplates.CUSTOM_ROLE_DECODER_TEMPLATE, false),

    CUSTOM_ROLE_MAPPER(Ids.ELYTRON_CUSTOM_ROLE_MAPPER,
            ModelDescriptionConstants.CUSTOM_ROLE_MAPPER,
            AddressTemplates.CUSTOM_ROLE_MAPPER_TEMPLATE, false),

    CUSTOM_SECURITY_EVENT_LISTENER(Ids.ELYTRON_CUSTOM_SECURITY_EVENT_LISTENER,
            ModelDescriptionConstants.CUSTOM_SECURITY_EVENT_LISTENER,
            AddressTemplates.CUSTOM_SECURITY_EVENT_LISTENER_TEMPLATE, false),

    DIR_CONTEXT(Ids.ELYTRON_DIR_CONTEXT,
            ModelDescriptionConstants.DIR_CONTEXT,
            AddressTemplates.DIR_CONTEXT_TEMPLATE, false),

    FILE_AUDIT_LOG(Ids.ELYTRON_FILE_AUDIT_LOG,
            ModelDescriptionConstants.FILE_AUDIT_LOG,
            AddressTemplates.FILE_AUDIT_LOG_TEMPLATE, false),

    FILESYSTEM_REALM(Ids.ELYTRON_FILESYSTEM_REALM,
            ModelDescriptionConstants.FILESYSTEM_REALM,
            AddressTemplates.FILESYSTEM_REALM_TEMPLATE, false),

    FILTERING_KEY_STORE(Ids.ELYTRON_FILTERING_KEY_STORE,
            ModelDescriptionConstants.FILTERING_KEY_STORE,
            AddressTemplates.FILTERING_KEY_STORE_TEMPLATE, false),

    HTTP_AUTHENTICATION_FACTORY(Ids.ELYTRON_HTTP_AUTHENTICATION_FACTORY,
            ModelDescriptionConstants.HTTP_AUTHENTICATION_FACTORY,
            AddressTemplates.HTTP_AUTHENTICATION_FACTORY_TEMPLATE, true),

    IDENTITY_REALM(Ids.ELYTRON_IDENTITY_REALM,
            ModelDescriptionConstants.IDENTITY_REALM,
            AddressTemplates.IDENTITY_REALM_TEMPLATE, false),

    JASPI_CONFIGURATION(Ids.ELYTRON_JASPI,
            ModelDescriptionConstants.JASPI_CONFIGURATION,
            AddressTemplates.JASPI_CONFIGURATION_TEMPLATE, false),

    JDBC_REALM(Ids.ELYTRON_JDBC_REALM,
            ModelDescriptionConstants.JDBC_REALM,
            AddressTemplates.JDBC_REALM_TEMPLATE, true),

    KERBEROS_SECURITY_FACTORY(Ids.ELYTRON_KERBEROS_SECURITY_FACTORY,
            ModelDescriptionConstants.KERBEROS_SECURITY_FACTORY,
            AddressTemplates.KERBEROS_SECURITY_FACTORY_TEMPLATE, false),

    KEY_MANAGER(Ids.ELYTRON_KEY_MANAGER,
            ModelDescriptionConstants.KEY_MANAGER,
            AddressTemplates.KEY_MANAGER_TEMPLATE, false),

    KEY_STORE(Ids.ELYTRON_KEY_STORE,
            ModelDescriptionConstants.KEY_STORE,
            AddressTemplates.KEY_STORE_TEMPLATE, false),

    KEY_STORE_REALM(Ids.ELYTRON_KEY_STORE_REALM,
            ModelDescriptionConstants.KEY_STORE_REALM,
            AddressTemplates.KEY_STORE_REALM_TEMPLATE, false),

    LDAP_KEY_STORE(Ids.ELYTRON_LDAP_KEY_STORE,
            ModelDescriptionConstants.LDAP_KEY_STORE,
            AddressTemplates.LDAP_KEY_STORE_TEMPLATE, true),

    LDAP_REALM(Ids.ELYTRON_LDAP_REALM,
            ModelDescriptionConstants.LDAP_REALM,
            AddressTemplates.LDAP_REALM_TEMPLATE, true),

    LOGICAL_PERMISSION_MAPPER(Ids.ELYTRON_LOGICAL_PERMISSION_MAPPER,
            ModelDescriptionConstants.LOGICAL_PERMISSION_MAPPER,
            AddressTemplates.LOGICAL_PERMISSION_MAPPER_TEMPLATE, false),

    LOGICAL_ROLE_MAPPER(Ids.ELYTRON_LOGICAL_ROLE_MAPPER,
            ModelDescriptionConstants.LOGICAL_ROLE_MAPPER,
            AddressTemplates.LOGICAL_ROLE_MAPPER_TEMPLATE, false),

    MAPPED_REGEX_REALM_MAPPER(Ids.ELYTRON_MAPPED_REGEX_REALM_MAPPER,
            ModelDescriptionConstants.MAPPED_REGEX_REALM_MAPPER,
            AddressTemplates.MAPPED_REGEX_REALM_MAPPER_TEMPLATE, false),

    MAPPED_ROLE_MAPPER(Ids.ELYTRON_MAPPED_ROLE_MAPPER,
            ModelDescriptionConstants.MAPPED_ROLE_MAPPER,
            AddressTemplates.MAPPED_ROLE_MAPPER_TEMPLATE, false),

    MECHANISM_PROVIDER_FILTERING_SASL_SERVER_FACTORY(Ids.ELYTRON_MECHANISM_PROVIDER_FILTERING_SASL_SERVER_FACTORY,
            ModelDescriptionConstants.MECHANISM_PROVIDER_FILTERING_SASL_SERVER_FACTORY,
            AddressTemplates.MECHANISM_PROVIDER_FILTERING_SASL_SERVER_FACTORY_TEMPLATE, false),

    PERIODIC_ROTATING_FILE_AUDIT_LOG(Ids.ELYTRON_PERIODIC_ROTATING_FILE_AUDIT_LOG,
            ModelDescriptionConstants.PERIODIC_ROTATING_FILE_AUDIT_LOG,
            AddressTemplates.PERIODIC_ROTATING_FILE_AUDIT_LOG_TEMPLATE, false),

    PERMISSION_SET(Ids.ELYTRON_PERMISSION_SET,
            ModelDescriptionConstants.PERMISSION_SET,
            AddressTemplates.PERMISSION_SET_TEMPLATE, false),

    POLICY(Ids.ELYTRON_POLICY,
            ModelDescriptionConstants.POLICY,
            AddressTemplates.POLICY_TEMPLATE, true),

    PROPERTIES_REALM(Ids.ELYTRON_PROPERTIES_REALM,
            ModelDescriptionConstants.PROPERTIES_REALM,
            AddressTemplates.PROPERTIES_REALM_TEMPLATE, false),

    PROVIDER_HTTP_SERVER_MECHANISM_FACTORY(Ids.ELYTRON_PROVIDER_HTTP_SERVER_MECHANISM_FACTORY,
            ModelDescriptionConstants.PROVIDER_HTTP_SERVER_MECHANISM_FACTORY,
            AddressTemplates.PROVIDER_HTTP_SERVER_MECHANISM_FACTORY_TEMPLATE, false),

    PROVIDER_LOADER(Ids.ELYTRON_PROVIDER_LOADER,
            ModelDescriptionConstants.PROVIDER_LOADER,
            AddressTemplates.PROVIDER_LOADER_TEMPLATE, false),

    PROVIDER_SASL_SERVER_FACTORY(Ids.ELYTRON_PROVIDER_SASL_SERVER_FACTORY,
            ModelDescriptionConstants.PROVIDER_SASL_SERVER_FACTORY,
            AddressTemplates.PROVIDER_SASL_SERVER_FACTORY_TEMPLATE, false),

    REGEX_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_REGEX_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.REGEX_PRINCIPAL_TRANSFORMER,
            AddressTemplates.REGEX_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    REGEX_VALIDATING_PRINCIPAL_TRANSFORMER(Ids.ELYTRON_REGEX_VALIDATING_PRINCIPAL_TRANSFORMER,
            ModelDescriptionConstants.REGEX_VALIDATING_PRINCIPAL_TRANSFORMER,
            AddressTemplates.REGEX_VALIDATING_PRINCIPAL_TRANSFORMER_TEMPLATE, false),

    SASL_AUTHENTICATION_FACTORY(Ids.ELYTRON_SASL_AUTHENTICATION_FACTORY,
            ModelDescriptionConstants.SASL_AUTHENTICATION_FACTORY,
            AddressTemplates.SASL_AUTHENTICATION_FACTORY_TEMPLATE, true),

    SECURITY_DOMAIN(Ids.ELYTRON_SECURITY_DOMAIN,
            ModelDescriptionConstants.SECURITY_DOMAIN,
            AddressTemplates.SECURITY_DOMAIN_TEMPLATE, false),

    SERVER_SSL_CONTEXT(Ids.ELYTRON_SERVER_SSL_CONTEXT,
            ModelDescriptionConstants.SERVER_SSL_CONTEXT,
            AddressTemplates.SERVER_SSL_CONTEXT_TEMPLATE, false),

    SERVER_SSL_SNI_CONTEXT(Ids.ELYTRON_SERVER_SSL_SNI_CONTEXT,
            ModelDescriptionConstants.SERVER_SSL_SNI_CONTEXT,
            AddressTemplates.SERVER_SSL_SNI_CONTEXT_TEMPLATE, false),

    SERVICE_LOADER_HTTP_SERVER_MECHANISM_FACTORY(Ids.ELYTRON_SERVICE_LOADER_HTTP_SERVER_MECHANISM_FACTORY,
            ModelDescriptionConstants.SERVICE_LOADER_HTTP_SERVER_MECHANISM_FACTORY,
            AddressTemplates.SERVICE_LOADER_HTTP_SERVER_MECHANISM_FACTORY_TEMPLATE, false),

    SERVICE_LOADER_SASL_SERVER_FACTORY(Ids.ELYTRON_SERVICE_LOADER_SASL_SERVER_FACTORY,
            ModelDescriptionConstants.SERVICE_LOADER_SASL_SERVER_FACTORY,
            AddressTemplates.SERVICE_LOADER_SASL_SERVER_FACTORY_TEMPLATE, false),

    SIMPLE_PERMISSION_MAPPER(Ids.ELYTRON_SIMPLE_PERMISSION_MAPPER,
            ModelDescriptionConstants.SIMPLE_PERMISSION_MAPPER,
            AddressTemplates.SIMPLE_PERMISSION_MAPPER_TEMPLATE, true),

    SIMPLE_REGEX_REALM_MAPPER(Ids.ELYTRON_SIMPLE_REGEX_REALM_MAPPER,
            ModelDescriptionConstants.SIMPLE_REGEX_REALM_MAPPER,
            AddressTemplates.SIMPLE_REGEX_REALM_MAPPER_TEMPLATE, false),

    SIMPLE_ROLE_DECODER(Ids.ELYTRON_SIMPLE_ROLE_DECODER,
            ModelDescriptionConstants.SIMPLE_ROLE_DECODER,
            AddressTemplates.SIMPLE_ROLE_DECODER_TEMPLATE, false),

    SIZE_ROTATING_FILE_AUDIT_LOG(Ids.ELYTRON_SIZE_ROTATING_FILE_AUDIT_LOG,
            ModelDescriptionConstants.SIZE_ROTATING_FILE_AUDIT_LOG,
            AddressTemplates.SIZE_ROTATING_FILE_AUDIT_LOG_TEMPLATE, false),

    SYSLOG_AUDIT_LOG(Ids.ELYTRON_SYSLOG_AUDIT_LOG,
            ModelDescriptionConstants.SYSLOG_AUDIT_LOG,
            AddressTemplates.SYSLOG_AUDIT_LOG_TEMPLATE, false),

    TOKEN_REALM(Ids.ELYTRON_TOKEN_REALM,
            ModelDescriptionConstants.TOKEN_REALM,
            AddressTemplates.TOKEN_REALM_TEMPLATE, false),

    TRUST_MANAGER(Ids.ELYTRON_TRUST_MANAGER,
            ModelDescriptionConstants.TRUST_MANAGER,
            AddressTemplates.TRUST_MANAGER_TEMPLATE, false),

    X500_ATTRIBUTE_PRINCIPAL_DECODER(Ids.ELYTRON_X500_ATTRIBUTE_PRINCIPAL_DECODER,
            ModelDescriptionConstants.X500_ATTRIBUTE_PRINCIPAL_DECODER,
            AddressTemplates.X500_ATTRIBUTE_PRINCIPAL_DECODER_TEMPLATE, false);

    final String baseId;
    final String resource;
    final AddressTemplate template;
    final boolean customElement; // TODO Is this really necessary?

    ElytronResource(String baseId, String resource, AddressTemplate template, boolean customElement) {
        this.baseId = baseId;
        this.resource = resource;
        this.template = template;
        this.customElement = customElement;
    }

    ResourceElement.Builder resourceElementBuilder(MbuiContext mbuiContext, Callback onCrud) {
        return new ResourceElement.Builder(baseId, resource,
                mbuiContext.metadataRegistry().lookup(template), mbuiContext)
                .column(NAME, (cell, type, row, meta) -> row.getName())
                .onCrud(onCrud);

    }

    ResourceElement resourceElement(MbuiContext mbuiContext, Callback onCrud) {
        return resourceElementBuilder(mbuiContext, onCrud).build();
    }
}
