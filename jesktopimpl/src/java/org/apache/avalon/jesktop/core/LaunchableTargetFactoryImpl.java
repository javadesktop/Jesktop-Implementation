/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;

import org.apache.avalon.jesktop.services.LaunchableTargetFactory;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.launchable.NormalLaunchableTarget;

import javax.swing.JComponent;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Collections;
import java.util.SortedSet;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class LaunchableTargetFactoryImpl extends AbstractLogEnabled
        implements Block, LaunchableTargetFactory, Contextualizable,
        Composable, Configurable, Initializable  {

    private ObjectRepository mObjectRepository;
    private Store mStore;
    private Configuration mRepository;
    private HashMap classloaders;
    private LaunchableTargetHolder mLaunchableTargetHolder;

    public LaunchableTarget[] getAllLaunchableTargets() {
        Object[] objs = subset(LaunchableTargetImpl.class);
        LaunchableTarget[] lts = new LaunchableTarget[objs.length];
        System.arraycopy(objs, 0, lts, 0, objs.length);
        return lts;
    }

    public void contextualize(Context context)
            throws ContextException {
    }

    public void compose(ComponentManager componentManager)
            throws ComponentException {
        mStore = (Store) componentManager.lookup(Store.class.getName());
    }

    public void configure(Configuration configuration)
            throws ConfigurationException {
        mRepository = configuration.getChild("repository");

    }

    public void initialize()
            throws Exception {
        mObjectRepository = (ObjectRepository) mStore.select(mRepository);

        boolean alreadySetup = mObjectRepository.containsKey(KEY);
        boolean appsDone = false;

        if (alreadySetup) {
            try {
                mLaunchableTargetHolder = (LaunchableTargetHolder) mObjectRepository.get(KEY, LaunchableTargetHolder.class.getClassLoader());

                appsDone = true;
            } catch (Exception e) {

                //if (e instanceof InvalidClassException) {
                if (e instanceof RuntimeException) {

                    // different serializtion ID
                    e.printStackTrace();
                    System.err.println("********************************");
                    System.err.println("**  Problem, the last time    **");
                    System.err.println("**  you ran Avalon/Jesktop    **");
                    System.err.println("**  the installed apps used   **");
                    System.err.println("**  a now incompatible class  **");
                    System.err.println("**  Default apps installed    **");
                    System.err.println("**  only, sorry!              **");
                    System.err.println("********************************");
                } else {
                    e.printStackTrace();
                    System.err.println("********************************");
                    e.printStackTrace();
                    System.err.println("*** Default Apps installed   ***");
                    System.err.println("********************************");
                }
            }
        }

        if (!appsDone) {
            mLaunchableTargetHolder = new LaunchableTargetHolder();
            setBuiltInApps();
            save();
        }

    }

    public DecoratorLaunchableTarget makeDecoratorLaunchableTarget(final String targetName, final String className,
                                                             final String displayName,
                                                             final String configPath) {

        LaunchableTarget lt = new DecoratorLaunchableTargetImpl(targetName, className,
                                                                displayName, configPath);

        mLaunchableTargetHolder.targets.put(targetName, lt);
        save();
        return (DecoratorLaunchableTarget) lt;
    }

    public DecoratorLaunchableTarget makeDecoratorLaunchableTarget(
            String targetName, String className,
            String appFilePrefix,
            Vector jarFileNames,
            String displayName,
            String configPath) {
        LaunchableTarget lt = new DecoratorLaunchableTargetImpl(targetName, className,
                                                                appFilePrefix, jarFileNames,
                                                                displayName, configPath);

        mLaunchableTargetHolder.targets.put(targetName, lt);
        save();
        return (DecoratorLaunchableTarget) lt;
    }

    public LaunchableTarget getLaunchableTarget(String targetName) {
        return (LaunchableTarget) mLaunchableTargetHolder.targets.get(targetName);
    }

    public boolean isRegistered(String targetName) {
        return (mLaunchableTargetHolder.targets.get(targetName) != null);
    }

    public ConfigletLaunchableTarget[] getConfigletLaunchableTargets() {
        Object[] objs = subset(ConfigletLaunchableTargetImpl.class);
        ConfigletLaunchableTarget[] clts = new ConfigletLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, clts, 0, objs.length);

        return clts;
    }

    public DecoratorLaunchableTarget[] getDecoratorLaunchableTargets() {
        Object[] objs = subset(DecoratorLaunchableTargetImpl.class);
        DecoratorLaunchableTarget[] dlts = new DecoratorLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, dlts, 0, objs.length);

        return dlts;
    }

    public NormalLaunchableTarget[] getNormalLaunchableTargets() {
        LaunchableTarget[] objs = subset(NormalLaunchableTargetImpl.class);
        NormalLaunchableTarget[] nlts = new NormalLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, nlts, 0, objs.length);

        //for (int f = 0 ; f < objs.length ; f++ ) {
        //    nlts[f] = (NormalLaunchableTarget) objs[f];
        //}
        return nlts;
    }

    public void removeLaunchableTarget(String targetName) {
    }

    public void confirmLaunchableTarget(LaunchableTarget launchableTarget) {
        mLaunchableTargetHolder.targets.put(launchableTarget.getTargetName(), launchableTarget);
        save();
    }

    public ClassLoader getClassLoader(LaunchableTarget launchableTarget) {

        LaunchableTargetImpl lti = (LaunchableTargetImpl) launchableTarget;

        return getClassLoader(lti.getAppFilePrefix(), lti.getAppJarNames());
    }

    // get or make it.
    protected synchronized ClassLoader getClassLoader(final String appFilePrefix, final Vector jarNames) {

        if (appFilePrefix == null) {
            return null;
        }

        if (classloaders == null) {
            classloaders = new HashMap();
        }

        ClassLoader cl = (ClassLoader) classloaders.get(appFilePrefix);

        if (cl == null) {
            try {
                URL[] urls = new URL[jarNames.size()];
                int x = 0;

                for (Enumeration e = jarNames.elements(); e.hasMoreElements(); ) {
                    String jarName = (String) e.nextElement();

                    urls[x++] = new File(jarName).toURL();
                }

                cl = new JesktopURLClassLoader(urls);
            } catch (MalformedURLException mfue) {
                mfue.printStackTrace();
            }

            classloaders.put(appFilePrefix, cl);
        }

        return cl;
    }



    public String getNewAppSuffix() {
        String tmp = "0000" + mLaunchableTargetHolder.appSuffix++;

        return tmp.substring(tmp.length() - 5, tmp.length());
    }

    public LaunchableTarget getLaunchableTarget(JComponent launched) {
        ClassLoader cl = launched.getClass().getClassLoader();
        String className = launched.getClass().getName();
        LaunchableTarget[] lts = getAllLaunchableTargets();

        //System.out.println("launched - " + launched.getClass().getName());


        for (int f = 0; f < lts.length; f++) {
            //System.out.println(" lts" + f + " - " + lts[f].getClassName());
            ClassLoader ltsCl = getClassLoader(lts[f]);

            if (ltsCl == null) {
                ltsCl = this.getClass().getClassLoader();
            }

            if ((ltsCl == cl) && lts[f].getClassName().equals(className)) {
                //System.out.println("*Matched");
                return lts[f];
            }
        }
        //System.out.println("*UnMatched");
        return null;    // not a launchable target

    }

    public void makeNormalLaunchableTarget(String targetName,
                                           String className,
                                           String displayName,
                                           boolean singleInstance) {
        LaunchableTarget lt = new NormalLaunchableTargetImpl(targetName, className, displayName,
                                                             singleInstance);
        mLaunchableTargetHolder.targets.put(lt.getTargetName(), lt);
        save();
    }

    public LaunchableTarget makeNormalLaunchableTarget(String targetName,
                                                       String className,
                                                       String appFilePrefix,
                                                       Vector jarFileNames,
                                                       String displayName,
                                                       boolean singleInstance) {
        LaunchableTarget lt = new NormalLaunchableTargetImpl(targetName, className,
                                                             appFilePrefix, jarFileNames,
                                                             displayName, singleInstance);
        mLaunchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    public LaunchableTarget makeConfigletLaunchableTarget(final String targetName, final String className,
                                                             final String displayName,
                                                             final String configPath) {

        LaunchableTarget lt = new ConfigletLaunchableTargetImpl(targetName, className,
                                                                displayName, configPath);

        mLaunchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    public LaunchableTarget makeConfigletLaunchableTarget(String targetName,
                                                          String className,
                                                          String appFilePrefix,
                                                          Vector jarFileNames,
                                                          String displayName,
                                                          String configPath) {
        LaunchableTarget lt = new ConfigletLaunchableTargetImpl(targetName, className,
                                                                appFilePrefix, jarFileNames,
                                                                    displayName, configPath);

        mLaunchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    private void save() {
        mObjectRepository.put(KEY, mLaunchableTargetHolder);
    }

    private LaunchableTarget[] subset(Class cls) {

        Vector vec = new Vector();
        Iterator it = mLaunchableTargetHolder.targets.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            LaunchableTarget lt = (LaunchableTarget) mLaunchableTargetHolder.targets.get(key);

            if (cls.isInstance(lt)) {
                vec.add(lt);
            }
        }

        SortedSet sortSet = Collections.unmodifiableSortedSet(new TreeSet(vec));

        it = sortSet.iterator();

        LaunchableTarget[] lts = new LaunchableTarget[sortSet.size()];
        int x = 0;

        while (it.hasNext()) {
            LaunchableTarget lt = (LaunchableTarget) it.next();

            lts[x++] = lt;
        }

        return lts;
    }

    /**
     * Method setBuiltInApps
     *
     *
     * @param launchableTargetHolder
     *
     */
    public void setBuiltInApps() {

        makeNormalLaunchableTarget(
            "System/SimpleInstaller", "org.apache.avalon.jesktop.builtinapps.installer.SimpleInstaller",
            "Simple Installer Tool", true);
        makeNormalLaunchableTarget(
            "System/ManageInstalled", "org.apache.avalon.jesktop.builtinapps.installer.ManageInstalled",
            "Installed App Management Tool", true);
        makeNormalLaunchableTarget("System/ControlPanel",
                                                          "org.apache.avalon.jesktop.builtinapps.config.ControlPanel",
                                                          "Control Panel", true);
        makeConfigletLaunchableTarget(
            "System/DecoratorChooser", "org.apache.avalon.jesktop.builtinapps.installer.DecoratorChooser",
            "Chooser for Decorators", "decorator/currentDecorator");
        makeNormalLaunchableTarget(
            MimeManagerImpl.ALLOWED_TARGET_NAME, "org.apache.avalon.jesktop.builtinapps.mime.MimeConfigurator",
            "Mimes And Extensions Management", true);
        makeNormalLaunchableTarget(
            SHUTDOWN_APP, "org.apache.avalon.jesktop.builtinapps.sys.ShutdownConfirmer", "Shutdown", true);
    }



}
