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
package org.drools.testcoverage.common.model;

public class EventA implements Event, Comparable<EventA> {

    private long timeValue;
    private long duration;

    public EventA() {
        //
    }

    @Override
    public long getTimeValue() {
        return timeValue;
    }

    @Override
    public void setTimeValue(final long timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(final long duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(final EventA o) {
        if (timeValue > o.getTimeValue()) return 1;
        if (timeValue < o.getTimeValue()) return -1;
        // time of insertion is same for both events, so does not matter which is first
        return 1;
    }

    @Override
    public String toString() {
        return "EventA{" +
                "timeValue=" + timeValue +
                ", duration=" + duration +
                '}';
    }
}
