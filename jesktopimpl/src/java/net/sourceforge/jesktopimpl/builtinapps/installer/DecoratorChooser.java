/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.installer;

import org.jesktop.api.DesktopKernelAware;
import org.jesktop.config.ObjConfiglet;
import org.jesktop.api.DesktopKernel;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.ConfigHelper;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Class ConfirmInstallation
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public class DecoratorChooser extends JPanel implements DesktopKernelAware, ObjConfiglet, PropertyChangeListener {

    private DesktopKernel mDesktopKernel;
    private final JComboBox choiceBox = new JComboBox();
    private DecComboBoxModel mDecComboBoxModel;
    private DecoratorLaunchableTarget[] decorators;
    private ConfigManager cm;
    private Object mConfig;

    /**
     * Constructor ConfirmInstallation
     *
     *
     */
    public DecoratorChooser() {

        this.setLayout(new BorderLayout());

        JPanel cpnl = new JPanel(new BorderLayout());

        this.add(cpnl, BorderLayout.CENTER);
        cpnl.add(choiceBox, BorderLayout.CENTER);
        cpnl.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    /**
     * Method getConfig
     *
     *
     * @return
     *
     */
    public Object getConfig() {
        return mDecComboBoxModel.getSelectedDecoratorLaunchableTarget().getTargetName();
    }

    /**
     * Method setConfig
     *
     *
     * @param config
     *
     */
    public void setConfig(Object config) {
        setDecorators(null, config);
    }
    
    private void setDecorators(DecoratorLaunchableTarget currentDecorator, Object config) {

        decorators = mDesktopKernel.getDecoratorLaunchableTargets();
        mConfig = config;

        for (int i = 0; i < decorators.length; i++) {
            DecoratorLaunchableTarget dec = decorators[i];
            
            if (dec.getTargetName().equals(config)) {
                currentDecorator = dec;

                break;
            }
        }

        mDecComboBoxModel = new DecComboBoxModel();

        choiceBox.setModel(mDecComboBoxModel);
        mDecComboBoxModel.setSelectedItem(currentDecorator, false);        
    }
    

    /**
     * Method setDesktopKernel
     *
     *
     * @param mDesktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {
        this.mDesktopKernel = desktopKernel;
        mDesktopKernel.addPropertyChangeListener(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, this);
        
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
     * Method propertyChange
     *
     *
     * @param evt
     *
     */    
    public void propertyChange( PropertyChangeEvent event ) {
        if (ConfigHelper.isConfigPropChange(event)) {
            setConfig(event.getNewValue());
        }
        if (event.getPropertyName().equals(DesktopKernel.LAUNCHABLE_TARGET_CHANGE)) {
            setDecorators(mDecComboBoxModel.getSelectedDecoratorLaunchableTarget(), mConfig);
        }
    }    
    
    /**
     * Class DecComboBoxModel
     *
     *
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
     * @version $Revision: 1.1 $
     */
    private class DecComboBoxModel extends DefaultListModel implements ComboBoxModel {

        private DecoratorLaunchableTarget currSelected;
        int x = 0;

        private DecComboBoxModel() {

            for (int f = 0; f < decorators.length; f++) {
                super.addElement(decorators[f]);
            }
        }

        /**
         * Method setSelectedItem
         *
         *
         * @param anItem
         * @param notifyCfgMgr
         *
         */
        public void setSelectedItem(Object anItem, boolean notifyCfgMgr) {

            currSelected = (DecoratorLaunchableTarget) anItem;

            if (notifyCfgMgr) {
                cm.notifyUpdated(DecoratorChooser.this);
            }
        }

        /**
         * Method setSelectedItem
         *
         *
         * @param anItem
         *
         */
        public void setSelectedItem(Object anItem) {
            this.setSelectedItem(anItem, true);
        }

        private DecoratorLaunchableTarget getSelectedDecoratorLaunchableTarget() {
            return currSelected;
        }

        /**
         * Method getSelectedItem
         *
         *
         * @return
         *
         */
        public Object getSelectedItem() {
            return currSelected;
        }
    }
}