/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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
    private ConfigManager cm;

    /**
     * Constructor TestDecoratorConfig
     *
     *
     */
    public TestDecoratorConfig() {

        this.setLayout(new BorderLayout());
        this.add(bigIcons, BorderLayout.NORTH);
        bigIcons.setEnabled(false);
        bigIcons.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                config.bigIcons = bigIcons.getModel().isSelected();

                cm.notifyUpdated(TestDecoratorConfig.this);
            }
        });
        this.add(motifLnF, BorderLayout.SOUTH);
        motifLnF.setEnabled(false);
        motifLnF.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                config.motifLnF = motifLnF.getModel().isSelected();

                cm.notifyUpdated(TestDecoratorConfig.this);
            }
        });
    }

    /**
     * Method setConfigManager
     *
     *
     * @param cm
     *
     */
    public void setConfigManager(ConfigManager cm) {
        this.cm = cm;
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
     * @param evt
     *
     */    
    public void propertyChange( PropertyChangeEvent event ) {
        if (ConfigHelper.isConfigPropChange(event)) {
            setConfig((Document)event.getNewValue());
        }
    }    
    
}
