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
package org.jboss.hal.core.finder;

/**
 * Holds state as the user navigates using the finder.
 *
 * @author Harald Pehl
 */
public class FinderContext {

    private String token;
    private FinderPath path;

    FinderContext() {
        token = null;
        path = new FinderPath();
    }

    void reset(final String token) {
        this.token = token;
        path.clear();
    }

    public void reset(final FinderPath path) {
        this.path = path;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public FinderPath getPath() {
        return path;
    }
}
