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
package org.drools.verifier.subsumption;

import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.report.components.Subsumption;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class SubsumptantSubPatternsTest {

    @Test
    void testSubpatternSubsumption1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantSubPatterns1.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        if (!noProblems) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
        }

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        int count = 0;
        for (Object object : subsumptionList) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if (((Subsumption) object).getLeft().getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                //                System.out.println( " ** " + ((SubPattern) ((Subsumption) object).getLeft()).getItems() + " - " + ((SubPattern) ((Subsumption) object).getRight()).getItems() );
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        verifier.dispose();
    }

    /**
     * Empty pattern
     *
     * @throws Exception
     */
    @Test
    void testSubpatternSubsumption2() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantSubPatterns2.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        int count = 0;
        for (Object object : subsumptionList) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if (((Subsumption) object).getLeft().getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        verifier.dispose();
    }

    /**
     * Different sources
     *
     * @throws Exception
     */
    @Test
    void testSubpatternSubsumption3() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantSubPatterns3.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        int count = 0;
        for (Object object : subsumptionList) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if (((Subsumption) object).getLeft().getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        verifier.dispose();
    }

    /**
     * Patterns that use from
     *
     * @throws Exception
     */
    @Test
    @Disabled
    void testSubpatternSubsumption4() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantSubPatterns4.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        int count = 0;
        for (Object object : subsumptionList) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if (((Subsumption) object).getLeft().getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        verifier.dispose();
    }

    /**
     * Different sources
     *
     * @throws Exception
     */
    @Test
    void testSubpatternSubsumption5() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantSubPatterns5.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        int count = 0;
        for (Object object : subsumptionList) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if (((Subsumption) object).getLeft().getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                count++;
            }
        }
        assertThat(count).isEqualTo(8);

        verifier.dispose();
    }
}
