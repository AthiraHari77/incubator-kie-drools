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
package org.kie.dmn.core.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNTestUtil {
    private DMNTestUtil() {
        // No constructor for util class.
    }

    public static DMNModel getAndAssertModelNoErrors(final DMNRuntime runtime, final String namespace, final String modelName) {
        DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        return dmnModel;
    }

    public static Function<String, Reader> getRelativeResolver(String key, String content) {
        return s -> s.equals(key) ? new StringReader(content) : null;
    }
}
