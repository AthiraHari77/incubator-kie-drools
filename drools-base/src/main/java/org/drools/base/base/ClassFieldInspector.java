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
package org.drools.base.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.kie.internal.builder.KnowledgeBuilderResult;

public interface ClassFieldInspector {
    Map<String, Integer> getFieldNames();

    boolean isNonGetter( String name );

    Map<String, Field> getFieldTypesField();

    Map<String, Class< ? >> getFieldTypes();

    Class< ? > getFieldType(String name);

    Map<String, Method> getGetterMethods();

    Map<String, Method> getSetterMethods();

    Collection<KnowledgeBuilderResult> getInspectionResults( String fieldName );
}
