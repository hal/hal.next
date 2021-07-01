/*
 * Copyright 2015-2021 Red Hat, Inc, and individual contributors.
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

import com.google.gwt.core.client.GWT;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.form.ModelNodeItem;
import org.jboss.hal.ballroom.form.TagsItem;
import org.jboss.hal.ballroom.form.TagsMapping;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.resources.Messages;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.jboss.hal.ballroom.form.Decoration.*;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PERMISSION_SET;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PERMISSION_SETS;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PermissionSetsItem extends TagsItem<ModelNode> implements ModelNodeItem {

    private static final Messages MESSAGES = GWT.create(Messages.class);

    protected PermissionSetsItem() {
        super(PERMISSION_SETS, new LabelBuilder().label(PERMISSION_SETS), MESSAGES.listHint(),
                EnumSet.of(DEFAULT, DEPRECATED, ENABLED, INVALID, REQUIRED, RESTRICTED, SUGGESTIONS), new PermissionSetsMapping());
    }

    @Override
    public void addTag(ModelNode tag) {
        ModelNode value = getValue();
        ModelNode newValue = value != null ? value.clone() : new ModelNode();
        newValue.add(tag);
        modifyValue(newValue);
    }

    @Override
    public void removeTag(ModelNode tag) {
        List<ModelNode> list = new ArrayList<>(getValue().asList());
        list.remove(tag);
        ModelNode newValue = new ModelNode();
        newValue.set(list);
        modifyValue(newValue);
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || getValue().asInt() == 0;
    }

    private static class PermissionSetsMapping implements TagsMapping<ModelNode> {

        @Override
        public ModelNode parseTag(String tag) {
            ModelNode ps = new ModelNode();
            ps.get(PERMISSION_SET).set(tag);
            return ps;
        }

        @Override
        public List<String> tags(final ModelNode value) {
            if (!value.isDefined()) {
                return emptyList();
            }
            return value.asList().stream()
                    .map(ps -> ps.get(PERMISSION_SET).asString())
                    .collect(toList());
        }

        @Override
        public String asString(final ModelNode value) {
            if (!value.isDefined()) {
                return "";
            }
            return value.asList().stream()
                    .map(ps -> ps.get(PERMISSION_SET).asString())
                    .collect(joining(", "));
        }
    }
}
