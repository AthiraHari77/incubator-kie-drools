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
package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.NormContinuous;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLNormContinuous;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomNormContinuous;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLNormContinuous;

public class KiePMMLNormContinuousInstanceFactoryTest {

    @Test
    void getKiePMMLNormContinuous() {
        final NormContinuous toConvert = getRandomNormContinuous();
        final KiePMMLNormContinuous retrieved =
                KiePMMLNormContinuousInstanceFactory.getKiePMMLNormContinuous(toConvert);
        commonVerifyKiePMMLNormContinuous(retrieved, toConvert);
    }
}