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
package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.EntryPointDeclarationDescr;

/**
 * An interface for the entry point declaration descriptor builder
 */
public interface EntryPointDeclarationDescrBuilder
    extends
    AnnotatedDescrBuilder<EntryPointDeclarationDescrBuilder>, 
    DescrBuilder<PackageDescrBuilder, EntryPointDeclarationDescr> {

    /**
     * Declares the entry point id 
     * 
     * @param name the name of the entry point to be declared
     * 
     * @return itself
     */
    public EntryPointDeclarationDescrBuilder entryPointId( String name );

}
