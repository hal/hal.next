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

import org.jboss.hal.meta.AddressTemplate;

import static org.jboss.hal.meta.StatementContext.Expression.SELECTED_PROFILE;

interface AddressTemplates {

    String EE_ADDRESS = "/{selected.profile}/subsystem=ee";
    String SERVICE_DEFAULT_BINDINGS_ADDRESS = EE_ADDRESS + "/service=default-bindings";
    String CONTEXT_SERVICE_ADDRESS = EE_ADDRESS + "/context-service=*";
    String MANAGED_EXECUTOR_ADDRESS = EE_ADDRESS + "/managed-executor-service=*";
    String MANAGED_EXECUTOR_SCHEDULED_ADDRESS = EE_ADDRESS + "/managed-scheduled-executor-service=*";
    String MANAGED_THREAD_FACTORY_ADDRESS = EE_ADDRESS + "/managed-thread-factory=*";
    String GLOBAL_DIRECTORY_ADDRESS = EE_ADDRESS + "/global-directory=*";

    AddressTemplate SERVICE_DEFAULT_BINDINGS_TEMPLATE = AddressTemplate.of(SERVICE_DEFAULT_BINDINGS_ADDRESS);
    AddressTemplate CONTEXT_SERVICE_TEMPLATE = AddressTemplate.of(CONTEXT_SERVICE_ADDRESS);
    AddressTemplate MANAGED_EXECUTOR_TEMPLATE = AddressTemplate.of(MANAGED_EXECUTOR_ADDRESS);
    AddressTemplate MANAGED_EXECUTOR_SCHEDULED_TEMPLATE = AddressTemplate.of(MANAGED_EXECUTOR_SCHEDULED_ADDRESS);
    AddressTemplate MANAGED_THREAD_FACTORY_TEMPLATE = AddressTemplate.of(MANAGED_THREAD_FACTORY_ADDRESS);
    AddressTemplate GLOBAL_DIRECTORY_TEMPLATE = AddressTemplate.of(GLOBAL_DIRECTORY_ADDRESS);

    AddressTemplate EE_SUBSYSTEM_TEMPLATE = AddressTemplate.of(SELECTED_PROFILE, "subsystem=ee");
}
