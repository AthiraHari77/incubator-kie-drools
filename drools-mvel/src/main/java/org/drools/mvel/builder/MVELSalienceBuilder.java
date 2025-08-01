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
package org.drools.mvel.builder;

import java.util.Arrays;
import java.util.Map;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MVELSalienceExpression;

import static org.drools.mvel.asm.AsmUtil.copyErrorLocation;

public class MVELSalienceBuilder
    implements
    SalienceBuilder {

    public void build(RuleBuildContext context) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getDeclarationResolver().pushOnBuildStack( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                           context.getRuleDescr(),
                                                                                           context.getRuleDescr().getSalience(),
                                                                                           new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ),
                                                                                                                 context ) );
            context.setTypesafe( analysis.isTypesafe() );
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = decls.get( id );
            }
            Arrays.sort( previousDeclarations, SortDeclarations.instance  ); 
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( context.getRuleDescr().getSalience(),
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false,
                                                                       MVELCompilationUnit.Scope.EXPRESSION );

            MVELSalienceExpression expr = new MVELSalienceExpression( unit,
                                                                      dialect.getId() );
            context.getRule().setSalience( expr );
            
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );
            
            expr.compile( data, context.getRule() );
        } catch ( final Exception e ) {
            copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'salience' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
