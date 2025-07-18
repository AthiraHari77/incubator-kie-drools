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
package org.drools.io;

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.drools.util.IoUtils;
import org.kie.api.io.Resource;

public class ByteArrayResource extends BaseResource
    implements
    InternalResource,
    Externalizable {

    private String encoding;

    public ByteArrayResource() { }

    public ByteArrayResource(byte[] bytes) {
        if ( bytes == null ) {
            throw new IllegalArgumentException( "Provided byte array can not be null" );
        }
        this.bytes = bytes;
    }

    public ByteArrayResource(byte[] bytes, String encoding) {
        this(bytes);
        this.encoding = encoding;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        encoding = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(this.encoding);
    }

    public String getEncoding() {
        return this.encoding;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream( this.bytes );
    }
    
    public Reader getReader() throws IOException {
        if (this.encoding != null) {
            return new InputStreamReader( getInputStream(), encoding );
        } else {
            return new InputStreamReader( getInputStream(), IoUtils.UTF8_CHARSET );
        }
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public boolean hasURL() {
        return false;
    }

    public URL getURL() throws IOException {
        throw new FileNotFoundException( "byte[] cannot be resolved to URL" );
    }
    
    public boolean isDirectory() {
        return false;
    }

    public Collection<Resource> listResources() {
        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }

    public boolean equals(Object object) {
        return (object == this || (object instanceof ByteArrayResource
                && Arrays.equals( ((ByteArrayResource) object).bytes, this.bytes )));
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
    
    public String toString() {
        return "ByteArrayResource[bytes=" + firstNBytesToString(10) + ", encoding=" + this.encoding + "]";
    }

    private String firstNBytesToString(int nrOfBytes) {
        // this.bytes cannot be empty or null (enforced by constructors)
        String str = Arrays.toString(Arrays.copyOf(this.bytes, Math.min(this.bytes.length, nrOfBytes)));
        // append the dots ("...") only if the resource has more bytes than requested
        return this.bytes.length > nrOfBytes ? str.substring(0, str.length() - 1) + ", ...]" : str;
    }

}
