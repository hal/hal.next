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
package org.jboss.hal.resources;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.joining;
import static org.jboss.hal.resources.Strings.substringAfterLast;

/**
 * IDs used in HTML elements and across multiple classes. Please add IDs to this interface even if there's already an
 * equivalent or similar constant in {@code ModelDescriptionConstants} (SoC).
 * <p>
 * The IDs defined here are reused by QA. So please make sure that IDs are not spread over the code base but gathered in
 * this interface. This is not always possible - for instance if the ID contains dynamic parts like a resource name or
 * selected server. But IDs which only contain static strings should be part of this interface.
 */
@SuppressWarnings({"DuplicateStringLiteralInspection", "SpellCheckingInspection"})
public interface Ids {

    // ------------------------------------------------------ ids (a-z)
    // Don't compose IDs with build(), otherwise they cannot be used in annotations.
    String ABOUT_MODAL = "about-modal";
    String ABOUT_MODAL_TITLE = "about-modal-title";
    String ACCESS_CONTROL_BROWSE_BY = "access-control-browse-by";
    String ACCESS_CONTROL_BROWSE_BY_GROUPS = "access-control-browse-by-groups";
    String ACCESS_CONTROL_BROWSE_BY_ROLES = "access-control-browse-by-roles";
    String ACCESS_CONTROL_BROWSE_BY_USERS = "access-control-browse-by-users";
    String ACTIVE_OPERATION = "active-operation";
    String ACTIVE_OPERATION_EMPTY = "active-operation-empty";
    String ADD = "add";
    String AFFINITY_LOCAL = "affinity-local";
    String AFFINITY_NONE = "affinity-none";
    String AFFINITY_PRIMARY_OWNER = "affinity-primary-owner";
    String AFFINITY_RANKED = "affinity-ranked";
    String ASSIGNMENT = "assignement";
    String ASSIGNMENT_EXCLUDE = "assignement-exclude";
    String ASSIGNMENT_INCLUDE = "assignement-include";
    String ATTRIBUTES = "attributes";
    String BADEGE_ICON = "badge-icon";
    String BOOT_ERRORS_ADDRESS_COLUMN = "boot-errors-address-column";
    String BOOT_ERRORS_EMPTY = "boot-errors-empty";
    String BOOT_ERRORS_FORM = "boot-errors-form";
    String BOOT_ERRORS_OPERATION_COLUMN = "boot-errors-operation-column";
    String BOOT_ERRORS_TABLE = "boot-errors-table";
    String BREADCRUMB = "breadcrumb";
    String BROWSE_CONTENT_SELECT_EMPTY = "browse-content-select-empty";
    String BROWSE_CONTENT_DEPLOYMENT_EMPTY = "browse-content-deployment-empty";
    String BROWSE_CONTENT_EXPLODED_EMPTY = "browse-content-exploded-empty";
    String BROWSE_CONTENT_UNSUPPORTED_EMPTY = "browse-content-unsupported-empty";
    String CACHE = "cache";
    String CACHE_ADD_ACTIONS = "cache-add-actions";
    String CACHE_REFRESH = "cache-refresh";
    String CACHE_COMPONENT_EXPIRATION = "cache-component-expiration";
    String CACHE_COMPONENT_LOCKING = "cache-component-locking";
    String CACHE_COMPONENT_PARTITION_HANDLING = "cache-component-partition-handling";
    String CACHE_COMPONENT_STATE_TRANSFER = "cache-component-state-transfer";
    String CACHE_COMPONENT_TRANSACTION = "cache-component-transaction";
    String CACHE_CONTAINER = "cc";
    String CACHE_CONTAINER_ADD = "cc-add";
    String CACHE_CONTAINER_ADD_ACTIONS = "cc-add-actions";
    String CACHE_CONTAINER_FORM = "cc-form";
    String CACHE_CONTAINER_ITEM = "cc-item";
    String CACHE_CONTAINER_REFRESH = "cc-refresh";
    String CACHE_CONTAINER_THREAD_POOL_ASYNC_OPERATIONS = "cc-thread-pool-async-operations";
    String CACHE_CONTAINER_THREAD_POOL_EXPIRATION = "cc-thread-pool-expiration";
    String CACHE_CONTAINER_THREAD_POOL_LISTENER = "cc-thread-pool-listener";
    String CACHE_CONTAINER_THREAD_POOL_PERSISTENCE = "cc-thread-pool-persistence";
    String CACHE_CONTAINER_THREAD_POOL_REMOTE_COMMAND = "cc-thread-pool-remote-command";
    String CACHE_CONTAINER_THREAD_POOL_STATE_TRANSFER = "cc-thread-pool-state-transfer";
    String CACHE_CONTAINER_THREAD_POOL_TRANSPORT = "cc-thread-pool-transport";
    String CACHE_CONTAINER_THREAD_POOLS_ITEM = "cc-thread-pools-item";
    String CACHE_CONTAINER_TRANSPORT_EMPTY = "cc-transport-empty";
    String CACHE_CONTAINER_TRANSPORT_FORM = "cc-transport-form";
    String CACHE_CONTAINER_TRANSPORT_ITEM = "cc-transport-item";
    String CACHE_MEMORY_BINARY = "cache-memory-binary";
    String CACHE_MEMORY_HEAP = "cache-memory-heap";
    String CACHE_MEMORY_OBJECT = "cache-memory-object";
    String CACHE_MEMORY_OFF_HEAP = "cache-memory-off-heap";
    String CACHE_STORE_BINARY_JDBC = "cache-store-binary-jdbc";
    String CACHE_STORE_BINARY_TABLE = "binary-table";
    String CACHE_STORE_CUSTOM = "cache-store-custom";
    String CACHE_STORE_FILE = "cache-store-file";
    String CACHE_STORE_HOT_ROD = "cache-store-hot-rod";
    String CACHE_STORE_JDBC = "cache-store-jdbc";
    String CACHE_STORE_MIXED_JDBC = "cache-store-mixed-jdbc";
    String CACHE_STORE_STRING_TABLE = "string-table";
    String CACHE_STORE_WRITE_BEHIND = "behind";
    String CACHE_STORE_WRITE_THROUGH = "write";
    String CANCEL_NON_PROGRESSING_OPERATION = "cancel-non-progressing-operation";
    String CONFIGURATION = "configuration";
    String CONFIGURATION_CHANGES = "configuration-changes";
    String CONFIGURATION_CHANGES_EMPTY = "configuration-changes-empty";
    String CONFIGURATION_CHANGES_NOT_ENABLED = "configuration-changes-not-enabled";
    String CONFIGURATION_SUBSYSTEM = "css";
    String CONNECTION = "connection";
    String CONNECTION_POOL_ITEM = "connection-pool-item";
    String CONNECTION_POOL_FORM = "connection-pool-form";
    String CONTENT = "content";
    String CONTENT_ADD = "content-add";
    String CONTENT_ADD_ACTIONS = "content-add-actions";
    String CONTENT_EDITOR = "content-editor";
    String CONTENT_NEW = "content-new";
    String CONTENT_REFRESH = "content-refresh";
    String CONTENT_SEARCH = "content-search";
    String CONTENT_TAB = "content-tab";
    String CONTENT_TREE = "content-tree";
    String CONTENT_TREE_ROOT = "content-tree-root";
    String CONTENT_TREE_SEARCH = "content-tree-search";
    String CONTENT_UNMANAGED_ADD = "content-unmanaged-add";
    String COOKIE = "hal-cookie";
    String CUSTOM_LOAD_METRIC = "custom-load-metric";
    String CREDENTIAL_REFERENCE = "credential-reference";
    String DATA_SOURCE_ADD = "ds-configuration-add";
    String DATA_SOURCE_ADD_ACTIONS = "ds-configuration-add-actions";
    String DATA_SOURCE_CONFIGURATION = "ds-configuration";
    String DATA_SOURCE_CONNECTION_FORM = "ds-configuration-connection-form";
    String DATA_SOURCE_DRIVER = "data-source-driver";
    String DATA_SOURCE_DRIVER_FORM = "ds-configuration-driver-form";
    String DATA_SOURCE_FORM = "ds-configuration-form";
    String DATA_SOURCE_NAMES_FORM = "ds-configuration-names-form";
    String DATA_SOURCE_PROPERTIES_FORM = "ds-configuration-properties-form";
    String DATA_SOURCE_REFRESH = "ds-configuration-refresh";
    String DATA_SOURCE_REVIEW_FORM = "ds-configuration-review-form";
    String DATA_SOURCE_RUNTIME = "ds-runtime";
    String DATA_SOURCE_RUNTIME_JDBC_FORM = "ds-runtime-jdbc-form";
    String DATA_SOURCE_RUNTIME_JDBC_TAB = "ds-runtime-jdbc-tab";
    String DATA_SOURCE_RUNTIME_POOL_FORM = "ds-runtime-pool-form";
    String DATA_SOURCE_RUNTIME_POOL_TAB = "ds-runtime-pool-tab";
    String DATA_SOURCE_RUNTIME_STATISTICS_NOT_AVAILABLE = "ds-runtime-statistics-na";
    String DATA_SOURCE_RUNTIME_STATISTICS_NOT_ENABLED = "ds-runtime-statistics-disabled";
    String DATA_SOURCE_RUNTIME_TAB_CONTAINER = "ds-runtime-tab-container";
    String DATA_SOURCE_TEST_CONNECTION = "ds-configuration-test-connection";
    String DEPLOYMENT = "deployment";
    String DEPLOYMENT_ADD_ACTIONS = "deployment-add-actions";
    String DEPLOYMENT_BROWSE_BY = "deployment-browse-by";
    String DEPLOYMENT_EMPTY_CREATE = "deployment-empty-create";
    String DEPLOYMENT_EMPTY_FORM = "deployment-empty-form";
    String DEPLOYMENT_NOT_ENABLED_EMPTY = "deployment-not-enabled-empty";
    String DEPLOYMENT_PERMISSIONS_EMPTY = "deployment-permissions-empty";
    String DEPLOYMENT_REFRESH = "deployment-refresh";
    String DEPLOYMENT_SERVER_GROUP = "deployment-sg";
    String DEPLOYMENT_TAB = "deployment-tab";
    String DEPLOYMENT_TAB_CONTAINER = "deployment-tab-container";
    String DEPLOYMENT_UNMANAGED_ADD = "deployment-unmanaged-add";
    String DEPLOYMENT_UPLOAD = "deployment-upload";
    String DISTRIBUTABLE_WEB_ROUTING_ITEM = "dw-routing-item";
    String DISTRIBUTABLE_WEB_ROUTING_INFINISPAN = "dw-routing-infinispan";
    String DISTRIBUTABLE_WEB_ROUTING_LOCAL = "dw-routing-local";
    String DISTRIBUTABLE_WEB_ROUTING_SELECT = "dw-routing-select";
    String DISTRIBUTED_CACHE = "distributed-cache";
    String DISABLE_SSL = "disable-ssl";
    String DOMAIN_BROWSE_BY = "domain-browse-by";
    String DRAG_AND_DROP_DEPLOYMENT = "drag-and-drop-deployment";
    String EE = "ee";
    String EE_ATTRIBUTES_FORM = "ee-attributes-form";
    String EE_ATTRIBUTES_ITEM = "ee-attributes-item";
    String EE_CONTEXT_SERVICE = "ee-service-context-service";
    String EE_DEFAULT_BINDINGS_FORM = "ee-default-bindings-form";
    String EE_DEFAULT_BINDINGS_ITEM = "ee-default-bindings-item";
    String EE_GLOBAL_MODULES_FORM = "ee-global-modules-form";
    String EE_GLOBAL_MODULES_ITEM = "ee-global-modules-item";
    String EE_GLOBAL_MODULES_TABLE = "ee-global-modules-table";
    String EE_MANAGED_EXECUTOR = "ee-service-executor";
    String EE_MANAGED_EXECUTOR_SCHEDULED = "ee-service-scheduled-executor";
    String EE_MANAGED_THREAD_FACTORY = "ee-service-thread-factories";
    String EE_SERVICES_ITEM = "ee-services-item";
    String EJB3 = "ejb3";
    String EJB3_APPLICATION_SECURITY_DOMAIN_ADD = "ejb3-app-security-domain-add";
    String EJB3_APPLICATION_SECURITY_DOMAIN_FORM = "ejb3-app-security-domain-form";
    String EJB3_APPLICATION_SECURITY_DOMAIN_ITEM = "ejb3-app-security-domain-item";
    String EJB3_APPLICATION_SECURITY_DOMAIN_TABLE = "ejb3-app-security-domain-table";
    String EJB3_DEPLOYMENT = "ejb3-deployment";
    String ELYTRON = "elytron";
    String ELYTRON_ADD_PREFIX_ROLE_MAPPER = "elytron-add-prefix-role-mapper";
    String ELYTRON_ADD_SUFFIX_ROLE_MAPPER = "elytron-add-suffix-role-mapper";
    String ELYTRON_AGGREGATE_HTTP_SERVER_MECHANISM_FACTORY = "elytron-aggregate-http-server-mechanism-factory";
    String ELYTRON_AGGREGATE_PRINCIPAL_DECODER = "elytron-aggregate-principal-decoder";
    String ELYTRON_AGGREGATE_PRINCIPAL_TRANSFORMER = "elytron-aggregate-principal-transformer";
    String ELYTRON_AGGREGATE_PROVIDERS = "elytron-aggregate-providers";
    String ELYTRON_AGGREGATE_REALM = "elytron-aggregate-realm";
    String ELYTRON_AGGREGATE_ROLE_MAPPER = "elytron-aggregate-role-mapper";
    String ELYTRON_AGGREGATE_SASL_SERVER_FACTORY = "elytron-aggregate-sasl-server-factory";
    String ELYTRON_AGGREGATE_SECURITY_EVENT_LISTENER = "elytron-aggregate-security-event-listener";
    String ELYTRON_AUTHENTICATION_ITEM = "authentication-item";
    String ELYTRON_AUTHENTICATION_CONFIGURATION = "elytron-authentication-configuration";
    String ELYTRON_AUTHENTICATION_CONTEXT = "elytron-authentication-context";
    String ELYTRON_CACHING_REALM = "elytron-caching-realm";
    String ELYTRON_CERTIFICATE_AUTHORITY = "elytron-certificate-authority";
    String ELYTRON_CERTIFICATE_AUTHORITY_ACCOUNT = "elytron-certificate-authority-account";
    String ELYTRON_CHAINED_PRINCIPAL_TRANSFORMER = "elytron-chained-principal-transformer";
    String ELYTRON_CLIENT_SSL_CONTEXT = "elytron-client-ssl-context";
    String ELYTRON_CONCATENATING_PRINCIPAL_DECODER = "elytron-concatenating-principal-decoder";
    String ELYTRON_CONFIGURABLE_HTTP_SERVER_MECHANISM_FACTORY = "elytron-configurable-http-server-mechanism-factory";
    String ELYTRON_CONFIGURABLE_SASL_SERVER_FACTORY = "elytron-configurable-sasl-server-factory";
    String ELYTRON_CONSTANT_PERMISSION_MAPPER = "elytron-constant-permission-mapper";
    String ELYTRON_CONSTANT_PRINCIPAL_DECODER = "elytron-constant-principal-decoder";
    String ELYTRON_CONSTANT_PRINCIPAL_TRANSFORMER = "elytron-constant-principal-transformer";
    String ELYTRON_CONSTANT_REALM_MAPPER = "elytron-constant-realm-mapper";
    String ELYTRON_CONSTANT_ROLE_MAPPER = "elytron-constant-role-mapper";
    String ELYTRON_CREDENTIAL_STORE = "elytron-credential-store";
    String ELYTRON_CUSTOM_CREDENTIAL_SECURITY_FACTORY = "elytron-custom-credential-security-factory";
    String ELYTRON_CUSTOM_MODIFIABLE_REALM = "elytron-custom-modifiable-realm";
    String ELYTRON_CUSTOM_PERMISSION_MAPPER = "elytron-custom-permission-mapper";
    String ELYTRON_CUSTOM_POLICY_EMPTY = "elytron-custom-policy-empty";
    String ELYTRON_CUSTOM_POLICY_FORM = "elytron-custom-policy-form";
    String ELYTRON_CUSTOM_PRINCIPAL_DECODER = "elytron-custom-principal-decoder";
    String ELYTRON_CUSTOM_PRINCIPAL_TRANSFORMER = "elytron-custom-principal-transformer";
    String ELYTRON_CUSTOM_REALM = "elytron-custom-realm";
    String ELYTRON_CUSTOM_REALM_MAPPER = "elytron-custom-realm-mapper";
    String ELYTRON_CUSTOM_ROLE_DECODER = "elytron-custom-role-decoder";
    String ELYTRON_CUSTOM_ROLE_MAPPER = "elytron-custom-role-mapper";
    String ELYTRON_CUSTOM_SECURITY_EVENT_LISTENER = "elytron-custom-security-event-listener";
    String ELYTRON_DIR_CONTEXT = "elytron-dir-context";
    String ELYTRON_FACTORIES_TRANSFORMERS = "elytron-factories-transformers";
    String ELYTRON_FILE_AUDIT_LOG = "elytron-file-audit-log";
    String ELYTRON_FILESYSTEM_REALM = "elytron-filesystem-realm";
    String ELYTRON_FILTERING_KEY_STORE = "elytron-filtering-key-store";
    String ELYTRON_HTTP_AUTHENTICATION_FACTORY = "elytron-http-authentication-factory";
    String ELYTRON_IDENTITY_REALM = "elytron-identity-realm";
    String ELYTRON_JACC_POLICY_FORM = "elytron-jacc-policy-form";
    String ELYTRON_JASPI = "elytron-jaspi";
    String ELYTRON_JDBC_REALM = "elytron-jdbc-realm";
    String ELYTRON_KERBEROS_SECURITY_FACTORY = "elytron-kerberos-security-factory";
    String ELYTRON_KEY_MANAGER = "elytron-key-manager";
    String ELYTRON_KEY_STORE = "elytron-key-store";
    String ELYTRON_KEY_STORE_REALM = "elytron-key-store-realm";
    String ELYTRON_LDAP_KEY_STORE = "elytron-ldap-key-store";
    String ELYTRON_LDAP_REALM = "elytron-ldap-realm";
    String ELYTRON_LOGICAL_PERMISSION_MAPPER = "elytron-logical-permission-mapper";
    String ELYTRON_LOGICAL_ROLE_MAPPER = "elytron-logical-role-mapper";
    String ELYTRON_LOGS_ITEM = "logs-item";
    String ELYTRON_MAPPED_REGEX_REALM_MAPPER = "elytron-mapped-regex-realm-mapper";
    String ELYTRON_MAPPED_ROLE_MAPPER = "elytron-mapped-role-mapper";
    String ELYTRON_MAPPERS_DECODERS = "elytron-mappers-decoders";
    String ELYTRON_MECHANISM_PROVIDER_FILTERING_SASL_SERVER_FACTORY = "elytron-mechanism-provider-filtering-sasl-server-factory";
    String ELYTRON_OTHER_ITEM = "other-item";
    String ELYTRON_PERIODIC_ROTATING_FILE_AUDIT_LOG = "elytron-periodic-rotating-file-audit-log";
    String ELYTRON_PERMISSION_MAPPINGS_ADD = "elytron-permission-mappings-add";
    String ELYTRON_PERMISSION_MAPPINGS_FORM = "elytron-permission-mappings-form";
    String ELYTRON_PERMISSION_MAPPINGS_PAGE = "elytron-permission-mappings-page";
    String ELYTRON_PERMISSION_MAPPINGS_TABLE = "elytron-permission-mappings-table";
    String ELYTRON_PERMISSION_SET = "elytron-permission-set";
    String ELYTRON_PERMISSIONS_ADD = "elytron-permissions-add";
    String ELYTRON_PERMISSIONS_FORM = "elytron-permissions-form";
    String ELYTRON_PERMISSIONS_PAGE = "elytron-permissions-page";
    String ELYTRON_PERMISSIONS_TABLE = "elytron-permissions-table";
    String ELYTRON_POLICY = "elytron-policy";
    String ELYTRON_PROPERTIES_REALM = "elytron-properties-realm";
    String ELYTRON_PROVIDER_HTTP_SERVER_MECHANISM_FACTORY = "elytron-provider-http-server-mechanism-factory";
    String ELYTRON_PROVIDER_LOADER = "elytron-provider-loader";
    String ELYTRON_PROVIDER_SASL_SERVER_FACTORY = "elytron-provider-sasl-server-factory";
    String ELYTRON_REGEX_PRINCIPAL_TRANSFORMER = "elytron-regex-principal-transformer";
    String ELYTRON_REGEX_VALIDATING_PRINCIPAL_TRANSFORMER = "elytron-regex-validating-principal-transformer";
    String ELYTRON_RUNTIME = "elytron-runtime";
    String ELYTRON_SASL_AUTHENTICATION_FACTORY = "elytron-sasl-authentication-factory";
    String ELYTRON_SECURITY_DOMAIN = "elytron-security-domain";
    String ELYTRON_SECURITY_REALMS = "elytron-security-realms";
    String ELYTRON_SERVER_SSL_CONTEXT = "elytron-server-ssl-context";
    String ELYTRON_SERVER_SSL_SNI_CONTEXT = "elytron-server-ssl-sni-context";
    String ELYTRON_SERVICE_LOADER_HTTP_SERVER_MECHANISM_FACTORY = "elytron-service-loader-http-server-mechanism-factory";
    String ELYTRON_SERVICE_LOADER_SASL_SERVER_FACTORY = "elytron-service-loader-sasl-server-factory";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER = "elytron-simple-permission-mapper";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER_ADD = "elytron-simple-permission-mapper-add";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER_FORM = "elytron-simple-permission-mapper-form";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER_PAGE = "elytron-simple-permission-mapper-page";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER_PAGES = "elytron-simple-permission-mapper-pages";
    String ELYTRON_SIMPLE_PERMISSION_MAPPER_TABLE = "elytron-simple-permission-mapper-table";
    String ELYTRON_SIMPLE_REGEX_REALM_MAPPER = "elytron-simple-regex-realm-mapper";
    String ELYTRON_SIMPLE_ROLE_DECODER = "elytron-simple-role-decoder";
    String ELYTRON_SIZE_ROTATING_FILE_AUDIT_LOG = "elytron-size-rotating-file-audit-log";
    String ELYTRON_SSL = "ssl";
    String ELYTRON_SSL_ITEM = "ssl-item";
    String ELYTRON_STORE_ITEM = "stores-item";
    String ELYTRON_STORES = "stores";
    String ELYTRON_SYSLOG_AUDIT_LOG = "elytron-syslog-audit-log";
    String ELYTRON_TOKEN_REALM = "elytron-token-realm";
    String ELYTRON_TRUST_MANAGER = "elytron-trust-manager";
    String ELYTRON_X500_ATTRIBUTE_PRINCIPAL_DECODER = "elytron-x500-attribute-principal-decoder";
    String EMPTY = "empty";
    String ENABLE_SSL = "enable-ssl";
    String ENDPOINT = "endpoint";
    String ENDPOINT_ADD = "endpoint-add";
    String ENDPOINT_PING = "endpoint-ping";
    String ENDPOINT_REFRESH = "endpoint-refresh";
    String ENDPOINT_SELECT = "endpoint-select";
    String ENDPOINT_STORAGE = "hal-local-storage-endpoint";
    String EXTENSION = "extension";
    String EXTENSION_ADD = "extension-add";
    String EXTENSION_REVIEW_FORM = "extension-review-form";
    String EXTENSION_STORAGE = "hal-local-storage-extension";
    String EXTENSION_URL_FORM = "extension-url-form";
    String FINDER = "hal-finder";
    String FOOTER_EXTENSIONS = "footer-extensions";
    String FOOTER_EXTENSIONS_DROPDOWN = "footer-extensions-dropdown";
    String FORM = "form";
    String GROUP = "group";
    String HAL_MODAL = "hal-modal";
    String HAL_MODAL_TITLE = "hal-modal-title";
    String HAL_WIZARD = "hal-wizard";
    String HAL_WIZARD_TITLE = "hal-wizard-title";
    String HEADER = "header";
    String HEADER_CONNECTED_TO = "header-connected-to";
    String HEADER_EXTENSIONS = "header-extensions";
    String HEADER_EXTENSIONS_DROPDOWN = "header-extensions-dropdown";
    String HEADER_USERNAME = "header-username";
    String HOMEPAGE = "homepage";
    String HOMEPAGE_ACCESS_CONTROL_MODULE = "homepage-access-control-module";
    String HOMEPAGE_ACCESS_CONTROL_SECTION = "homepage-access-control-section";
    String HOMEPAGE_CONFIGURATION_MODULE = "homepage-configuration-module";
    String HOMEPAGE_CONFIGURATION_SECTION = "homepage-configuration-section";
    String HOMEPAGE_DEPLOYMENTS_MODULE = "homepage-deployments-module";
    String HOMEPAGE_DEPLOYMENTS_SECTION = "homepage-deployments-section";
    String HOMEPAGE_PATCHING_MODULE = "homepage-patching-module";
    String HOMEPAGE_PATCHING_SECTION = "homepage-patching-section";
    String HOMEPAGE_RUNTIME_MONITOR_SECTION = "homepage-runtime-monitor-section";
    String HOMEPAGE_RUNTIME_MODULE = "homepage-runtime-module";
    String HOMEPAGE_RUNTIME_SECTION = "homepage-runtime-section";
    String HOMEPAGE_RUNTIME_SERVER_GROUP_SECTION = "homepage-runtime-server-group-section";
    String HOMEPAGE_RUNTIME_SERVER_SECTION = "homepage-runtime-server-section";
    String HOST = "host";
    String HOST_PRUNE_ACTIONS = "host-prune-actions";
    String HOST_PRUNE_DISCONNECTED = "host-prune-disconnected";
    String HOST_PRUNE_EXPIRED = "host-prune-expired";
    String HOST_REFRESH = "host-refresh";
    String HTTP_INTERFACE_ITEM = "http-interface-item";
    String INLINE_ACTION = "inline-action";
    String INLINE_ACTION_DROPDOWN = "inline-action-dropdown";
    String INTERFACE = "interface";
    String INTERFACE_ADD = "interface-add";
    String INTERFACE_REFRESH = "interface-refresh";
    String INVALIDATION_CACHE = "invalidation-cache";
    String ITEM = "item";
    String JCA_ARCHIVE_VALIDATION_FORM = "jca-archive-validation-form";
    String JCA_ARCHIVE_VALIDATION_TAB = "jca-archive-validation-tab";
    String JCA_BEAN_VALIDATION_FORM = "jca-bean-validation-form";
    String JCA_BEAN_VALIDATION_TAB = "jca-bean-validation-tab";
    String JCA_BOOTSTRAP_CONTEXT_ADD = "jca-bootstrap-context-add";
    String JCA_BOOTSTRAP_CONTEXT_FORM = "jca-bootstrap-context-form";
    String JCA_BOOTSTRAP_CONTEXT_ITEM = "jca-bootstrap-context-item";
    String JCA_BOOTSTRAP_CONTEXT_TABLE = "jca-bootstrap-context-table";
    String JCA_CCM_FORM = "jca-ccm-form";
    String JCA_CCM_TAB = "jca-ccm-tab";
    String JCA_CONFIGURATION_ITEM = "jca-configuration-item";
    String JCA_DISTRIBUTED_WORKMANAGER = "jca-distributed-workmanager";
    String JCA_DISTRIBUTED_WORKMANAGER_ADD = "jca-distributed-workmanager-add";
    String JCA_DISTRIBUTED_WORKMANAGER_FORM = "jca-distributed-workmanager-form";
    String JCA_DISTRIBUTED_WORKMANAGER_ITEM = "jca-distributed-workmanager-item";
    String JCA_DISTRIBUTED_WORKMANAGER_PAGE = "jca-distributed-workmanager-page";
    String JCA_DISTRIBUTED_WORKMANAGER_PAGES = "jca-distributed-workmanager-pages";
    String JCA_DISTRIBUTED_WORKMANAGER_TABLE = "jca-distributed-workmanager-table";
    String JCA_TAB_CONTAINER = "jca-tab-container";
    String JCA_THREAD_POOL_ADD = "thread-pool-add";
    String JCA_THREAD_POOL_ATTRIBUTES_FORM = "thread-pool-attributes-form";
    String JCA_THREAD_POOL_ATTRIBUTES_TAB = "thread-pool-attributes-tab";
    String JCA_THREAD_POOL_PAGE = "thread-pool-page";
    String JCA_THREAD_POOL_SIZING_FORM = "thread-pool-sizing-form";
    String JCA_THREAD_POOL_SIZING_TAB = "thread-pool-sizing-tab";
    String JCA_THREAD_POOL_TAB_CONTAINER = "thread-pool-tab-container";
    String JCA_THREAD_POOL_TABLE = "thread-pool-table";
    String JCA_TRACER_FORM = "jca-tracer-form";
    String JCA_TRACER_ITEM = "jca-tracer-item";
    String JCA_WORKMANAGER = "jca-workmanager";
    String JCA_WORKMANAGER_ADD = "jca-workmanager-add";
    String JCA_WORKMANAGER_ITEM = "jca-workmanager-item";
    String JCA_WORKMANAGER_PAGE = "jca-workmanager-page";
    String JCA_WORKMANAGER_PAGES = "jca-workmanager-pages";
    String JCA_WORKMANAGER_TABLE = "jca-workmanager-table";
    String JDBC = "jdbc";
    String JDBC_DRIVER = "jdbc-driver";
    String JDBC_DRIVER_ADD = "jdbc-driver-add";
    String JDBC_DRIVER_ADD_FORM = "jdbc-driver-add-form";
    String JDBC_DRIVER_REFRESH = "jdbc-driver-refresh";
    String JGROUPS_CHANNEL_CONFIG = "jgroups-channel";
    String JGROUPS_CHANNEL_FORK = "fork";
    String JGROUPS_CHANNEL_FORK_ITEM = "jgroups-channel-item-fork-item";
    String JGROUPS_CHANNEL_FORK_PROTOCOL = "fork-protocol";
    String JGROUPS_CHANNEL_ITEM = "jgroups-channel-item";
    String JGROUPS_FORM = "jgroups-form";
    String JGROUPS_ITEM = "jgroups-item";
    String JGROUPS_PROTOCOL = "protocol";
    String JGROUPS_RELAY = "relay";
    String JGROUPS_REMOTE_SITE = "remote-site";
    String JGROUPS_STACK_COLUMN = "jgroups-stack-column";
    String JGROUPS_STACK_CONFIG = "jgroups-stack";
    String JGROUPS_STACK_ITEM = "jgroups-stack-item";
    String JGROUPS_TRANSPORT = "transport";
    String JGROUPS_TRANSPORT_THREADPOOL_DEFAULT_FORM = "jgroups-transport-thread-pool-default-form";
    String JGROUPS_TRANSPORT_THREADPOOL_DEFAULT_TAB = "jgroups-transport-thread-pool-default-tab";
    String JGROUPS_TRANSPORT_THREADPOOL_INTERNAL_FORM = "jgroups-transport-thread-pool-internal-form";
    String JGROUPS_TRANSPORT_THREADPOOL_INTERNAL_TAB = "jgroups-transport-thread-pool-internal-tab";
    String JGROUPS_TRANSPORT_THREADPOOL_OOB_FORM = "jgroups-transport-thread-pool-oob-form";
    String JGROUPS_TRANSPORT_THREADPOOL_OOB_TAB = "jgroups-transport-thread-pool-oob-tab";
    String JGROUPS_TRANSPORT_THREADPOOL_TAB_CONTAINER = "jgroups-transport-thread-pool-tab-container";
    String JGROUPS_TRANSPORT_THREADPOOL_TIMER_FORM = "jgroups-transport-thread-pool-timer-form";
    String JGROUPS_TRANSPORT_THREADPOOL_TIMER_TAB = "jgroups-transport-thread-pool-timer-tab";
    String JMS_BRIDGE = "jms-bridge";
    String JMS_BRIDGE_ADD = "jms-bridge-add";
    String JMS_BRIDGE_FORM = "jms-bridge-form";
    String JMS_BRIDGE_ITEM = "jms-bridge-item";
    String JMS_BRIDGE_REFRESH = "jms-bridge-refresh";
    String JMS_BRIDGE_TAB = "jms-bridge-tab";
    String JMS_MESSAGE_CHANGE_PRIORITY = "jms-message-change-priority";
    String JMS_MESSAGE_CHANGE_PRIORITY_FORM = "jms-message-change-priority-form";
    String JMS_MESSAGE_EXPIRE = "jms-message-expire";
    String JMS_MESSAGE_LIST = "jms-message-list";
    String JMS_MESSAGE_LIST_CHANGE_PRIORITY = "jms-message-list-change-priority";
    String JMS_MESSAGE_LIST_CLEAR_SELECTION = "jms-message-list-clear-selection";
    String JMS_MESSAGE_LIST_EXPIRE = "jms-message-list-expire";
    String JMS_MESSAGE_LIST_MOVE = "jms-message-list-move";
    String JMS_MESSAGE_LIST_REFRESH = "jms-message-list-refresh";
    String JMS_MESSAGE_LIST_REMOVE = "jms-message-list-remove";
    String JMS_MESSAGE_LIST_SELECT_ALL = "jms-message-list-select-all";
    String JMS_MESSAGE_LIST_SEND_TO_DEAD_LETTER = "jms-message-list-send-to-dead-letter";
    String JMS_MESSAGE_LIST_TOO_MANY = "jms-message-list-too-many";
    String JMS_MESSAGE_MOVE = "jms-message-move";
    String JMS_MESSAGE_MOVE_FORM = "jms-message-move-form";
    String JMS_MESSAGE_REMOVE = "jms-message-remove";
    String JMS_MESSAGE_SEND_TO_DEAD_LETTER = "jms-message-send-to-dead-letter";
    String JMX_AUDIT_LOG_FORM = "jmx-audit-log-form";
    String JMX_AUDIT_LOG_ITEM = "jmx-audit-log-item";
    String JMX_CONFIGURATION_FORM = "jmx-configuration-form";
    String JMX_CONFIGURATION_ITEM = "jmx-configuration-item";
    String JMX_REMOTING_CONNECTOR_FORM = "jmx-remoting-connector-form";
    String JMX_REMOTING_CONNECTOR_ITEM = "jmx-remoting-connector-item";
    String JNDI = "jndi";
    String JNDI_DETAILS = "jndi-details";
    String JNDI_SEARCH = "jndi-search";
    String JNDI_TREE = "jndi-tree";
    String JNDI_TREE_APPLICATIONS_ROOT = "jndi-tree-applications-root";
    String JNDI_TREE_JAVA_CONTEXTS_ROOT = "jndi-tree-java-contexts-root";
    String JOB = "job";
    String JOB_LIST = "job-list";
    String JOB_REFRESH = "job-refresh";
    String JOP_EXECUTION_EMPTY = "job-execution-empty";
    String JOP_EXECUTION_REFRESH = "job-execution-refresh";
    String JOP_EXECUTION_RESTART = "job-execution-restart";
    String JOP_EXECUTION_STOP = "job-execution-stop";
    String JPA_RUNTIME = "jpa-runtime-column";
    String JPA_RUNTIME_COLLECTION_ITEM = "jpa-runtime-collection-item";
    String JPA_RUNTIME_ENTITY_CACHE_ITEM = "jpa-runtime-entity-cache-item";
    String JPA_RUNTIME_ENTITY_ITEM = "jpa-runtime-entity-item";
    String JPA_RUNTIME_MAIN_ATTRIBUTES_ITEM = "jpa-runtime-main-attributes-item";
    String JPA_RUNTIME_QUERY_CACHE_ITEM = "jpa-runtime-query-cache-item";
    String JPA_RUNTIME_STATISTICS_DISABLED = "jpa-runtime-statistics-disabled";
    String JPA_RUNTIME_TAB_CONTAINER = "jpa-runtime-tab-container";
    String LOAD_METRIC = "load-metric";
    String LOCAL_CACHE = "local-cache";
    String LOG_FILE = "lf";
    String LOG_FILE_EDITOR = "lf-editor";
    String LOG_FILE_FOLLOW = "lf-follow";
    String LOG_FILE_REFRESH = "lf-refresh";
    String LOG_FILE_SEARCH = "lf-search";
    String LOGGING_CONFIG_AND_PROFILES = "lcap";
    String LOGGING_CONFIGURATION = "logging-configuration";
    String LOGGING_FORMATTER_ITEM = "logging-formatter-item";
    String LOGGING_PROFILE = "logging-profile";
    String LOGGING_PROFILE_ADD = "logging-profile-add";
    String LOGOUT_LINK = "logout-link";
    String MACRO_EDITOR = "macro-editor";
    String MACRO_EMPTY = "macro-empty";
    String MACRO_LIST = "macro-list";
    String MACRO_OPTIONS = "macro-options";
    String MACRO_STORAGE = "hal-local-storage-macro";
    String MAIL_SERVER = "mail-server";
    String MAIL_SERVER_DIALOG = "mail-server-add-form";
    String MAIL_SERVER_FORM = "mail-server-form";
    String MAIL_SERVER_ITEM = "mail-server-item";
    String MAIL_SERVER_TAB_CONTAINER = "mail-server-tab-container";
    String MAIL_SERVER_TABLE = "mail-server-table";
    String MAIL_SESSION = "mail-session";
    String MAIL_SESSION_ADD = "mail-session-add";
    String MAIL_SESSION_DIALOG = "mail-session-form";
    String MAIL_SESSION_FORM = "mail-session-form";
    String MAIL_SESSION_ITEM = "mail-session-item";
    String MAIL_SESSION_REFRESH = "mail-session-refresh";
    String MANAGEMENT = "management";
    String MANAGEMENT_OPERATIONS = "mgmt-operations";
    String MEMBERSHIP = "membership";
    String MEMBERSHIP_EXCLUDE = "membership-exclude";
    String MEMBERSHIP_INCLUDE = "membership-include";
    String MESSAGES_LINK = "messages-link";
    String MESSAGING_ACCEPTOR = "messaging-acceptor";
    String MESSAGING_ADDRESS_SETTING = "messaging-address-setting";
    String MESSAGING_BRIDGE = "messaging-bridge";
    String MESSAGING_BROADCAST_GROUP = "messaging-broadcast-group";
    String MESSAGING_CATEGORY = "messaging-category";
    String MESSAGING_CLUSTER_CONNECTION = "messaging-cluster-connection";
    String MESSAGING_CONNECTION_FACTORY = "messaging-connection-factory";
    String MESSAGING_CONNECTOR = "messaging-connector";
    String MESSAGING_CONNECTOR_SERVICE = "messaging-connector-service";
    String MESSAGING_CORE_QUEUE = "messaging-core-queue";
    String MESSAGING_DISCOVERY_GROUP = "messaging-discovery-group";
    String MESSAGING_DIVERT = "messaging-divert";
    String MESSAGING_REMOTE_ACTIVEMQ = "msg-remote-activemq";
    String MESSAGING_GLOBAL_SETTINGS = "messaging-global-settings";
    String MESSAGING_GROUPING_HANDLER = "messaging-grouping-handler";
    String MESSAGING_HA_CHOOSE_STRATEGY = "messaging-ha-choose-strategy";
    String MESSAGING_HA_POLICY_EMPTY = "messaging-ha-policy=empty";
    String MESSAGING_HA_REPLICATION = "messaging-ha-replication";
    String MESSAGING_HA_REPLICATION_COLOCATED = "messaging-ha-replication-colocated";
    String MESSAGING_HA_REPLICATION_COLOCATED_MASTER = "messaging-ha-replication-colocated-master";
    String MESSAGING_HA_REPLICATION_COLOCATED_SLAVE = "messaging-ha-replication-colocated-slave";
    String MESSAGING_HA_REPLICATION_LIVE_ONLY = "messaging-ha-replication-live-only";
    String MESSAGING_HA_REPLICATION_MASTER = "messaging-ha-replication-master";
    String MESSAGING_HA_REPLICATION_SLAVE = "messaging-ha-replication-replication-slave";
    String MESSAGING_HA_SHARED_STORE = "messaging-ha-shared-store";
    String MESSAGING_HA_SHARED_STORE_COLOCATED = "messaging-ha-shared-store-colocated";
    String MESSAGING_HA_SHARED_STORE_COLOCATED_MASTER = "messaging-ha-shared-store-colocated-master";
    String MESSAGING_HA_SHARED_STORE_COLOCATED_SLAVE = "messaging-ha-shared-store-colocated-slave";
    String MESSAGING_HA_SHARED_STORE_MASTER = "messaging-ha-shared-store-master";
    String MESSAGING_HA_SHARED_STORE_SLAVE = "messaging-ha-shared-store-slave";
    String MESSAGING_HTTP_ACCEPTOR = "messaging-http-acceptor";
    String MESSAGING_HTTP_CONNECTOR = "messaging-http-connector";
    String MESSAGING_IN_VM_ACCEPTOR = "messaging-in-vm-acceptor";
    String MESSAGING_IN_VM_CONNECTOR = "messaging-in-vm-connector";
    String MESSAGING_JMS_QUEUE = "messaging-jms-queue";
    String MESSAGING_JMS_TOPIC = "messaging-jms-topic";
    String MESSAGING_POOLED_CONNECTION_FACTORY = "messaging-pooled-connection-factory";
    String MESSAGING_REMOTE_ACCEPTOR = "messaging-remote-acceptor";
    String MESSAGING_REMOTE_CONNECTOR = "messaging-remote-connector";
    String MESSAGING_SECURITY_SETTING_ROLE_ADD = "messaging-security-setting-role-add";
    String MESSAGING_SECURITY_SETTING_ROLE_FORM = "messaging-security-setting-role-form";
    String MESSAGING_SECURITY_SETTING_ROLE_ITEM = "messaging-security-setting-role-item";
    String MESSAGING_SECURITY_SETTING_ROLE_TABLE = "messaging-security-setting-role-table";
    String MESSAGING_SERVER = "msg-server";
    String MESSAGING_SERVER_ADD = "msg-server-c-add";
    String MESSAGING_SERVER_BINDING_DIRECTORY = "msg-server-bindings-directory";
    String MESSAGING_SERVER_BINDING_DIRECTORY_FORM = "msg-server-bindings-directory-form";
    String MESSAGING_SERVER_CLUSTERING = "msg-server-clustering";
    String MESSAGING_SERVER_CONFIGURATION = "msg-server-c";
    String MESSAGING_SERVER_CONFIGURATION_REFRESH = "msg-server-c-refresh";
    String MESSAGING_SERVER_CONNECTION = "msg-server-connection";
    String MESSAGING_SERVER_CONNECTION_PAGE = "msg-server-connection-page";
    String MESSAGING_SERVER_CONNECTION_PAGES = "msg-server-connection-pages";
    String MESSAGING_SERVER_CONNECTION_CLOSE_TABS = "msg-server-connection-close-for-address-form";
    String MESSAGING_SERVER_CONNECTION_CLOSE_CONSUMER_FORM = "msg-server-connection-close-consumer-form";
    String MESSAGING_SERVER_CONNECTION_CLOSE_CONSUMER_TAB = "msg-server-connection-close-consumer-tab";
    String MESSAGING_SERVER_CONNECTION_CLOSE_FOR_ADDRESS_FORM = "msg-server-connection-close-for-address-form";
    String MESSAGING_SERVER_CONNECTION_CLOSE_FOR_ADDRESS_TAB = "msg-server-connection-close-for-address-tab";
    String MESSAGING_SERVER_CONNECTION_CLOSE_FOR_USER_FORM = "msg-server-connection-close-for-user-form";
    String MESSAGING_SERVER_CONNECTION_CLOSE_FOR_USER_TAB = "msg-server-connection-close-for-user-tab";
    String MESSAGING_SERVER_CONNECTION_ITEM = "msg-server-connection-item";
    String MESSAGING_SERVER_CONNECTION_FORM = "msg-server-connection-form";
    String MESSAGING_SERVER_CONNECTION_TABLE = "msg-server-connection-table";
    String MESSAGING_SERVER_CONNECTION_CONSUMER_FORM = "msg-server-connection-consumer-form";
    String MESSAGING_SERVER_CONNECTION_CONSUMER_PAGE = "msg-server-connection-consumer-page";
    String MESSAGING_SERVER_CONNECTION_CONSUMER_TABLE = "msg-server-connection-consumer-table";
    String MESSAGING_SERVER_CONSUMER_ITEM = "msg-server-consumer-item";
    String MESSAGING_SERVER_CONSUMER_FORM = "msg-server-consumer-form";
    String MESSAGING_SERVER_CONSUMER_TABLE = "msg-server-consumer-table";
    String MESSAGING_SERVER_CONNECTOR_ITEM = "msg-server-connector-item";
    String MESSAGING_SERVER_CONNECTOR_FORM = "msg-server-connector-form";
    String MESSAGING_SERVER_CONNECTOR_TABLE = "msg-server-connector-table";
    String MESSAGING_SERVER_DESTINATION = "msg-server-destination";
    String MESSAGING_SERVER_DESTINATION_RUNTIME = "msg-server-destination-r";
    String MESSAGING_SERVER_DESTINATION_REFRESH = "msg-server-destination-refresh";
    String MESSAGING_SERVER_DIRECTORY_ITEM = "msg-server-directory-item";
    String MESSAGING_SERVER_HA_POLICY = "msg-server-ha-policy";
    String MESSAGING_SERVER_JOURNAL_DIRECTORY = "msg-server-journal-directory";
    String MESSAGING_SERVER_JOURNAL_DIRECTORY_FORM = "msg-server-journal-directory-form";
    String MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY = "msg-server-large-messages-directory";
    String MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY_FORM = "msg-server-large-messages-directory-form";
    String MESSAGING_SERVER_PAGING_DIRECTORY = "msg-server-paging-directory";
    String MESSAGING_SERVER_PAGING_DIRECTORY_FORM = "msg-server-paging-directory-form";
    String MESSAGING_SERVER_PRODUCER_ITEM = "msg-server-producer-item";
    String MESSAGING_SERVER_PRODUCER_FORM = "msg-server-producer-form";
    String MESSAGING_SERVER_PRODUCER_TABLE = "msg-server-producer-table";
    String MESSAGING_SERVER_ROLE_ITEM = "msg-server-role-item";
    String MESSAGING_SERVER_ROLE_FORM = "msg-server-role-form";
    String MESSAGING_SERVER_ROLE_TABLE = "msg-server-role-table";
    String MESSAGING_SERVER_RUNTIME = "msg-server-r";
    String MESSAGING_SERVER_RUNTIME_REFRESH = "msg-server-r-refresh";
    String MESSAGING_SERVER_SESSION_FORM = "msg-server-session-form";
    String MESSAGING_SERVER_SESSION_PAGE = "msg-server-session-page";
    String MESSAGING_SERVER_SESSION_TABLE = "msg-server-session-table";
    String MESSAGING_SERVER_SETTINGS = "msg-server-settings";
    String MESSAGING_SERVER_TRANSACTION_ITEM = "msg-server-transaction-item";
    String MESSAGING_SERVER_TRANSACTION_FORM = "msg-server-transaction-form";
    String MESSAGING_SERVER_TRANSACTION_TABLE = "msg-server-transaction-table";
    String MICRO_PROFILE_CONFIG_SOURCE = "microprofile-config-source";
    String MICRO_PROFILE_HEALTH = "microprofile-health";
    String MICRO_PROFILE_METRICS_FORM = "microprofile-metrics-form";
    String MODCLUSTER_PROXY = "modcluster-proxy";
    String MODCLUSTER_PROXY_ADD = "modcluster-proxy-add";
    String MODCLUSTER_PROXY_REFRESH = "modcluster-proxy-refresh";
    String MODEL_BROWSER = "model-browser";
    String MODEL_BROWSER_CREATE_SINGLETON_FORM = "model-browser-create-singleton-form";
    String MODEL_BROWSER_ROOT = "model-browser-root";
    String NO_MATCH = "no-match";
    String NONE_PROGRESSING_LINK = "none-progressing-link";
    String NATIVE_INTERFACE_ITEM = "native-interface-item";
    String NEAR_CACHE_NONE = "near-cache-none";
    String NEAR_CACHE_INVALIDATION = "near-cache-invalidation";
    String NOTIFICATION_DRAWER_CLEAR_ALL = "notification-drawer-clear-all";
    String NOTIFICATION_DRAWER_CLOSE = "notification-drawer-close";
    String NOTIFICATION_DRAWER_MARK_ALL_READ = "notification-drawer-mark-all-read";
    String PAGE = "page";
    String PAGES = "pages";
    String PATCH_ADD = "patch-add";
    String PATCH_UPLOAD_NAMES_FORM = "patch-names-form";
    String PATCHES_AGEOUT = "patching-ageout-history";
    String PATCHES_REFRESH = "patching-refresh";
    String PATCHING = "patching";
    String PATCHING_DOMAIN = "patching-domain";
    String POOL = "pool";
    String PREVIEW_ID = "hal-finder-preview";
    String PROFILE = "profile";
    String PROFILE_ADD = "profile-add";
    String PROFILE_CLONE = "profile-clone";
    String PROFILE_REFRESH = "profile-refresh";
    String REFERENCE_SERVER_EMPTY = "reference-server-empty";
    String REFRESH = "refresh";
    String RELOAD_LINK = "reload-link";
    String REMOTE_CACHE_CONTAINER_ADD = "rcc-add";
    String REMOTE_CACHE_CONTAINER_CONFIGURATION_FORM = "rcc-configuration-form";
    String REMOTE_CACHE_CONTAINER_CONFIGURATION_TAB = "rcc-configuration-tab";
    String REMOTE_CACHE_CONTAINER_ITEM = "rcc-item";
    String REMOTE_CACHE_CONTAINER_FORM = "rcc-form";
    String REMOTE_CACHE_CONTAINER_NEAR_CACHE_TAB = "rcc-near-cache-tab";
    String REMOTE_CACHE_CONTAINER_TABS = "rcc-tabs";
    String REMOTE_CLUSTER_ADD = "rc-add";
    String REMOTE_CLUSTER_ITEM = "rc-item";
    String REMOTE_CLUSTER_FORM = "rc-form";
    String REMOTE_CLUSTER_TABLE = "rc-table";
    String REMOTING_CONNECTOR_FORM = "remoting-connector-form";
    String REMOTING_CONNECTOR_SECURITY_FORM = "remoting-connector-security-form";
    String REMOTING_CONNECTOR_SECURITY_POLICY_FORM = "remoting-connector-security-policy-form";
    String REMOTING_CONNECTOR_SECURITY_POLICY_TAB = "remoting-connector-security-policy-tab";
    String REMOTING_CONNECTOR_SECURITY_TAB = "remoting-connector-security-tab";
    String REMOTING_CONNECTOR_TAB = "remoting-connector-tab";
    String REMOTING_CONNECTOR_TAB_CONTAINER = "remoting-connector-tab-container";
    String REMOTING_HTTP_CONNECTOR_FORM = "remoting-http-connector-form";
    String REMOTING_HTTP_CONNECTOR_SECURITY_FORM = "remoting-http-connector-security-form";
    String REMOTING_HTTP_CONNECTOR_SECURITY_POLICY_FORM = "remoting-http-connector-security-policy-form";
    String REMOTING_HTTP_CONNECTOR_SECURITY_POLICY_TAB = "remoting-http-connector-security-policy-tab";
    String REMOTING_HTTP_CONNECTOR_SECURITY_TAB = "remoting-http-connector-security-tab";
    String REMOTING_HTTP_CONNECTOR_TAB = "remoting-http-connector-tab";
    String REMOTING_HTTP_CONNECTOR_TAB_CONTAINER = "remoting-http-connector-tab-container";
    String REPLICATED_CACHE = "replicated-cache";
    String RESET_MESSAGE_COUNTERS = "reset-message-counters";
    String RESOLVE_EXPRESSION_FORM = "resolve-expression-form";
    String RESOURCE_ADAPTER = "resource-adapter";
    String RESOURCE_ADAPTER_ADD = "resource-adapter-add";
    String RESOURCE_ADAPTER_ADMIN_OBJECT_ADD = "resource-adapter-admin-object-add";
    String RESOURCE_ADAPTER_CONNECTION_DEFINITION_ADD = "resource-adapter-connection-definition-add";
    String RESOURCE_ADAPTER_FORM = "resource-adapter-form";
    String REST_RESOURCE = "rest-rsc";
    String REST_RESOURCE_PATH_PARAM_FORM = "rest-rsc-path-param-form";
    String REST_RESOURCE_REFRESH = "rest-rsc-refresh";
    String ROLE = "role";
    String ROLE_ADD = "role-add";
    String ROLE_HOST_SCOPED_ADD = "role-host-add";
    String ROLE_HOST_SCOPED_FORM = "role-host-form";
    String ROLE_MAPPING_FORM = "role-mapping-form";
    String ROLE_REFRESH = "role-refresh";
    String ROLE_SERVER_GROUP_SCOPED_ADD = "role-server-group-add";
    String ROLE_SERVER_GROUP_SCOPED_FORM = "role-server-group-form";
    String ROOT_CONTAINER = "hal-root-container";
    String RUNTIME_SUBSYSTEM = "rss";
    String SCATTERED_CACHE = "scattered-cache";
    String SEARCH = "search";
    String SECURITY = "security";
    String SECURITY_ITEM = "security-item";
    String SECURITY_FORM = "security-form";
    String SECURITY_DOMAIN = "sd";
    String SECURITY_DOMAIN_ACL_MODULE_ADD = "security-domain-acl-add";
    String SECURITY_DOMAIN_ADD = "sd-add";
    String SECURITY_DOMAIN_AUDIT_ADD = "security-domain-provider-add";
    String SECURITY_DOMAIN_AUTHENTICATION_ADD = "security-domain-authentication-add";
    String SECURITY_DOMAIN_AUTHORIZATION_ADD = "security-domain-authorization-add";
    String SECURITY_DOMAIN_MAPPING_ADD = "security-domain-mapping-add";
    String SECURITY_DOMAIN_TRUST_MODULE_ADD = "security-domain-identity-trust-add";
    String SECURITY_MANAGER_MAXIMUM_PERMISSIONS = "sm-max-permissions";
    String SECURITY_MANAGER_MINIMUM_PERMISSIONS = "sm-min-permissions";
    String SERVER = "server";
    String SERVER_ADD = "server-add";
    String SERVER_GROUP = "server-group";
    String SERVER_GROUP_ADD = "server-group-add";
    String SERVER_GROUP_DEPLOYMENT = "server-group-deployment";
    String SERVER_GROUP_DEPLOYMENT_ADD = "server-group-deployment-add";
    String SERVER_GROUP_DEPLOYMENT_ADD_ACTIONS = "server-group-deployment-add-actions";
    String SERVER_GROUP_DEPLOYMENT_ENABLE = "server-group-deployment-enable";
    String SERVER_GROUP_DEPLOYMENT_REFRESH = "server-group-deployment-refresh";
    String SERVER_GROUP_DEPLOYMENT_TABLE = "server-group-deployment-table";
    String SERVER_GROUP_DEPLOYMENT_UNMANAGED_ADD = "server-group-deployment-unmanaged-add";
    String SERVER_GROUP_DEPLOYMENT_UPLOAD = "server-group-deployment-upload";
    String SERVER_GROUP_REFRESH = "server-group-refresh";
    String SERVER_REFRESH = "server-refresh";
    String SERVER_RUNTIME_BOOTSTRAP_FORM = "server-runtime-bootstrap-form";
    String SERVER_RUNTIME_ITEM = "server-runtime-item";
    String SERVER_RUNTIME_JVM_ATTRIBUTES_FORM = "server-runtime-jvm-attributes-form";
    String SERVER_RUNTIME_PROPERTIES_TABLE = "server-runtime-properties-table";
    String SERVER_RUNTIME_STATUS = "server-runtime-status";
    String SERVER_RUNTIME_STATUS_HEAP_COMMITTED = "server-runtime-status-heap-committed";
    String SERVER_RUNTIME_STATUS_HEAP_USED = "server-runtime-status-heap-used";
    String SERVER_RUNTIME_STATUS_NON_HEAP_COMMITTED = "server-runtime-status-non-heap-committed";
    String SERVER_RUNTIME_STATUS_NON_HEAP_USED = "server-runtime-status-non-heap-used";
    String SERVER_RUNTIME_STATUS_THREADS = "server-runtime-status-threads";
    String SERVER_STATUS_BOOTSTRAP_ITEM = "server-runtime-bootstrap-item";
    String SERVER_STATUS_MAIN_ATTRIBUTES_ITEM = "server-runtime-main-attributes-item";
    String SERVER_STATUS_SYSTEM_PROPERTIES_ITEM = "server-runtime-system-properties-item";
    String SERVER_URL_FORM = "server-url-form";
    String SERVER_URL_STORAGE = "hal-local-storage-server-url";
    String SESSION = "session";
    String SETTINGS_FORM = "settings-form";
    String SOCKET_BINDING_GROUP = "socket-binding-group";
    String SOCKET_BINDING_GROUP_ADD = "socket-binding-group-add";
    String SOCKET_BINDING_GROUP_INBOUND = "socket-binding-group-inbound";
    String SOCKET_BINDING_GROUP_INBOUND_CLIENT_MAPPING_ADD = "socket-binding-group-inbound-client-mapping-add";
    String SOCKET_BINDING_GROUP_INBOUND_CLIENT_MAPPING_FORM = "socket-binding-group-inbound-client-mapping-form";
    String SOCKET_BINDING_GROUP_INBOUND_CLIENT_MAPPING_PAGE = "socket-binding-group-inbound-client-mapping-page";
    String SOCKET_BINDING_GROUP_INBOUND_CLIENT_MAPPING_TABLE = "socket-binding-group-inbound-client-mapping-table";
    String SOCKET_BINDING_GROUP_OUTBOUND_LOCAL = "socket-binding-group-outbound-local";
    String SOCKET_BINDING_GROUP_OUTBOUND_REMOTE = "socket-binding-group-outbound-remote";
    String SOCKET_BINDING_GROUP_REFRESH = "socket-binding-group-refresh";
    String STANDALONE_HOST = "standalone-host";
    String STANDALONE_SERVER_COLUMN = "standalone-server-column";
    String STATEMENTS = "statements";
    String STORAGE = "hal-local-storage";
    String SYSTEM_PROPERTY_ADD = "system-property-add";
    String SYSTEM_PROPERTY_FORM = "system-property-form";
    String SYSTEM_PROPERTY_TABLE = "system-property-table";
    String TAB = "tab";
    String TAB_CONTAINER = "tab-container";
    String TABLE = "table";
    String TASKS_ACTIVE = "tasks-active";
    String TASKS_COMPLETED = "tasks-completed";
    String TASKS_QUEUE = "tasks-queue";
    String TASKS_REJECTED = "tasks-rejected";
    String THREAD_POOL_ITEM = "thread-pool-item";
    String THREAD_POOL_FORM = "thread-pool-form";
    String TIMEOUT = "timeout";
    String TLC_ACCESS_CONTROL = "tlc-access-control";
    String TLC_ACCESS_CONTROL_SSO = "tlc-access-control-sso";
    String TLC_CONFIGURATION = "tlc-configuration";
    String TLC_DEPLOYMENTS = "tlc-deployments";
    String TLC_HOMEPAGE = "tlc-homepage";
    String TLC_MANAGEMENT = "tlc-management";
    String TLC_PATCHING = "tlc-patching";
    String TLC_RUNTIME = "tlc-runtime";
    String TOOLBAR = "toolbar";
    String TOOLBAR_ACTION_DROPDOWN = "toolbar-action-dropdown";
    String TOOLBAR_FILTER = "toolbar-filter";
    String TOUR_BUTTON_BACK = "tour-button-back";
    String TOUR_BUTTON_DONE = "tour-button-done";
    String TOUR_BUTTON_NEXT = "tour-button-next";
    String TRANSACTION = "transaction";
    String TRANSACTION_PARTICIPANTS_PAGE = "transaction-Participants-page";
    String TRANSACTION_STATISTICS_DISABLED = "transaction-statistics-disabled";
    String UNDERTOW_APP_SECURITY_DOMAIN = "undertow-application-security-domain";
    String UNDERTOW_APP_SECURITY_DOMAIN_ADD = "undertow-application-security-domain-add";
    String UNDERTOW_APP_SECURITY_DOMAIN_FORM = "undertow-application-security-domain-form";
    String UNDERTOW_APP_SECURITY_DOMAIN_REFRESH = "undertow-application-security-domain-refresh";
    String UNDERTOW_APP_SECURITY_DOMAIN_TAB = "undertow-application-security-domain-tab";
    String UNDERTOW_APP_SECURITY_DOMAIN_TAB_CONTAINER = "undertow-application-security-domain-tab-container";
    String UNDERTOW_DEPLOYMENT_STATISTICS_DISABLED = "undertow-deployment-statistics-disabled";
    String UNDERTOW_GLOBAL_SETTINGS = "undertow-global-settings";
    String UNDERTOW_HOST_ACCESS_LOG = "undertow-host-access-log";
    String UNDERTOW_HOST_ACTION_COLUMN = "undertow-host-action-column";
    String UNDERTOW_HOST_ADD = "undertow-host-add";
    String UNDERTOW_HOST_ATTRIBUTES_FORM = "undertow-host-form";
    String UNDERTOW_HOST_ATTRIBUTES_TAB = "undertow-host-tab";
    String UNDERTOW_HOST_ATTRIBUTES_TAB_CONTAINER = "undertow-host-tab-container";
    String UNDERTOW_HOST_FILTER_REF_ADD = "undertow-host-filter-ref-add";
    String UNDERTOW_HOST_FILTER_REF_FORM = "undertow-host-filter-ref-form";
    String UNDERTOW_HOST_FILTER_REF_PAGE = "undertow-host-filter-ref-page";
    String UNDERTOW_HOST_FILTER_REF_TABLE = "undertow-host-filter-ref-table";
    String UNDERTOW_HOST_HTTP_INVOKER = "undertow-host-http-invoker";
    String UNDERTOW_HOST_ITEM = "undertow-host-item";
    String UNDERTOW_HOST_LOCATION_ADD = "undertow-host-location-add";
    String UNDERTOW_HOST_LOCATION_FILTER_REF_ADD = "undertow-host-location-filter-ref-add";
    String UNDERTOW_HOST_LOCATION_FILTER_REF_FORM = "undertow-host-location-filter-ref-form";
    String UNDERTOW_HOST_LOCATION_FILTER_REF_PAGE = "undertow-host-location-filter-ref-page";
    String UNDERTOW_HOST_LOCATION_FILTER_REF_TABLE = "undertow-host-location-filter-ref-table";
    String UNDERTOW_HOST_LOCATION_FORM = "undertow-host-location-form";
    String UNDERTOW_HOST_LOCATION_PAGE = "undertow-host-location-page";
    String UNDERTOW_HOST_LOCATION_TABLE = "undertow-host-location-table";
    String UNDERTOW_HOST_MAIN_PAGE = "undertow-host-main-page";
    String UNDERTOW_HOST_PAGES = "undertow-host-pages";
    String UNDERTOW_HOST_SINGLE_SIGN_ON = "undertow-host-single-sign-on";
    String UNDERTOW_HOST_TABLE = "undertow-host-table";
    String UNDERTOW_LISTENER_PROCESSING_DISABLED = "undertow-listener-processing-disabled";
    String UNDERTOW_LISTENER_REFRESH = "undertow-listener-refresh";
    String UNDERTOW_MODCLUSTER_BALANCER_NODE_CONTEXT_REFRESH = "undertow-modcluster-balancer-node-context-refresh";
    String UNDERTOW_MODCLUSTER_BALANCER_NODE_REFRESH = "undertow-modcluster-balancer-node-refresh";
    String UNDERTOW_MODCLUSTER_BALANCER_REFRESH = "undertow-modcluster-balancer-refresh";
    String UNDERTOW_MODCLUSTER_REFRESH = "undertow-modcluster-refresh";
    String UNDERTOW_RESPONSE_HEADER_ADD = "undertow-response-header-add";
    String UNDERTOW_RUNTIME = "undertow-runtime";
    String UNDERTOW_RUNTIME_APP_SEC_DOMAIN = "undertow-runtime-app-sec-domain";
    String UNDERTOW_RUNTIME_DEPLOYMENT = "undertow-runtime-deployment-column";
    String UNDERTOW_RUNTIME_LISTENER = "undertow-runtime-listener";
    String UNDERTOW_RUNTIME_MODCLUSTER = "undertow-runtime-modcluster";
    String UNDERTOW_RUNTIME_MODCLUSTER_BALANCER = "undertow-runtime-modcluster-balancer";
    String UNDERTOW_RUNTIME_MODCLUSTER_BALANCER_NODE = "undertow-runtime-modcluster-balancer-node";
    String UNDERTOW_RUNTIME_MODCLUSTER_BALANCER_NODE_CONTEXT = "undertow-runtime-modcluster-balancer-node-context";
    String UNDERTOW_RUNTIME_REFRESH = "undertow-runtime-deployment-refresh";
    String UNDERTOW_RUNTIME_SERVER = "undertow-runtime-server";
    String UNDERTOW_SERVER = "undertow-server";
    String UNDERTOW_SERVER_ADD = "undertow-server-add";
    String UNDERTOW_SERVER_AJP_LISTENER = "undertow-server-ajp-listener";
    String UNDERTOW_SERVER_CONFIGURATION_FORM = "undertow-server-configuration-form";
    String UNDERTOW_SERVER_CONFIGURATION_ITEM = "undertow-server-configuration-item";
    String UNDERTOW_SERVER_HTTP_LISTENER = "undertow-server-http-listener";
    String UNDERTOW_SERVER_HTTPS_LISTENER = "undertow-server-https-listener";
    String UNDERTOW_SERVER_LISTENER_ITEM = "undertow-server-listener-item";
    String UNDERTOW_SERVER_REFRESH = "undertow-server-refresh";
    String UNDERTOW_SERVLET_CONTAINER = "undertow-servlet-container";
    String UNDERTOW_SERVLET_CONTAINER_ADD = "undertow-servlet-container-add";
    String UNDERTOW_SERVLET_CONTAINER_CONFIGURATION_FORM = "undertow-servlet-container-configuration-form";
    String UNDERTOW_SERVLET_CONTAINER_CONFIGURATION_ITEM = "undertow-servlet-container-configuration-item";
    String UNDERTOW_SERVLET_CONTAINER_CONFIGURATION_TAB = "undertow-servlet-container-configuration-tab";
    String UNDERTOW_SERVLET_CONTAINER_COOKIE = "undertow-servlet-container-cookie";
    String UNDERTOW_SERVLET_CONTAINER_CRAWLER = "undertow-servlet-container-crawler";
    String UNDERTOW_SERVLET_CONTAINER_JSP = "undertow-servlet-container-jsp";
    String UNDERTOW_SERVLET_CONTAINER_MIME_MAPPING_FORM = "undertow-servlet-container-mime-mapping-form";
    String UNDERTOW_SERVLET_CONTAINER_MIME_MAPPING_TAB = "undertow-servlet-container-mime-mapping-tab";
    String UNDERTOW_SERVLET_CONTAINER_REFRESH = "undertow-servlet-container-refresh";
    String UNDERTOW_SERVLET_CONTAINER_SESSION = "undertow-servlet-container-session";
    String UNDERTOW_SERVLET_CONTAINER_TAB_CONTAINER = "undertow-servlet-container-tab-container";
    String UNDERTOW_SERVLET_CONTAINER_WEBSOCKET = "undertow-servlet-container-websocket";
    String UNDERTOW_SERVLET_CONTAINER_WELCOME_FILE_FORM = "undertow-servlet-container-welcome-file-form";
    String UNDERTOW_SERVLET_CONTAINER_WELCOME_FILE_TAB = "undertow-servlet-container-welcome-file-tab";
    String UNDERTOW_SETTINGS = "undertow-settings";
    String UNDERTOW_SINGLE_SIGN_ON_ADD = "undertow-single-sign-on-add";
    String UNDERTOW_SINGLE_SIGN_ON_FORM = "undertow-single-sign-on-form";
    String UNDERTOW_SINGLE_SIGN_ON_TAB = "undertow-single-sign-on-table";
    String UNDERTOW_STATISTICS_DISABLED = "undertow-statistics-disabled";
    String UNMANAGED_FORM = "unmanaged-form";
    String UPLOAD_FILE_INPUT = "upload-file-input";
    String UPLOAD_NAMES_FORM = "upload-names-form";
    String USER = "user";
    String VALIDATION = "validations";
    String WEBSERVICES_CLIENT_CONFIG = "webservices-client-config";
    String WEBSERVICES_CLIENT_CONFIG_ITEM = "webservices-client-config-item";
    String WEBSERVICES_ENDPOINT_CONFIG = "webservices-endpoint-config";
    String WEBSERVICES_ENDPOINT_CONFIG_ITEM = "webservices-endpoint-config-item";
    String WEBSERVICES_FORM = "webservices-form";
    String WEBSERVICES_HANDLER_ADD = "webservices-handler-add";
    String WEBSERVICES_HANDLER_CHAIN_ADD = "webservices-handler-chain-add";
    String WEBSERVICES_HANDLER_CHAIN_COLUMN = "webservices-handler-chain-column";
    String WEBSERVICES_ITEM = "webservices-item";
    String WEBSERVICES_STATISTICS_DISABLED = "webservices-statistics-disabled";
    String WORKER = "worker";
    String XA_DATA_SOURCE = "xa-data-source";
    String XA_DATA_SOURCE_ADD = "xa-data-source-add";
    String XA_DATA_SOURCE_FORM = "xa-data-source-form";
    String XA_DATA_SOURCE_RUNTIME_JDBC_FORM = "xa-data-source-runtime-jdbc-form";
    String XA_DATA_SOURCE_RUNTIME_JDBC_TAB = "xa-data-source-runtime-jdbc-tab";
    String XA_DATA_SOURCE_RUNTIME_POOL_FORM = "xa-data-source-runtime-pool-form";
    String XA_DATA_SOURCE_RUNTIME_POOL_TAB = "xa-data-source-runtime-pool-tab";
    String XA_DATA_SOURCE_RUNTIME_TAB_CONTAINER = "xa-data-source-runtime-tab-container";


    // ------------------------------------------------------ resource ids (a-z)

    static String cacheContainer(String name) {
        return Ids.build("cc", name);
    }

    static String extractCacheContainer(String id) {
        return substringAfterLast(id, "cc-");
    }

    static String content(String name) {
        return name;
    }

    static String dataSourceConfiguration(String name, boolean xa) {
        return build(xa ? "xa" : "non-xa", "dsc", name);
    }

    static String dataSourceRuntime(String name, boolean xa) {
        return build(xa ? "xa" : "non-xa", "dsr", name);
    }

    static String deployment(String name) {
        return Ids.build("dply", name);
    }

    static String destination(String deployment, String subdeployment, String messageServer, String type, String name) {
        if (deployment == null) {
            return build(messageServer, type, name);
        }
        return build(deployment, subdeployment, messageServer, type, name);
    }

    static String ejb3(String deployment, String subdeployment, String type, String name) {
        return build(deployment, subdeployment, type, name);
    }

    static String host(String name) {
        return build(HOST, name);
    }

    static String hostServer(String host, String server) {
        return build(host, server);
    }

    static String job(String deployment, String subdeployment, String name) {
        return build(JOB, deployment, subdeployment, name);
    }

    static String jmsBridge(String name) {
        return build("jmsb", name);
    }

    static String jpaStatistic(String deployment, String subdeployment, String persistenceUnit) {
        return build(deployment, subdeployment, persistenceUnit);
    }

    static String loggingProfile(String name) {
        return build(LOGGING_CONFIG_AND_PROFILES, name);
    }

    static String mailSession(String name) {
        return build("ms", name);
    }

    static String modclusterProxy(String name) {
        return build("mcp", name);
    }

    static String messagingServer(String name) {
        return build("msgs", name);
    }

    static String extractMessagingServer(String id) {
        return substringAfterLast(id, "msgs-");
    }

    static String webServer(String name) {
        return build("us", name);
    }

    static String webListener(String name) {
        return build("ulst", name);
    }

    /**
     * @param type must be one of "user" or "group"
     */
    static String principal(String type, String name) {
        return build(type, name);
    }

    static String remoteCacheContainer(String name) {
        return Ids.build("rcc", name);
    }

    static String resourceAdapter(String name) {
        return build("ra", name);
    }

    static String restResource(String deployment, String subdeployment, String name) {
        return build(deployment, subdeployment, name);
    }

    static String role(String name) {
        return asId(name);
    }

    static String securityDomain(String name) {
        return build("sd", name);
    }

    static String serverGroup(String name) {
        return build("sg", name);
    }

    static String serverGroupDeployment(String serverGroup, String name) {
        return build(serverGroup, name);
    }

    static String undertowApplicationSecurityDomain(String name) {
        return build("uasd", name);
    }

    static String undertowModcluster(String name) {
        return build("umc", name);
    }

    static String extractUndertowModcluster(String id) {
        return substringAfterLast(id, "umc-");
    }

    static String extractUndertowModclusterBalancer(String id) {
        return substringAfterLast(id, "undertow-modcluster-balancer-");
    }

    static String undertowServer(String name) {
        return build("us", name);
    }

    static String extractUndertowServer(String id) {
        return substringAfterLast(id, "us-");
    }

    static String undertowServletContainer(String name) {
        return build("usc", name);
    }


    // ------------------------------------------------------ methods

    /**
     * Turns a label which can contain whitespace and upper/lower case characters into an all lowercase id separated by
     * "-".
     */
    static String asId(String text) {
        String[] parts = text.split("[-\\s]");
        List<String> sanitized = new ArrayList<>();
        for (String part : parts) {
            if (part != null) {
                String s = part.replaceAll("\\s+", "");
                s = s.replaceAll("[^a-zA-Z0-9-_]", "");
                s = s.replace('_', '-');
                if (s.length() != 0) {
                    sanitized.add(s);
                }
            }
        }
        if (sanitized.isEmpty()) {
            return null;
        } else {
            return sanitized.stream()
                    .filter(s -> s != null && s.trim().length() != 0)
                    .map(String::toLowerCase)
                    .collect(joining("-"));
        }
    }

    static String build(String id, String... additionalIds) {
        return build(id, '-', additionalIds);
    }

    static String build(String id, char separator, String... additionalIds) {
        if (emptyToNull(id) == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        List<String> ids = Lists.newArrayList(id);
        if (additionalIds != null) {
            for (String additionalId : additionalIds) {
                if (!isNullOrEmpty(additionalId)) {
                    ids.add(additionalId);
                }
            }
        }
        return ids.stream().map(Ids::asId).collect(joining(String.valueOf(separator)));
    }

    Counter counter = new Counter();

    static String uniqueId() {
        return "hal-uid-" + counter.value++;
    }
}
