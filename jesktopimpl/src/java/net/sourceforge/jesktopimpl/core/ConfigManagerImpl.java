
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.services.DesktopKernelService;
import org.jesktop.services.KernelConfigManager;
import org.jesktop.services.KernelConfigManager;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.WindowManager;
import org.jesktop.Decorator;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.Configlet;
import org.jesktop.config.ObjConfiglet;
import org.jesktop.config.XMLConfiglet;
import org.jesktop.ObjectRepository;
import org.jesktop.Decorator;
import org.jesktop.WindowManager;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Class ConfigManagerImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.5 $
 */
public class ConfigManagerImpl implements KernelConfigManager, ConfigManager {

    private final static String CFG = "cfg-";
    private LaunchableTargetFactory mLaunchableTargetFactory;
    private ObjectRepository objectRepository;
    private PropertyChangeSupport propChgSupport = new PropertyChangeSupport("DummyBean");
    //private HashMap configListeners = new HashMap();
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;
 
    public ConfigManagerImpl(DocumentBuilderFactory documentBuilderFactory, LaunchableTargetFactory launchableTargetFactory,
                             ObjectRepository objectRepository) throws ParserConfigurationException {
 
        mDocumentBuilderFactory = documentBuilderFactory;
        mLaunchableTargetFactory = launchableTargetFactory;
        this.objectRepository = objectRepository;
        mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder(); 
    }

    /**
     * Method getObjConfig
     *
     *
     * @param configPath
     * @param classLoader
     *
     * @return
     *
     */
    public Object getObjConfig(final String configPath, final ClassLoader classLoader) {

        if (objectRepository.containsKey(CFG + configPath)) {
            return objectRepository.get(CFG + configPath, classLoader);
        } else {
            return null;
        }
    }

    /**
     * Method getXMLConfig
     *
     *
     * @param configPath
     * @param classLoader
     *
     * @return
     *
     */
    public Document getXMLConfig(final String configPath, final ClassLoader classLoader) {

        if (objectRepository.containsKey(CFG + configPath)) {
            System.out.println("in mObjectRepository");

            return (Document) objectRepository.get(CFG + configPath, classLoader);
        } else {
            Document doc = mDocumentBuilder.newDocument();
            Element root = doc.createElement("config");

            doc.appendChild(root);

            return doc;
        }
    }

    public String getStringConfig(final String configPath, final String defaultVal) {

        if (objectRepository.containsKey(CFG + configPath)) {
            return (String) objectRepository.get(CFG + configPath);
        } else {
            objectRepository.put(CFG + configPath, defaultVal);

            return defaultVal;
        }
    }

    public void registerConfigInterest(final Configlet clet, final String configPath) {
        propChgSupport.addPropertyChangeListener(ConfigManager.PROPCHG_PREFIX + configPath, clet);
    }

    public void unRegisterConfigInterest(final Configlet clet) {
        propChgSupport.removePropertyChangeListener(clet);
    }

    public void registerConfigInterest(final DesktopKernelService dks, final String configPath) {

        propChgSupport.addPropertyChangeListener(ConfigManager.PROPCHG_PREFIX + configPath, dks);

    }

    public void unRegisterConfigInterest(final DesktopKernelService dks) {
        propChgSupport.removePropertyChangeListener(dks);
    }

    public void registerConfigInterest(final WindowManager wm, final String configPath) {

        propChgSupport.addPropertyChangeListener(ConfigManager.PROPCHG_PREFIX + configPath, wm);
    }

    public void unRegisterConfigInterest(final WindowManager wm) {
        propChgSupport.removePropertyChangeListener(wm);
    }

    public void registerConfigInterest(final Decorator dec, final String configPath) {
        propChgSupport.addPropertyChangeListener(ConfigManager.PROPCHG_PREFIX + configPath, (PropertyChangeListener) dec);
    }

    public void unRegisterConfigInterest(final Decorator dec) {
        propChgSupport.removePropertyChangeListener((PropertyChangeListener) dec);
    }

    public void notifyInterested(final String configPath, final Configlet sendingConfiglet,
                                  final Object config) {
        propChgSupport.firePropertyChange(ConfigManager.PROPCHG_PREFIX + configPath,null,config);
    }

    public void notifyObjConfig(final String configPath, ClassLoader classLoader) {

        Object obj = getObjConfig(configPath, classLoader);
        notifyInterested(configPath,null,obj);

    }

    public void notifyXMLConfig(final String configPath, ClassLoader classLoader) {

        Document doc = this.getXMLConfig(configPath, classLoader);
        notifyInterested(configPath,null,doc);

    }


    /**
     * Method notifyUpdated
     *
     *
     * @param clet
     *
     */
    public void notifyUpdated(final ObjConfiglet clet) {

        ConfigletLaunchableTarget clt =
            (ConfigletLaunchableTarget) mLaunchableTargetFactory
                .getLaunchableTarget((JComponent) clet);
        Object oldCfg = getObjConfig(clt.getConfigPath(), clet.getClass().getClassLoader());
        Object newCfg = clet.getConfig();

        if ((oldCfg == null) ||!oldCfg.equals(newCfg)) {
            objectRepository.put(CFG + clt.getConfigPath(), clet.getConfig());
            notifyInterested(clt.getConfigPath(), clet, clet.getConfig());
        }
    }

    /**
     * Method notifyUpdated
     *
     *
     * @param clet
     *
     */
    public void notifyUpdated(final XMLConfiglet clet) {

        ConfigletLaunchableTarget clt =
            (ConfigletLaunchableTarget) mLaunchableTargetFactory
                .getLaunchableTarget((JComponent) clet);
        Object oldCfg = getObjConfig(clt.getConfigPath(), clet.getClass().getClassLoader());
        Object newCfg = clet.getConfig();

        //if (oldCfg == null || !oldCfg.equals(newCfg)) {
        objectRepository.put(CFG + clt.getConfigPath(), clet.getConfig());
        notifyInterested(clt.getConfigPath(), clet, clet.getConfig());

        //}
    }

    /**
     * Method notifyUpdatedNoSave
     *
     *
     * @param clet
     *
     */
    public void notifyUpdatedNoSave(final ObjConfiglet clet) {

        ConfigletLaunchableTarget clt =
            (ConfigletLaunchableTarget) mLaunchableTargetFactory
                .getLaunchableTarget((JComponent) clet);
        Object oldCfg = getObjConfig(clt.getConfigPath(), clet.getClass().getClassLoader());
        Object newCfg = clet.getConfig();

        if ((oldCfg == null) ||!oldCfg.equals(newCfg)) {
            notifyInterested(clt.getConfigPath(), clet, clet.getConfig());
        }
    }

    /**
     * Method notifyUpdatedNoSave
     *
     *
     * @param clet
     *
     */
    public void notifyUpdatedNoSave(final XMLConfiglet clet) {

        ConfigletLaunchableTarget clt =
            (ConfigletLaunchableTarget) mLaunchableTargetFactory
                .getLaunchableTarget((JComponent) clet);
        Object oldCfg = getObjConfig(clt.getConfigPath(), clet.getClass().getClassLoader());
        Object newCfg = clet.getConfig();

        //if (oldCfg == null || !oldCfg.equals(newCfg)) {
        notifyInterested(clt.getConfigPath(), clet, clet.getConfig());

        //}
    }

}
