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
package net.sourceforge.jesktopimpl.builtinapps.installer;

import org.jesktop.config.ObjConfiglet;
import org.jesktop.DesktopKernel;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.ConfigHelper;
import org.jesktop.DesktopKernel;

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
 * @version $Revision: 1.4 $
 */
public class DecoratorChooser extends JPanel implements ObjConfiglet, PropertyChangeListener {

    private DesktopKernel desktopKernel;
    private final JComboBox choiceBox = new JComboBox();
    private DecComboBoxModel mDecComboBoxModel;
    private DecoratorLaunchableTarget[] decorators;
    private ConfigManager configManager;
    private Object mConfig;

    /**
     * Constructor ConfirmInstallation
     *
     *
     */
    public DecoratorChooser(DesktopKernel desktopKernel, ConfigManager configManager) {
        this.desktopKernel = desktopKernel;
        this.configManager = configManager;
        desktopKernel.addPropertyChangeListener(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, this);

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

        decorators = desktopKernel.getDecoratorLaunchableTargets();
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
     * Method propertyChange
     *
     *
     * @param event
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
     * @version $Revision: 1.4 $
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
                configManager.notifyUpdated(DecoratorChooser.this);
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
