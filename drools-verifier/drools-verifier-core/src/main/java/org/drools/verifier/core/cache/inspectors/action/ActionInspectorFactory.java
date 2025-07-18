/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.core.cache.inspectors.action;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.maps.InspectorFactory;

public class ActionInspectorFactory
        extends InspectorFactory<ActionInspector, Action> {

    public ActionInspectorFactory(final AnalyzerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ActionInspector make(final Action action) {
        if (action instanceof FieldAction) {
            return new FieldActionInspector((FieldAction) action,
                                            configuration);
        } else {
            return null;
        }
    }
}
