/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.core.mbui.table;

import java.util.Collections;
import java.util.function.Function;
import javax.inject.Inject;

import org.jboss.hal.ballroom.table.Api;
import org.jboss.hal.ballroom.table.Button;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Callback;

import static org.jboss.hal.ballroom.table.Button.Scope.SELECTED_SINGLE;

/**
 * @author Harald Pehl
 */
public class TableButtonFactory {

    private final CrudOperations crud;
    private final Resources resources;

    @Inject
    public TableButtonFactory(final CrudOperations crud, final Resources resources) {
        this.crud = crud;
        this.resources = resources;
    }

    public <T extends ModelNode> Button<T> add(String id, String type, AddressTemplate template,
            CrudOperations.AddCallback callback) {
        return add(id, type, template, Collections.emptyList(), callback);
    }

    public <T extends ModelNode> Button<T> add(String id, String type, AddressTemplate template,
            Iterable<String> attributes, CrudOperations.AddCallback callback) {
        Button<T> button = new Button<>();
        button.text = resources.constants().add();
        button.action = (event, api) -> crud.add(id, type, template, attributes, callback);
        return button;
    }

    public <T> Button<T> remove(String type, AddressTemplate template, Function<Api<T>, String> nameFunction,
            Callback callback) {
        Button<T> button = new Button<>();
        button.text = resources.constants().remove();
        button.extend = SELECTED_SINGLE.selector();
        button.action = (event, api) -> crud.remove(type, nameFunction.apply(api), template, callback);
        return button;
    }
}