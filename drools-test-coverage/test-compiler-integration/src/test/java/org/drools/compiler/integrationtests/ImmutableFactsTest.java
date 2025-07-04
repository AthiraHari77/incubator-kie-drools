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
package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableFactsTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
    	return Stream.of(KieBaseTestConfiguration.CLOUD_IDENTITY_IMMUTABLE, KieBaseTestConfiguration.CLOUD_IDENTITY_IMMUTABLE_MODEL_PATTERN);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAlphaConstraint(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    String()\n" +
                "    Person( name.startsWith(\"M\") )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("immutable-facts-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        try {
            Person p = new Person("Edson");
            FactHandle fh = ksession.insert(p);

            ksession.insert(new Person("Mark"));
            assertThat(ksession.fireAllRules()).isEqualTo(0);

            Collection<?> persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            // Person("Edson") won't match any rule so it can be automatically evicted
            assertThat(persons.size()).isEqualTo(1);
            assertThat(((Person) persons.iterator().next()).getName()).isEqualTo("Mark");

            ksession.insert("test");
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            p.setName("Sofia");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(0);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(1);

            p.setName("Mario");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testBetaConstraint(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s: String()\n" +
                "    Person( name.startsWith($s) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("immutable-facts-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        try {
            Person p = new Person("Edson");
            FactHandle fh = ksession.insert(p);

            ksession.insert(new Person("Mark"));
            assertThat(ksession.fireAllRules()).isEqualTo(0);

            Collection<?> persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            // this time nothing can be said about Person("Edson") so it cannot be automatically evicted
            assertThat(persons.size()).isEqualTo(2);

            ksession.insert("M");
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            p.setName("Sofia");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(0);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);

            p.setName("Mario");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }
}
