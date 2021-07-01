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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.autocomplete.ReadChildrenAutoComplete;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.core.ComplexAttributeOperations;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.mbui.MbuiPresenter;
import org.jboss.hal.core.mbui.MbuiView;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.dialog.NameItem;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mvp.SupportsExpertMode;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
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
import org.jboss.hal.spi.Requires;

import static java.util.Arrays.asList;
import static org.jboss.hal.client.configuration.subsystem.elytron.AddressTemplates.*;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.dmr.ModelNodeHelper.asNamedNodes;

public class MapperDecoderPresenter extends MbuiPresenter<MapperDecoderPresenter.MyView, MapperDecoderPresenter.MyProxy>
        implements SupportsExpertMode {

    private final CrudOperations crud;
    private final FinderPathFactory finderPathFactory;
    private final StatementContext statementContext;
    private final Dispatcher dispatcher;
    private final Resources resources;
    private ComplexAttributeOperations ca;
    private MetadataRegistry metadataRegistry;

    @Inject
    public MapperDecoderPresenter(EventBus eventBus,
            MapperDecoderPresenter.MyView view,
            MapperDecoderPresenter.MyProxy proxy,
            Finder finder,
            CrudOperations crud,
            ComplexAttributeOperations ca,
            FinderPathFactory finderPathFactory,
            StatementContext statementContext,
            Dispatcher dispatcher,
            MetadataRegistry metadataRegistry,
            Resources resources) {
        super(eventBus, view, proxy, finder);
        this.crud = crud;
        this.ca = ca;
        this.finderPathFactory = finderPathFactory;
        this.statementContext = statementContext;
        this.dispatcher = dispatcher;
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
                .append(Ids.ELYTRON, Ids.ELYTRON_MAPPERS_DECODERS,
                        resources.constants().settings(), Names.MAPPERS_DECODERS);
    }

    @Override
    protected void reload() {
        ResourceAddress address = ELYTRON_SUBSYSTEM_TEMPLATE.resolve(statementContext);
        crud.readChildren(address, asList(
                ElytronResource.ADD_PREFIX_ROLE_MAPPER.resource,
                ElytronResource.ADD_SUFFIX_ROLE_MAPPER.resource,
                ElytronResource.AGGREGATE_PRINCIPAL_DECODER.resource,
                ElytronResource.AGGREGATE_ROLE_MAPPER.resource,
                ElytronResource.CONCATENATING_PRINCIPAL_DECODER.resource,
                ElytronResource.CONSTANT_PERMISSION_MAPPER.resource,
                ElytronResource.CONSTANT_PRINCIPAL_DECODER.resource,
                ElytronResource.CONSTANT_ROLE_MAPPER.resource,
                ElytronResource.CUSTOM_PERMISSION_MAPPER.resource,
                ElytronResource.CUSTOM_PRINCIPAL_DECODER.resource,
                ElytronResource.CUSTOM_ROLE_DECODER.resource,
                ElytronResource.CUSTOM_ROLE_MAPPER.resource,
                ElytronResource.LOGICAL_PERMISSION_MAPPER.resource,
                ElytronResource.LOGICAL_ROLE_MAPPER.resource,
                ElytronResource.MAPPED_ROLE_MAPPER.resource,
                ElytronResource.SIMPLE_PERMISSION_MAPPER.resource,
                ElytronResource.SIMPLE_ROLE_DECODER.resource,
                ElytronResource.X500_ATTRIBUTE_PRINCIPAL_DECODER.resource),
                result -> {
                    // @formatter:off
                    int i = 0;
                    getView().updateAddPrefixRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateAddSuffixRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateAggregatePrincipalDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateAggregateRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateConcatenatingPrincipalDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateConstantPermissionMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateConstantPrincipalDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateConstantRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateCustomPermissionMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateCustomPrincipalDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateCustomRoleDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateCustomRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateLogicalPermissionMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateLogicalRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateMappedRoleMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateSimplePermissionMapper(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateSimpleRoleDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    getView().updateX500AttributePrincipalDecoder(asNamedNodes(result.step(i++).get(RESULT).asPropertyList()));
                    // @formatter:on
                });
    }

    void reload(String resource, Consumer<List<NamedNode>> callback) {
        crud.readChildren(AddressTemplates.ELYTRON_SUBSYSTEM_TEMPLATE, resource,
                children -> callback.accept(asNamedNodes(children)));
    }

    // -------------------------------------------- Simple Permission Mapper

    void reloadSimplePermissionMapper() {
        crud.readChildren(AddressTemplates.ELYTRON_SUBSYSTEM_TEMPLATE,
                ModelDescriptionConstants.SIMPLE_PERMISSION_MAPPER,
                children -> getView().updateSimplePermissionMapper(asNamedNodes(children)));
    }

    void saveSimplePermissionMapping(String name, Map<String, Object> changedValues) {
        crud.save(Names.SIMPLE_PERMISSION_MAPPER, name, AddressTemplates.SIMPLE_PERMISSION_MAPPER_TEMPLATE,
                changedValues, this::reloadSimplePermissionMapper);
    }

    void addPermissionMappings(String resource) {
        ca.listAdd(Ids.ELYTRON_PERMISSION_MAPPINGS_ADD, resource, PERMISSION_MAPPINGS, Names.PERMISSION_MAPPINGS,
                SIMPLE_PERMISSION_MAPPER_TEMPLATE, this::reloadSimplePermissionMapper);
    }

    void removePermissionMappings(String resource, int index) {
        ca.remove(resource, PERMISSION_MAPPINGS, Names.PERMISSION_MAPPINGS, index, SIMPLE_PERMISSION_MAPPER_TEMPLATE,
                this::reloadSimplePermissionMapper);
    }

    void savePermissionMappings(String resource, int i, Map<String, Object> changedValues) {
        ResourceAddress address = SIMPLE_PERMISSION_MAPPER_TEMPLATE.resolve(statementContext, resource);
        Metadata metadata = metadataRegistry.lookup(SIMPLE_PERMISSION_MAPPER_TEMPLATE)
                .forComplexAttribute(PERMISSION_MAPPINGS);
        ca.save(PERMISSION_MAPPINGS, Names.PERMISSION_MAPPINGS, i, address, changedValues, metadata,
                this::reloadSimplePermissionMapper);
    }

    void addPermissions(String resource, int pmIndex) {
        Metadata metadata = metadataRegistry.lookup(SIMPLE_PERMISSION_MAPPER_TEMPLATE)
                .forComplexAttribute(PERMISSION_MAPPINGS)
                .forComplexAttribute(PERMISSIONS);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(Ids.ELYTRON_PERMISSIONS_ADD, metadata)
                .addOnly()
                //.include(TO, INDEX)
                .build();
        AddResourceDialog dialog = new AddResourceDialog(resources.messages().addResourceTitle(Names.PERMISSIONS),
                form, (name, model) -> ca.listAdd(resource, permissionsAttribute(pmIndex),
                Names.PERMISSIONS, SIMPLE_PERMISSION_MAPPER_TEMPLATE, model, this::reloadSimplePermissionMapper));
        dialog.show();
    }

    void removePermissions(String resource, int pmIndex, int permissionsIndex) {
        ca.remove(resource, permissionsAttribute(pmIndex), Names.PERMISSIONS, permissionsIndex,
                SIMPLE_PERMISSION_MAPPER_TEMPLATE,
                this::reloadSimplePermissionMapper);
    }

    void savePermissions(String resource, int pmIndex, int permissionsIndex, Map<String, Object> changedValues) {

        ResourceAddress address = SIMPLE_PERMISSION_MAPPER_TEMPLATE.resolve(statementContext, resource);
        Metadata metadata = metadataRegistry.lookup(SIMPLE_PERMISSION_MAPPER_TEMPLATE)
                .forComplexAttribute(PERMISSION_MAPPINGS)
                .forComplexAttribute(PERMISSIONS);
        ca.save(permissionsAttribute(pmIndex), Names.PERMISSIONS, permissionsIndex, address, changedValues, metadata,
                this::reloadSimplePermissionMapper);
    }

    private String permissionsAttribute(int pmIndex) {
        return PERMISSION_MAPPINGS + "[" + pmIndex + "]." + PERMISSIONS;
    }

    // -------------------------------------------- Mapped Role Mapper

    void addMappedRoleMapper() {
        Metadata metadata = metadataRegistry.lookup(MAPPED_ROLE_MAPPER_TEMPLATE);
        String id = Ids.build(Ids.ELYTRON_MAPPED_ROLE_MAPPER, Ids.ADD);
        Form<ModelNode> form = new ModelNodeForm.Builder<>(id, metadata)
                .unboundFormItem(new NameItem(), 0)
                .customFormItem(ROLE_MAP, desc -> new RoleMapListItem(ROLE_MAP, new LabelBuilder().label(ROLE_MAP)))
                .addOnly()
                .build();
        String title = new LabelBuilder().label(MAPPED_ROLE_MAPPER);
        new AddResourceDialog(title, form, (name, payload) -> {
            // :add(role-map=[role1 => [role2,role3],role2 => [mane,m2n3]])
            crud.add(title, name, MAPPED_ROLE_MAPPER_TEMPLATE, payload, (name1, address) -> reload());
        }).show();
    }

    ReadChildrenAutoComplete getAutocomplete(AddressTemplate template) {
        return new ReadChildrenAutoComplete(dispatcher, statementContext, template);
    }

    @ProxyCodeSplit
    @Requires(value = {
            ADD_PREFIX_ROLE_MAPPER_ADDRESS,
            ADD_SUFFIX_ROLE_MAPPER_ADDRESS,
            AGGREGATE_PRINCIPAL_DECODER_ADDRESS,
            AGGREGATE_ROLE_MAPPER_ADDRESS,
            CONCATENATING_PRINCIPAL_DECODER_ADDRESS,
            CONSTANT_PERMISSION_MAPPER_ADDRESS,
            CONSTANT_PRINCIPAL_DECODER_ADDRESS,
            CONSTANT_ROLE_MAPPER_ADDRESS,
            CUSTOM_PERMISSION_MAPPER_ADDRESS,
            CUSTOM_PRINCIPAL_DECODER_ADDRESS,
            CUSTOM_ROLE_DECODER_ADDRESS,
            CUSTOM_ROLE_MAPPER_ADDRESS,
            LOGICAL_PERMISSION_MAPPER_ADDRESS,
            LOGICAL_ROLE_MAPPER_ADDRESS,
            MAPPED_ROLE_MAPPER_ADDRESS,
            SIMPLE_PERMISSION_MAPPER_ADDRESS,
            SIMPLE_ROLE_DECODER_ADDRESS,
            X500_ATTRIBUTE_PRINCIPAL_DECODER_ADDRESS})
    @NameToken(NameTokens.ELYTRON_MAPPERS_DECODERS)
    public interface MyProxy extends ProxyPlace<MapperDecoderPresenter> {
    }


    // @formatter:off
    public interface MyView extends MbuiView<MapperDecoderPresenter> {
        void updateAddPrefixRoleMapper(List<NamedNode> model);
        void updateAddSuffixRoleMapper(List<NamedNode> model);
        void updateAggregatePrincipalDecoder(List<NamedNode> model);
        void updateAggregateRoleMapper(List<NamedNode> model);
        void updateConcatenatingPrincipalDecoder(List<NamedNode> model);
        void updateConstantPermissionMapper(List<NamedNode> model);
        void updateConstantPrincipalDecoder(List<NamedNode> model);
        void updateConstantRoleMapper(List<NamedNode> model);
        void updateCustomPermissionMapper(List<NamedNode> model);
        void updateCustomPrincipalDecoder(List<NamedNode> model);
        void updateCustomRoleDecoder(List<NamedNode> model);
        void updateCustomRoleMapper(List<NamedNode> model);
        void updateLogicalPermissionMapper(List<NamedNode> model);
        void updateLogicalRoleMapper(List<NamedNode> model);
        void updateMappedRoleMapper(List<NamedNode> model);
        void updateSimplePermissionMapper(List<NamedNode> model);
        void updateSimpleRoleDecoder(List<NamedNode> model);
        void updateX500AttributePrincipalDecoder(List<NamedNode> model);
    }
    // @formatter:on
}
