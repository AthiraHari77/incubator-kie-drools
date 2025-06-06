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
package org.kie.efesto.compilationmanager.core.mocks;

import java.util.Collections;
import java.util.List;

import java.util.Map;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;
import org.kie.memorycompiler.KieMemoryCompiler;

public class MockEfestoCallableOutputE extends AbstractMockEfestoCallableOutput {

    private static final String fullClassName ="mock.efesto.output.ModuleE";

    private static final String moduleClass = "package mock.efesto.output;\n" +
            "public class ModuleE {}";

    public MockEfestoCallableOutputE() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/efesto/output/ModuleE")), MockEfestoCallableOutputE.class.getName(), getMappedCompiledBytes(fullClassName, moduleClass));
    }

    @Override
    public List<String> getFullClassNames() {
        return Collections.singletonList(fullClassName);
    }

}
