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
package org.drools.template.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * /**
 * <p>A simple example of using the ResultSetGenerator.
 * The template used is "Cheese.drt" the same used by SimpleRuleTemplateExample.
 * Rather than use the spreadsheet ExampleCheese.xls, this example reads the data
 * from an HSQL database (which is created in this example).</p>
 */
public class ResultSetGeneratorTest {

    @Test
    public void testResultSet() throws Exception {

        // setup the HSQL database with our rules.
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:drools-templates", "sa", "");

        try {
            update("CREATE TABLE cheese_rules ( id INTEGER IDENTITY, persons_age INTEGER, birth_date DATE, cheese_type VARCHAR(256), log VARCHAR(256) )", conn);

            update("INSERT INTO cheese_rules(persons_age,birth_date,cheese_type,log) VALUES(42, '1950-01-01', 'stilton', 'Old man stilton')", conn);
            update("INSERT INTO cheese_rules(persons_age,birth_date,cheese_type,log) VALUES(10, '2009-01-01', 'cheddar', 'Young man cheddar')", conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize in memory database", e);
        }

        // query the DB for the rule rows, convert them using the template.

        Statement sta = conn.createStatement();
        ResultSet rs = sta.executeQuery("SELECT persons_age, cheese_type, log " +
                                        " FROM cheese_rules");

        final ResultSetGenerator converter = new ResultSetGenerator();
        final String drl = converter.compile(rs, getRulesStream());

        System.out.println(drl);

        sta.close();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession kSession = kbase.newKieSession();

        //now create some test data
        kSession.insert(new Cheese("stilton", 42));
        kSession.insert(new Person("michael", "stilton", 42));
        List<String> list = new ArrayList<String>();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);

    }

     /**
     * simple getter method looks up our template as a Resource
     *
     * @return the template as an InputStream
     */
    private InputStream getRulesStream() {
        return this.getClass().getResourceAsStream("/templates/Cheese.drt");
    }

    /**
     * An HSQL update wrapper from http://hsqldb.org/doc/guide/apb.html
     *
     * @param expression SQL expression
     * @throws SQLException just rethrowing all the errors for the example
     */
    private void update(String expression, Connection conn) throws SQLException {
        Statement st;
        st = conn.createStatement();
        int i = st.executeUpdate(expression);
        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }

}
