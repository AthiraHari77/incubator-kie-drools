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
package org.drools.traits.compiler.factmodel.traits;

public class PojoFact {

	private int id;

	private boolean flag;

	public PojoFact() {
		super();
	}

	public PojoFact(int id, boolean flag) {
		super();
		this.id = id;
		this.flag = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String toString() {
		return "PojoFact [id=" + id + ", flag=" + flag + "]";
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if ( ! ( o instanceof PojoFact ) ) return false;
        PojoFact pojoFact = (PojoFact) o;
        if ( getId() != pojoFact.getId() ) return false;
        return true;
    }

    public int hashCode() {
        return id;
    }
}