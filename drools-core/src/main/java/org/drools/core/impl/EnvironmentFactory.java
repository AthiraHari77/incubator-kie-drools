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
package org.drools.core.impl;

import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

public class EnvironmentFactory {

    public static Environment newEnvironment() {
            Environment env = new EnvironmentImpl();
            env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, 
                    new ObjectMarshallingStrategy [] {
                        RuntimeComponentFactory.get().createDefaultObjectMarshallingStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
                    });
        return env;
    }

}
