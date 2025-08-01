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
package org.drools.tms.beliefsystem.simple;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.BaseNode;
import org.drools.core.common.SuperCacheFixer;
import org.drools.tms.TruthMaintenanceSystemEqualityKey;
import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.common.PropagationContext;
import org.drools.tms.LogicalDependency;
import org.drools.tms.SimpleMode;
import org.drools.tms.agenda.TruthMaintenanceSystemInternalMatch;
import org.drools.tms.beliefsystem.BeliefSystem;

import static org.drools.base.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class SimpleBeliefSystem
        implements
        BeliefSystem<SimpleMode> {
    private InternalWorkingMemoryEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public SimpleBeliefSystem(InternalWorkingMemoryEntryPoint ep,
                              TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    @Override
    public SimpleMode asMode( Object value ) {
        return new SimpleMode();
    }

    public BeliefSet<SimpleMode> insert(LogicalDependency<SimpleMode> node,
                                        BeliefSet<SimpleMode> beliefSet,
                                        PropagationContext context,
                                        ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();

        beliefSet.add( node.getMode() );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        if ( empty && bfh.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED ) {
            ep.insert(bfh,
                      bfh.getObject(),
                      node.getJustifier().getRule(),
                      SuperCacheFixer.asTerminalNode(node.getJustifier().getTuple()),
                      typeConf);
        }
        return beliefSet;
    }

    public BeliefSet<SimpleMode> insert( SimpleMode mode,
                                         RuleImpl rule,
                                         TruthMaintenanceSystemInternalMatch activation,
                                         Object payload,
                                         BeliefSet<SimpleMode> beliefSet,
                                         PropagationContext context,
                                         ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();

        beliefSet.add( mode );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        if ( empty && bfh.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED ) {
            ep.insert( bfh,
                       bfh.getObject(),
                       rule,
                       activation != null ? SuperCacheFixer.asTerminalNode(activation.getTuple()) : null,
                       typeConf );
        }
        return beliefSet;
    }

    public void read(LogicalDependency<SimpleMode> node,
                     BeliefSet<SimpleMode> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        //insert(node, beliefSet, context, typeConf );
        beliefSet.add( node.getMode() );
    }

    public void delete(LogicalDependency<SimpleMode> node,
                       BeliefSet<SimpleMode> beliefSet,
                       PropagationContext context) {
        delete( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context );
    }

    @Override
    public void delete(SimpleMode mode, RuleImpl rule, InternalMatch internalMatch, Object payload, BeliefSet<SimpleMode> beliefSet, PropagationContext context) {

        beliefSet.remove( mode );

        InternalFactHandle bfh = beliefSet.getFactHandle();

        if ( beliefSet.isEmpty() && bfh.getEqualityKey() != null && bfh.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED ) {
            ep.immediateDelete(bfh, bfh.getObject(), getObjectTypeConf(beliefSet), context.getRuleOrigin(),
                               internalMatch != null ? SuperCacheFixer.asTerminalNode(internalMatch.getTuple()) : null);
        } else if ( !beliefSet.isEmpty() && bfh.getObject() == payload && payload != bfh.getObject() ) {
            // prime has changed, to update new object
            // Equality might have changed on the object, so remove (which uses the handle id) and add back in
            WorkingMemoryEntryPoint ep = bfh.getEntryPoint(this.ep.getReteEvaluator());
            ep.getObjectStore().updateHandle(bfh, beliefSet.getFirst().getObject().getObject());
            ep.update( bfh, bfh.getObject(), allSetButTraitBitMask(), Object.class, null );
        }

        if ( beliefSet.isEmpty() && bfh.getEqualityKey() != null ) {
            // if the beliefSet is empty, we must null the logical handle
            EqualityKey key = bfh.getEqualityKey();
            key.setLogicalFactHandle( null );
            ((TruthMaintenanceSystemEqualityKey)key).setBeliefSet( null );

            if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                // if it's stated, there will be other handles, so leave it in the TMS
                tms.remove( key );
            }
        }
    }

    public void stage(PropagationContext context,
                      BeliefSet<SimpleMode> beliefSet) {
        InternalFactHandle bfh = beliefSet.getFactHandle();
        // Remove the FH from the network
        ep.immediateDelete(bfh, bfh.getObject(), getObjectTypeConf(beliefSet), context.getRuleOrigin(), null);

        bfh.getEqualityKey().setStatus( EqualityKey.STATED ); // revert to stated
    }

    public void unstage(PropagationContext context,
                        BeliefSet<SimpleMode> beliefSet) {
        InternalFactHandle bfh = beliefSet.getFactHandle();
        bfh.getEqualityKey().setStatus( EqualityKey.JUSTIFIED ); // revert to justified

        // Add the FH back into the network
        ep.insert(bfh, bfh.getObject(), context.getRuleOrigin(), null, getObjectTypeConf(beliefSet) );
    }

    private ObjectTypeConf getObjectTypeConf(BeliefSet beliefSet) {
        InternalFactHandle fh = beliefSet.getFactHandle();
        ObjectTypeConfigurationRegistry reg;
        ObjectTypeConf typeConf;
        reg = ep.getObjectTypeConfigurationRegistry();
        typeConf = reg.getOrCreateObjectTypeConf( ep.getEntryPoint(), fh.getObject() );
        return typeConf;
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(TruthMaintenanceSystemInternalMatch activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        SimpleMode mode = new SimpleMode();
        SimpleLogicalDependency dep =  new SimpleLogicalDependency( activation, beliefSet, object, mode );
        mode.setObject( dep );
        return dep;
    }

    public InternalWorkingMemoryEntryPoint getEp() {
        return ep;
    }

    public void setEp( NamedEntryPoint ep ) {
        this.ep = ep;
    }

    public TruthMaintenanceSystem getTms() {
        return tms;
    }

    public void setTms( TruthMaintenanceSystem tms ) {
        this.tms = tms;
    }
}
