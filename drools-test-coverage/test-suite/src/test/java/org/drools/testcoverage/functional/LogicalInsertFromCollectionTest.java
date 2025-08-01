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
package org.drools.testcoverage.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.SimplePerson;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which takes a collection in working memory and calls iserLogical on each
 * its elements, than changes the collection in program and checks the correct
 * changes.
 */
public class LogicalInsertFromCollectionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRemoveElement(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession ksession = getKieBaseForTest(kieBaseTestConfiguration).newKieSession();
        final Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(i);
        }

        final FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        for (int i = 5; i > 1; i--) {

            // before remove 5,4,3,2,1 facts
            assertThat(ksession.getFactCount()).isEqualTo(i);

            collection.remove(collection.iterator().next());
            ksession.update(handle, collection);
            ksession.fireAllRules();
            // after removing 4,3,2,1,0 facts
            assertThat(ksession.getFactCount()).isEqualTo(i - 1);
        }

    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAddElement(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession ksession = getKieBaseForTest(kieBaseTestConfiguration).newKieSession();

        final Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(i);
        }

        FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        // before adding 5 facts
        assertThat(ksession.getFactCount()).isEqualTo(5);

        collection.add(42);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        // after adding should be 6 facts
        assertThat(ksession.getFactCount()).isEqualTo(6);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testChangeElement(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession ksession = getKieBaseForTest(kieBaseTestConfiguration).newKieSession();

        final Collection<SimplePerson> collection = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            collection.add(new SimplePerson("Person " + i, 10 * i));
        }

        final FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        // before change - expecting 5 facts
        assertThat(ksession.getFactCount()).isEqualTo(5);

        collection.iterator().next().setAge(80);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        // after change - expecting 4 facts
        assertThat(ksession.getFactCount()).isEqualTo(4);

        collection.iterator().next().setAge(30);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        assertThat(ksession.getFactCount()).isEqualTo(5);

    }

    private KieBase getKieBaseForTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("logicalInsertFromCollectionTest.drl", getClass());
        return KieBaseUtil.getKieBaseFromKieModuleFromResources(TestConstants.PACKAGE_REGRESSION,
                                                                kieBaseTestConfiguration, drlResource);
    }
}
