/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.builtinapps.config;

import org.jesktop.frimble.JFrimble;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.config.Configlet;
import org.jesktop.config.ObjConfiglet;
import org.jesktop.config.XMLConfiglet;
import org.jesktop.config.ConfigManager;
import org.w3c.dom.Document;
import org.apache.avalon.jesktop.core.LaunchableTargetHolder;
import org.apache.avalon.jesktop.core.ConfigManagerImpl;
import org.apache.avalon.jesktop.services.KernelConfigManager;
import org.apache.avalon.jesktop.services.LaunchableTargetFactory;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SingleSelectionModel;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Vector;
import java.util.Iterator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * Class ConfirmInstallation
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1.1.1 $
 */
public class ControlPanel extends JFrimble implements DesktopKernelAware, PropertyChangeListener {

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

    /**
     * Constructor ConfirmInstallation
     *
     *
     */
    public ControlPanel() {

        super("Control Panel");

        super.getContentPane().setLayout(new BorderLayout());

        footer = new JPanel(new FlowLayout());

        footer.add(tryBtn);
        footer.add(revertBtn);
        footer.add(okBtn);
        this.getContentPane().add(footer, BorderLayout.SOUTH);
    }

    /**
     * Method setLaunchableTargetHolder
     *
     *
     * @param mLaunchableTargetHolder
     *
     */
    public void setLaunchableTargetFactory(LaunchableTargetFactory ltf) {
        this.mLaunchableTargetFactory = ltf;
    }

    /**
     * Method setConfigManager
     *
     *
     * @param mConfigManager
     *
     */
    public void setConfigManager(KernelConfigManager cm) {
        this.mConfigManager = cm;
        this.cpConfigManger = new ControlPanelConfigManager();
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setDesktopKernel
     *
     *
     * @param mDesktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {

        this.desktopKernel = desktopKernel;

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

                    clet = (Configlet) cLetClass.newInstance();

                    clet.setConfigManager(cpConfigManger);

                    if (clet instanceof DesktopKernelAware) {
                        DesktopKernelAware dka = (DesktopKernelAware) clet;

                        dka.setDesktopKernel(desktopKernel);
                    }

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
                } catch (InstantiationException ie) {
                    ie.printStackTrace();
                } catch (IllegalAccessException iae) {
                    iae.printStackTrace();
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
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
     * @version $Revision: 1.1.1.1 $
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
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
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
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
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
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
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
