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
package org.drools.testcoverage.regression;

import java.io.StringReader;

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;

public class ImportReplaceTest {

    private static final String declares =
            "package " + TestConstants.PACKAGE_REGRESSION + ".importreplace\n"
            + "import " + TestConstants.PACKAGE_TESTCOVERAGE + ".common.model.Person\n"
            + "declare SomePerson\n"
            + "    person : Person\n"
            + "    weight : double\n"
            + "    height : double\n"
            + "end\n";

    private static final String rules =
            "package " + TestConstants.PACKAGE_REGRESSION + ".importreplace\n"
            + "import " + TestConstants.PACKAGE_TESTCOVERAGE + ".common.model.Person\n"
            + "declare Holder\n"
            + "    person : Person\n"
            + "end\n"
            + "rule \"create holder\"\n"
            + "    when\n"
            + "        person : Person( )\n"
            + "        not (\n"
            + "            Holder( person; )\n"
            + "        )\n"
            + "    then\n"
            + "        insert(new Holder(person));\n"
            + "end\n";

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void test(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final Resource declaresResource =
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(declares));
        declaresResource.setTargetPath("src/main/resources/declares.drl");

        final Resource rulesResource =
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(rules));
        rulesResource.setTargetPath("src/main/resources/rules.drl");

        // this should be OK
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, declaresResource, rulesResource);

        // this should be fine too
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, rulesResource, declaresResource);
    }

}
