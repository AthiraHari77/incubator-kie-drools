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
package org.kie.util.maven.support;

import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kie.api.builder.ReleaseId;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MinimalPomParser extends DefaultHandler {

    private int           depth;

    private PomModel.InternalModel model;

    private StringBuilder characters;

    private String        pomGroupId;
    private String        pomArtifactId;
    private String        pomVersion;

    private String        currentGroupId;
    private String        currentArtifactId;
    private String        currentVersion;
    private String        currentScope;

    private MinimalPomParser() {
        model = new PomModel.InternalModel();
    }

    public static PomModel parse(String path, InputStream is) {
        MinimalPomParser handler = new MinimalPomParser();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //https://rules.sonarsource.com/java/RSPEC-2755
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setValidating( false );
            factory.setNamespaceAware( false );
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            SAXParser parser = factory.newSAXParser();
            parser.parse( is, handler );
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse File '" + path + "'", e);
        }

        return handler.getPomModel();


    }

    public PomModel getPomModel() {
        return this.model;
    }

    public void startElement(final String uri,
                             final String localName,
                             final String qname,
                             final Attributes attrs) throws SAXException {
        if ( "groupId".equals( qname ) || "artifactId".equals( qname ) || "version".equals( qname ) || "scope".equals( qname ) ) {
            this.characters = new StringBuilder();
        }

        depth++;
    }

    public void endElement(final String uri,
                           final String localName,
                           final String qname) throws SAXException {
        if ( "project".equals( qname ) ) {
            ReleaseId parentReleaseId = model.getParentReleaseId();
            model.setReleaseId(new ReleaseIdImpl(pomGroupId != null ? pomGroupId : parentReleaseId.getGroupId(),
                    pomArtifactId,
                    pomVersion != null ? pomVersion : parentReleaseId.getVersion()));
        } else if ( "parent".equals( qname ) ) {
            if ( currentGroupId != null && currentArtifactId != null && currentVersion != null ) {
                model.setParentReleaseId(new ReleaseIdImpl(currentGroupId, currentArtifactId, currentVersion));
            }
            currentGroupId = null;
            currentArtifactId = null;
            currentVersion = null;
        } else if ( "dependency".equals( qname ) ) {
            if ( currentGroupId != null && currentArtifactId != null && currentVersion != null ) {
                model.addDependency(new ReleaseIdImpl(currentGroupId, currentArtifactId, currentVersion), currentScope != null ? currentScope : "");
            }
            currentGroupId = null;
            currentArtifactId = null;
            currentVersion = null;
            currentScope = null;
        } else {
            String text = ( this.characters != null ) ? this.characters.toString() : null;
            if ( text != null) {
                if ( "groupId".equals( qname ) ) {
                    if ( depth == 2 ) {
                        pomGroupId = text;
                    } else {
                        currentGroupId = text;
                    }
                } else if ( "artifactId".equals( qname ) ) {
                    if ( depth == 2 ) {
                        pomArtifactId = text;
                    } else {
                        currentArtifactId = text;
                    }
                } else if ( "version".equals( qname ) ) {
                    if ( depth == 2 ) {
                        pomVersion = text;
                    } else {
                        currentVersion = text;
                    }
                } else if ( "scope".equals( qname ) ) {
                    currentScope = text;
                }
            }
        }
        this.characters = null;
        depth--;
    }

    public void characters(final char[] chars,
                           final int start,
                           final int len) {
        if ( this.characters != null ) {
            this.characters.append( chars,
                    start,
                    len );
        }
    }
}
