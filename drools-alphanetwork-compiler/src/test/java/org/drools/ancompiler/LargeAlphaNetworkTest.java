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
package org.drools.ancompiler;

import java.util.ArrayList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class LargeAlphaNetworkTest extends BaseModelTest {


    @ParameterizedTest(name = "{0}")
	@MethodSource("parameters")
    public void testLargeCompiledAlphaNetwork(RUN_TYPE testRunType) {
        final StringBuilder rule =
                new StringBuilder("global java.util.List results;\n" +
                                          "import " + Person.class.getCanonicalName() + ";\n");

        int alphalength = 620;
        for (int i = 0; i < alphalength; i++) {
            rule.append(ruleWithIndex(i));
        }

        try (KieSession ksession = getKieSession(testRunType, rule.toString())) {
            ArrayList<Object> results = new ArrayList<>();
            ksession.setGlobal("results", results);
            Person a = new Person("a", 1);
            Person b = new Person("b", 0);
            Person c = new Person("a", 7);
            ksession.insert(a);
            ksession.insert(b);
            ksession.insert(c);

            ksession.fireAllRules();
            assertThat(results).contains(a, b, c);
        }
    }


    private String ruleWithIndex(final Integer index) {

        return "rule rule" + index + "A when\n" +
                "    $p : Person( age == " + index + ", name.startsWith(\"a\")  )\n" +
                "then\n" +
                " results.add($p);\n" +
                "end\n" +
                "rule rule" + index + "B when\n" +
                "    $p : Person( age == " + index + ", name.startsWith(\"b\")  )\n" +
                "then\n" +
                " results.add($p);\n" +
                "end\n" +
                "rule rule" + index + "c when\n" +
                "    $p : Person( age == " + index + ", name.startsWith(\"c\")  )\n" +
                "then\n" +
                " results.add($p);\n" +
                "end\n";
    }
}
