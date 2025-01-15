/**
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
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.exceptions.EndpointOfRangeNotValidTypeException;
import org.kie.dmn.feel.exceptions.EndpointOfRangeOfDifferentTypeException;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.ASTEventBase;
import org.kie.dmn.feel.util.Msg;

public class ForIterationUtils {

    private ForIterationUtils() {
    }

    public static ForIteration getForIteration(EvaluationContext ctx, String name, Object start, Object end) {
        validateValues(ctx, start, end);
        if (start instanceof BigDecimal bigDecimal) {
            return new ForIteration(name, bigDecimal, (BigDecimal) end);
        }
        if (start instanceof LocalDate localDate) {
            return new ForIteration(name, localDate, (LocalDate) end);
        }
        ctx.notifyEvt(() -> new ASTEventBase(FEELEvent.Severity.ERROR,
                                             Msg.createMessage(Msg.VALUE_X_NOT_A_VALID_ENDPOINT_FOR_RANGE_BECAUSE_NOT_A_NUMBER_NOT_A_DATE, start), null));
        throw new EndpointOfRangeOfDifferentTypeException();
    }

    public static ForIteration getForIterationRangeValues(Range result, String name, EvaluationContext ctx) {
        validateValues(ctx, result.getLowEndPoint(), result.getHighEndPoint());
        if (result.getLowEndPoint() instanceof BigDecimal && result.getHighEndPoint() instanceof BigDecimal) {
            return getForIterationBigDecimalRange(result,name,ctx);
        }
        if (result.getLowEndPoint() instanceof LocalDate && result.getHighEndPoint() instanceof LocalDate) {
            return getForIterationLocalDateRange(result, name, ctx);
        }
        return null;
    }

    static void validateValues(EvaluationContext ctx, Object start, Object end) {
        if (start.getClass() != end.getClass()) {
            ctx.notifyEvt(() -> new ASTEventBase(FEELEvent.Severity.ERROR,
                    Msg.createMessage(Msg.X_TYPE_INCOMPATIBLE_WITH_Y_TYPE, start, end), null));
            throw new EndpointOfRangeOfDifferentTypeException();
        }
        valueMustBeValid(ctx, start);
        valueMustBeValid(ctx, end);
    }

    static void valueMustBeValid(EvaluationContext ctx, Object value) {
        if (!(value instanceof BigDecimal) && !(value instanceof LocalDate)) {
            ctx.notifyEvt(() -> new ASTEventBase(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.VALUE_X_NOT_A_VALID_ENDPOINT_FOR_RANGE_BECAUSE_NOT_A_NUMBER_NOT_A_DATE, value), null));
            throw new EndpointOfRangeNotValidTypeException();
        }
    }

    static ForIteration getForIterationBigDecimalRange(Range result, String name, EvaluationContext ctx) {
        BigDecimal start = (BigDecimal) result.getLowEndPoint();
        BigDecimal end = (BigDecimal) result.getHighEndPoint();
        if (result.getLowBoundary() == Range.RangeBoundary.OPEN) {
            start = start.add(BigDecimal.ONE);
        }
        if (result.getHighBoundary() == Range.RangeBoundary.OPEN) {
            end = end.subtract(BigDecimal.ONE);
        }
        return getForIteration(ctx, name, start, end);
    }

    static ForIteration getForIterationLocalDateRange(Range result, String name, EvaluationContext ctx) {
        LocalDate start = (LocalDate) result.getLowEndPoint();
        LocalDate end = (LocalDate) result.getHighEndPoint();
        if (result.getLowBoundary() == Range.RangeBoundary.OPEN) {
            start = start.plusDays(1);
        }
        if (result.getHighBoundary() == Range.RangeBoundary.OPEN) {
            end = end.minusDays(1);
        }
        return getForIteration(ctx, name, start, end);
    }
}


