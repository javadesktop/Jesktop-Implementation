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
package net.sourceforge.jesktopimpl.builtinapps.config;

import org.jesktop.services.KernelConfigManager;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.DesktopKernel;
import org.jesktop.services.KernelConfigManager;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.Configlet;
import org.jesktop.config.ObjConfiglet;
import org.jesktop.config.XMLConfiglet;
import org.jesktop.frimble.JFrimble;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.w3c.dom.Document;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;


/**
 * Class ConfirmInstallation
 *
 *
 * @author  Dec 2000.
 * @version $Revision: 1.6 $
 */
public class ControlPanel extends JFrimble implements PropertyChangeListener {

    private JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
    private DesktopKernel desktopKernel;
    private final JPanel footer;
    private final JButton okBtn = new OKBtn();
    private final JButton tryBtn = new TryBtn();
    private final JButton revertBtn = new RevertBtn();
    private ConfigletLaunchableTarget[] mConfiglets;
    private Vector changedConfigs = new Vector();
    private Vector tryableConfigs = new Vector();
    private Vector triedConfigs = new Vector();
    private LaunchableTargetFactory mLaunchableTargetFactory;
    private KernelConfigManager mConfigManager;
    private ControlPanelConfigManager cpConfigManger;
    private Configlet selectedConfiglet;
    private Vector configletWrappers = new Vector();
    private MutablePicoContainer picoContainer;

    /**
     * Constructor ConfirmInstallation
     *
     *
     */
    public ControlPanel(DesktopKernel desktopKernel, LaunchableTargetFactory launchableTargetFactory, KernelConfigManager kernelConfigManager) {

        super("Control Panel");
        this.desktopKernel = desktopKernel;
        mLaunchableTargetFactory = launchableTargetFactory;
        mConfigManager = kernelConfigManager;
        cpConfigManger = new ControlPanelConfigManager();

        picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentInstance(desktopKernel);
        picoContainer.registerComponentInstance(launchableTargetFactory);
        picoContainer.registerComponentInstance(kernelConfigManager);

        super.getContentPane().setLayout(new BorderLayout());

        footer = new JPanel(new FlowLayout());

        footer.add(tryBtn);
        footer.add(revertBtn);
        footer.add(okBtn);
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        jtp.getModel().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                SingleSelectionModel model = (SingleSelectionModel) e.getSource();

                selectedConfiglet = ((ConfigletWrapper) jtp.getComponentAt(model.getSelectedIndex())).getConfiglet();

                updateButtons();
            }
        });

        setConfiglets(desktopKernel.getConfigletLaunchableTargets());

        desktopKernel.addPropertyChangeListener(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, this);

        this.getContentPane().add(jtp, BorderLayout.CENTER);

    }

    public void setConfiglets(ConfigletLaunchableTarget[] configlets) {
        mConfiglets = configlets;

        // remove obsolete configlets
        Iterator it = configletWrappers.iterator();
        while (it.hasNext()) {
            ConfigletWrapper cw = (ConfigletWrapper) it.next();
            boolean present = false;
            for (int f = 0; f < mConfiglets.length; f++) {
              if (cw.getConfigletLaunchableTarget().equals(mConfiglets[f])) {
                  present = true;
                  break;
              }
            }
            if (!present) {
                jtp.remove(cw);
                configletWrappers.remove(cw);
            }
        }



        for (int f = 0; f < mConfiglets.length; f++) {
            it = configletWrappers.iterator();
            boolean present = false;
            while (it.hasNext()) {
                ConfigletWrapper cw = (ConfigletWrapper) it.next();
                if (cw.getConfigletLaunchableTarget().equals(mConfiglets[f])) {
                    present = true;
                    break;
                }
            }
            if (!present) {
               Configlet clet = null;

                try {
                    ClassLoader cLetClassLoader = mLaunchableTargetFactory.getClassLoader(mConfiglets[f]);

                    if (cLetClassLoader == null) {
                        cLetClassLoader = this.getClass().getClassLoader();
                    }

                    Class cLetClass = cLetClassLoader.loadClass(mConfiglets[f].getClassName());

                    clet = (Configlet) new DefaultPicoContainer(picoContainer).registerComponentImplementation(cLetClass).getComponentInstance();

                    if (clet instanceof ObjConfiglet) {
                        ((ObjConfiglet) clet).setConfig(mConfigManager.getObjConfig(mConfiglets[f].getConfigPath(),
                                                                    cLetClassLoader));
                    } else if (clet instanceof XMLConfiglet) {
                        ((XMLConfiglet) clet).setConfig(mConfigManager.getXMLConfig(mConfiglets[f].getConfigPath(),
                                                                    cLetClassLoader));
                    }

                    ConfigletWrapper cw = new ConfigletWrapper (mConfiglets[f], clet);
                    jtp.add(mConfiglets[f].getDisplayName(), cw);
                    configletWrappers.add(cw);
                } catch (ClassNotFoundException cnfe) {
                   cnfe.printStackTrace();
                }
            }
        }

        // TODO - need to account for changed (diff classloader) configlets
    }


    private void updateButtons() {

        okBtn.setEnabled(changedConfigs.contains(selectedConfiglet));
        revertBtn.setEnabled(triedConfigs.contains(selectedConfiglet));
        tryBtn.setEnabled(tryableConfigs.contains(selectedConfiglet));
    }

    /**
     * Class OKBtn
     *
     *
     * @author  Dec 2000.
     * @version $Revision: 1.6 $
     */
    private class OKBtn extends JButton {

        private OKBtn() {

            super("OK");

            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    if (selectedConfiglet instanceof ObjConfiglet) {
                        mConfigManager.notifyUpdated((ObjConfiglet) selectedConfiglet);
                    } else {
                        mConfigManager.notifyUpdated((XMLConfiglet) selectedConfiglet);
                    }

                    changedConfigs.remove(selectedConfiglet);

                    if (tryableConfigs.contains(selectedConfiglet)) {
                        tryableConfigs.remove(selectedConfiglet);
                    }

                    updateButtons();
                }
            });
        }
    }

    /**
     * Class TryBtn
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.6 $
     */
    private class TryBtn extends JButton {

        private TryBtn() {

            super("Try");

            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    if (selectedConfiglet instanceof ObjConfiglet) {
                        mConfigManager.notifyUpdatedNoSave((ObjConfiglet) selectedConfiglet);
                    } else {
                        mConfigManager.notifyUpdatedNoSave((XMLConfiglet) selectedConfiglet);
                    }

                    tryableConfigs.remove(selectedConfiglet);

                    if (!triedConfigs.contains(selectedConfiglet)) {
                        triedConfigs.add(selectedConfiglet);
                    }

                    updateButtons();
                }
            });
        }
    }

    /**
     * Class RevertBtn
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.6 $
     */
    private class RevertBtn extends JButton {

        private RevertBtn() {

            super("Revert");

            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    ConfigletLaunchableTarget clt =
                        (ConfigletLaunchableTarget) mLaunchableTargetFactory
                            .getLaunchableTarget((JComponent) selectedConfiglet);

                    if (selectedConfiglet instanceof ObjConfiglet) {
                        Object oldCfg =
                            mConfigManager.getObjConfig(clt.getConfigPath(),
                                            selectedConfiglet.getClass().getClassLoader());

                        ((ObjConfiglet) selectedConfiglet).setConfig(oldCfg);
                        mConfigManager.notifyUpdatedNoSave((ObjConfiglet) selectedConfiglet);
                    } else {
                        Document oldCfg =
                            mConfigManager.getXMLConfig(clt.getConfigPath(),
                                            selectedConfiglet.getClass().getClassLoader());

                        ((XMLConfiglet) selectedConfiglet).setConfig(oldCfg);
                        mConfigManager.notifyUpdatedNoSave((XMLConfiglet) selectedConfiglet);
                    }

                    triedConfigs.remove(selectedConfiglet);
                    updateButtons();
                }
            });
        }
    }

    /**
     * Class ControlPanelConfigManager
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.6 $
     */
    private class ControlPanelConfigManager implements ConfigManager {

        /**
         * Method notifyUpdated
         *
         *
         * @param clet
         *
         */
        public void notifyUpdated(ObjConfiglet clet) {

            if (!changedConfigs.contains(clet)) {
                changedConfigs.add(clet);
            }

            if (!tryableConfigs.contains(clet)) {
                tryableConfigs.add(clet);
            }

            updateButtons();
        }

        /**
         * Method notifyUpdated
         *
         *
         * @param clet
         *
         */
        public void notifyUpdated(XMLConfiglet clet) {

            if (!changedConfigs.contains(clet)) {
                changedConfigs.add(clet);
            }

            if (!tryableConfigs.contains(clet)) {
                tryableConfigs.add(clet);
            }

            updateButtons();
        }
    }

    public void propertyChange( PropertyChangeEvent event ) {
        setConfiglets(desktopKernel.getConfigletLaunchableTargets());
    }

    private class ConfigletWrapper extends JPanel {
        private ConfigletLaunchableTarget configletLaunchableTarget;
        private Configlet cfglet;
        private ConfigletWrapper(ConfigletLaunchableTarget clt, Configlet clet) {
            configletLaunchableTarget = clt;
            cfglet = clet;
            setLayout(new BorderLayout());
            add((JComponent) cfglet, BorderLayout.CENTER);
        }
        private Configlet getConfiglet() {
            return cfglet;
        }
        private ConfigletLaunchableTarget getConfigletLaunchableTarget() {
            return configletLaunchableTarget;
        }
    }

}
