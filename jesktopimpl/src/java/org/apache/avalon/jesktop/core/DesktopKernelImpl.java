/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.avalon.jesktop.builtinapps.installer.ConfirmInstallation;
import org.apache.avalon.jesktop.builtinapps.sys.ErrorApp;
import org.apache.avalon.jesktop.services.DesktopKernelService;
import org.apache.avalon.jesktop.services.KernelConfigManager;
import org.apache.avalon.jesktop.services.WindowManager;
import org.apache.avalon.jesktop.services.LaunchableTargetFactory;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.proxy.DynamicProxy;
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
import org.jesktop.api.DesktopKernel;
import org.jesktop.api.Decorator;
import org.jesktop.api.ImageRepository;
import org.jesktop.api.AppLauncher;
import org.jesktop.api.AppInstaller;
import org.jesktop.api.LaunchedTarget;
import org.jesktop.api.JesktopPackagingException;
import org.jesktop.api.JesktopLaunchException;

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
public class DesktopKernelImpl extends AbstractLogEnabled
        implements Block, DesktopKernelService, DesktopKernel, ShutdownConfirmer, Contextualizable, Composable,
                   Initializable, Configurable, PropertyChangeListener {

    private ComponentManager mCompManager;
    private Configuration phoenixConfiguration;

    //  protected final static Logger LOGGER = LogKit.getLoggerFor("jesktop-kernel");
    private static boolean LOG = true;
    private final Vector launchedTargets = new Vector();
    private LaunchableTargetFactory mLaunchableTargetFactory;
    private AppInstaller mAppInstallerProxy;
    private AppInstallerImpl mAppInstaller;
    private AppLauncher mAppLauncherProxy, mAppLauncher;
    private ImageRepository mImageRepository;
    private ImageRepository mImageRepositoryProxy;
    private KernelConfigManager mConfigManager;
    private DecoratorLaunchableTarget currentDecoratorLaunchableTarget;
    private Decorator mCurrentDecorator;
    private final KernelFrimbleListener kernelFrimbleListener = new KernelFrimbleListener();
    private final PropertyChangeSupport propertyChangeSupport;
    private LaunchableTarget noRegViewer = new NormalLaunchableTargetImpl("*NoRegisteredViewer*",
                                               "org.apache.avalon.jesktop.builtinapps.sys.NoRegisteredViewer",
                                               "No Register Viewer - Error", true);
    private LaunchableTarget installationConfirmerTarget =
        new NormalLaunchableTargetImpl("*InstallationConfirmer*",
                                       "org.apache.avalon.jesktop.builtinapps.installer.ConfirmInstallation",
                                       "Please Confirm Installation", false);
    private String DFT_DECORATOR = "*DefaultDecorator*";
    private DecoratorLaunchableTarget defaultDecorator;
    private LaunchableTarget errorAppTarget = new NormalLaunchableTargetImpl("*ErrorApp*",
                                                  "org.apache.avalon.jesktop.builtinapps.sys.ErrorApp", "Error", false);
    private WindowManager mWindowManager;
    private Store mJesktopStore;
    private ThreadManager       mThreadManager;
    private ObjectRepository repository;
    private DropAware currentDropApp;
    private DraggedItem currentDraggedItem;
    protected BlockContext mBlockContext;
    private DesktopKernel mDesktopKernelProxy;
    protected MimeManager mMimeManager;
    protected MimeManager mMimeManagerProxy;
    private File mBaseDirectory;
    private Configuration mRepository;

    /**
     * Constructor DesktopKernelImpl
     *
     *
     */
    public DesktopKernelImpl() {
        propertyChangeSupport = new PropertyChangeSupport(DesktopKernel.class.getName());
    }


    /**
     * Method propertyChange
     *
     *
     * @param evt
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
     * Method contextualize
     *
     *
     * @param mContext
     *
     */
    public void contextualize(final Context context) {
        mBlockContext = (BlockContext) context;
        mBaseDirectory = mBlockContext.getBaseDirectory();
    }

    protected final BlockContext getBlockContext() {
        return mBlockContext;
    }

    /**
     * Method configure
     *
     *
     *
     * @param confManager
     *
     */
    public void configure(final Configuration confManager) throws ConfigurationException {
        this.phoenixConfiguration = confManager;
        mRepository = phoenixConfiguration.getChild("repository");
    }

    /**
     * Method compose
     *
     *
     *
     * @param compManager
     *
     *
     */
    public void compose(final ComponentManager compManager) {
        mCompManager = compManager;
    }

    /**
     * Method notifyLaunchableTargetListeners
     *
     *
     */
    public void notifyLaunchableTargetListeners() {

        propertyChangeSupport.firePropertyChange(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, null,
                                                 mLaunchableTargetFactory
                                                     .getAllLaunchableTargets());
    }

    /**
     * Method initialize
     *
     *
     */
    public void initialize() {

        //setupLogger(this);
        getLogger().info("Jesktop Kernel Initialized");

        try {
            mWindowManager = (WindowManager) mCompManager.lookup(WindowManager.class.getName());
            mJesktopStore =  (Store) mCompManager.lookup(Store.ROLE);
            mThreadManager = (ThreadManager) mCompManager.lookup(ThreadManager.ROLE);
            mConfigManager = (KernelConfigManager) mCompManager.lookup(KernelConfigManager.class.getName());
            mImageRepository = (ImageRepository) mCompManager.lookup(ImageRepository.class.getName());
            mLaunchableTargetFactory = (LaunchableTargetFactory) mCompManager.lookup(LaunchableTargetFactory.class.getName());

            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());



            if (mJesktopStore != null) {
                repository = (ObjectRepository) mJesktopStore.select(mRepository);

                if (LOG) {
                    getLogger().info("Got repository");
                }
            } else {
                if (LOG) {
                    getLogger().info("Whoops! store component is null");
                }
            }

            defaultDecorator =
                (DecoratorLaunchableTarget) mLaunchableTargetFactory.makeDecoratorLaunchableTarget(
                    DFT_DECORATOR, "org.apache.avalon.jesktop.builtinapps.decorators.DefaultDecorator",
                    "Default Decorator", "decorators/default");

            mMimeManager = new MimeManagerImpl(repository);
            mDesktopKernelProxy = (DesktopKernel) DynamicProxy.newInstance(this, new Class[] {DesktopKernel.class});
            mMimeManagerProxy = (MimeManager) DynamicProxy.newInstance(mMimeManager, new Class[] {MimeManager.class});
            mImageRepositoryProxy = (ImageRepository) DynamicProxy.newInstance(mImageRepository, new Class[] {ImageRepository.class});
            mAppInstaller = new AppInstallerImpl(propertyChangeSupport, this,
                                                mLaunchableTargetFactory, mImageRepository, mBaseDirectory);
            mAppInstallerProxy = (AppInstaller) DynamicProxy.newInstance(mAppInstaller, new Class[] {AppInstaller.class});

            mConfigManager.registerConfigInterest(this, "decorator/currentDecorator");
            mConfigManager.registerConfigInterest(mWindowManager, "desktop/settings");
            setDecoratorLaunchableTarget(defaultDecorator);

            initializeDecorator();
            mConfigManager.notifyObjConfig("decorator/currentDecorator", getClass().getClassLoader());
            mWindowManager.setKernelCallback(mDesktopKernelProxy);
            mWindowManager
                .setPersistableConfig(new PersistableConfigImpl(repository,
                                                                "WM-"
                                                                + mWindowManager.getClass()
                                                                    .getName().hashCode()));
            notifyLaunchableTargetListeners();
            mWindowManager.initializeView();
            mConfigManager.notifyXMLConfig("desktop/settings", getClass().getClassLoader());

            new File("Jesktop/Temp").mkdir();

            installStartupApplications();

            notifyLaunchableTargetListeners();

        } catch (ComponentException ce) {
            getLogger().error("init(): ComponentException", ce);
        }
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
                  mAppInstaller.installAppWithoutConfirmation(jarURL);
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
     * Method getAppLauncher
     *
     *
     * @return
     *
     */
    public AppLauncher getAppLauncher() {
        return mAppLauncherProxy;
    }

    /**
     * Method getImageRepository
     *
     *
     * @return
     *
     */
    public ImageRepository getImageRepository() {
        return mImageRepositoryProxy;
    }

    /**
     * Method getMimeManager
     *
     *
     * @return
     *
     */
    public MimeManager getMimeManager() {
        return mMimeManagerProxy;
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

        obj = getAppLauncher().launchApp(getAssociatedViewer(url), inHere);

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

    /**
     * Method getAppInstaller
     *
     *
     * @return
     *
     */
    public AppInstaller getAppInstaller() {
        return mAppInstallerProxy;
    }

    protected ConfigManager getConfigManager() {
        return mConfigManager;
    }

    private LaunchableTarget getAssociatedViewer(final URL url) {
        LaunchableTarget retval = null;
        // hack hack hack, till Laurent delivers the Mime Registry :-))
        if (url.toExternalForm().toLowerCase().endsWith(".txt")) {
            retval = (LaunchableTarget) mLaunchableTargetFactory.getLaunchableTarget("Tools/TextViewer");
        } else if (url.toExternalForm().toLowerCase().endsWith(".jpg") | url.toExternalForm().toLowerCase().endsWith(".gif")) {
            retval = (LaunchableTarget) mLaunchableTargetFactory.getLaunchableTarget("Tools/ImageViewer");
        }
        if (retval == null) {
            retval = noRegViewer;
        }
        return retval;
    }

    protected void confirmAppInstallation(final LaunchableTarget[] launchableTargets)
            throws JesktopLaunchException {

        Object obj = mAppLauncherProxy.launchApp(installationConfirmerTarget);
        InstallationConfirmer ic = new InstallationConfirmer() {

            public boolean isRegistered(String targetName) {
                return mLaunchableTargetFactory.isRegistered(targetName);
            }

            public void confirmLaunchableTarget(LaunchableTarget launchableTarget) {
                mLaunchableTargetFactory.confirmLaunchableTarget(launchableTarget);
            }
        };

        ((ConfirmInstallation) obj).setConfirmationDetails(ic, launchableTargets);    // should be a proxy for parm 1.
    }

    protected void showErrorApp(final Exception e) throws JesktopLaunchException {

        ErrorApp errorApp = (ErrorApp) mAppLauncherProxy.launchApp(errorAppTarget);

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
            mAppLauncherProxy
                .launchApp(mLaunchableTargetFactory
                    .getLaunchableTarget(mLaunchableTargetFactory.SHUTDOWN_APP));
    }

    /**
     * Method runAsychronously
     *
     *
     * @param runnable
     *
     * @throws Exception
     *
     */
    public void runAsychronously(final Runnable runnable) throws Exception {    // bad to declare throw of Exception
        ThreadPool tp = mThreadManager.getDefaultThreadPool();
        tp.execute(runnable);
    }

    /**
     * Method uninstall
     *
     *
     * @param launchableTarget
     *
     */
    public void uninstall(final LaunchableTarget launchableTarget) {
        mLaunchableTargetFactory.removeLaunchableTarget(launchableTarget.getTargetName());
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
        return mLaunchableTargetFactory.getNormalLaunchableTargets();
    }

    /**
     * Method getAllLaunchableTargets
     *
     *
     * @return
     *
     */
    public LaunchableTarget[] getAllLaunchableTargets() {
        return mLaunchableTargetFactory.getAllLaunchableTargets();
    }

    private void closeApps() throws PropertyVetoException {

        Vector openApps = new Vector(launchedTargets);
        Iterator it = openApps.iterator();

        while (it.hasNext()) {
            KernelLaunchedTarget klt = (KernelLaunchedTarget) it.next();

            if (klt.getTargetName().equals(mLaunchableTargetFactory.SHUTDOWN_APP)) {
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
     * Method shutdownAvalon
     *
     *
     * @param force
     *
     * @throws PropertyVetoException
     *
     */
    public void shutdownAvalon(final boolean force) throws PropertyVetoException {

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
                cLoader = mLaunchableTargetFactory.getClassLoader(dlt);
                cl = cLoader.loadClass(dlt.getClassName());
            }

            Decorator oldDecorator = mCurrentDecorator;

            // all fine, put in place decorator
            mCurrentDecorator = (Decorator) cl.newInstance();

            mCurrentDecorator.setDesktopKernel(mDesktopKernelProxy);

            if (mCurrentDecorator instanceof ObjConfigurable) {
                ((ObjConfigurable) mCurrentDecorator)
                    .setConfig(mConfigManager.getObjConfig(dlt.getConfigPath(), cLoader));
            }

            currentDecoratorLaunchableTarget = dlt;

            if (oldDecorator instanceof PropertyChangeListener) {
                mConfigManager.unRegisterConfigInterest(oldDecorator);
            }

            if (mCurrentDecorator instanceof PropertyChangeListener) {
                mConfigManager.registerConfigInterest(mCurrentDecorator,currentDecoratorLaunchableTarget.getConfigPath());
            }

            mAppLauncher = new AppLauncherImpl(mWindowManager, mLaunchableTargetFactory, this,
                                              mConfigManager, launchedTargets, mImageRepository,
                                              mCurrentDecorator, mBaseDirectory);

            mAppLauncherProxy = (AppLauncher) DynamicProxy.newInstance(mAppLauncher, new Class[] {AppLauncher.class});

            mWindowManager.updateComponentTreeUI();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
    }

    public DesktopKernel getProxy() {
      return mDesktopKernelProxy;
    }

    private void initializeDecorator() {

        String targetName = mConfigManager.getStringConfig("decorator/currentDecorator", DFT_DECORATOR);

        //if (targetName.equals(DFT_DECORATOR)) {
        //    setDecoratorLaunchableTarget(defaultDecorator);
        //} else {
        //    setDecoratorLaunchableTarget((DecoratorLaunchableTarget) mLaunchableTargetFactory
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
        return mLaunchableTargetFactory.getDecoratorLaunchableTargets();
    }

    /**
     * Method getConfigletLaunchableTargets
     *
     *
     * @return
     *
     */
    public ConfigletLaunchableTarget[] getConfigletLaunchableTargets() {
        return mLaunchableTargetFactory.getConfigletLaunchableTargets();
    }

    protected void setConfig(final String configPath, final Object config) {

        if (config instanceof String) {
            String cfg = (String) config;

            if (configPath.equals("decorator/currentDecorator")) {
                setDecoratorLaunchableTarget((DecoratorLaunchableTarget) mLaunchableTargetFactory
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
     * @version $Revision: 1.1 $
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
     * @version $Revision: 1.1 $
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
