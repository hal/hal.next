/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.hal.dmr.dispatch;

import com.ekuefler.supereventbus.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.inject.Provider;
import elemental.dom.Element;
import elemental.html.InputElement;
import org.jboss.hal.config.Endpoints;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.Property;
import org.jboss.hal.dmr.model.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

/**
 * The dispatcher executes operations against the management endpoint. You can register different callbacks to react on
 * failed management operations ({@link #setFailedCallback(FailedCallback)} or technical errors
 * {@link #setExceptionCallback(ExceptionCallback)}.
 * <p>
 * TODO Add a way to track the management operations.
 * TODO Handle bootstrap finished event and setup response processor based on operation mode
 *
 * @author Harald Pehl
 */
public class Dispatcher {

    @FunctionalInterface
    public interface SuccessCallback {

        void onSuccess(ModelNode payload);
    }


    @FunctionalInterface
    public interface FailedCallback {

        void onFailed(Operation operation, String failure);
    }


    @FunctionalInterface
    public interface ExceptionCallback {

        void onException(Operation operation, Throwable exception);
    }


    @FunctionalInterface
    private interface PayloadProcessor {

        ModelNode processPayload(String method, String payload);
    }


    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String DMR_ENCODED = "application/dmr-encoded";

    /**
     * The read resource description supports the following parameters:
     * recursive, proxies, operations, inherited plus one not documented: locale.
     * See https://docs.jboss.org/author/display/AS72/Global+operations#Globaloperations-readresourcedescription
     * for a more detailed description
     */
    private static final String[] READ_RESOURCE_DESCRIPTION_OPTIONAL_PARAMETERS = new String[]{
            RECURSIVE, PROXIES, OPERATIONS, INHERITED, LOCALE
    };

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private final Endpoints endpoints;
    private final EventBus eventBus;
    private final Provider<ResponseProcessor> responseProcessor;
    private final PayloadProcessor dmrPayloadProcessor;
    private final PayloadProcessor uploadPayloadProcessor;
    private FailedCallback failedCallback;
    private ExceptionCallback exceptionCallback;

    @Inject
    public Dispatcher(final Endpoints endpoints, final EventBus eventBus,
            Provider<ResponseProcessor> responseProcessor) {
        this.endpoints = endpoints;
        this.eventBus = eventBus;
        this.responseProcessor = responseProcessor;

        this.dmrPayloadProcessor = (method, payload) -> {
            ModelNode response;
            try {
                response = ModelNode.fromBase64(payload);
                if ("GET".equals(method)) {
                    // For GET request the response is purely the model nodes result. The outcome
                    // is not send as part of the response but expressed with the HTTP status code.
                    // In order to not break existing code, we repackage the payload into a
                    // new model node with an "outcome" and "result" key.
                    ModelNode repackaged = new ModelNode();
                    repackaged.get(OUTCOME).set(SUCCESS);
                    repackaged.get(RESULT).set(response);
                    response = repackaged;
                }
            } catch (Throwable e) {
                ModelNode err = new ModelNode();
                err.get(OUTCOME).set(FAILED);
                err.get(FAILURE_DESCRIPTION)
                        .set("Failed to decode response: " + e.getClass().getName() + ": " + e.getMessage());
                response = err;
            }
            return response;
        };
        this.uploadPayloadProcessor = (method, payload) -> {
            // TODO Parse basic JSON properties like 'outcome' and 'failureDescription'.
            // Repack the 'result' as 'payload' string model node
            return new ModelNode();
        };

        // TODO Come up with some more useful defaults
        this.failedCallback = (operation, failure) -> logger.error("DMR operation {} failed: {}", operation, failure);
        this.exceptionCallback = (operation, t) -> logger.error("Error while executing DRM operation {}: {}", operation,
                t.getMessage());
    }


    // ------------------------------------------------------ execute methods

    public void execute(final Operation operation, final SuccessCallback successCallback) {
        execute(operation, successCallback, failedCallback, exceptionCallback);
    }

    public void execute(final Operation operation, final SuccessCallback successCallback,
            FailedCallback failedCallback) {
        execute(operation, successCallback, failedCallback, exceptionCallback);
    }

    public void execute(final Operation operation, final SuccessCallback successCallback,
            final FailedCallback failedCallback, ExceptionCallback exceptionCallback) {
        final RequestBuilder requestBuilder = chooseRequestBuilder(operation);
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(final Request request, final Response response) {
                processResponse(response.getStatusCode(), requestBuilder.getUrl(), requestBuilder.getHTTPMethod(),
                        response.getText(), operation, dmrPayloadProcessor, successCallback, failedCallback,
                        exceptionCallback);
            }

            @Override
            public void onError(final Request request, final Throwable throwable) {
                logger.error("Error getting DMR response for operation {}: {}", operation, throwable.getMessage());
                exceptionCallback.onException(operation, throwable);
            }
        };
        requestBuilder.setCallback(requestCallback);

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            logger.error("Error sending DMR request for operation {}: {}", operation, e.getMessage());
            exceptionCallback.onException(operation, e);
        }
    }


    // ------------------------------------------------------ upload

    public void upload(final InputElement fileInput, final Operation operation, final SuccessCallback successCallback) {
        upload(fileInput, operation, successCallback, failedCallback, exceptionCallback);
    }

    public void upload(final InputElement fileInput, final Operation operation, final SuccessCallback successCallback,
            FailedCallback failedCallback) {
        upload(fileInput, operation, successCallback, failedCallback, exceptionCallback);
    }

    public void upload(final InputElement fileInput, final Operation operation, final SuccessCallback successCallback,
            final FailedCallback failedCallback, ExceptionCallback exceptionCallback) {
        uploadNative(endpoints.upload(), fileInput, operation.toJSONString(true), operation,
                successCallback, failedCallback, exceptionCallback);
    }

    private native void uploadNative(String endpoint, Element fileInput, String operationValue, Operation operation,
            SuccessCallback successCallback, FailedCallback failedCallback, ExceptionCallback exceptionCallback) /*-{
        var that = this;

        var formData = new FormData();
        formData.append(fileInput.name, fileInput.files[0]);
        formData.append("operation", operationValue);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = true;
        xhr.onreadystatechange = $entry(function (evt) {
            var status, payload, readyState;
            try {
                readyState = evt.target.readyState;
                payload = evt.target.responseText;
                status = evt.target.status;
            }
            catch (e) {
                that.@org.jboss.hal.dmr.dispatch.Dispatcher::processUploadException(*)(e.message, operation,
                    exceptionCallback);
            }
            if (readyState == 4 && payload) {
                that.@org.jboss.hal.dmr.dispatch.Dispatcher::processUploadResponse(*)(status, endpoint, "POST", payload,
                    operation, exceptionCallback, successCallback, failedCallback);
            }
        });
        xhr.open("POST", endpoint, true);
        xhr.send(formData);
    }-*/;

    private void processUploadException(final String error, final Operation operation,
            final ExceptionCallback exceptionCallback) {
        exceptionCallback.onException(operation, new DispatchException(error, 500));
    }

    private void processUploadResponse(final int status, final String url, final String method, final String payload,
            final Operation operation, final SuccessCallback successCallback, final FailedCallback failedCallback,
            final ExceptionCallback exceptionCallback) {
        processResponse(status, url, method, payload, operation, uploadPayloadProcessor, successCallback,
                failedCallback, exceptionCallback);
    }


    // ------------------------------------------------------ request / response handling

    private RequestBuilder chooseRequestBuilder(final Operation operation) {
        RequestBuilder requestBuilder;

        final String op = operation.get(OP).asString();
        if (READ_RESOURCE_DESCRIPTION_OPERATION.equals(op)) {
            String endpoint = endpoints.dmr();
            if (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }
            String descriptionUrl = endpoint + descriptionOperationToUrl(operation);
            requestBuilder = new RequestBuilder(RequestBuilder.GET,
                    com.google.gwt.http.client.URL.encode(descriptionUrl));
            requestBuilder.setRequestData(null);
        } else {
            requestBuilder = new RequestBuilder(RequestBuilder.POST, endpoints.dmr());
            requestBuilder.setRequestData(operation.toBase64String());
        }
        requestBuilder.setIncludeCredentials(true);
        requestBuilder.setHeader(HEADER_ACCEPT, DMR_ENCODED);
        requestBuilder.setHeader(HEADER_CONTENT_TYPE, DMR_ENCODED);
        return requestBuilder;
    }

    private String descriptionOperationToUrl(final ModelNode operation) {
        StringBuilder url = new StringBuilder();
        final List<Property> address = operation.get(ADDRESS).asPropertyList();
        for (Property property : address) {
            url.append("/").append(property.getName()).append("/").append(property.getValue().asString());
        }

        url.append("?operation=").append("resource-description");
        for (String parameter : READ_RESOURCE_DESCRIPTION_OPTIONAL_PARAMETERS) {
            if (operation.has(parameter)) {
                url.append("&").append(parameter).append("=").append(operation.get(parameter).asString());
            }
        }
        return url.toString();
    }

    private void processResponse(final int status, final String url, final String method, final String payload,
            final Operation operation, final PayloadProcessor payloadProcessor, final SuccessCallback successCallback,
            final FailedCallback failedCallback, final ExceptionCallback exceptionCallback) {
        if (200 == status) {
            ModelNode responseNode = payloadProcessor.processPayload(method, payload);
            if (!responseNode.isFailure()) {
                if (responseProcessor.get().accepts(responseNode)) {
                    ProcessState processState = responseProcessor.get().process(responseNode);
                    eventBus.post(processState);
                }
                successCallback.onSuccess(responseNode.get(RESULT));
            } else {
                failedCallback.onFailed(operation, responseNode.getFailureDescription());
            }
        } else if (401 == status || 0 == status) {
            exceptionCallback
                    .onException(operation, new DispatchException("Authentication required.", status));
        } else if (403 == status) {
            exceptionCallback
                    .onException(operation, new DispatchException("Authentication required.", status));
        } else if (404 == status) {
            exceptionCallback.onException(operation, new DispatchException(
                    "Management interface at '" + url + " not found'.", status));
        } else if (503 == status) {
            exceptionCallback.onException(operation,
                    new DispatchException("Service temporarily unavailable. Is the server is still booting?",
                            status));
        } else {
            exceptionCallback
                    .onException(operation, new DispatchException("Unexpected status code + " + status, status));
        }
    }


    // ------------------------------------------------------ callbacks

    public void setFailedCallback(final FailedCallback failedCallback) {
        this.failedCallback = failedCallback;
    }

    public void setExceptionCallback(final ExceptionCallback exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
    }
}
