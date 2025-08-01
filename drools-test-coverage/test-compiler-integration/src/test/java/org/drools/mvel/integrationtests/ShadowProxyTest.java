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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.Child;
import org.drools.mvel.compiler.MockPersistentSet;
import org.drools.mvel.compiler.ObjectWithSet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ShadowProxyTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testShadowProxyInHierarchies(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ShadowProxyInHierarchies.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Child("gp"));
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testShadowProxyOnCollections(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ShadowProxyOnCollections.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Cheesery cheesery = new Cheesery();
            ksession.insert(cheesery);

            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(1);
            assertThat(cheesery.getCheeses().size()).isEqualTo(1);
            assertThat(cheesery.getCheeses().get(0)).isEqualTo(results.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testShadowProxyOnCollections2(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ShadowProxyOnCollections2.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final List list = new ArrayList();
            list.add("example1");
            list.add("example2");

            final MockPersistentSet mockPersistentSet = new MockPersistentSet(false);
            mockPersistentSet.addAll(list);
            final ObjectWithSet objectWithSet = new ObjectWithSet();
            objectWithSet.setSet(mockPersistentSet);

            ksession.insert(objectWithSet);

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(objectWithSet.getMessage()).isEqualTo("show");
        } finally {
            ksession.dispose();
        }
    }
}
