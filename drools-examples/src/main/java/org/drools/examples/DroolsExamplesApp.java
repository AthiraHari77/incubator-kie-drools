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
package org.drools.examples;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drools.examples.decisiontable.PricingRuleDTExample;
import org.drools.examples.fibonacci.FibonacciExample;
import org.drools.examples.golfing.GolfingExample;
import org.drools.examples.helloworld.HelloWorldExample;
import org.drools.examples.honestpolitician.HonestPoliticianExample;
import org.drools.examples.petstore.PetStoreExample;
import org.drools.examples.shopping.ShoppingExample;
import org.drools.examples.state.StateExampleUsingAgendaGroup;
import org.drools.examples.state.StateExampleUsingSalience;
import org.drools.examples.sudoku.SudokuExample;
import org.drools.examples.troubleticket.TroubleTicketExample;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDSL;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDT;
import org.drools.games.adventures.TextAdventure;
import org.drools.games.pong.PongMain;
import org.drools.games.wumpus.WumpusWorldMain;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.examples.sudoku.SudokuExample.createSudokuKieContainer;

public class DroolsExamplesApp extends JFrame {

    public static void main(String[] args) {
        DroolsExamplesApp droolsExamplesApp = new DroolsExamplesApp();
        droolsExamplesApp.pack();
        droolsExamplesApp.setVisible(true);
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final KieServices ks = KieServices.get();

    private final KieContainer kieContainer;

    public DroolsExamplesApp() {
        super("Drools examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logger.info("DroolsExamplesApp started.");
        kieContainer = createKieContainer();
    }

    private KieContainer createKieContainer() {
        KieContainer kc = ks.getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        return kc;
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which GUI example do you want to see?"));
        contentPane.add(new JButton(new AbstractAction("SudokuExample") {
            public void actionPerformed(ActionEvent e) {
                new SudokuExample().init(createSudokuKieContainer(), false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PetStoreExample") {
            public void actionPerformed(ActionEvent e) {
                new PetStoreExample().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TextAdventure") {
            public void actionPerformed(ActionEvent e) {
                new TextAdventure().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Pong") {
            public void actionPerformed(ActionEvent e) {
                new PongMain().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WumpusWorld") {
            public void actionPerformed(ActionEvent e) {
                new WumpusWorldMain().init(kieContainer, false);
            }
        }));

        contentPane.add(new JLabel("Which output example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("HelloWorldExample") {
            public void actionPerformed(ActionEvent e) {
                HelloWorldExample.execute( ks, kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("FibonacciExample") {
            public void actionPerformed(ActionEvent e) {
                FibonacciExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("ShoppingExample") {
            public void actionPerformed(ActionEvent e) {
                ShoppingExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("HonestPoliticianExample") {
            public void actionPerformed(ActionEvent e) {
                HonestPoliticianExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("GolfingExample") {
            public void actionPerformed(ActionEvent e) {
                GolfingExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExample") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDT") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDT.execute(kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDSL") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDSL.execute(kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingSalience") {
            public void actionPerformed(ActionEvent e) {
                StateExampleUsingSalience.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingAgendaGroup") {
            public void actionPerformed(ActionEvent e) {
                StateExampleUsingAgendaGroup.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PricingRuleDTExample") {
            public void actionPerformed(ActionEvent e) {
                PricingRuleDTExample.execute( kieContainer );
            }
        }));
        return contentPane;
    }

}
