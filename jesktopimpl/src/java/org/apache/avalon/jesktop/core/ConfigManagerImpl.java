
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;



import org.jesktop.config.ConfigManager;
import org.jesktop.config.Configlet;
import org.jesktop.config.ObjConfiglet;
import org.jesktop.config.XMLConfiglet;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.api.DesktopKernel;
import org.jesktop.api.Decorator;

import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.cornerstone.services.dom.DocumentBuilderFactory;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.jesktop.services.KernelConfigManager;
import org.apache.avalon.jesktop.services.DesktopKernelService;
import org.apache.avalon.jesktop.services.WindowManager;
import org.apache.avalon.jesktop.services.LaunchableTargetFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.JComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * Class ConfigManagerImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class ConfigManagerImpl extends AbstractLogEnabled
        implements Block, KernelConfigManager, ConfigManager, Contextualizable, Composable, Configurable, Initializable  {

    private final static String CFG = "cfg-";
    private ObjectRepository mObjectRepository;
    private Store mStore;
    private LaunchableTargetFactory mLaunchableTargetFactory;
    private PropertyChangeSupport propChgSupport = new PropertyChangeSupport("DummyBean");
    //private HashMap configListeners = new HashMap();
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;
    private Configuration mRepository;

    public ConfigManagerImpl() {
    }

    public void contextualize(Context context)
            throws ContextException {
    }

    public void configure(Configuration configuration)
            throws ConfigurationException {
        mRepository = configuration.getChild("repository");
    }

    /**
     * Method compose
     *
     *
     * @param componentManager
     *
     * @throws ComponentException
     *
     */
    public void compose(ComponentManager componentManager) throws ComponentException {
        mStore = (Store) componentManager.lookup(Store.class.getName());
        mDocumentBuilderFactory =
            (DocumentBuilderFactory) componentManager.lookup(DocumentBuilderFactory.class.getName());
        mLaunchableTargetFactory = (LaunchableTargetFactory) componentManager.lookup(LaunchableTargetFactory.class.getName());
        try
        {
            mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce)
        {
            throw new ComponentException("ParserConfiguration Exception in compose()",pce);
        }
    }

    public void initialize()
            throws Exception {
        mObjectRepository = (ObjectRepository) mStore.select(mRepository);
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

        if (mObjectRepository.containsKey(CFG + configPath)) {
            return mObjectRepository.get(CFG + configPath, classLoader);
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

        if (mObjectRepository.containsKey(CFG + configPath)) {
            System.out.println("in mObjectRepository");

            return (Document) mObjectRepository.get(CFG + configPath, classLoader);
        } else {
            Document doc = mDocumentBuilder.newDocument();
            Element root = doc.createElement("config");

            doc.appendChild(root);

            return doc;
        }
    }

    public String getStringConfig(final String configPath, final String defaultVal) {

        if (mObjectRepository.containsKey(CFG + configPath)) {
            return (String) mObjectRepository.get(CFG + configPath);
        } else {
            mObjectRepository.put(CFG + configPath, defaultVal);

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
            mObjectRepository.put(CFG + clt.getConfigPath(), clet.getConfig());
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
        mObjectRepository.put(CFG + clt.getConfigPath(), clet.getConfig());
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
