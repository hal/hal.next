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
package org.jboss.hal.client.configuration.subsystem.datasource.wizard;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import elemental2.dom.HTMLElement;
import org.jboss.hal.ballroom.wizard.WizardStep;
import org.jboss.hal.core.datasource.DataSource;
import org.jboss.hal.core.elytron.CredentialReference;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;

import static java.util.Arrays.asList;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTION_URL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY_DOMAIN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TYPE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.USER_NAME;
import static org.jboss.hal.dmr.ModelNodeHelper.move;

class ConnectionStep extends WizardStep<Context, State> {

    private final ModelNodeForm<DataSource> form;

    ConnectionStep(Metadata metadata, Resources resources, boolean xa) {
        super(Names.CONNECTION);

        List<String> credRefAttrs = asList(STORE, ALIAS, CLEAR_TEXT, TYPE);
        List<String> otherAttrs = asList(USER_NAME, PASSWORD, SECURITY_DOMAIN);

        List<String> attributes = new ArrayList<>();
        if (!xa) {
            attributes.add(CONNECTION_URL);
        }

        attributes.addAll(otherAttrs);
        attributes.addAll(credRefAttrs);

        // split credential reference into individual attributes
        Metadata connMetadata = metadata.forComplexAttribute(CREDENTIAL_REFERENCE, true);
        for (String attr : otherAttrs) {
            metadata.copyAttribute(attr, connMetadata);
        }

        form = new ModelNodeForm.Builder<DataSource>(Ids.DATA_SOURCE_CONNECTION_FORM, connMetadata)
                .include(attributes)
                .unsorted()
                .onSave((form, changedValues) -> {
                    changedValues.forEach((k, v) -> wizard().getContext().recordChange(k, v));
                    // re-create credential reference
                    for (String credRefAttr : credRefAttrs) {
                        move(form.getModel(), credRefAttr, CREDENTIAL_REFERENCE + "/" + credRefAttr);
                    }
                    wizard().getContext().dataSource = form.getModel();
                })
                .build();

        form.addFormValidation(new CredentialReference.AlternativeValidation(PASSWORD, () -> {
            // supply credential reference but do not alter model
            ModelNode credRef = new ModelNode();
            for (String credRefAttr : credRefAttrs) {
                String value = form.<String>getFormItem(credRefAttr).getValue();
                if (!Strings.isNullOrEmpty(value)) {
                    credRef.get(credRefAttr).set(value);
                }
            }
            return credRef;
        }, resources));

        registerAttachable(form);
    }

    @Override
    public HTMLElement element() {
        return form.element();
    }

    @Override
    protected void onShow(Context context) {
        form.edit(context.dataSource);
    }

    @Override
    protected boolean onNext(Context context) {
        return form.save();
    }

    @Override
    protected boolean onBack(Context context) {
        form.cancel();
        return true;
    }

    @Override
    protected boolean onCancel(Context context) {
        form.cancel();
        return true;
    }
}
