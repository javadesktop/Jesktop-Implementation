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
package net.sourceforge.jesktopimpl.core;

import org.jesktop.services.DesktopKernelService;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.services.KernelConfigManager;
import org.jesktop.WindowManager;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.FrimbleCallback;
import org.jesktop.frimble.JFrimble;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.*;
import org.jesktop.services.DesktopKernelService;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;


/**
 * Class AppLauncherImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.8 $
 */
public class AppLauncherImpl extends AppBase implements AppLauncher, FrimbleCallback {

    private static int TEMPAPPSUFFIX = 1;
    private org.jesktop.WindowManager windowManager;
    private LaunchableTargetFactory launchableTargetFactory;
    private DesktopKernelService desktopKernelService;
    private Vector launchedTargets;
    private Decorator decorator;
    private KernelConfigManager kernelConfigManager;
    private MutablePicoContainer picoContainer;

    protected AppLauncherImpl(final WindowManager windowManager,
                              final LaunchableTargetFactory launchableTargetFactory,
                              final DesktopKernelService desktopKernelService,
                              final Vector launchedTargets,
                              final Decorator decorator,
                              final KernelConfigManager kernelConfigManager,
                              final AppInstaller appInstaller,
                              final File baseDir) {
        super(baseDir);
        this.launchableTargetFactory = launchableTargetFactory;
        this.windowManager = windowManager;
        this.desktopKernelService = desktopKernelService;
        this.launchedTargets = launchedTargets;
        this.decorator = decorator;
        this.kernelConfigManager = kernelConfigManager;

        picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentInstance(windowManager);
        picoContainer.registerComponentInstance(launchableTargetFactory);
        picoContainer.registerComponentInstance(desktopKernelService);
        picoContainer.registerComponentInstance(launchedTargets);
        picoContainer.registerComponentInstance(decorator);
        picoContainer.registerComponentInstance(kernelConfigManager);
        picoContainer.registerComponentInstance(appInstaller);

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
     * @throws org.jesktop.JesktopPackagingException
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
                Frimble frimble = windowManager.createFrimble(displayName);

                launchApp(jcl, className, tmpTarget, frimble.getContentPane(), true);
                decorator.decorate(frimble, null);
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
        ClassLoader classLoader = launchableTargetFactory.getClassLoader(launchableTarget);

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

            DefaultPicoContainer defaultPicoContainer = new DefaultPicoContainer(picoContainer);
            Object instantiatedApp = defaultPicoContainer.registerComponentImplementation(cl).getComponentInstance();

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
        }
    }

    private Object launchApp2(final ClassLoader classLoader, final Object instantiatedApp,
                              final LaunchableTarget launchableTarget, final java.awt.Container inHere,
                              final boolean fullClosable) {

        Frimble frimble = null;
        java.awt.Container inContainer = inHere;
        if (inContainer == null) {
            if (!(instantiatedApp instanceof JFrimble)) {
                frimble = windowManager.createFrimble(launchableTarget.getDisplayName());
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

        if (frimble != null) {
            if (instantiatedApp instanceof FrimbleAware) {
                FrimbleAware fa = (FrimbleAware) instantiatedApp;

                fa.setFrimble(frimble);
            }

            //frimble.pack();
            frimble.setVisible(true);
            frimble.addFrimbleListener(desktopKernelService.getKernelFrimbleListener());
        }

        if (fullClosable) {
            LaunchedTarget lchd = desktopKernelService.makeKernelLaunchedTarget(frimble,
                                      instantiatedApp, launchableTarget);

            windowManager.addLaunchedTarget(lchd);
            launchedTargets.add(lchd);
        }

        if (fullClosable && (frimble != null)) {
            decorator.decorate(frimble, launchableTarget);

            if (launchableTarget.getTargetName().equals(LaunchableTargetFactory.SHUTDOWN_APP)) {
                ShutdownConfirmer sc = new ShutdownConfirmer() {

                    public void shutdownJesktopOnly(boolean force) throws PropertyVetoException {
                        desktopKernelService.shutdownJesktopOnly(force);
                    }

                    public void shutdownSystem(boolean force) throws PropertyVetoException {
                        desktopKernelService.shutdownSystem(force);
                    }
                };

                ((net.sourceforge.jesktopimpl.builtinapps.sys.ShutdownConfirmer) instantiatedApp)
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
            launchableTargetFactory.getLaunchableTarget((JComponent) frimbleAware);

        if (lt != null) {
            Frimble frimble = windowManager.createFrimble(lt.getDisplayName());

            launchApp2(launchableTargetFactory.getClassLoader(lt), (JComponent) frimbleAware, lt,
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

        LaunchableTarget[] lts = launchableTargetFactory.getAllLaunchableTargets();

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

        LaunchableTarget[] lts = launchableTargetFactory.getAllLaunchableTargets();
        String[] retval = new String[lts.length];

        for (int f = 0; f < lts.length; f++) {
            retval[f] = lts[f].getTargetName();
        }

        return retval;
    }

    private void checkSecondInstance(final LaunchableTarget launchableTarget)
            throws AlreadyLaunchedException {

        if (launchableTarget.isSingleInstanceApp()) {
            Iterator it = launchedTargets.iterator();

            while (it.hasNext()) {
                LaunchedTarget lt = (LaunchedTarget) it.next();

                if (lt.isFromThisLaunchableTarget(launchableTarget)) {
                    throw new AlreadyLaunchedException(lt);
                }
            }
        }
    }
}
