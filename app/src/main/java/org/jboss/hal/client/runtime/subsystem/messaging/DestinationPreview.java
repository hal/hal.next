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
package org.jboss.hal.client.runtime.subsystem.messaging;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.finder.PreviewAttributes.PreviewAttribute;
import org.jboss.hal.core.finder.PreviewAttributes.PreviewAttributeFunction;
import org.jboss.hal.core.finder.PreviewContent;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.ModelNodeHelper;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;

import static elemental2.dom.DomGlobal.document;
import static java.util.stream.Collectors.joining;
import static org.jboss.gwt.elemento.core.Elements.a;
import static org.jboss.gwt.elemento.core.Elements.collect;
import static org.jboss.gwt.elemento.core.Elements.span;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.dmr.ModelNodeHelper.failSafeBoolean;
import static org.jboss.hal.resources.CSS.marginRight5;
import static org.jboss.hal.resources.Icons.flag;

class DestinationPreview extends PreviewContent<Destination> {

    private final Dispatcher dispatcher;
    private final ResourceAddress address;
    private final PreviewAttributes<Destination> attributes;
    private final PreviewAttributes<Destination> messages;
    private final PreviewAttributes<Destination> subscriptions;

    DestinationPreview(Destination destination, FinderPathFactory finderPathFactory, Places places,
            Dispatcher dispatcher, Resources resources) {
        super(destination.getName(), destination.type.type);
        this.dispatcher = dispatcher;
        this.address = destination.getAddress();

        getHeaderContainer().appendChild(refreshLink(() -> update(destination)));
        if (destination.fromDeployment()) {
            FinderPath path = finderPathFactory.deployment(destination.getDeployment());
            PlaceRequest placeRequest = places.finderPlace(NameTokens.DEPLOYMENTS, path).build();
            Elements.removeChildrenFrom(getLeadElement());
            getLeadElement().appendChild(
                    document.createTextNode(destination.type.type + " @ "));
            getLeadElement().appendChild(a(places.historyToken(placeRequest))
                    .textContent(destination.getPath())
                    .title(resources.messages().goTo(Names.DEPLOYMENTS)).element());
        }

        PreviewAttributeFunction<Destination> jndiNamesFn = model -> {
            String jndiNames = ModelNodeHelper.failSafeList(model, ENTRIES).stream()
                    .map(ModelNode::asString)
                    .collect(joining(", "));
            return new PreviewAttribute(resources.constants().jndiNames(), jndiNames);
        };
        attributes = new PreviewAttributes<>(destination);
        messages = new PreviewAttributes<>(destination, resources.constants().messages());
        subscriptions = new PreviewAttributes<>(destination, resources.constants().subscriptions());

        LabelBuilder labelBuilder = new LabelBuilder();
        if (destination.type == Destination.Type.JMS_QUEUE || destination.type == Destination.Type.QUEUE) {
            attributes.append(QUEUE_ADDRESS)
                    .append(jndiNamesFn)
                    .append(EXPIRY_ADDRESS)
                    .append(DEAD_LETTER_ADDRESS)
                    .append(PAUSED)
                    .append(model -> {
                        String label = labelBuilder.label(DURABLE) + ", " + labelBuilder.label(TEMPORARY);
                        Iterable<HTMLElement> elements = collect()
                                .add(span()
                                        .title(labelBuilder.label(DURABLE))
                                        .css(flag(failSafeBoolean(model, DURABLE)), marginRight5))
                                .add(span()
                                        .title(labelBuilder.label(TEMPORARY))
                                        .css(flag(failSafeBoolean(model, TEMPORARY)))).elements();
                        return new PreviewAttribute(label, elements);
                    });
            messages.append(MESSAGES_ADDED)
                    .append(MESSAGE_COUNT)
                    .append(DELIVERING_COUNT)
                    .append(SCHEDULED_COUNT)
                    .append(CONSUMER_COUNT);

        } else if (destination.type == Destination.Type.JMS_TOPIC) {
            attributes.append(TOPIC_ADDRESS)
                    .append(jndiNamesFn)
                    .append(PAUSED)
                    .append(TEMPORARY);
            messages.append(MESSAGE_COUNT)
                    .append(DURABLE_MESSAGE_COUNT)
                    .append(NON_DURABLE_MESSAGE_COUNT)
                    .append(MESSAGES_ADDED)
                    .append(DELIVERING_COUNT);
            subscriptions.append(SUBSCRIPTION_COUNT)
                    .append(DURABLE_SUBSCRIPTION_COUNT)
                    .append(NON_DURABLE_SUBSCRIPTION_COUNT);
        }

        previewBuilder().addAll(attributes);
        previewBuilder().addAll(messages);
        if (destination.type == Destination.Type.JMS_TOPIC) {
            previewBuilder().addAll(subscriptions);
        }
    }

    @Override
    public void update(Destination item) {
        Operation operation = new Operation.Builder(address, READ_RESOURCE_OPERATION)
                .param(INCLUDE_RUNTIME, true)
                .build();
        dispatcher.execute(operation, result -> {
            Destination update = new Destination(item.getAddress(), item);
            attributes.refresh(update);
            messages.refresh(update);
            if (update.type == Destination.Type.JMS_TOPIC) {
                subscriptions.refresh(item);
            }
        });
    }
}
