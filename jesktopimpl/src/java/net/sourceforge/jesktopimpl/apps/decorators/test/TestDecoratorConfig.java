/* ====================================================================
 * Copyright 2000 - 2004, The Jesktop project committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Portions of this code are copyright Apache Software Foundation, and licensed
 * under the Apache Software License 1.1
 */
package net.sourceforge.jesktopimpl.apps.decorators.test;

import org.jesktop.config.ObjConfiglet;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.ConfigHelper;
import org.w3c.dom.Document;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;


/**
 * Class TestDecoratorConfig allows the settings to be manipulated for TestDecorator
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version %I%, %G%
 */
public class TestDecoratorConfig extends JPanel implements ObjConfiglet {

    private final JCheckBox bigIcons = new JCheckBox("Big Icons");
    private final JCheckBox motifLnF = new JCheckBox("Motif Look and Feel");
    private Config config;
    private final ConfigManager configManager;

    /**
     * Constructor TestDecoratorConfig
     *
     *
     */
    public TestDecoratorConfig(ConfigManager cm) {
        this.configManager = cm;

        this.setLayout(new BorderLayout());
        this.add(bigIcons, BorderLayout.NORTH);
        bigIcons.setEnabled(false);
        bigIcons.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                config.bigIcons = bigIcons.getModel().isSelected();

                configManager.notifyUpdated(TestDecoratorConfig.this);
            }
        });
        this.add(motifLnF, BorderLayout.SOUTH);
        motifLnF.setEnabled(false);
        motifLnF.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                config.motifLnF = motifLnF.getModel().isSelected();

                configManager.notifyUpdated(TestDecoratorConfig.this);
            }
        });
    }

    /**
     * Method getConfig
     *
     *
     * @return
     *
     */
    public Object getConfig() {
        return config;
    }

    /**
     * Method setConfig
     *
     *
     * @param obj
     *
     */
    public void setConfig(Object obj) {

        if (obj != null) {
            config = (Config) obj;
        } else {
            config = new Config();
        }

        bigIcons.setEnabled(true);
        motifLnF.setEnabled(true);
        bigIcons.getModel().setSelected(config.bigIcons);
        motifLnF.getModel().setSelected(config.motifLnF);
    }

    /**
     * Method propertyChange
     *
     *
     * @param event
     *
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if (ConfigHelper.isConfigPropChange(event)) {
            setConfig((Document)event.getNewValue());
        }
    }

}
