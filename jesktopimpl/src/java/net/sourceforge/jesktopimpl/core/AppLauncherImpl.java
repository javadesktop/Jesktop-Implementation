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

import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.AlreadyLaunchedException;
import org.jesktop.AppInstaller;
import org.jesktop.AppLauncher;
import org.jesktop.Decorator;
import org.jesktop.DesktopKernel;
import org.jesktop.JesktopLaunchException;
import org.jesktop.JesktopPackagingException;
import org.jesktop.LaunchedTarget;
import org.jesktop.WindowManager;
import org.jesktop.config.ConfigManager;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.FrimbleCallback;
import org.jesktop.frimble.JFrimble;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.mime.MimeManager;
import org.jesktop.services.DesktopKernelService;
import org.jesktop.services.KernelConfigManager;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;


/**
 * Class AppLauncherImpl
 *
 * @author Paul Hammant
 * @version $Revision: 1.13 $
 */
public class AppLauncherImpl extends AppBase implements AppLauncher, FrimbleCallback {

    private static int TEMPAPPSUFFIX = 1;
    private org.jesktop.WindowManager windowManager;
    private LaunchableTargetFactory launchableTargetFactory;
    private DesktopKernelService desktopKernelService;
    private Vector launchedTargets;
    private Decorator decorator;
    private ImplementationHidingPicoContainer configletTargetPicoContainer;
    private ImplementationHidingPicoContainer decoratorTargetPicoContainer;
    private ImplementationHidingPicoContainer normalTargetPicoContainer;
    private ImplementationHidingPicoContainer kernelTargetPicoContainer;

    protected AppLauncherImpl(final WindowManager windowManager,
                              final LaunchableTargetFactory launchableTargetFactory,
                              final DesktopKernelService desktopKernelService,
                              final Vector launchedTargets,
                              final Decorator decorator,
                              final KernelConfigManager kernelConfigManager,
                              final AppInstaller appInstaller,
                              final MimeManager mimeManager,
                              final DocumentBuilderFactory dbf,
                              final File baseDir) {
        super(dbf, baseDir);
        this.launchableTargetFactory = launchableTargetFactory;
        this.windowManager = windowManager;
        this.desktopKernelService = desktopKernelService;
        this.launchedTargets = launchedTargets;
        this.decorator = decorator;

        configletTargetPicoContainer = new ImplementationHidingPicoContainer();
        //configletTargetPicoContainer.registerComponentInstance(windowManager);
        //configletTargetPicoContainer.registerComponentInstance(launchableTargetFactory);
        //configletTargetPicoContainer.registerComponentInstance(desktopKernelService);
        //configletTargetPicoContainer.registerComponentInstance(launchedTargets);
        //configletTargetPicoContainer.registerComponentInstance(decorator);
        configletTargetPicoContainer.registerComponentInstance(ConfigManager.class, kernelConfigManager);
        //configletTargetPicoContainer.registerComponentInstance(appInstaller);

        decoratorTargetPicoContainer = new ImplementationHidingPicoContainer();
        decoratorTargetPicoContainer.registerComponentInstance(WindowManager.class, windowManager);
        //decoratorTargetPicoContainer.registerComponentInstance(launchableTargetFactory);
        //decoratorTargetPicoContainer.registerComponentInstance(desktopKernelService);
        decoratorTargetPicoContainer.registerComponentInstance(launchedTargets);
        decoratorTargetPicoContainer.registerComponentInstance(Decorator.class, decorator);
        //decoratorTargetPicoContainer.registerComponentInstance(kernelConfigManager);
        //decoratorTargetPicoContainer.registerComponentInstance(appInstaller);

        normalTargetPicoContainer = new ImplementationHidingPicoContainer();
        //normalTargetPicoContainer.registerComponentInstance(windowManager);
        //normalTargetPicoContainer.registerComponentInstance(LaunchableTargetFactory.class, launchableTargetFactory);
        //normalTargetPicoContainer.registerComponentInstance(DesktopKernel.class, desktopKernelService);
        //normalTargetPicoContainer.registerComponentInstance(launchedTargets);
        //normalTargetPicoContainer.registerComponentInstance(decorator);
        //normalTargetPicoContainer.registerComponentInstance(KernelConfigManager.class, kernelConfigManager);
        //normalTargetPicoContainer.registerComponentInstance(AppInstaller.class, appInstaller);

        kernelTargetPicoContainer = new ImplementationHidingPicoContainer();
        //kernelTargetPicoContainer.registerComponentInstance(windowManager);
        kernelTargetPicoContainer.registerComponentInstance(LaunchableTargetFactory.class, launchableTargetFactory);
        kernelTargetPicoContainer.registerComponentInstance(DesktopKernel.class, desktopKernelService);
        //kernelTargetPicoContainer.registerComponentInstance(launchedTargets);
        //kernelTargetPicoContainer.registerComponentInstance(decorator);
        kernelTargetPicoContainer.registerComponentInstance(KernelConfigManager.class, kernelConfigManager);
        kernelTargetPicoContainer.registerComponentInstance(AppInstaller.class, appInstaller);
        kernelTargetPicoContainer.registerComponentInstance(MimeManager.class, mimeManager);

    }

    protected String getNewTemporaryAppSuffix() {

        String tmp = "0000" + TEMPAPPSUFFIX++;

        return tmp.substring(tmp.length() - 5, tmp.length());
    }

    /**
     * Method launchAppWithoutInstallation
     *
     * @param jarURL
     * @return
     * @throws JesktopLaunchException
     * @throws org.jesktop.JesktopPackagingException
     *
     */
    public Object launchAppWithoutInstallation(final URL jarURL)
            throws JesktopPackagingException, JesktopLaunchException {
        System.out.println("\n\nlaunchAppWithoutInstallation " + jarURL);
        try {
            String appFilePrefix = "JesktopUninstalledApp" + getNewTemporaryAppSuffix();
            AppBase.JarSuffixHolder jarSuffix = new AppBase.JarSuffixHolder();
            String jarName = internJar(true, jarURL.openStream(), appFilePrefix, jarSuffix);
            JesktopURLClassLoader jcl = new JesktopURLClassLoader(new URL[]{
                new File(jarName).toURL()});
            Document appsXml = super.getApplicationsDotXML(jarURL.toExternalForm(), jcl);
            String defaultApp;

            NodeList das = appsXml.getElementsByTagName("default-application");
            Element da = null;
            for (int p = 0; p < das.getLength(); p++) {
                da = (Element) das.item(p);
            }
            if (da == null) {
                throw new JesktopPackagingException("\"default-application\" missing from JESKTOP-INF//applications.xml");
            }

            defaultApp = da.getAttribute("target-name");

            String displayName = "";
            String targetName = "";
            String className = "";
            NodeList apps = appsXml.getElementsByTagName("application");

            for (int q = 0; q < apps.getLength(); q++) {
                Element appE = (Element) apps.item(q);
                String ltn = super.getSingleElemValue(appE,"launchable-target-name",true);
                if (ltn.equals(defaultApp)) {
                    targetName = ltn;
                    className = super.getSingleElemValue(appE,"class",true);
                    displayName = super.getSingleElemValue(appE,"display-name",true);
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


            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
        /**
         * Method launchApp
         *
         *
         * @param launchableTarget
         *
         * @return the launched app.
         *
         */
        public Object launchApp(final LaunchableTarget launchableTarget) throws JesktopLaunchException
        {
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
         * @return the launched app
         *
         */
        public Object launchApp
        (
        final LaunchableTarget launchableTarget,
        final JComponent inHere)
        throws JesktopLaunchException
        {
            return launchApp(launchableTarget, inHere, false);
        }

        private Object launchApp
        (
        final LaunchableTarget launchableTarget,
        final java.awt.Container inHere,
        final boolean fullClosable) throws JesktopLaunchException
        {

            String className = launchableTarget.getClassName();
            ClassLoader classLoader = launchableTargetFactory.getClassLoader(launchableTarget);

            return launchApp(classLoader, className, launchableTarget, inHere, fullClosable);
        }

        private Object launchApp
        (
        final ClassLoader classLoader,
        final String className,
        final LaunchableTarget launchableTarget,
        final java.awt.Container inHere,
        final boolean fullClosable) throws JesktopLaunchException
        {

            checkSecondInstance(launchableTarget);

            try {
                DefaultNanoPicoContainer defaultPicoContainer;
                if (launchableTarget instanceof ConfigletLaunchableTarget) {
                    defaultPicoContainer = new DefaultNanoPicoContainer(classLoader, configletTargetPicoContainer);
                } else if (launchableTarget instanceof DecoratorLaunchableTarget) {
                    defaultPicoContainer = new DefaultNanoPicoContainer(classLoader, decoratorTargetPicoContainer);
                    //     } else if(launchableTarget instanceof KernelLaunchableTarget) {
                    //       defaultPicoContainer = new DefaultSoftCompositionPicoContainer(classLoader, kernelTargetPicoContainer);
                } else {
                    defaultPicoContainer = new DefaultNanoPicoContainer(classLoader, kernelTargetPicoContainer);
                }
                Object instantiatedApp = defaultPicoContainer.registerComponentImplementation(className).getComponentInstance(defaultPicoContainer);

                return launchApp2(classLoader, instantiatedApp, launchableTarget, inHere,
                        fullClosable);
            } catch (ClassNotFoundException cnfe) {
                System.out.println("--> " + cnfe.getMessage());
                cnfe.printStackTrace();
                throw new JesktopLaunchException("App " + launchableTarget.getTargetName()
                        + " can't launch as class '"
                        + launchableTarget.getClassName()
                        + "' is missing from it's jar");
            } catch (NoClassDefFoundError ncdfe) {
                ncdfe.printStackTrace();
                throw new JesktopLaunchException("App " + launchableTarget.getTargetName()
                        + " can't launch as class '" + ncdfe.getMessage() + "' cannot be found. ");
            }
        }

        private Object launchApp2
        (
        final ClassLoader classLoader,
        final Object instantiatedApp,
        final LaunchableTarget launchableTarget,
        final java.awt.Container inHere,
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
         * @return the Frimble
         *
         */
        public Frimble makeFrimble
        (
        final FrimbleAware frimbleAware) {

            LaunchableTarget lt =
                    launchableTargetFactory.getLaunchableTarget((JComponent) frimbleAware);

            if (lt != null) {
                Frimble frimble = windowManager.createFrimble(lt.getDisplayName());

                launchApp2(launchableTargetFactory.getClassLoader(lt), frimbleAware, lt,
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
        public Object launchAppByName
        (
        final String appTargetName) throws JesktopLaunchException
        {

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
         * @return the list of apps
         *
         */
        public String[] getAllAppList
        () {

            LaunchableTarget[] lts = launchableTargetFactory.getAllLaunchableTargets();
            String[] retval = new String[lts.length];

            for (int f = 0; f < lts.length; f++) {
                retval[f] = lts[f].getTargetName();
            }

            return retval;
        }

        private void checkSecondInstance
        (
        final LaunchableTarget launchableTarget)
        throws AlreadyLaunchedException
        {

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
