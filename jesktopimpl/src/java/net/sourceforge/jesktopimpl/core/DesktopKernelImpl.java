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

import net.sourceforge.jesktopimpl.builtinapps.installer.ConfirmInstallation;
import net.sourceforge.jesktopimpl.builtinapps.sys.ErrorApp;
import org.jesktop.services.DesktopKernelService;
import org.jesktop.services.WindowManagerService;
import org.jesktop.services.KernelConfigManager;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.appsupport.DropAware;
import org.jesktop.appsupport.DraggedItem;
import org.jesktop.appsupport.ContentViewer;
import org.jesktop.frimble.FrimbleListener;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAdapter;
import org.jesktop.frimble.FrimbleEvent;
import org.jesktop.config.ObjConfigurable;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.ConfigHelper;
import org.jesktop.mime.MimeManager;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.Decorator;
import org.jesktop.AppLauncher;
import org.jesktop.LaunchedTarget;
import org.jesktop.JesktopPackagingException;
import org.jesktop.JesktopLaunchException;
import org.jesktop.ImageRepository;
import org.jesktop.*;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import javax.swing.JComponent;
import java.util.Vector;
import java.util.Iterator;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.awt.Point;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version 1.0
 */
public class DesktopKernelImpl
        implements DesktopKernelService, DesktopKernel, ShutdownConfirmer,
                   PropertyChangeListener {

    //private ComponentManager mCompManager;
    //private Configuration phoenixConfiguration;

    //  protected final static Logger LOGGER = LogKit.getLoggerFor("jesktop-kernel");
    private final Vector launchedTargets = new Vector();
    private LaunchableTargetFactory launchableTargetFactory;
    private AppInstallerImpl appInstaller;
    private AppLauncher appLauncher;
    private ImageRepository imageRepository;
    private KernelConfigManager configManager;
    private DecoratorLaunchableTarget currentDecoratorLaunchableTarget;
    private Decorator currentDecorator;
    private final KernelFrimbleListener kernelFrimbleListener = new KernelFrimbleListener();
    private final PropertyChangeSupport propertyChangeSupport;
    private LaunchableTarget noRegViewer = new NormalLaunchableTargetImpl("*NoRegisteredViewer*",
                                               "net.sourceforge.jesktopimpl.builtinapps.sys.NoRegisteredViewer",
                                               "No Register Viewer - Error", true);
    private LaunchableTarget installationConfirmerTarget =
        new NormalLaunchableTargetImpl("*InstallationConfirmer*",
                                       "net.sourceforge.jesktopimpl.builtinapps.installer.ConfirmInstallation",
                                       "Please Confirm Installation", false);
    private String DFT_DECORATOR = "*DefaultDecorator*";
    private DecoratorLaunchableTarget defaultDecorator;
    private LaunchableTarget errorAppTarget = new NormalLaunchableTargetImpl("*ErrorApp*",
                                                  "net.sourceforge.jesktopimpl.builtinapps.sys.ErrorApp", "Error", false);
    private WindowManagerService mWindowManager;
    private DropAware currentDropApp;
    private DraggedItem currentDraggedItem;
    protected MimeManager mMimeManager;
    private File mBaseDirectory;
    private ThreadPool threadPool;
    private KernelConfigManager kernelConfigManager;
    private MutablePicoContainer picoContainer;

    /**
     * Constructor DesktopKernelImpl
     *
     *
     */
    public DesktopKernelImpl(WindowManagerService windowManager, ObjectRepository repository, ThreadPool threadPool,
                             KernelConfigManager kernelCongigManager, ImageRepository imageRepository,
                             LaunchableTargetFactory launchableTargetFactory, KernelConfigManager kernelConfigManager,
                             File baseDirectory) {
        this.threadPool = threadPool;
        this.kernelConfigManager = kernelConfigManager;
        propertyChangeSupport = new PropertyChangeSupport(DesktopKernel.class.getName());
        mWindowManager = windowManager;
        configManager = kernelCongigManager;
        this.imageRepository = imageRepository;
        this.launchableTargetFactory = launchableTargetFactory;
        mBaseDirectory = baseDirectory;

        picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentInstance(windowManager);
        picoContainer.registerComponentInstance(kernelCongigManager);
        picoContainer.registerComponentInstance(imageRepository);
        picoContainer.registerComponentInstance(launchableTargetFactory);

        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        defaultDecorator =
            (DecoratorLaunchableTarget) this.launchableTargetFactory.makeDecoratorLaunchableTarget(
                DFT_DECORATOR, "net.sourceforge.jesktopimpl.builtinapps.decorators.DefaultDecorator",
                "Default Decorator", "decorators/default");

        mMimeManager = new MimeManagerImpl(repository, this.launchableTargetFactory);
        appInstaller = new AppInstallerImpl(propertyChangeSupport, this,
                this.launchableTargetFactory, this.imageRepository, mBaseDirectory);
        configManager.registerConfigInterest(this, "decorator/currentDecorator");
        configManager.registerConfigInterest(mWindowManager, "desktop/settings");
        setDecoratorLaunchableTarget(defaultDecorator);

        initializeDecorator();
        configManager.notifyObjConfig("decorator/currentDecorator", getClass().getClassLoader());
        mWindowManager.setKernelCallback(this);
        mWindowManager
            .setPersistableConfig(new PersistableConfigImpl(repository,
                                                            "WM-"
                                                            + mWindowManager.getClass()
                                                                .getName().hashCode()));
        notifyLaunchableTargetListeners();
        mWindowManager.initializeView();
        configManager.notifyXMLConfig("desktop/settings", getClass().getClassLoader());

        new File("Jesktop/Temp").mkdir();

        installStartupApplications();

        notifyLaunchableTargetListeners();


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
            this.setConfig(ConfigHelper.getConfigPath(event), event.getNewValue());
        }
    }

    /**
     * Method addPropertyChangeListener
     *
     *
     * @param listener
     *
     */
    public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Method removePropertyChangeListener
     *
     *
     * @param listener
     *
     */
    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public FrimbleListener getKernelFrimbleListener() {
        return kernelFrimbleListener;
    }

    public LaunchedTarget makeKernelLaunchedTarget(final Frimble frimble, final Object instantiatedApp,
                                                      final LaunchableTarget launchableTarget) {
        return new KernelLaunchedTarget(frimble, instantiatedApp, launchableTarget);
    }

    /**
     * Method notifyLaunchableTargetListeners
     *
     *
     */
    public void notifyLaunchableTargetListeners() {

        propertyChangeSupport.firePropertyChange(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, null,
                                                 launchableTargetFactory
                                                     .getAllLaunchableTargets());
    }

    /**
     * Method installStartupApplications
     *
     *
     *
     */
    private void installStartupApplications() {

        File dir = new File(mBaseDirectory, "InstallAtStartup");
        String[] jars = dir.list();
        // no pre-install directory.
        if (jars == null) {
            return;
        }
        for (int i = 0; i < jars.length; i++) {
            if (jars[i].endsWith(".jar")) {
                try {
                  File jarFile = new File(dir,jars[i]);
                  URL jarURL = jarFile.toURL();
                  appInstaller.installAppWithoutConfirmation(jarURL);
                  jarFile.delete();
                } catch (MalformedURLException mufe) {
                    System.out.println(mufe.getMessage());
                } catch (JesktopPackagingException jpe) {
                    System.out.println(jpe.getMessage());
                }
            }
        }
    }

    /**
     * Method launchedTargetSelected
     *
     *
     * @param launchedTarget
     *
     */
    public void launchedTargetSelected(final LaunchedTarget launchedTarget) {

        KernelLaunchedTarget klt = (KernelLaunchedTarget) launchedTarget;

        klt.getFrimble().toFront();
    }

    /**
     * Method launchedTargetCloseRequested
     *
     *
     * @param launchedTarget
     *
     */
    public void launchedTargetCloseRequested(final LaunchedTarget launchedTarget) {

        KernelLaunchedTarget klt = (KernelLaunchedTarget) launchedTarget;

        klt.getFrimble().dispose();
    }

    /**
     * Method itemBeingDragged
     *
     *
     * @param draggedItem
     * @param pt
     *
     */
    public void itemBeingDragged(final DraggedItem draggedItem, final Point pt) {

        mWindowManager.renderDragRepresentation(draggedItem, pt);

        if (draggedItem != null) {
            Iterator it = launchedTargets.iterator();
            boolean recognized = false;

            while (it.hasNext()) {
                KernelLaunchedTarget klt = (KernelLaunchedTarget) it.next();

                if (klt.getInstantiatedApp() instanceof DropAware) {
                    DropAware da = (DropAware) klt.getInstantiatedApp();

                    if (!recognized) {
                        recognized =
                            da.doYouRecognizeDraggedItem(pt, draggedItem.getDraggedItemClass());

                        if (recognized) {
                            currentDropApp = da;
                            currentDraggedItem = draggedItem;
                        }
                    } else {
                        da.anotherHasRecognizedDraggedItem(draggedItem.getDraggedItemClass());
                    }
                }
            }

            if (!recognized) {
                currentDropApp = null;
            }
        } else {
            Iterator it = launchedTargets.iterator();

            if (currentDropApp != null) {
                currentDropApp.droppedOnYou(currentDraggedItem.getDraggedIem());
            }

            while (it.hasNext()) {
                KernelLaunchedTarget klt = (KernelLaunchedTarget) it.next();

                if (klt.getInstantiatedApp() instanceof DropAware) {
                    DropAware da = (DropAware) klt.getInstantiatedApp();

                    da.draggingStopped();
                }
            }
        }
    }

    /**
     * Method viewDocument
     *
     *
     *
     * @param url
     * @param inHere
     * @param thumbNail
     *
     */
    public void viewDocument(final URL url, final JComponent inHere, final boolean thumbNail)
            throws JesktopLaunchException {

        Object obj;

        System.out.println( "URL x " + url);

        obj = appLauncher.launchApp(getAssociatedViewer(url), inHere);

        if (obj instanceof ContentViewer) {
            ContentViewer cv = (ContentViewer) obj;

            cv.viewContent(url, thumbNail);
        }
    }

    /**
     * Method viewDocument
     *
     *
     * @param url
     *
     * @throws JesktopLaunchException
     *
     */
    public void viewDocument(final URL url) throws JesktopLaunchException {
        this.viewDocument(url, null, false);
    }

    /**
     * Method editDocument
     *
     *
     * @param docURL
     *
     */
    public void editDocument(final URL docURL) {

        // unimplemented as yet
    }

    /**
     * Method editDocument
     *
     *
     * @param file
     *
     */
    public void editDocument(final File file) {

        // unimplemented as yet
    }

    protected ConfigManager getConfigManager() {
        return configManager;
    }

    private LaunchableTarget getAssociatedViewer(final URL url) {
        LaunchableTarget retval = null;
        // hack hack hack, till Laurent delivers the Mime Registry :-))
        if (url.toExternalForm().toLowerCase().endsWith(".txt")) {
            retval = (LaunchableTarget) launchableTargetFactory.getLaunchableTarget("Tools/TextViewer");
        } else if (url.toExternalForm().toLowerCase().endsWith(".jpg") | url.toExternalForm().toLowerCase().endsWith(".gif")) {
            retval = (LaunchableTarget) launchableTargetFactory.getLaunchableTarget("Tools/ImageViewer");
        }
        if (retval == null) {
            retval = noRegViewer;
        }
        return retval;
    }

    protected void confirmAppInstallation(final LaunchableTarget[] launchableTargets)
            throws JesktopLaunchException {

        Object obj = appLauncher.launchApp(installationConfirmerTarget);
        InstallationConfirmer ic = new InstallationConfirmer() {

            public boolean isRegistered(String targetName) {
                return launchableTargetFactory.isRegistered(targetName);
            }

            public void confirmLaunchableTarget(LaunchableTarget launchableTarget) {
                launchableTargetFactory.confirmLaunchableTarget(launchableTarget);
            }
        };

        ((ConfirmInstallation) obj).setConfirmationDetails(ic, launchableTargets);    // should be a proxy for parm 1.
    }

    protected void showErrorApp(final Exception e) throws JesktopLaunchException {

        ErrorApp errorApp = (ErrorApp) appLauncher.launchApp(errorAppTarget);

        errorApp.setException(e);
    }

    /**
     * Method initiateShutdown
     *
     *
     * @param shutdownType
     *
     * @throws JesktopLaunchException
     *
     */
    public void initiateShutdown(final String shutdownType) throws JesktopLaunchException {

        Object ojb =
            appLauncher
                .launchApp(launchableTargetFactory
                    .getLaunchableTarget(launchableTargetFactory.SHUTDOWN_APP));
    }

    /**
     * Method runAsychronously
     *
     *
     * @param runnable
     *
     *
     */
    public void runAsychronously(final Runnable runnable) {
        threadPool.execute(runnable);
    }

    /**
     * Method uninstall
     *
     *
     * @param launchableTarget
     *
     */
    public void uninstall(final LaunchableTarget launchableTarget) {
        launchableTargetFactory.removeLaunchableTarget(launchableTarget.getTargetName());
        notifyLaunchableTargetListeners();
    }

    /**
     * Method getNormalLaunchableTargets
     *
     *
     * @return
     *
     */
    public LaunchableTarget[] getNormalLaunchableTargets() {
        return launchableTargetFactory.getNormalLaunchableTargets();
    }

    /**
     * Method getAllLaunchableTargets
     *
     *
     * @return
     *
     */
    public LaunchableTarget[] getAllLaunchableTargets() {
        return launchableTargetFactory.getAllLaunchableTargets();
    }

    private void closeApps() throws PropertyVetoException {

        Vector openApps = new Vector(launchedTargets);
        Iterator it = openApps.iterator();

        while (it.hasNext()) {
            KernelLaunchedTarget klt = (KernelLaunchedTarget) it.next();

            if (klt.getTargetName().equals(launchableTargetFactory.SHUTDOWN_APP)) {
                klt.getFrimble().setClosed(true);
            }
        }
    }

    /**
     * Method shutdownJesktopOnly
     *
     *
     * @param force
     *
     * @throws PropertyVetoException
     *
     */
    public void shutdownJesktopOnly(final boolean force) throws PropertyVetoException {
        closeApps();
        mWindowManager.close();
    }

    /**
     * Method shutdownSystem
     *
     *
     * @param force
     *
     * @throws PropertyVetoException
     *
     */
    public void shutdownSystem(final boolean force) throws PropertyVetoException {

        closeApps();
        mWindowManager.close();
        System.exit(0);
    }

    /**
     * Method setDecoratorLaunchableTarget
     *
     *
     * @param dlt
     *
     */
    public void setDecoratorLaunchableTarget(final DecoratorLaunchableTarget dlt) {

        try {
            ClassLoader cLoader;
            Class cl;

            if (!dlt.canBeUnInstalled()) {
                cLoader = this.getClass().getClassLoader();
                cl = cLoader.loadClass(dlt.getClassName());
            } else {
                cLoader = launchableTargetFactory.getClassLoader(dlt);
                cl = cLoader.loadClass(dlt.getClassName());
            }

            Decorator oldDecorator = currentDecorator;

            // all fine, put in place decorator

            currentDecorator = (Decorator) new DefaultPicoContainer(picoContainer).registerComponentImplementation(cl).getComponentInstance();

            if (currentDecorator instanceof ObjConfigurable) {
                ((ObjConfigurable) currentDecorator)
                    .setConfig(configManager.getObjConfig(dlt.getConfigPath(), cLoader));
            }

            currentDecoratorLaunchableTarget = dlt;

            if (oldDecorator instanceof PropertyChangeListener) {
                configManager.unRegisterConfigInterest(oldDecorator);
            }

            if (currentDecorator instanceof PropertyChangeListener) {
                configManager.registerConfigInterest(currentDecorator,currentDecoratorLaunchableTarget.getConfigPath());
            }

            appLauncher = new AppLauncherImpl(mWindowManager, launchableTargetFactory, this,
                                              launchedTargets,
                                              currentDecorator, kernelConfigManager , appInstaller, mBaseDirectory);

            mWindowManager.setAppLauncher(appLauncher);
            mWindowManager.updateComponentTreeUI();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void initializeDecorator() {

        String targetName = configManager.getStringConfig("decorator/currentDecorator", DFT_DECORATOR);

        //if (targetName.equals(DFT_DECORATOR)) {
        //    setDecoratorLaunchableTarget(defaultDecorator);
        //} else {
        //    setDecoratorLaunchableTarget((DecoratorLaunchableTarget) launchableTargetFactory
        //        .getLaunchableTarget(targetName));
        //}
    }

    /**
     * Method getDecoratorLaunchableTargets
     *
     *
     * @return
     *
     */
    public DecoratorLaunchableTarget[] getDecoratorLaunchableTargets() {
        return launchableTargetFactory.getDecoratorLaunchableTargets();
    }

    /**
     * Method getConfigletLaunchableTargets
     *
     *
     * @return
     *
     */
    public ConfigletLaunchableTarget[] getConfigletLaunchableTargets() {
        return launchableTargetFactory.getConfigletLaunchableTargets();
    }

    protected void setConfig(final String configPath, final Object config) {

        if (config instanceof String) {
            String cfg = (String) config;

            if (configPath.equals("decorator/currentDecorator")) {
                setDecoratorLaunchableTarget((DecoratorLaunchableTarget) launchableTargetFactory
                    .getLaunchableTarget(cfg));
            }
        }
    }

    // this class adds some private info to the public class LaunchedTarget so that
    // only the kernel can access the info.

    /**
     * Class KernelLaunchedTarget
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.8 $
     */
    private class KernelLaunchedTarget extends LaunchedTargetImpl {

        private Frimble frimble;
        private Object instantiatedApp;
        private LaunchableTarget launchableTarget;

        private KernelLaunchedTarget(final Frimble frimble, final Object instantiatedApp,
                                     final LaunchableTarget launchableTarget) {

            super(launchableTarget.getTargetName(), launchableTarget.getDisplayName());

            this.launchableTarget = launchableTarget;
            this.frimble = frimble;
            this.instantiatedApp = instantiatedApp;
        }

        /**
         * Method getInstantiatedApp
         *
         *
         * @return
         *
         */
        public Object getInstantiatedApp() {
            return instantiatedApp;
        }

        private Frimble getFrimble() {
            return frimble;
        }

        /**
         * Method isFromThisLaunchableTarget
         *
         *
         * @param launchableTarget
         *
         * @return
         *
         */
        public boolean isFromThisLaunchableTarget(final LaunchableTarget launchableTarget) {
            return launchableTarget == this.launchableTarget;
        }

        /**
         * Method toFront
         *
         *
         */
        public void toFront() {
            frimble.toFront();
        }
    }

    /**
     * Class KernelFrimbleListener
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.8 $
     */
    private class KernelFrimbleListener extends FrimbleAdapter {

        /**
         * Method frimbleClosed
         *
         *
         * @param e
         *
         */
        public void frimbleClosed(final FrimbleEvent e) {

            Frimble fr = e.getFrimble();
            Iterator it = launchedTargets.iterator();

            while (it.hasNext()) {
                KernelLaunchedTarget klt = (KernelLaunchedTarget) it.next();

                if (klt.getFrimble() == fr) {
                    mWindowManager.removeLaunchedTarget(klt);
                    it.remove();
                    fr.removeFrimbleListener(this);

                    break;
                }
            }
        }
    }
}
