/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;

import org.jesktop.api.AppLauncher;
import org.jesktop.api.ImageRepository;
import org.jesktop.api.Decorator;
import org.jesktop.api.JesktopLaunchException;
import org.jesktop.api.JesktopPackagingException;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.api.AlreadyLaunchedException;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.LaunchedTarget;
import org.jesktop.api.DesktopKernel;
import org.jesktop.frimble.FrimbleCallback;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.JFrimble;
import org.jesktop.frimble.FrimbleAware;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.jesktop.builtinapps.config.ControlPanel;
import org.apache.avalon.jesktop.services.DesktopKernelService;
import org.apache.avalon.jesktop.services.KernelConfigManager;
import org.apache.avalon.jesktop.services.WindowManager;
import org.apache.avalon.jesktop.services.LaunchableTargetFactory;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;


/**
 * Class AppLauncherImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class AppLauncherImpl extends AppBase implements AppLauncher, FrimbleCallback {

    private static int TEMPAPPSUFFIX = 1;
    private org.apache.avalon.jesktop.services.WindowManager mWindowManager;
    private LaunchableTargetFactory mLaunchableTargetFactory;
    private DesktopKernelService mDesktopKernelService;
    private Vector mLaunchedTargets;
    private ImageRepository mImageRepository;
    private Decorator mDecorator;
    private KernelConfigManager mConfigManager;

    protected AppLauncherImpl(final WindowManager windowManager,
                              final LaunchableTargetFactory launchableTargetFactory,
                              final DesktopKernelService desktopKernelService,
                              final KernelConfigManager configManager, final Vector launchedTargets,
                              final ImageRepository imageRepository, final Decorator decorator,
                              final File baseDir) {
        super(baseDir);
        mLaunchableTargetFactory = launchableTargetFactory;
        mWindowManager = windowManager;
        mDesktopKernelService = desktopKernelService;
        mConfigManager = configManager;
        mLaunchedTargets = launchedTargets;
        mImageRepository = imageRepository;
        mDecorator = decorator;
    }

    protected String getNewTemporaryAppSuffix() {

        String tmp = "0000" + TEMPAPPSUFFIX++;

        return tmp.substring(tmp.length() - 5, tmp.length());
    }

    /**
     * Method launchAppWithoutInstallation
     *
     *
     * @param jarURL
     *
     * @return
     *
     * @throws JesktopLaunchException
     * @throws JesktopPackagingException
     *
     */
    public Object launchAppWithoutInstallation(final URL jarURL)
            throws JesktopPackagingException, JesktopLaunchException {
        System.out.println( "\n\nlaunchAppWithoutInstallation " + jarURL);    
        try {
            String appFilePrefix = "JesktopUninstalledApp" + getNewTemporaryAppSuffix();
            JarSuffixHolder jarSuffix = new JarSuffixHolder();
            String jarName = internJar(true, jarURL.openStream(), appFilePrefix, jarSuffix);
            JesktopURLClassLoader jcl = new JesktopURLClassLoader(new URL[]{
                new File(jarName).toURL() });
            Configuration appsXml = super.getApplicationsDotXML(jarURL.toExternalForm(), jcl);
            String defaultApp = null;

            try {
                Configuration da = appsXml.getChild("default-application");

                defaultApp = da.getAttribute("target-name");
            } catch (ConfigurationException ce) {
                throw new JesktopPackagingException("\"default-application\" missing from JESKTOP-INF//applications.xml");
            }

            String displayName = "";
            String targetName = "";
            String className = "";
            Configuration[] apps = appsXml.getChildren("application");

            for (int q = 0; q < apps.length; q++) {
                if (apps[q].getChild("launchable-target-name").getValue().equals(defaultApp)) {
                    targetName = apps[q].getChild("launchable-target-name").getValue();
                    className = apps[q].getChild("class").getValue();
                    displayName = apps[q].getChild("display-name").getValue();
                    break;
                }
            }

            if (!className.equals("")) {
                Vector jars = new Vector();

                jars.add(jarName);

                LaunchableTarget tmpTarget = new NormalLaunchableTargetImpl(targetName,
                                                                            className,
                                                                            appFilePrefix, jars,
                                                                            displayName, false);
                Frimble frimble = mWindowManager.createFrimble(displayName);

                launchApp(jcl, className, tmpTarget, frimble.getContentPane(), true);
                mDecorator.initDecoratation(frimble, null);
            } else {

                //TODO issue error dialog, not a jar Jesktop recognizes.
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ConfigurationException ce) {
            ce.printStackTrace();
        }

        return null;
    }

    /**
     * Method launchApp
     *
     *
     * @param launchableTarget
     *
     * @return
     *
     */
    public Object launchApp(final LaunchableTarget launchableTarget) throws JesktopLaunchException {
        try {
            return launchApp(launchableTarget, null, true);
        } catch (AlreadyLaunchedException ale) {
            ale.getAlreadyLaunchedTarget().toFront();

            return ale.getAlreadyLaunchedTarget().getInstantiatedApp();
        }
    }

    /**
     * Method launchApp
     *
     *
     * @param launchableTarget
     * @param inHere
     *
     * @return
     *
     */
    public Object launchApp(final LaunchableTarget launchableTarget, final JComponent inHere)
            throws JesktopLaunchException {
        return launchApp(launchableTarget, inHere, false);
    }

    private Object launchApp(final LaunchableTarget launchableTarget, final java.awt.Container inHere, 
                             final boolean fullClosable) throws JesktopLaunchException {

        String className = launchableTarget.getClassName();
        ClassLoader classLoader = mLaunchableTargetFactory.getClassLoader(launchableTarget);

        return launchApp(classLoader, className, launchableTarget, inHere, fullClosable);
    }

    private Object launchApp(
            final ClassLoader classLoader, final String className, final LaunchableTarget launchableTarget, 
            final java.awt.Container inHere, final boolean fullClosable) throws JesktopLaunchException {

        checkSecondInstance(launchableTarget);

        try {
            Class cl;

            if (classLoader == null) {
                cl = Class.forName(className);
            } else {
                cl = classLoader.loadClass(className);
            }

            Object instantiatedApp = cl.newInstance();

            if (instantiatedApp instanceof ControlPanel) {
                ((ControlPanel) instantiatedApp)
                    .setLaunchableTargetFactory(mLaunchableTargetFactory);
                ((ControlPanel) instantiatedApp).setConfigManager(mConfigManager);
                
            }

            return launchApp2(classLoader, instantiatedApp, launchableTarget, inHere,
                              fullClosable);
        } catch (ClassNotFoundException cnfe) {
            throw new JesktopLaunchException("App " + launchableTarget.getTargetName()
                                             + " can't launch as class "
                                             + launchableTarget.getClassName()
                                             + " missing from it's jar");
        } catch (NoClassDefFoundError cndfe) {
            cndfe.printStackTrace();
            throw new JesktopLaunchException("App " + launchableTarget.getTargetName()
                                             + " can't launch some dependant/parent class cannot be found. ");
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            System.out.println("InstantiationException " + ie.getMessage());
            ie.printStackTrace();
        }

        return new JLabel("No application found, see trace");
    }

    private Object launchApp2(final ClassLoader classLoader, final Object instantiatedApp,
                              final LaunchableTarget launchableTarget, final java.awt.Container inHere,
                              final boolean fullClosable) {

        Frimble frimble = null;
        java.awt.Container inContainer = inHere;
        if (inContainer == null) {
            if (!(instantiatedApp instanceof JFrimble)) {
                frimble = mWindowManager.createFrimble(launchableTarget.getDisplayName());
            } else {
                frimble = ((JFrimble) instantiatedApp).getFrimble();
            }

            frimble.registerFrimbleCallback(this);

            inContainer = frimble.getContentPane();
        }

        //System.out.println("Frimble=" + frimble);
        if (instantiatedApp instanceof JComponent) {
            if (inContainer instanceof JScrollPane) {
                JScrollPane jsp = (JScrollPane) inContainer;

                jsp.setViewportView((JComponent) instantiatedApp);
            } else {
                inContainer.add((JComponent) instantiatedApp, BorderLayout.CENTER);
            }
        }

        if (instantiatedApp instanceof DesktopKernelAware) {
            DesktopKernelAware dak = (DesktopKernelAware) instantiatedApp;
            dak.setDesktopKernel(mDesktopKernelService.getProxy());
        }

        if (frimble != null) {
            if (instantiatedApp instanceof FrimbleAware) {
                FrimbleAware fa = (FrimbleAware) instantiatedApp;

                fa.setFrimble(frimble);
            }

            //frimble.pack();
            frimble.setVisible(true);
            frimble.addFrimbleListener(mDesktopKernelService.getKernelFrimbleListener());
        }

        if (fullClosable) {
            LaunchedTarget lchd = mDesktopKernelService.makeKernelLaunchedTarget(frimble,
                                      instantiatedApp, launchableTarget);

            mWindowManager.addLaunchedTarget(lchd);
            mLaunchedTargets.add(lchd);
        }

        if (fullClosable && (frimble != null)) {
            mDecorator.initDecoratation(frimble, launchableTarget);

            if (launchableTarget.getTargetName().equals(LaunchableTargetFactory.SHUTDOWN_APP)) {
                ShutdownConfirmer sc = new ShutdownConfirmer() {

                    public void shutdownJesktopOnly(boolean force) throws PropertyVetoException {
                        mDesktopKernelService.shutdownJesktopOnly(force);
                    }

                    public void shutdownAvalon(boolean force) throws PropertyVetoException {
                        mDesktopKernelService.shutdownAvalon(force);
                    }
                };

                ((org.apache.avalon.jesktop.builtinapps.sys.ShutdownConfirmer) instantiatedApp)
                    .setShutdownCallback(sc);    // should be a proxy for parm 1.
            }
        }

        return instantiatedApp;
    }

    /**
     * Method makeFrimble
     *
     *
     * @param frimbleAware
     *
     * @return
     *
     */
    public Frimble makeFrimble(final FrimbleAware frimbleAware) {

        LaunchableTarget lt =
            mLaunchableTargetFactory.getLaunchableTarget((JComponent) frimbleAware);

        if (lt != null) {
            Frimble frimble = mWindowManager.createFrimble(lt.getDisplayName());

            launchApp2(mLaunchableTargetFactory.getClassLoader(lt), (JComponent) frimbleAware, lt,
                       null, true);

            return frimble;
        }

        return null;    // frimbleAware not a launchable target.
    }

    /**
     * Method launchAppByName
     *
     *
     * @param appTargetName
     *
     * @return
     *
     * @throws JesktopLaunchException
     *
     */
    public Object launchAppByName(final String appTargetName) throws JesktopLaunchException {

        LaunchableTarget[] lts = mLaunchableTargetFactory.getAllLaunchableTargets();

        for (int f = 0; f < lts.length; f++) {
            if (lts[f].getTargetName().equals(appTargetName)) {
                return launchApp(lts[f]);
            }
        }

        System.err.println("Target (" + appTargetName + ") not found to launch");

        throw new JesktopLaunchException("Target (" + appTargetName + ") not found to launch");
    }

    /**
     * Method getAllAppList
     *
     *
     * @return
     *
     */
    public String[] getAllAppList() {

        LaunchableTarget[] lts = mLaunchableTargetFactory.getAllLaunchableTargets();
        String[] retval = new String[lts.length];

        for (int f = 0; f < lts.length; f++) {
            retval[f] = lts[f].getTargetName();
        }

        return retval;
    }

    private void checkSecondInstance(final LaunchableTarget launchableTarget)
            throws AlreadyLaunchedException {

        if (launchableTarget.isSingleInstanceApp()) {
            Iterator it = mLaunchedTargets.iterator();

            while (it.hasNext()) {
                LaunchedTarget lt = (LaunchedTarget) it.next();

                if (lt.isFromThisLaunchableTarget(launchableTarget)) {
                    throw new AlreadyLaunchedException(lt);
                }
            }
        }
    }
}