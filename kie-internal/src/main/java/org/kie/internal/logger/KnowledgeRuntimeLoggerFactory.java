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
package org.kie.internal.logger;

import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;

/**
 * <p>
 * The KnowledgeRuntimeLogger uses the comprehensive event system to create an audit log that can be used
 * to log the execution of the session for later inspection, in tools such as the Eclipse audit viewer.
 * </p>
 *
 * <pre>
 * KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "logdir/mylogfile");
 * ....
 * logger.close();
 * </pre>
 *
 * <p>
 * Don't forget to close the logger when it is no longer needed, so resources can be released.
 * </p>
 */
public class KnowledgeRuntimeLoggerFactory {

    private static KieLoggers knowledgeRuntimeLoggerFactoryService;

    /**
     * Creates a file logger in the current thread. The file is in XML format, suitable for interpretation by Eclipse's Drools Audit View
     * or other tools. Note that while events are written as they happen, the file will not be flushed until it is closed or the underlying
     * file buffer is filled. If you need real time logging then use a Console Logger or a Threaded File Logger.
     *
     * @param session
     * @param fileName - .log is appended to this.
     * @return
     */
    public static KieRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                       String fileName) {
        return getKnowledgeRuntimeLoggerProvider().newFileLogger( session,
                                                                  fileName );
    }

    /**
     * Creates a file logger that executes in a different thread, where information is written on given intervals (in milliseconds).
     * The file is in XML format, suitable for interpretation by Eclipse's Drools Audit View or other tools.
     *
     * @param session
     * @param fileName - .log is appended to this.
     * @param interval - in milliseconds.
     * @return
     */
    public static KieRuntimeLogger newThreadedFileLogger(KieRuntimeEventManager session,
                                                               String fileName,
                                                               int interval) {
        return getKnowledgeRuntimeLoggerProvider().newThreadedFileLogger( session,
                                                                          fileName,
                                                                          interval );
    }

    /**
     * Logs events to command line console. This is not in XML format, so it cannot be parsed
     * by other tools, but is in real time and is more human readable.
     *
     * @param session
     * @return
     */
    public static KieRuntimeLogger newConsoleLogger(KieRuntimeEventManager session) {
        return getKnowledgeRuntimeLoggerProvider().newConsoleLogger( session );
    }

    private static synchronized void setKnowledgeRuntimeLoggerProvider(KieLoggers provider) {
        KnowledgeRuntimeLoggerFactory.knowledgeRuntimeLoggerFactoryService = provider;
    }

    private static synchronized KieLoggers getKnowledgeRuntimeLoggerProvider() {
        if ( knowledgeRuntimeLoggerFactoryService == null ) {
            loadProvider();
        }
        return knowledgeRuntimeLoggerFactoryService;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            Class<KieLoggers> cls = (Class<KieLoggers>) Class.forName( "org.drools.kiesession.audit.KnowledgeRuntimeLoggerProviderImpl" );
            setKnowledgeRuntimeLoggerProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Provider org.drools.runtime.audit.KnowledgeRuntimeLoggerProviderImpl could not be set.", e );
        }
    }

}
