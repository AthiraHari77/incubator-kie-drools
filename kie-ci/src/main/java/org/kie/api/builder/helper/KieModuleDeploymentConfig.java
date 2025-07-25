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
package org.kie.api.builder.helper;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.util.maven.support.ReleaseIdImpl;

public class KieModuleDeploymentConfig {

    // Getter/setter's only made when the code actually needs them
    
    private String groupId = null;
    private String artifactId = null;
    private String version = null;
    private ReleaseId releaseId = null;
    
    private String kbaseName = null;
    private String ksessionName = null;
    
    List<String> resourceFilePaths = new ArrayList<>();
    List<Class<?>> classes = new ArrayList<>();
    List<String> dependencies = new ArrayList<>();
    
    private KieModuleModel kproj = null;
    String pomText;

    public KieModuleDeploymentConfig() { 
        KieServices ks = new KieServicesImpl() {
            public KieRepository getRepository() {
                // override repository to not store the artifact on deploy to trigger load from maven repo
                return new KieRepositoryImpl();
            }
        };
        kieServicesLocal.set(ks);
    }
    
    private final ThreadLocal<KieServices> kieServicesLocal = new ThreadLocal<>();
    
    KieServices getKieServicesInstance() { 
        KieServices ks = kieServicesLocal.get();
        if( ks == null ) { 
            throw new IllegalStateException(KieModuleDeploymentHelper.class.getSimpleName() + " instances are not thread-safe!");
        }
        return ks;        
    }
    
    
    /**
     * Getter/Setter's
     */
    
    void setGroupId(String groupId) { 
        this.groupId = groupId;
    }
    
    void setArtifactId(String artifactId) { 
        this.artifactId = artifactId;
    }
    
    void setVersion(String version) { 
        this.version = version;
    }
    
    ReleaseId getReleaseId() {
        if (releaseId == null) {
            releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        }
        return releaseId;
    }

    void setKbaseName(String kbaseName) {
        this.kbaseName = kbaseName;
    }
    
    String getKbaseName() {
        if( kbaseName == null ) { 
            this.kbaseName = "defaultKieBase";
        }
        return kbaseName;
    }
    
    void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }
    
    String getKsessionName() {
        if( ksessionName == null ) { 
            this.ksessionName = "defaultKieSession";
        }
        return ksessionName;
    }
    
    
    /**
     * Other methods
     */
    
    void checkComplete() {
        if ((groupId != null && artifactId != null && version != null) || releaseId != null) {
            return;
        } else if (releaseId == null) {
            if (groupId == null) {
                throw new IllegalStateException("No groupId has been set yet.");
            } else if (artifactId == null) {
                throw new IllegalStateException("No artifactId has been set yet.");
            } else if (version == null) {
                throw new IllegalStateException("No version has been set yet.");
            } else if (groupId.equals(artifactId) && artifactId.equals(version) && version == null) {
                throw new IllegalStateException("None of groupId, artifactId, version or releaseId have been set.");
            }
        }
    }

    KieModuleModel getKieProject() {
        if (kproj != null) {
            return kproj;
        }
        kproj = getKieServicesInstance().newKieModuleModel();

        KieBaseModel kieBaseModel = kproj.newKieBaseModel(getKbaseName()).setDefault(true);
        kieBaseModel.newKieSessionModel(getKsessionName()).setDefault(true);

        return kproj;
    }
}
