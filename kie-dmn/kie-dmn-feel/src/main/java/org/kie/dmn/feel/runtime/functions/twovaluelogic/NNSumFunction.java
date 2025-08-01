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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.NumberEvalHelper;

public class NNSumFunction
        extends BaseFEELFunction {

    public static final NNSumFunction INSTANCE = new NNSumFunction();

    private NNSumFunction() {
        super( "nn sum" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofResult(null);
        }
        BigDecimal sum = BigDecimal.ZERO;
        for ( Object element : list ) {
            if( element == null ) {
                continue;
            } else if ( element instanceof BigDecimal ) {
                sum = sum.add( (BigDecimal) element );
            } else if ( element instanceof Number ) {
                BigDecimal value = NumberEvalHelper.getBigDecimalOrNull(element );
                if (value == null) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not suitable for the sum"));
                } else {
                    sum = sum.add( value );
                }
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not a number"));
            }
        }
        return FEELFnResult.ofResult( sum );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Number single) {
        if ( single == null ) { 
            return FEELFnResult.ofResult( null );
        }
        if( single instanceof BigDecimal ) {
            return FEELFnResult.ofResult((BigDecimal) single );
        } 
        BigDecimal result = NumberEvalHelper.getBigDecimalOrNull( single );
        if ( result != null ) {
            return FEELFnResult.ofResult( result );
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "single element in list not a number"));
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) { 
            return FEELFnResult.ofResult(null);
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
