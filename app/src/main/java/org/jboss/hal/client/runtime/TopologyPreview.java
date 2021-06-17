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
package org.jboss.hal.client.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.inject.Provider;

import com.google.common.base.Strings;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableSectionElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.NodeList;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.EventCallbackFn;
import org.jboss.gwt.elemento.core.builder.HtmlContentBuilder;
import org.jboss.hal.ballroom.Format;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.client.runtime.server.ServerStatusSwitch;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.finder.PreviewAttributes.PreviewAttribute;
import org.jboss.hal.core.finder.PreviewContent;
import org.jboss.hal.core.finder.StaticItem;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.TopologyTasks;
import org.jboss.hal.core.runtime.group.ServerGroup;
import org.jboss.hal.core.runtime.group.ServerGroupActionEvent;
import org.jboss.hal.core.runtime.group.ServerGroupActionEvent.ServerGroupActionHandler;
import org.jboss.hal.core.runtime.group.ServerGroupActions;
import org.jboss.hal.core.runtime.group.ServerGroupResultEvent;
import org.jboss.hal.core.runtime.group.ServerGroupResultEvent.ServerGroupResultHandler;
import org.jboss.hal.core.runtime.host.Host;
import org.jboss.hal.core.runtime.host.HostActionEvent;
import org.jboss.hal.core.runtime.host.HostActionEvent.HostActionHandler;
import org.jboss.hal.core.runtime.host.HostActions;
import org.jboss.hal.core.runtime.host.HostPreviewAttributes;
import org.jboss.hal.core.runtime.host.HostResultEvent;
import org.jboss.hal.core.runtime.host.HostResultEvent.HostResultHandler;
import org.jboss.hal.core.runtime.server.Server;
import org.jboss.hal.core.runtime.server.ServerActionEvent;
import org.jboss.hal.core.runtime.server.ServerActionEvent.ServerActionHandler;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.core.runtime.server.ServerPreviewAttributes;
import org.jboss.hal.core.runtime.server.ServerResultEvent;
import org.jboss.hal.core.runtime.server.ServerResultEvent.ServerResultHandler;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.NamedNode;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.flow.FlowContext;
import org.jboss.hal.flow.Outcome;
import org.jboss.hal.flow.Progress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.security.AuthorisationDecision;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.security.Constraints;
import org.jboss.hal.meta.security.SecurityContextRegistry;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.CSS;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.resources.UIConstants;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;

import static com.google.common.collect.Lists.asList;
import static elemental2.dom.DomGlobal.clearTimeout;
import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.setTimeout;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.jboss.gwt.elemento.core.Elements.*;
import static org.jboss.gwt.elemento.core.EventType.click;
import static org.jboss.hal.core.runtime.TopologyTasks.topology;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.flow.Flow.series;
import static org.jboss.hal.resources.CSS.centerBlock;
import static org.jboss.hal.resources.CSS.clickable;
import static org.jboss.hal.resources.CSS.disconnected;
import static org.jboss.hal.resources.CSS.divider;
import static org.jboss.hal.resources.CSS.dropdownMenu;
import static org.jboss.hal.resources.CSS.dropdownToggle;
import static org.jboss.hal.resources.CSS.empty;
import static org.jboss.hal.resources.CSS.error;
import static org.jboss.hal.resources.CSS.fontAwesome;
import static org.jboss.hal.resources.CSS.height;
import static org.jboss.hal.resources.CSS.hostContainer;
import static org.jboss.hal.resources.CSS.inactive;
import static org.jboss.hal.resources.CSS.marginLeft5;
import static org.jboss.hal.resources.CSS.marginRight5;
import static org.jboss.hal.resources.CSS.name;
import static org.jboss.hal.resources.CSS.ok;
import static org.jboss.hal.resources.CSS.pullRight;
import static org.jboss.hal.resources.CSS.px;
import static org.jboss.hal.resources.CSS.rowHeader;
import static org.jboss.hal.resources.CSS.selected;
import static org.jboss.hal.resources.CSS.server;
import static org.jboss.hal.resources.CSS.serverGroupContainer;
import static org.jboss.hal.resources.CSS.spinner;
import static org.jboss.hal.resources.CSS.spinnerLg;
import static org.jboss.hal.resources.CSS.suspended;
import static org.jboss.hal.resources.CSS.topology;
import static org.jboss.hal.resources.CSS.warning;
import static org.jboss.hal.resources.CSS.withProgress;
import static org.jboss.hal.resources.UIConstants.MEDIUM_TIMEOUT;

class TopologyPreview extends PreviewContent<StaticItem> implements HostActionHandler, HostResultHandler,
        ServerGroupActionHandler, ServerGroupResultHandler, ServerActionHandler, ServerResultHandler {

    private static final String DOT = ".";
    private static final String CONTAINER = "container";

    private final SecurityContextRegistry securityContextRegistry;
    private final Environment environment;
    private final Dispatcher dispatcher;
    private final Provider<Progress> progress;
    private final EventBus eventBus;
    private final Places places;
    private final HostActions hostActions;
    private final ServerGroupActions serverGroupActions;
    private final ServerActions serverActions;
    private final Resources resources;
    private final HTMLElement loadingSection;
    private final HTMLElement topologySection;
    private final HTMLElement hostAttributesSection;
    private final HTMLElement serverGroupAttributesSection;
    private final HTMLElement serverAttributesSection;
    private final PreviewAttributes<ServerGroup> serverGroupAttributes;
    private final PreviewAttributes<Host> hostAttributes;
    private final PreviewAttributes<Server> serverAttributes;
    private final HTMLElement serverUrl;
    private final LabelBuilder labelBuilder;

    TopologyPreview(SecurityContextRegistry securityContextRegistry,
            Environment environment,
            Dispatcher dispatcher,
            Provider<Progress> progress,
            EventBus eventBus,
            Places places,
            FinderPathFactory finderPathFactory,
            HostActions hostActions,
            ServerGroupActions serverGroupActions,
            ServerActions serverActions,
            Resources resources) {
        super(Names.TOPOLOGY, resources.previews().runtimeTopology());
        this.securityContextRegistry = securityContextRegistry;
        this.environment = environment;
        this.dispatcher = dispatcher;
        this.progress = progress;
        this.eventBus = eventBus;
        this.places = places;
        this.hostActions = hostActions;
        this.serverGroupActions = serverGroupActions;
        this.serverActions = serverActions;
        this.resources = resources;
        this.labelBuilder = new LabelBuilder();

        eventBus.addHandler(HostActionEvent.getType(), this);
        eventBus.addHandler(HostResultEvent.getType(), this);
        eventBus.addHandler(ServerGroupActionEvent.getType(), this);
        eventBus.addHandler(ServerGroupResultEvent.getType(), this);
        eventBus.addHandler(ServerActionEvent.getType(), this);
        eventBus.addHandler(ServerResultEvent.getType(), this);

        previewBuilder()
                .add(p()
                        .add(a().css(clickable, pullRight).on(click, event -> update(null))
                                .add(span().css(fontAwesome("refresh"), marginRight5))
                                .add(span().textContent(resources.constants().refresh()))))
                .add(loadingSection = section().css(centerBlock)
                        .add(p().textContent(resources.constants().loading()))
                        .add(div().css(spinner, spinnerLg)).element())
                .add(topologySection = section().element());

        hostAttributes = new PreviewAttributes<>(new Host(new ModelNode()), Names.HOST)
                .append(model -> {
                    String token = lazyToken(NameTokens.RUNTIME, model, ModelNode::isDefined,
                            m -> finderPathFactory.runtimeHostPath(m.getAddressName()));
                    return new PreviewAttribute(resources.constants().name(), model.getName(), token);
                })
                .append(RELEASE_CODENAME)
                .append(RELEASE_VERSION)
                .append(PRODUCT_NAME)
                .append(PRODUCT_VERSION)
                .append(HOST_STATE)
                .append(RUNNING_MODE)
                .append(model -> new PreviewAttribute(labelBuilder.label(MANAGEMENT_VERSION),
                        model.getManagementVersion().toString()))
                .append(model -> new PreviewAttribute(labelBuilder.label(LAST_CONNECTED),
                        model.getLastConnected() != null
                                ? Format.mediumDateTime(model.getLastConnected())
                                : Names.NOT_AVAILABLE))
                .append(model -> new PreviewAttribute(labelBuilder.label(DISCONNECTED),
                        model.getLastConnected() != null
                                ? Format.mediumDateTime(model.getDisconnected())
                                : Names.NOT_AVAILABLE));

        serverGroupAttributes = new PreviewAttributes<>(new ServerGroup("", new ModelNode()), Names.SERVER_GROUP)
                .append(model -> {
                    String token = lazyToken(NameTokens.RUNTIME, model, ModelNode::isDefined,
                            m -> finderPathFactory.runtimeServerGroupPath(m.getName()));
                    return new PreviewAttribute(resources.constants().name(), model.getName(), token);
                })
                .append(model -> {
                    String token = lazyToken(NameTokens.CONFIGURATION, model, ModelNode::isDefined,
                            m -> new FinderPath()
                                    .append(Ids.CONFIGURATION, Ids.asId(Names.PROFILES))
                                    .append(Ids.PROFILE, m.getProfile()));
                    return new PreviewAttribute(Names.PROFILE, model.getProfile(), token);
                })
                .append(model -> {
                    String token = lazyToken(NameTokens.CONFIGURATION, model, ModelNode::isDefined,
                            m -> new FinderPath()
                                    .append(Ids.CONFIGURATION, Ids.asId(Names.SOCKET_BINDINGS))
                                    .append(Ids.SOCKET_BINDING_GROUP, model.get(SOCKET_BINDING_GROUP).asString()));
                    return new PreviewAttribute(Names.SOCKET_BINDING_GROUP, model.get(SOCKET_BINDING_GROUP).asString(),
                            token);
                })
                .append(SOCKET_BINDING_PORT_OFFSET)
                .append(SOCKET_BINDING_DEFAULT_INTERFACE);

        serverUrl = span().textContent(Names.NOT_AVAILABLE).element();
        serverAttributes = new PreviewAttributes<>(new Server("", new ModelNode()), Names.SERVER)
                .append(model -> {
                    String token = lazyToken(NameTokens.RUNTIME, model, m -> !Strings.isNullOrEmpty(m.getHost()),
                            m -> finderPathFactory.runtimeServerPath(model.getHost(), model.getName()));
                    return new PreviewAttribute(resources.constants().name(), model.getName(), token);
                })
                .append(model -> {
                    String token = lazyToken(NameTokens.RUNTIME, model, m -> !Strings.isNullOrEmpty(m.getHost()),
                            m -> finderPathFactory.runtimeHostPath(model.getHost()));
                    return new PreviewAttribute(Names.HOST, model.getHost(), token);
                })
                .append(model -> {
                    String token = lazyToken(NameTokens.RUNTIME, model, m -> !Strings.isNullOrEmpty(m.getHost()),
                            m -> finderPathFactory.runtimeServerGroupPath(model.getServerGroup()));
                    return new PreviewAttribute(Names.SERVER_GROUP, model.getServerGroup(), token);
                })
                .append(model -> {
                    String token = lazyToken(NameTokens.CONFIGURATION, model, m -> !Strings.isNullOrEmpty(m.getHost()),
                            m -> new FinderPath()
                                    .append(Ids.CONFIGURATION, Ids.asId(Names.PROFILES))
                                    .append(Ids.PROFILE, model.get(PROFILE_NAME).asString()));
                    return new PreviewAttribute(Names.PROFILE, model.get(PROFILE_NAME).asString(), token);
                })
                .append(model -> new PreviewAttribute(Names.URL, serverUrl))
                .append(AUTO_START)
                .append(SOCKET_BINDING_PORT_OFFSET)
                .append(STATUS)
                .append(RUNNING_MODE)
                .append(SERVER_STATE)
                .append(SUSPEND_STATE);

        previewBuilder()
                .add(hostAttributesSection = section()
                        .addAll(hostAttributes).element())
                .add(serverGroupAttributesSection = section()
                        .addAll(serverGroupAttributes).element())
                .add(serverAttributesSection = section()
                        .addAll(serverAttributes).element());
    }

    private <T extends NamedNode> String lazyToken(String tlc, T model,
            Predicate<T> defined, Function<T, FinderPath> path) {
        String token = "";
        if (defined.test(model)) {
            PlaceRequest placeRequest = places.finderPlace(tlc, path.apply(model)).build();
            token = places.historyToken(placeRequest);
        }
        return token;
    }


    // ------------------------------------------------------ dmr functions

    @Override
    public void update(StaticItem item) {
        // remember selection
        HTMLElement element = (HTMLElement) document.querySelector(DOT + topology + " ." + selected);
        String hostName = element != null ? String.valueOf(element.dataset.get("host")) : null;
        String serverGroupName = element != null ? String.valueOf(element.dataset.get("serverGroup")) : null;
        String serverName = element != null ? String.valueOf(element.dataset.get("server")) : null;

        clearSelected();
        setVisible(loadingSection, false);
        setVisible(topologySection, false);
        hideDetails();

        // show the loading indicator if the operations take too long
        double timeoutHandle = setTimeout((o) -> setVisible(loadingSection, true), MEDIUM_TIMEOUT);
        series(new FlowContext(progress.get()), topology(environment, dispatcher))
                .subscribe(new Outcome<FlowContext>() {
                    @Override
                    public void onError(FlowContext context, Throwable error) {
                        clearTimeout(timeoutHandle);
                        setVisible(loadingSection, false);
                        MessageEvent.fire(eventBus,
                                Message.error(resources.messages().topologyError(), error.getMessage()));
                    }

                    @Override
                    public void onSuccess(FlowContext context) {
                        clearTimeout(timeoutHandle);
                        setVisible(loadingSection, false);
                        Elements.removeChildrenFrom(topologySection);

                        List<Host> hosts = context.get(TopologyTasks.HOSTS);
                        List<ServerGroup> serverGroups = context.get(TopologyTasks.SERVER_GROUPS);
                        List<Server> servers = context.get(TopologyTasks.SERVERS);

                        topologySection.appendChild(buildTable(hosts, serverGroups, servers));
                        setVisible(topologySection, true);
                        adjustTdHeight();

                        // restore selection
                        if (hostName != null) {
                            hosts.stream()
                                    .filter(host -> hostName.equals(host.getName()))
                                    .findAny()
                                    .ifPresent(host -> hostDetails(host));
                        }
                        if (serverGroupName != null) {
                            serverGroups.stream()
                                    .filter(serverGroup -> serverGroupName.equals(serverGroup.getName()))
                                    .findAny()
                                    .ifPresent(serverGroup -> serverGroupDetails(serverGroup));
                        }
                        if (serverName != null) {
                            servers.stream()
                                    .filter(server -> serverName.equals(server.getName()))
                                    .findAny()
                                    .ifPresent(server -> serverDetails(server));
                        }
                    }
                });
    }

    private void updateServer(Server server) {
        series(new FlowContext(progress.get()), topology(environment, dispatcher))
                .subscribe(new Outcome<FlowContext>() {
                    @Override
                    public void onError(FlowContext context, Throwable error) {
                        MessageEvent.fire(eventBus,
                                Message.error(resources.messages().updateServerError(server.getName()),
                                        error.getMessage()));
                    }

                    @Override
                    public void onSuccess(FlowContext context) {
                        Host host = null;
                        List<Host> hosts = context.get(TopologyTasks.HOSTS);
                        for (Host h : hosts) {
                            if (h.getName().equals(server.getHost())) {
                                host = h;
                                break;
                            }
                        }
                        ServerGroup serverGroup = null;
                        List<ServerGroup> serverGroups = context.get(TopologyTasks.SERVER_GROUPS);
                        for (ServerGroup sg : serverGroups) {
                            if (sg.getName().equals(server.getServerGroup())) {
                                serverGroup = sg;
                                break;
                            }
                        }

                        if (host != null && serverGroup != null) {
                            // Does not matter where we take the updated server from (must be included in both
                            // host and server group)
                            Host finalHost = host;
                            ServerGroup finalServerGroup = serverGroup;
                            host.getServers().stream()
                                    .filter(srv -> srv.getId().equals(server.getId()))
                                    .findAny()
                                    .ifPresent(updatedServer -> {
                                        String updatedContainerId = Ids.build(updatedServer.getId(), CONTAINER);
                                        replaceElement(document.getElementById(updatedContainerId),
                                                () -> serverElement(updatedServer),
                                                whatever -> serverDetails(updatedServer));
                                    });

                            // Update not only the server, but also the host and server group elements. Since the
                            // server's state has changed the host and server group dropdown links need to be updated
                            // as well.
                            replaceElement(document.querySelector(hostSelector(host)),
                                    () -> hostElement(finalHost),
                                    whatever -> hostDetails(finalHost));
                            replaceElement(document.querySelector(serverGroupSelector(serverGroup)),
                                    () -> serverGroupElement(finalServerGroup),
                                    updatedElement -> serverGroupDetails(finalServerGroup));
                        }
                    }
                });
    }


    // ------------------------------------------------------ UI methods

    private HTMLElement buildTable(List<Host> hosts, List<ServerGroup> serverGroups, List<Server> servers) {
        HTMLTableElement table = table().css(topology).element();

        // <colgroup>
        double width = 100.0 / (serverGroups.size() + 1);
        HtmlContentBuilder<HTMLTableColElement> colgroup = colgroup()
                .add(col().attr("width", width + "%"));
        for (int i = 0; i < serverGroups.size(); i++) {
            colgroup.add(col().attr("width", width + "%"));
        }
        table.appendChild(colgroup.element());
        // </colgroup>

        // <thead>
        HtmlContentBuilder<HTMLTableSectionElement> thead = thead()
                .add(tr()
                        .add(th().css(empty)
                                .innerHtml(new SafeHtmlBuilder()
                                        .appendEscaped(Names.SERVER_GROUPS + " ")
                                        .appendHtmlConstant("&rarr;").appendHtmlConstant("<br/>")
                                        .appendEscaped(Names.HOSTS + " ")
                                        .appendHtmlConstant("&darr;").toSafeHtml()))
                        .addAll(serverGroups.stream().map(this::serverGroupElement).collect(toList())));
        table.appendChild(thead.element());
        // </thead>

        // <tbody>
        HTMLElement tbody = tbody().element();
        for (Host host : hosts) {
            HTMLElement tr;
            tbody.appendChild(tr = tr().element());
            tr.appendChild(hostElement(host));
            for (ServerGroup serverGroup : serverGroups) {
                List<HTMLElement> matchingServers = servers.stream()
                        .filter(sc -> host.getName().equals(sc.getHost()) &&
                                serverGroup.getName().equals(sc.getServerGroup()))
                        .sorted(comparing(Server::getName))
                        .map(this::serverElement)
                        .collect(toList());
                if (matchingServers.isEmpty()) {
                    tr.appendChild(td().css(empty).element());
                } else {
                    tr.appendChild(td()
                            .add(div().css(CSS.servers)
                                    .addAll(matchingServers)).element());
                }
            }
        }
        table.appendChild(tbody);
        // </tbody>

        return table;
    }

    private HTMLElement hostElement(Host host) {
        HTMLElement dropdown;
        HTMLTableCellElement th = th()
                .css(asList(rowHeader, statusCss(host)).toArray(new String[]{}))
                .on(click, event -> hostDetails(host))
                .data("host", host.getName()) //NON-NLS
                .add(div().css(hostContainer)
                        .add(dropdown = div().css(CSS.dropdown).element())).element();

        HTMLElement hostNameElement;
        if (host.isAlive() && !hostActions.isPending(host) && isAllowed(host)) {
            String hostDropDownId = Ids.host(host.getAddressName());
            dropdown.appendChild(hostNameElement = a()
                    .id(hostDropDownId)
                    .css(clickable, dropdownToggle, name)
                    .data(UIConstants.TOGGLE, UIConstants.DROPDOWN)
                    .aria(UIConstants.HAS_POPUP, UIConstants.TRUE)
                    .title(host.getName()).element());
            dropdown.appendChild(ul()
                    .css(dropdownMenu)
                    .attr(UIConstants.ROLE, UIConstants.MENU)
                    .aria(UIConstants.LABELLED_BY, hostDropDownId)
                    .addAll(hostActions(host)).element());
        } else {
            dropdown.appendChild(hostNameElement = span().css(name).title(host.getName()).element());
        }
        hostNameElement.appendChild(hostNameElement.ownerDocument.createTextNode(host.getName()));
        if (!host.isConnected()) {
            hostNameElement.classList.add(disconnected);
            hostNameElement.title = hostNameElement.title + " (" + resources.constants().disconnected() + ")";
        }
        if (host.isDomainController()) {
            hostNameElement.appendChild(
                    span().css(fontAwesome("star"), marginLeft5).title(Names.DOMAIN_CONTROLLER).element());
        }

        return th;
    }

    private HTMLElement serverGroupElement(ServerGroup serverGroup) {
        HTMLElement dropdown;
        HTMLTableCellElement element = th()
                .on(click, event -> serverGroupDetails(serverGroup))
                .data("serverGroup", serverGroup.getName()) //NON-NLS
                .add(div().css(serverGroupContainer)
                        .add(dropdown = div().css(CSS.dropdown).element())).element();

        if (!serverGroupActions.isPending(serverGroup) && isAllowed(serverGroup)) {
            String serverGroupDropDownId = Ids.serverGroup(serverGroup.getName());
            dropdown.appendChild(a()
                    .id(serverGroupDropDownId)
                    .css(clickable, dropdownToggle, name)
                    .data(UIConstants.TOGGLE, UIConstants.DROPDOWN)
                    .aria(UIConstants.HAS_POPUP, UIConstants.TRUE)
                    .title(serverGroup.getName())
                    .textContent(serverGroup.getName()).element());
            dropdown.appendChild(ul()
                    .css(dropdownMenu)
                    .attr(UIConstants.ROLE, UIConstants.MENU)
                    .aria(UIConstants.LABELLED_BY, serverGroupDropDownId)
                    .addAll(serverGroupActions(serverGroup)).element());
        } else {
            dropdown.appendChild(span()
                    .css(name)
                    .title(serverGroup.getName())
                    .textContent(serverGroup.getName()).element());
        }
        return element;
    }

    private HTMLElement serverElement(Server srv) {
        HTMLElement dropdown;
        HTMLDivElement element = div()
                .id(Ids.build(srv.getId(), CONTAINER))
                .css(asList(server, statusCss(srv)).toArray(new String[]{}))
                .data(SERVER, srv.getId())
                .on(click, event -> serverDetails(srv))
                .add(dropdown = div().css(CSS.dropdown).element()).element();

        if (!serverActions.isPending(srv) && isAllowed(srv)) {
            dropdown.appendChild(a()
                    .id(srv.getId())
                    .css(clickable, dropdownToggle, name)
                    .data(UIConstants.TOGGLE, UIConstants.DROPDOWN)
                    .aria(UIConstants.HAS_POPUP, UIConstants.TRUE)
                    .title(srv.getName())
                    .textContent(srv.getName()).element());
            dropdown.appendChild(ul()
                    .css(dropdownMenu)
                    .attr(UIConstants.ROLE, UIConstants.MENU)
                    .aria(UIConstants.LABELLED_BY, srv.getId())
                    .addAll(serverActions(srv)).element());
        } else {
            dropdown.appendChild(span()
                    .css(name)
                    .title(srv.getName())
                    .textContent(srv.getName()).element());
        }
        return element;
    }

    private void replaceElement(Element existingElement, Supplier<Element> updateElement, Consumer<Element> select) {
        if (existingElement != null) {
            boolean hasSelection = existingElement.classList.contains(selected);
            Element parent = (Element) existingElement.parentNode;
            if (parent != null) {
                Element updatedElement = updateElement.get();
                parent.replaceChild(updatedElement, existingElement);
                if (hasSelection && select != null) {
                    select.accept(updatedElement);
                }
            }
        }
    }

    private void adjustTdHeight() {
        NodeList<Element> servers = document.querySelectorAll(DOT + topology + " ." + CSS.servers);
        Elements.stream(servers)
                .filter(htmlElements())
                .map(asHtmlElement())
                .forEach(element -> {
                    HTMLElement parent = (HTMLElement) element.parentNode;
                    element.style.height = height(px(parent.offsetHeight - 1));
                });
    }

    private void hideDetails() {
        setVisible(serverGroupAttributesSection, false);
        setVisible(hostAttributesSection, false);
        setVisible(serverAttributesSection, false);
    }

    private void clearSelected() {
        NodeList<Element> selectedNodes = document.querySelectorAll(DOT + topology + " ." + selected);
        Elements.elements(selectedNodes).forEach(element -> element.classList.remove(selected));
    }

    private HTMLElement actionLink(EventCallbackFn<MouseEvent> listener, String text) {
        return li().attr(UIConstants.ROLE, UIConstants.PRESENTATION)
                .add(a().css(clickable).on(click, listener).textContent(text)).element();
    }

    private void startProgress(String selector) {
        Elements.stream(document.querySelectorAll(selector))
                .forEach(element -> element.classList.add(withProgress));
    }

    private void stopProgress(String selector) {
        Elements.stream(document.querySelectorAll(selector))
                .forEach(element -> element.classList.remove(withProgress));
    }

    private void disableDropdown(String id, String name) {
        Element link = document.getElementById(id);
        if (link != null) {
            Element parent = (Element) link.parentNode;
            Element ul = link.nextElementSibling;
            if (parent != null && ul != null) {
                HTMLElement noLink = span().css(CSS.name).title(name).textContent(name).element();
                parent.classList.remove("opened"); //NON-NLS
                parent.replaceChild(noLink, link);
                parent.removeChild(ul);
            }
        }
    }

    private boolean isVisible() {
        return Elements.isVisible(topologySection) && topologySection.parentNode != null;
    }


    // ------------------------------------------------------ host

    private void hostDetails(Host host) {
        clearSelected();
        HTMLElement element = (HTMLElement) document.querySelector(hostSelector(host));
        if (element != null) {
            element.classList.add(selected);
        }

        HostPreviewAttributes.refresh(host, hostAttributes, hostActions);
        setVisible(serverGroupAttributesSection, false);
        setVisible(hostAttributesSection, true);
        setVisible(serverAttributesSection, false);
    }

    private boolean isAllowed(Host host) {
        // To keep it simple, we take a all or nothing approach:
        // We check *one* action and assume that the other actions have the same constraints
        return AuthorisationDecision.from(environment, securityContextRegistry)
                .isAllowed(Constraint.executable(AddressTemplate.of("/host=" + host.getAddressName()), RELOAD));
    }

    private List<HTMLElement> hostActions(Host host) {
        List<HTMLElement> actions = new ArrayList<>();
        actions.add(actionLink(event -> hostActions.reload(host), resources.constants().reload()));
        actions.add(actionLink(event -> hostActions.restart(host), resources.constants().restart()));
        return actions;
    }

    private String[] statusCss(Host host) {
        return hostActions.isPending(host) ? new String[]{withProgress} : new String[]{};
    }

    @Override
    public void onHostAction(HostActionEvent event) {
        if (isVisible()) {
            Host host = event.getHost();

            disableDropdown(Ids.host(host.getAddressName()), host.getName());
            startProgress(hostSelector(host));

            event.getServers().forEach(server -> {
                disableDropdown(server.getId(), server.getName());
                startProgress(serverSelector(server));
            });
        }
    }

    @Override
    public void onHostResult(HostResultEvent event) {
        if (isVisible()) {
            Host host = event.getHost();

            stopProgress(hostSelector(host));
            event.getServers().forEach(server -> stopProgress(serverSelector(server)));
            update(null);
        }
    }

    private String hostSelector(Host host) {
        return "[data-host='" + host.getName() + "']"; //NON-NLS
    }


    // ------------------------------------------------------ server group

    private void serverGroupDetails(ServerGroup serverGroup) {
        clearSelected();
        HTMLElement element = (HTMLElement) document.querySelector(serverGroupSelector(serverGroup));
        if (element != null) {
            element.classList.add(selected);
        }

        serverGroupAttributes.refresh(serverGroup);
        setVisible(serverGroupAttributesSection, true);
        setVisible(hostAttributesSection, false);
        setVisible(serverAttributesSection, false);
    }

    private boolean isAllowed(ServerGroup serverGroup) {
        // To keep it simple, we take a all or nothing approach:
        // We check *one* action and assume that the other actions have the same constraints
        Constraints constraints = Constraints.or(
                Constraint.executable(AddressTemplate.of("/server-group=*"), RELOAD_SERVERS),
                Constraint.executable(AddressTemplate.of("/server-group=" + serverGroup.getName()), RELOAD_SERVERS));
        return AuthorisationDecision.from(environment, securityContextRegistry)
                .isAllowed(constraints);
    }

    private List<HTMLElement> serverGroupActions(ServerGroup serverGroup) {
        List<HTMLElement> actions = new ArrayList<>();

        // Order is: reload, restart, suspend, resume, stop, start
        if (serverGroup.hasServers(Server::isStarted)) {
            actions.add(actionLink(event -> serverGroupActions.reload(serverGroup), resources.constants().reload()));
            actions.add(actionLink(event -> serverGroupActions.restart(serverGroup), resources.constants().restart()));
        }
        if (serverGroup.getServers(Server::isStarted).size() - serverGroup.getServers(Server::isSuspended).size() > 0) {
            actions.add(actionLink(event -> serverGroupActions.suspend(serverGroup), resources.constants().suspend()));
        }
        if (serverGroup.hasServers(Server::isSuspended)) {
            actions.add(actionLink(event -> serverGroupActions.resume(serverGroup), resources.constants().resume()));
        }
        if (serverGroup.hasServers(Server::isStarted)) {
            actions.add(actionLink(event -> serverGroupActions.stop(serverGroup), resources.constants().stop()));
        }
        if (serverGroup.hasServers(server -> server.isStopped() || server.isFailed())) {
            actions.add(actionLink(event -> serverGroupActions.start(serverGroup), resources.constants().start()));
            actions.add(actionLink(event -> serverGroupActions.startInSuspendedMode(serverGroup), resources.constants().startInSuspendedMode()));
        }
        // add kill link regardless of state to destroy and kill servers which might show a wrong state
        actions.add(actionLink(event -> serverGroupActions.destroy(serverGroup), resources.constants().destroy()));
        actions.add(actionLink(event -> serverGroupActions.kill(serverGroup), resources.constants().kill()));

        // add remove action to groups which have only stopped servers or no servers at all
        if (!serverGroup.hasServers(Server::isStarted)) {
            actions.add(actionLink(event -> serverGroupActions.remove(serverGroup), resources.constants().remove()));
        }
        return actions;
    }

    @Override
    public void onServerGroupAction(ServerGroupActionEvent event) {
        if (isVisible()) {
            ServerGroup serverGroup = event.getServerGroup();
            disableDropdown(Ids.serverGroup(serverGroup.getName()), serverGroup.getName());
            event.getServers().forEach(server -> {
                disableDropdown(server.getId(), server.getName());
                startProgress(serverSelector(server));
            });
        }
    }

    @Override
    public void onServerGroupResult(ServerGroupResultEvent event) {
        if (isVisible()) {
            event.getServers().forEach(server -> stopProgress(serverSelector(server)));
            update(null);
        }
    }

    private String serverGroupSelector(ServerGroup serverGroup) {
        return "[data-server-group='" + serverGroup.getName() + "']"; //NON-NLS
    }


    // ------------------------------------------------------ server

    private void serverDetails(Server server) {
        clearSelected();
        HTMLElement element = (HTMLElement) document.querySelector(serverSelector(server));
        if (element != null) {
            element.classList.add(selected);
        }

        if (server.hasBootErrors()) {
            PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.SERVER_BOOT_ERRORS)
                    .with(HOST, server.getHost())
                    .with(SERVER, server.getName())
                    .build();
            String token = places.historyToken(placeRequest);
            serverAttributes.setDescription(resources.messages().serverBootErrorsAndLink(server.getName(), token));
        } else {
            serverAttributes.hideDescription();
        }
        ServerPreviewAttributes.refresh(server, serverAttributes);
        setVisible(serverGroupAttributesSection, false);
        setVisible(hostAttributesSection, false);
        setVisible(serverAttributesSection, true);

        if (server.isStarted()) {
            serverActions.readUrl(server, serverUrl);
        }
    }

    private boolean isAllowed(Server server) {
        // To keep it simple, we take a all or nothing approach:
        // We check *one* action and assume that the other actions have the same constraints
        return AuthorisationDecision.from(environment, securityContextRegistry)
                .isAllowed(Constraint.executable(AddressTemplate.of("/host=" + server.getHost() + "/server-config=*"),
                        RELOAD));
    }

    private List<HTMLElement> serverActions(Server server) {
        List<HTMLElement> actions = new ArrayList<>();

        if (!server.isStarted()) {
            actions.add(actionLink(event -> serverActions.start(server), resources.constants().start()));
            actions.add(actionLink(event -> serverActions.startInSuspendedMode(server), resources.constants().startInSuspendedMode()));
        } else {
            actions.add(actionLink(event -> serverActions.editUrl(server, () -> {
                if (isVisible()) {
                    updateServer(server);
                }
            }), resources.constants().editURL()));
            actions.add(li().css(divider).attr(UIConstants.ROLE, UIConstants.SEPARATOR).element());
            // Order is: reload, restart, (resume | suspend), stop
            actions.add(actionLink(event -> serverActions.reload(server), resources.constants().reload()));
            actions.add(actionLink(event -> serverActions.restart(server), resources.constants().restart()));
            if (server.isSuspended()) {
                actions.add(actionLink(event -> serverActions.resume(server), resources.constants().resume()));
            } else {
                actions.add(actionLink(event -> serverActions.suspend(server), resources.constants().suspend()));
            }
            actions.add(actionLink(event -> serverActions.stop(server), resources.constants().stop()));
        }
        // add kill link regardless of state to destroy and kill servers which might show a wrong state
        actions.add(actionLink(event -> serverActions.destroy(server), resources.constants().destroy()));
        actions.add(actionLink(event -> serverActions.kill(server), resources.constants().kill()));

        return actions;
    }

    private String[] statusCss(Server server) {
        Set<String> status = new HashSet<>();
        ServerStatusSwitch sss = new ServerStatusSwitch(serverActions) {
            @Override
            protected void onPending(Server server) {
            }

            @Override
            protected void onBootErrors(Server server) {
                status.add(error);
            }

            @Override
            protected void onFailed(Server server) {
                status.add(error);
            }

            @Override
            protected void onAdminMode(Server server) {
                status.add(inactive);
            }

            @Override
            protected void onStarting(Server server) {
            }

            @Override
            protected void onSuspended(Server server) {
                status.add(suspended);
            }

            @Override
            protected void onNeedsReload(Server server) {
                status.add(warning);
            }

            @Override
            protected void onNeedsRestart(Server server) {
                status.add(warning);
            }

            @Override
            protected void onRunning(Server server) {
                status.add(ok);
            }

            @Override
            protected void onStopped(Server server) {
                status.add(inactive);
            }

            @Override
            protected void onUnknown(Server server) {
            }
        };
        sss.accept(server);
        if (serverActions.isPending(server) || server.isStandalone()) {
            status.add(withProgress);
        }
        return status.toArray(new String[0]);
    }

    @Override
    public void onServerAction(ServerActionEvent event) {
        if (isVisible()) {
            Server server = event.getServer();
            disableDropdown(server.getId(), server.getName());
            startProgress(serverSelector(server));
        }
    }

    @Override
    public void onServerResult(ServerResultEvent event) {
        if (isVisible()) {
            stopProgress(serverSelector(event.getServer()));
            updateServer(event.getServer());
        }
    }

    private String serverSelector(Server server) {
        return "[data-server='" + server.getId() + "']"; //NON-NLS
    }
}
