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
package org.drools.drl.parser.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XpathAnalysis implements Iterable<XpathAnalysis.XpathPart> {

    private final List<XpathPart> parts;
    private final String error;

    public XpathAnalysis( List<XpathPart> parts, String error ) {
        this.parts = parts;
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    @Override
    public Iterator<XpathPart> iterator() {
        return parts.iterator();
    }

    public List<XpathPart> getParts() {
        return parts;
    }

    public XpathPart getPart( int i) {
        return parts.get( i );
    }

    public boolean isSinglePart() {
        return parts.size() == 1;
    }

    public static class XpathPart {
        private final String field;
        private final boolean iterate;
        private final boolean lazy;
        private final List<String> constraints;
        private final String inlineCast;
        private final int index;
        private final int start;

        public XpathPart(String field, boolean iterate, boolean lazy, List<String> constraints, String inlineCast, int index, int start) {
            this.field = field;
            this.iterate = iterate;
            this.lazy = lazy;
            this.constraints = constraints;
            this.inlineCast = inlineCast;
            this.index = index;
            this.start = start;
        }

        public String getField() {
            return field;
        }

        public boolean isIterate() {
            return iterate;
        }

        public boolean isLazy() {
            return lazy;
        }

        public List<String> getConstraints() {
            return constraints;
        }

        public String getInlineCast() {
            return inlineCast;
        }

        public int getIndex() {
            return index;
        }

        public int getStart() {
            return start;
        }

        public void addInlineCastConstraint( Class<?> clazz ) {
            constraints.add(0, "this instanceof " + clazz.getCanonicalName());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder( field );
            if (inlineCast != null) {
                sb.append( "#" ).append( inlineCast );
            }
            if (index >= 0) {
                sb.append( "[" ).append( index ).append( "]" );
            }
            if (!constraints.isEmpty()) {
                sb.append( "[ " );
                sb.append( constraints.get(0) );
                for (int i = 1; i < constraints.size(); i++) {
                    sb.append( ", " ).append( constraints.get( i ) );
                }
                sb.append( " ]" );
            }
            return sb.toString();
        }
    }

    public static XpathAnalysis analyze(String xpath) {
        List<XpathPart> parts = new ArrayList<>();
        boolean lazyPath = false;
        int i = 0;
        if (xpath.length() >= 1 && xpath.charAt(0) == '/') {
            i = 1;
        } else if (xpath.length() >= 2 && xpath.charAt(0) == '?' && xpath.charAt(1) == '/') {
            lazyPath = true;
            i = 2;
        } else {
            return new XpathAnalysis(parts, "An oopath expression has to start with '/' or '?/'");
        }

        List<String> constraints = new ArrayList<>();

        String inlineCast = null;
        int index = -1;
        int lastStart = i;
        int nestedParam = 0;
        int nestedSquare = 0;

        boolean iterate = true;
        boolean isQuoted = false;
        boolean isDoubleQuoted = false;
        boolean isInlineCast = false;

        String field = null;
        String error = null;
        int partStart = 0;

        for (; i < xpath.length() && error == null; i++) {
            switch (xpath.charAt(i)) {
                case '/':
                case '.':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 0) {
                        if (field == null) {
                            field = xpath.substring(lastStart, xpath.charAt(i-1) == '?' ? i-1 : i).trim();
                        } else if (isInlineCast) {
                            inlineCast = xpath.substring(lastStart, xpath.charAt(i-1) == '?' ? i-1 : i).trim();
                            isInlineCast = false;
                        }
                        parts.add(new XpathPart(field, iterate, lazyPath, constraints, inlineCast, index, partStart));
                        partStart = i;

                        iterate = xpath.charAt(i) == '/';
                        if (xpath.charAt(i-1) == '?') {
                            if (lazyPath) {
                                error = "It is not possible to have 2 non-reactive parts in the same oopath";
                                break;
                            } else {
                                lazyPath = true;
                            }
                        }
                        constraints = new ArrayList<>();
                        inlineCast = null;
                        index = -1;
                        lastStart = i + 1;
                        field = null;
                    }
                    break;
                case '(':
                    if (!isQuoted) {
                        nestedParam++;
                    }
                    break;
                case ')':
                    if (!isQuoted) {
                        nestedParam--;
                        if (nestedParam < 0) {
                            error = "Unbalanced parenthesis";
                        }
                    }
                    break;
                case '#':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 0) {
                        if (field == null) {
                            field = xpath.substring( lastStart, i ).trim();
                        }
                        lastStart = i+1;
                        isInlineCast = true;
                    }
                    break;
                case '[':
                    if (!isQuoted && nestedParam == 0) {
                        if (nestedSquare == 0) {
                            if (field == null) {
                                field = xpath.substring( lastStart, i ).trim();
                            } else if (isInlineCast) {
                                inlineCast = xpath.substring( lastStart, i ).trim();
                                isInlineCast = false;
                            }
                            lastStart = i+1;
                        }
                        nestedSquare++;
                    }
                    break;
                case ']':
                    if (!isQuoted && nestedParam == 0) {
                        nestedSquare--;
                        if (nestedSquare == 0) {
                            String constraint = xpath.substring( lastStart, i ).trim();
                            if ( Character.isDigit(constraint.charAt( 0 )) ) {
                                try {
                                    index = Integer.parseInt( constraint );
                                } catch (Exception e) {
                                    constraints.add( constraint );
                                }
                            } else {
                                constraints.add( constraint );
                            }
                        } else if (nestedSquare < 0) {
                            error = "Unbalanced square brackets";
                        }
                    }
                    break;
                case ',':
                    if (!isQuoted && nestedParam == 0 && nestedSquare == 1) {
                        String constraint = xpath.substring(lastStart, i).trim();
                        constraints.add(constraint);
                        lastStart = i+1;
                    }
                    break;
                case '"':
                    if (isQuoted) {
                        if (isDoubleQuoted) {
                            isQuoted = false;
                            isDoubleQuoted = false;
                        }
                    } else {
                        isQuoted = true;
                        isDoubleQuoted = true;
                    }
                    break;
                case '\'':
                    if (isQuoted) {
                        if (!isDoubleQuoted) {
                            isQuoted = false;
                        }
                    } else {
                        isQuoted = true;
                    }
                    break;
            }
        }

        if (field == null) {
            field = xpath.substring(lastStart).trim();
        } else if (isInlineCast) {
            inlineCast = xpath.substring(lastStart).trim();
            isInlineCast = false;
        }
        parts.add(new XpathPart(field, iterate, lazyPath, constraints, inlineCast, index, partStart));

        return new XpathAnalysis(parts, error);
    }
}
