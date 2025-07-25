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

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryCepTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseStreamConfigurations(true).stream();
    }
    
    private KieSession ksession;
    
    private SessionPseudoClock clock;
    
    private EntryPoint firstEntryPoint, secondEntryPoint;

    public void prepare(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = "package org.drools.mvel.integrationtests\n" +
                "import " + TestEvent.class.getCanonicalName() + "\n" +
                "declare TestEvent\n" +
                "    @role( event )\n" + 
                "end\n" + 
                "query EventsFromStream\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "end\n" + 
                "query ZeroToNineteenSeconds\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "    $result : TestEvent ( this after [0s, 19s] $event) from entry-point SecondStream\n" + 
                "end\n";
        
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.PSEUDO);

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                                  .setTargetPath("org/drools/compiler/integrationtests/queries.drl") );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, true);
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        firstEntryPoint = ksession.getEntryPoint("FirstStream");
        secondEntryPoint = ksession.getEntryPoint("SecondStream"); 
        clock = ksession.getSessionClock();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void noResultTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	prepare(kieBaseTestConfiguration);
        QueryResults results = ksession.getQueryResults("EventsFromStream");
        assertThat(results.size()).isEqualTo(0);
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void withResultTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	prepare(kieBaseTestConfiguration);
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("two"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");

        assertThat(results.size()).isEqualTo(1);
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void withNoResultTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	prepare(kieBaseTestConfiguration);
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("two"));
        // the following expires event "zero" and "one", causing the query to no longer match
        clock.advanceTime(10, TimeUnit.SECONDS); 

        secondEntryPoint.insert(new TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");

        assertThat(results.size()).isEqualTo(0);
    }
    
    @AfterEach
    public void cleanup() {
        
        if (ksession != null) {
            ksession.dispose();
        }
    }
    
    public static class TestEvent {
        
        private final String name;
        
        public TestEvent(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }        
        
        @Override
        public String toString() {
            return "TestEvent[" + name + "]";
        }
    }
}
