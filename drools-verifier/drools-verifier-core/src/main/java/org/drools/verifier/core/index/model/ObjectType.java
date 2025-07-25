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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;

public class ObjectType
        implements HasKeys {

    private final static KeyDefinition TYPE = KeyDefinition.newKeyDefinition()
            .withId("type")
            .build();

    private final UUIDKey uuidKey;
    private final String type;
    private final ObjectFields fields = new ObjectFields();

    public ObjectType(final String type,
                      final AnalyzerConfiguration configuration) {
        this.type = type;
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers type() {
        return new Matchers(TYPE);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                TYPE
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getType() {
        return type;
    }

    public ObjectFields getFields() {
        return fields;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(TYPE,
                        type)
        };
    }
}
