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
package org.drools.mvel;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ValueType;
import org.drools.base.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.reteoo.builder.BuildUtils;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.GroupElement.Type;
import org.drools.base.rule.Pattern;
import org.drools.core.test.model.StockTick;
import org.drools.base.time.Interval;
import org.drools.core.time.TemporalDependencyMatrix;
import org.drools.mvel.evaluators.AfterEvaluatorDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.base.time.Interval.MAX;
import static org.drools.base.time.Interval.MIN;

public class BuildUtilsTest {
    
    private BuildUtils utils;

    @BeforeEach
    public void setUp() throws Exception {
        utils = new BuildUtils();
    }

    /**
     * Test method for {@link org.drools.core.reteoo.builder.BuildUtils#calculateTemporalDistance(GroupElement)}.
     */
    @Test
    public void testCalculateTemporalDistance() {
        // input is here just for "documentation" purposes
        Interval[][] input = new Interval[][] {
                                                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 4), new Interval(MIN, MAX), new Interval(MIN, MAX) },
                                                { new Interval(-2,2), new Interval(0,0), new Interval(MIN, MAX), new Interval(1,2), new Interval(MIN, MAX) },
                                                { new Interval(-4,3), new Interval(MIN,MAX), new Interval(0, 0), new Interval(2, 3), new Interval(MIN, MAX) },
                                                { new Interval(MIN,MAX), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(1, 10) },
                                                { new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(-10, -1), new Interval(0,0) }
                                        };
        Interval[][] expected = new Interval[][] {
                                                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 2), new Interval(-1, 4), new Interval(0, 14) },
                                                { new Interval(-2,2), new Interval(0,0), new Interval(-2, 0), new Interval(1,2), new Interval(2, 12) },
                                                { new Interval(-2,3), new Interval(0,2), new Interval(0, 0), new Interval(2, 3), new Interval(3, 13) },
                                                { new Interval(-4,1), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(1, 10) },
                                                { new Interval(-14,0), new Interval(-12,-2), new Interval(-13,-3), new Interval(-10, -1), new Interval(0,0) }
                                        };

        AfterEvaluatorDefinition evals = new AfterEvaluatorDefinition();
        ClassObjectType ot = new ClassObjectType(StockTick.class, true);
        
        Pattern a = new Pattern( 0, ot, "$a" );
        Pattern b = new Pattern( 1, ot, "$b" );

        b.addConstraint( new EvaluatorConstraint( new Declaration[] { a.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "-2,2"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern c = new Pattern( 2, ot, "$c" );
        c.addConstraint( new EvaluatorConstraint( new Declaration[] { a.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "-3,4"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern d = new Pattern( 3, ot, "$d" );
        d.addConstraint( new EvaluatorConstraint( new Declaration[] { b.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "1,2"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        d.addConstraint( new EvaluatorConstraint( new Declaration[] { c.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "2,3"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern e = new Pattern( 4, ot, "$e" );
        e.addConstraint(new EvaluatorConstraint(new Declaration[]{d.getDeclaration()},
                evals.getEvaluator(ValueType.OBJECT_TYPE,
                        AfterEvaluatorDefinition.AFTER,
                        "1,10"),
                new SelfReferenceClassFieldReader(StockTick.class)));

        GroupElement not = new GroupElement( Type.NOT );
        not.addChild( e );
        GroupElement and = new GroupElement( Type.AND );
        and.addChild( a );
        and.addChild( b );
        and.addChild( c );
        and.addChild( d );
        and.addChild( not );
        
        TemporalDependencyMatrix matrix = utils.calculateTemporalDistance( and );
        
        assertThat(matrix.getMatrix()).isDeepEqualTo(expected);
        assertThat(matrix.getExpirationOffset(a)).isEqualTo(15);
        assertThat(matrix.getExpirationOffset(d)).isEqualTo(11);
        assertThat(matrix.getExpirationOffset(e)).isEqualTo(1);
        
    }
    
}
