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
package org.drools.core.time.impl;

import java.io.Serializable;

import org.drools.core.time.Job;

/**
 * A default implementation for the JobHandle interface
 */
public class DefaultJobHandle extends AbstractJobHandle<DefaultJobHandle> implements Serializable {

    private static final long serialVersionUID = 510l;

    private volatile boolean cancel = false;

    private long              id;

    private TimerJobInstance  timerJobInstance;

    public DefaultJobHandle(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void cancel() {
        if (!this.cancel) {
            timerJobInstance.cancel();
        }
        this.cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    protected Job getJob() {
        return timerJobInstance != null ? timerJobInstance.getJob() : null;
    }

    public void setTimerJobInstance(TimerJobInstance scheduledJob) {
        this.timerJobInstance = scheduledJob;
    }

    public TimerJobInstance getTimerJobInstance() {
        return this.timerJobInstance;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getJob() == null) ? 0 : getJob().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final DefaultJobHandle other = (DefaultJobHandle) obj;
        if ( getJob() == null ) {
            if ( other.getJob() != null ) {
                return false;
            }
        } else if ( !getJob().equals( other.getJob() ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JobHandle #" + id;
    }
}
